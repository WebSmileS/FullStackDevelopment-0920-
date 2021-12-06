package logger

import (
	"fmt"

	"bjyixiu.com/gateway/models"
	"bjyixiu.com/gateway/protocol"
	"github.com/pkg/errors"
)

type Inserter func(*models.HTTP) error

// type LedisRecorder struct {
// 	ID string
// }

// func NewLedisRecorder() *LedisRecorder {
// 	return new(LedisRecorder)
// }

// func (r *LedisRecorder) NewLogID() string {
// 	r.ID = LogIDUUID()
// 	return r.ID
// }

func LedisHandler(sendMsg SendMsg, insert Inserter) LogHandler {
	return func() (string, func(*models.HTTP)) {
		id := LogIDUUID()
		return id, func(h *models.HTTP) {
			// 处理输入的原始HTTP
			// 解析Request

			// Response值

			// 装载消息
			var msg = &protocol.WSMessage{
				Type: "access_log",
				Msg:  "",
				Data: models.NewHttpLogInfo(h),
			}
			// 发送消息给websocket
			sendMsg(msg)
			if err := insert(h); err != nil {
				fmt.Println("Http Log持久化失败：", errors.WithStack(err))
			}
		}
	}
}
