package proxy

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/url"

	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
	"github.com/pkg/errors"
)

// Echo自带代理（全局代理）
func EchoProxy(g *echo.Group, host string) error {
	url, err := url.Parse(host)

	if err != nil {
		return errors.Wrap(err, "解析Host失败")
	}

	targets := []*middleware.ProxyTarget{
		&middleware.ProxyTarget{
			URL: url,
		}}

	g.Use(middleware.Proxy(middleware.NewRoundRobinBalancer(targets)))
	return nil
}

// 专用于跨域访问代理，用本机代理访问远程后再返回解决跨域访问
func CrosProxy(g *echo.Group, host string) error {
	proxyHost, err := url.Parse(host)

	if err != nil {
		return errors.Wrap(err, "解析Host失败")
	}

	g.Use(CrosProxyHandler(proxyHost))
	return nil
}

// 执行代理，用Echo Middleware方式
func CrosProxyHandler(host *url.URL) func(echo.HandlerFunc) echo.HandlerFunc {
	return func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			defer next(c)
			// 拼接Url
			url := host.String() + c.Request().RequestURI
			// 获取Request拷贝
			req, err := copyRequest(c.Request(), url)
			if err != nil {
				return err
			}
			// 修改Header
			// TODO：改进为referer重组为Host + URI形式
			req.Header.Set("Referer", host.String())
			req.Header.Set("Origin", host.String())
			// 访问
			client := &http.Client{}
			resp, err := client.Do(req)
			if err != nil {
				return err
			}
			// 返回
			body, err := ioutil.ReadAll(resp.Body)
			if err != nil {
				return errors.Wrap(err, "读取远程返回的Response错误")
			}
			if _, err := c.Response().Write(body); err != nil {
				return errors.Wrap(err, "写入Response Body错误")
			}
			// c.Response(). = resp.Header
			for key, value := range resp.Header {
				c.Response().Header()[key] = value
			}
			c.Response().WriteHeader(resp.StatusCode)
			return nil
		}
	}
}

// 拷贝Request
func copyRequest(source *http.Request, url string) (*http.Request, error) {
	// 拷贝body
	body := []byte{}
	var err error
	if source.Body != nil {
		body, err = ioutil.ReadAll(source.Body)
		if err != nil {
			return nil, errors.Wrap(err, "读取Request Body失败")
		}
	}

	req, err := http.NewRequest(source.Method, url, bytes.NewBuffer(body))
	if err != nil {
		return nil, errors.Wrap(err, "生成新的request错误")
	}

	// 拷贝header
	for key, value := range source.Header {
		req.Header[key] = value
	}
	return req, nil
}

// 显示Response
func showResponse(source *http.Response) {
	for key, value := range source.Header {
		fmt.Println("key:", key, " value:", value)
	}
}
