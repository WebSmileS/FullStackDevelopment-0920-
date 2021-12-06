package protocol

import "github.com/pkg/errors"

const (
	CMD_HEART_BEAT = "heart_beat"
	CMD_SAY_HELLO  = "say_hello"
	CMD_ERROR      = "error"
	CMD_MESSAGE    = "message"
)

const (
	CODE_SUCCESS         = 0
	CODE_ERR_WRONG_DATA  = 1
	CODE_ERR_NOTFOUND    = 2
	CODE_ERR_DB_FAILED   = 3
	CODE_ERR_AUTH_FAILED = 4
	CODE_ERR_FORBIDDEN   = 5
	CODE_ERR_EXIST       = 6
	CODE_ERR_EXTERNAL    = 7
)

const (
	MSG_NEW_LOG = "new_log"
)

type WSMessage struct {
	To   []string    `json:"to"`
	Type string      `json:"type"`
	Msg  string      `json:"msg"`
	Data interface{} `json:"data"`
	Json []byte
}

type ClientMessage struct {
	From string
	To   string
	Type string
	Msg  string
	Data interface{}
	Json []byte
}

// 返回消息Json，包含错误代码和信息，如正常返回Code值为0
// swagger:model jsonMessage
type JsonMessage struct {
	// 返回代码，0为正确，其他为错误
	Code int `json:"code"`
	// 返回消息
	Msg string `json:"msg"`
	// 返回实体，有可能为空
	// Required: false
	Data interface{} `json:"data"`
}

// 返回消息Json，包含错误代码和信息，如正常返回Code值为0
// swagger:response responseMessage
type responseMessage struct {
	// in: body
	jsonMessage JsonMessage
}

func ErrMsg(code int, msg string) JsonMessage {
	return JsonMessage{
		Code: code,
		Msg:  msg,
	}
}

func SuccessMsg(msg string) JsonMessage {
	return JsonMessage{
		Code: CODE_SUCCESS,
		Msg:  msg,
	}
}

func SuccessData(data interface{}) JsonMessage {
	return JsonMessage{
		Code: CODE_SUCCESS,
		Msg:  "success",
		Data: data,
	}
}

func Success() JsonMessage {
	return JsonMessage{
		Code: CODE_SUCCESS,
		Msg:  "success",
		Data: nil,
	}
}

func WrongParams() JsonMessage {
	return JsonMessage{
		Code: CODE_ERR_WRONG_DATA,
		Msg:  "wrong parameters",
		Data: nil,
	}
}

func FlatErrMeg(err error) string {
	return errors.WithStack(err).Error()
}
