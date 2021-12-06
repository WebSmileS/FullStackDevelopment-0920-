package controller

import (
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"log"
	"net/http"

	"github.com/bitly/go-simplejson"

	"bjyixiu.com/gateway/models"

	"github.com/pkg/errors"
)

type resultHandle func(*http.Response, interface{}) error
type restFunc func(string, string, string, io.Reader, int64) (*http.Response, error)

func RESTWithResponse(method, remote,
	contentType string,
	requestBody io.Reader,
	user *SessionUser) (*http.Response, error) {
	//var body []byte
	//var resp *http.Response
	if len(contentType) == 0 {
		contentType = "application/json"
	}
	var resp *http.Response
	var err error

	//remote = url.(remote)

	req, err := http.NewRequest(method, remote, requestBody)
	if err != nil {
		return nil, fmt.Errorf("建立内部调用请求错误: %w", err)
	}
	req.Header.Set("Content-Type", contentType)

	if user != nil {
		SetHeader(user, req)
	}

	client := new(http.Client)
	resp, err = client.Do(req)

	if err != nil {
		return nil, fmt.Errorf("内部接口调用错误:%w", err)
	}
	if resp.StatusCode != 200 {
		return nil, errors.New("内部接口调用错误")
	}

	return resp, nil
}

// 调用外部（医修库v3）接口
// Params:
//		method: 方法，"get" "post" "put" "delete", 调用http.MethodXX
//		url: 调用的地址
//		contenType: 内容类型，默认为："application/json"
//		requestBody: 请求内容,无内容传入Nil
//		resultJson: 接收返回的json
//		handleFunc: 接收返回值的处理函数
//		userID: 当前用户的id，有的可能需要
// Return:
//		error: 错误返回
func REST(method, url,
	contentType string,
	requestBody io.Reader,
	result interface{},
	handle resultHandle,
	user *SessionUser) error {
	//var body []byte
	//var resp *http.Response

	if len(contentType) == 0 {
		contentType = "application/json"
	}

	req, err := http.NewRequest(method, url, requestBody)
	if err != nil {
		return fmt.Errorf("建立内部调用请求错误: %w", err)
	}

	req.Header.Set("Content-Type", contentType)
	SetHeader(user, req)

	client := new(http.Client)
	resp, err := client.Do(req)

	defer resp.Body.Close()

	if resp.StatusCode != 200 {
		return errors.New("内部接口调用错误")
	}

	return handle(resp, result)
}

func HandleResult2Json(resp *http.Response, resultJson interface{}) error {
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return fmt.Errorf("调用接口内部错误:%w", err)
	}

	var respJson models.V3YxkResponseJson
	respJson.Data = resultJson
	if err := json.Unmarshal(body, &respJson); err != nil {
		return fmt.Errorf("调用接口数据解析错误:%w", err)
	}

	if respJson.Code != 0 {
		log.Println(respJson.InternalError)
		return errors.New(respJson.Msg)
	}
	return nil
}

func HandleResult2SimpleJson(resp *http.Response, err error) (*simplejson.Json, error) {
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	//fmt.Println("%w", er)
	//fmt.Println(string(body))
	//respJson, err := simplejson.NewJson(body)

	respJson, err := simplejson.NewFromReader(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("调用接口内部错误:%w", err)
	}

	code, err := respJson.Get("code").Int()
	if code != 0 {
		errMsg, err := respJson.Get("msg").String()
		if err != nil {
			return nil, fmt.Errorf("调用接口内部错误:%w", err)
		}
		return nil, errors.New(errMsg)
	}

	//simpleJson = respJson.Get("data")
	return respJson, nil
}

func downloadFile(method, url,
	contentType string,
	requestBody io.Reader,
	user *SessionUser) (*http.Response, error) {

	if len(contentType) == 0 {
		contentType = "application/json"
	}

	req, err := http.NewRequest(method, url, requestBody)
	if err != nil {
		return nil, fmt.Errorf("建立内部调用请求错误: %w", err)
	}

	req.Header.Set("Content-Type", contentType)

	SetHeader(user, req)

	client := new(http.Client)
	resp, err := client.Do(req)

	if resp.StatusCode != 200 {
		return nil, errors.New("内部接口调用错误")
	}

	return resp, nil
}
