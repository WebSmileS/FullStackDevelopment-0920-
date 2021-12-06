package proto

type ResponseCommon struct {
	// 返回代码，0为正确，其他为错误
	Code int `json:"code"`
	// 返回消息
	Msg string `json:"msg"`
}
