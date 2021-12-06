package router

import (
	"bjyixiu.com/gateway/controller"
	"github.com/labstack/echo/v4"
)

func tempRouter(e *echo.Group) {
	// 更新节点-角色
	//e.GET("/tree/update", controller.LinkRoleUser)
	// 查找有权限的用户
	e.GET("/permission/get_right_users", controller.GetRightUserID)
	//	发送消息 TODO 新版本上限後廢除
	e.Any("/notify", controller.Notify)

}
