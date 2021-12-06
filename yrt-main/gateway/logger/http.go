package logger

import (
	"bufio"
	"bytes"
	"io"
	"io/ioutil"
	"net"
	"net/http"
	"strings"
	"time"

	"bjyixiu.com/gateway/models"
	"github.com/labstack/echo/v4"
)

const (
	BODY_TYPE_JSON    = "json"
	BODY_TYPE_FORM    = "form"
	BODY_TYPE_XML     = "xml"
	BODY_TYPE_IMAGE   = "image"
	BODY_TYPE_FONT    = "font"
	BODY_TYPE_JS      = "js"
	BODY_TYPE_CSS     = "css"
	BODY_TYPE_FILE    = "file"
	BODY_TYPE_HTML    = "html"
	BODY_TYPE_PLAIN   = "plain"
	BODY_TYPE_MPFD    = "mpfd"
	BODY_TYPE_UNKNOWN = "unknown"
)

type LogHandler func() (ID string, DO func(*models.HTTP))

type bodyDumpResponseWriter struct {
	io.Writer
	http.ResponseWriter
}

// 根据Header判断是否要保存Body，如Image，File等都不要保存
// 会设置传入的payload.body.type值，非JSON和FORM值都返回false,即不要保存
func setContentType(p *models.Payload) bool {
	// 获得请求内容
	values, ok := p.Header["content-type"]
	if !ok {
		// 无请求内容
		p.Type = BODY_TYPE_UNKNOWN
		return false
	}
	contentType := models.InOneLine(values)

	switch {
	case strings.Contains(contentType, "json"):
		p.Type = BODY_TYPE_JSON
		return true
	case strings.Contains(contentType, "x-www-form-urlencoded"):
		p.Type = BODY_TYPE_FORM
		return true
	case strings.Contains(contentType, "x-font-woff"):
		p.Type = BODY_TYPE_FONT
	case strings.Contains(contentType, "image"):
		p.Type = BODY_TYPE_IMAGE
	case strings.Contains(contentType, "javascript"):
		p.Type = BODY_TYPE_JS
	case strings.Contains(contentType, "css"):
		p.Type = BODY_TYPE_CSS
	case strings.Contains(contentType, "html"):
		p.Type = BODY_TYPE_HTML
	case strings.Contains(contentType, "multipart/form-data"):
		p.Type = BODY_TYPE_MPFD
	case strings.Contains(contentType, "text/plain"):
		p.Type = BODY_TYPE_PLAIN
	default:
		p.Type = BODY_TYPE_UNKNOWN
	}

	return false
}

// echo中间件
func HttpLogger(handler LogHandler) func(echo.HandlerFunc) echo.HandlerFunc {
	return func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) (err error) {
			// log查询的不需要记录
			uri := c.Request().RequestURI
			if strings.Index(uri, "api/log") >= 0 {
				if err = next(c); err != nil {
					c.Error(err)
				}
			}
			// 开始记录
			h := new(models.HTTP)
			// 记录开始时间
			h.BeginTime = time.Now()
			// General
			h.RemoteIP = c.RealIP()
			h.TLS = c.IsTLS()
			h.URL = c.Request().Host + c.Request().RequestURI
			h.Method = c.Request().Method
			// 拷贝请求头
			h.Request.Header = make(map[string][]string)
			for k, v := range c.Request().Header {
				h.Request.Header[strings.ToLower(k)] = v
			}
			// Request
			isPayload := setContentType(&h.Request)

			// 有意义的请求Body才需要保存
			if isPayload {
				if c.Request().Body != nil { // Read
					h.Request.Body.Content, _ = ioutil.ReadAll(c.Request().Body)
				}
				c.Request().Body = ioutil.NopCloser(bytes.NewBuffer(h.Request.Body.Content)) // Reset
			}

			// Response
			resBody := new(bytes.Buffer)
			mw := io.MultiWriter(c.Response().Writer, resBody)
			writer := &bodyDumpResponseWriter{Writer: mw, ResponseWriter: c.Response().Writer}
			c.Response().Writer = writer
			// 在返回头中设置ID，用于从日志中快速定位
			logid, do := handler()
			h.ID = logid
			c.Response().Header().Add("logid", logid)
			// Next
			if err = next(c); err != nil {
				c.Error(err)
			}

			// 记录结束时间
			h.EndTime = time.Now()
			// 响应状态
			h.ResponseCode = c.Response().Status
			// 拷贝响应头
			h.Response.Header = make(map[string][]string)
			for k, v := range c.Response().Header() {
				h.Response.Header[strings.ToLower(k)] = v
			}
			// 判断是否有意义的Response Body
			isPayload = setContentType(&h.Response)

			if isPayload {
				h.Response.Content = resBody.Bytes()
			}

			// 交付处理的日志
			if do != nil {
				do(h)
			}

			return
		}
	}
}

// abandoned
// 导出 请求/响应 的内容
func dumpPayLoad(headers map[string][]string, bodys io.ReadCloser) (models.Payload, error) {
	var p models.Payload
	p.Header = make(map[string][]string)
	for k, v := range headers {
		p.Header[k] = v
	}

	if bodys == nil {
		return p, nil
	}
	p.Body.Content, _ = ioutil.ReadAll(bodys)
	bodys = ioutil.NopCloser(bytes.NewBuffer(p.Body.Content))

	return p, nil
}

func (w *bodyDumpResponseWriter) WriteHeader(code int) {
	w.ResponseWriter.WriteHeader(code)
}

func (w *bodyDumpResponseWriter) Write(b []byte) (int, error) {
	return w.Writer.Write(b)
}

func (w *bodyDumpResponseWriter) Flush() {
	w.ResponseWriter.(http.Flusher).Flush()
}

func (w *bodyDumpResponseWriter) Hijack() (net.Conn, *bufio.ReadWriter, error) {
	return w.ResponseWriter.(http.Hijacker).Hijack()
}

func (w *bodyDumpResponseWriter) CloseNotify() <-chan bool {
	return w.ResponseWriter.(http.CloseNotifier).CloseNotify()
}
