package models

import (
	"encoding/json"
	"fmt"
	"net/url"

	"github.com/pkg/errors"
)

type HttpLogInfo struct {
	Spent string
	General
	Request  PayloadInfo
	Response PayloadInfo
}

type PayloadInfo struct {
	Header map[string][]string
	BodyInfo
}

type BodyInfo struct {
	Type string
	Data interface{}
}

func NewHttpLogInfo(h *HTTP) *HttpLogInfo {
	info := new(HttpLogInfo)
	info.General = h.General
	info.Spent = h.Spend().String()

	// unmarshal request
	// TODO: h.Method可能返回方法不规范,或不符合要求,需要从源头上处理
	// h.URL返回不带协议名称的,需要添加
	info.Request.Header = h.Request.Header
	info.Request.BodyInfo.Type = h.Request.Body.Type
	info.Request.Data = uncodingBody(h.Request)
	// unmarshal response
	info.Response.Header = h.Response.Header
	info.Response.BodyInfo.Type = h.Response.Body.Type
	info.Response.Data = uncodingBody(h.Response)

	return info
}

func uncodingBody(p Payload) (body interface{}) {
	if p.Type == "json" {

		if err := unzip(&p); err != nil {
			fmt.Println("对Gzip解压错误:", errors.WithStack(err))
			return
		}

		if err := json.Unmarshal(p.Content, &body); err != nil {
			// TODO 记录错误
			fmt.Println("反解析JSON错误:", err, "Header", p.Header)
		}

	} else if p.Type == "form" {
		var err error
		body, err = url.ParseQuery(string(p.Content))
		if err != nil {
			// TODO 记录错误
			fmt.Println("反解析FORM错误:", err)
		}
	}
	return
}
