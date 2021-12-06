package models

import (
	"bytes"
	"compress/gzip"
	"io/ioutil"
	"strings"
	"time"

	"github.com/pkg/errors"
)

// Dump http 请求和返回
type HTTP struct {
	General
	Request  Payload
	Response Payload
}

type General struct {
	ID           string
	BeginTime    time.Time
	EndTime      time.Time
	RemoteIP     string
	TLS          bool
	URL          string
	Method       string
	ResponseCode int
}

type Payload struct {
	Header map[string][]string
	Body
}

type Body struct {
	Type    string
	Content []byte
}

func (h *HTTP) Spend() time.Duration {
	return h.EndTime.Sub(h.BeginTime)
}

func unzip(p *Payload) error {
	// 如果进行了Gzip压缩，则解压再解析
	values, ok := p.Header["content-encoding"]
	if ok {
		encoding := InOneLine(values)
		if strings.Contains(encoding, "gzip") {
			buf := bytes.NewBuffer(p.Content)
			zr, err := gzip.NewReader(buf)
			if err != nil {
				return errors.Wrap(err, "解压Gzip Body出错")
			}
			p.Content, err = ioutil.ReadAll(zr)
			if err != nil {
				return errors.Wrap(err, "读取解压后的Body值出错")
			}
		}
	}
	return nil
}

// 整理Header到一行中
func InOneLine(values []string) (str string) {
	if len(values) == 1 {
		str = values[0]
	} else {
		for _, s := range values {
			str = str + " " + s
		}
	}
	return
}
