package controller

import (
	"encoding/json"
	"github.com/pkg/errors"
	"io/ioutil"
	"math/rand"
	"net/http"
	"net/url"
	"time"
)

type SMSHostResponse struct {
	Error_code int
	Reason     string
	Result     SMSHostResponseResult
}

type SMSHostResponseResult struct {
	Count int
	Fee   int
	Sid   string
}

const (
	RAND_NUMBER        = "0123456789"
	RAND_NUMBER_LETTER = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	RAND_NUMBER_LOW    = "0123456789abcdefghijklmnopqrstuvwxyz"
	RAND_NUMBER_UPPER  = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
)

var smsHost string
var smsKey string
var randSeek int64

func sendSMS(mobile string, tplID string, tplValue string) error {
	param := url.Values{}
	param.Set("mobile", mobile)
	param.Set("tpl_id", tplID)
	param.Set("tpl_value", tplValue)
	param.Set("key", smsKey)

	resp, err := http.PostForm(smsHost, param)
	if err != nil {
		return errors.Wrap(err, "访问短信接口错误")
	}
	defer resp.Body.Close()

	respByties, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return errors.Wrap(err, "访问短信接口返回数据错误")
	}

	result := new(SMSHostResponse)
	if err := json.Unmarshal(respByties, result); err != nil {
		return errors.Wrap(err, "访问短信接口返回数据解析错误")
	}

	if result.Error_code != 0 {
		return errors.Wrap(err, "短信发送失败请稍后再试")
	}

	return nil
}

func sendSMSVerifyCode(mobile string, code string) error {
	tmpl := `#code#=` + code
	if err := sendSMS(mobile, "100293", tmpl); err != nil {
		return errors.Wrap(err, "发送验证码短信失败")
	}
	return nil
}

func sendSMSResetPwd(mobile string, pwd string) error {
	tmpl := `#password#=` + pwd
	if err := sendSMS(mobile, "183023", tmpl); err != nil {
		return errors.Wrap(err, "发送重置密码失败")
	}
	return nil
}

func genSMSVerifyCode() string {
	return string(RandSeq(6, RAND_NUMBER))
}

func genResetPwd(count int) string {
	return string(RandSeq(count, RAND_NUMBER_UPPER))
}

func getSeed() int64 {
	if randSeek >= 100000000 {
		randSeek = 1
	}
	randSeek++
	return time.Now().UnixNano() + randSeek
}

func RndNum(count int) int {
	rand.Seed(getSeed())
	return rand.Intn(count)
}

func RandSeq(n int, seq string) []byte {
	rand.Seed(getSeed())
	letters := []byte(seq)
	b := make([]byte, n)
	for i := range b {
		b[i] = letters[rand.Intn(len(letters))]
	}
	return b
}
