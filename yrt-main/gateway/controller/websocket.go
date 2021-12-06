package controller

import (
	"log"
	"net/http"
	"sync"

	"bjyixiu.com/gateway/logger"

	"bjyixiu.com/gateway/protocol"
	"github.com/labstack/echo/v4"
	"golang.org/x/net/websocket"
)

type hub struct {
	// 安全
	mu sync.RWMutex
	// 注册了的连接器
	connections sync.Map
	// 从连接器中发入的信息
	broadcast chan *protocol.WSMessage
	// 从连接器中注册
	checkin chan *connection
	// 从连接器中注销
	checkout chan *connection
}

var wsHub = hub{
	broadcast:   make(chan *protocol.WSMessage),
	checkin:     make(chan *connection),
	checkout:    make(chan *connection),
	connections: sync.Map{},
}

func NewWebsocket() func(*protocol.WSMessage) {
	go wsHub.run()
	return func(msg *protocol.WSMessage) {
		wsHub.broadcast <- msg
	}
}

func (h *hub) run() {
	for {
		select {
		case c := <-h.checkin:
			h.connections.Store(c.sessionID, c)
		case c := <-h.checkout:
			h.connections.Delete(c.sessionID)
			close(c.send)
			if err := c.ws.Close(); err != nil {
				log.Println("websocket closed error", err.Error())
			}

		case m := <-h.broadcast:
			h.sendHandler(m)
		}
	}
}

type connection struct {
	ws        *websocket.Conn
	send      chan *protocol.WSMessage
	sessionID string
	Name      string
	ErrCount  int
}

func (c *connection) reader(handler func(*protocol.WSMessage, *connection)) {
	for {
		var cmd protocol.WSMessage
		if err := websocket.JSON.Receive(c.ws, &cmd); err != nil {

			log.Println("websocket消息接收错误：", err)
			if c.ErrCount > 2 {
				break
			}
			c.ErrCount++
			// 返回错误，增加错误计数，超出限制断开连接
			m := protocol.WSMessage{
				Type: protocol.CMD_ERROR,
				Msg:  "wrong schema: " + err.Error(),
			}
			c.send <- &m

		} else {
			c.ErrCount = 0
			handler(&cmd, c)
		}
	}
}

func (c *connection) writer() {
	for msg := range c.send {
		if err := websocket.JSON.Send(c.ws, msg.Data); err != nil {
			// TODO 重试次数，最终断开
			log.Println("websocket发送错误sid:" + c.sessionID + "错误:" + err.Error())
			// TODO 注销，等待前端自动重连
		}
	}
}

func WebsocketController(c echo.Context) error {
	sessionID, err := AuthConnection(c)
	if err != nil {
		return c.String(http.StatusUnauthorized, err.Error())
	}
	websocket.Handler(func(ws *websocket.Conn) {
		conn := &connection{
			// 100的缓存量
			send:      make(chan *protocol.WSMessage, 100),
			ws:        ws,
			sessionID: sessionID}
		wsHub.checkin <- conn
		defer func() {
			wsHub.checkout <- conn
		}()
		go conn.writer()

		//向新接入client发送初始化数据
		conn.reader(receiveHandler)
	}).ServeHTTP(c.Response(), c.Request())
	return nil
}

func (h *hub) sendHandler(m *protocol.WSMessage) {
	switch m.Type {
	case logger.MSG_TYPE_ACCESS_LOG:
	case MSG_TYPE_NOTIFICATION:
		if len(m.To) == 0 {
			h.connections.Range(func(key, value interface{}) bool {
				c := value.(*connection)
				c.send <- m
				return true
			})
		} else {
			for _, sessionID := range m.To {
				value, ok := h.connections.Load(sessionID)
				if ok {
					c := value.(*connection)
					c.send <- m
				}
			}
		}
	default:

	}

}

func receiveHandler(wsm *protocol.WSMessage, conn *connection) {
	if env_runtime_dev {
		log.Println(wsm)
	}
}

// func Client(c echo.Context) error {
// 	websocket.Handler(func(ws *websocket.Conn) {
// 		conn := &connection{
// 			send: make(chan protocol.Message, 100),
// 			ws:   ws,
// 			IP:   c.RealIP()}
// 		clientHub.checkin <- conn
// 		defer func() {
// 			ClientCheckout(conn)
// 			clientHub.checkout <- conn
// 		}()
// 		go conn.writer()

// 		//向新接入client发送初始化数据
// 		conn.reader(clientHandler)
// 	}).ServeHTTP(c.Response(), c.Request())
// 	return nil
// }
