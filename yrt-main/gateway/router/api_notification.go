package router

import (
	"bjyixiu.com/gateway/controller"
	"github.com/labstack/echo/v4"
)

func notificationRouter(e *echo.Group) {
	// 发送消息
	e.POST("/notification/send", controller.SendNotification)
	// app端单独注册
	e.POST("/notification/app_registration/:id", controller.AppOnlineSignup)
}
