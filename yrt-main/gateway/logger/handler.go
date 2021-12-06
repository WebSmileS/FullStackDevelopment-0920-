package logger

import (
	"bjyixiu.com/gateway/models"
	"bjyixiu.com/gateway/protocol"
	"github.com/rs/xid"
)

const MSG_TYPE_ACCESS_LOG = "access_log"

func persistence() {

}

type Recoder interface {
	NewLogID() string
	Send(*models.HTTP)
}

type SendMsg func(*protocol.WSMessage)

type DefaultRecoder struct {
}

func LogIDUUID() string {
	guid := xid.New()
	return guid.String()
}

func (r *DefaultRecoder) NewLogID() string {
	return LogIDUUID()
}

func (r *DefaultRecoder) Send(h *models.HTTP) {
	// fmt.Println(h.Request.Content)
	// fmt.Println(h.Response.Content)
}

func DefaultHandler(sendMsg SendMsg, recoder Recoder) LogHandler {
	return func() (string, func(*models.HTTP)) {
		return recoder.NewLogID(), func(h *models.HTTP) {
			// 处理输入的原始HTTP
			// 解析Request

			// Response值

			// 装载消息
			var msg = &protocol.WSMessage{
				Type: MSG_TYPE_ACCESS_LOG,
				Msg:  "",
				Data: h,
			}
			// 发送消息给websocket
			sendMsg(msg)
			recoder.Send(h)
		}
	}
}
