package router

import (
	"bjyixiu.com/gateway/controller"
	"github.com/labstack/echo/v4"
)

func objectRouter(e *echo.Group) {
	// 建立七牛图片服务和controller
	//imageController := controller.NewImageController(twisted.NewQiNiu())
	// 图片上传跳转
	// e.POST("/v1/image/upload")
	// 获取七牛token
	e.GET("/image/token", controller.GetUpImageToken)
	// 获取七牛token和过期时间
	e.GET("/image/token_with_expire", controller.GetUpImageTokenWithExpire)
	// 下载图片
	e.GET("/image/get/:id", controller.ImageDownloadUrl)
	// 获取七牛token
	e.GET("/att/token", controller.GetUpAttToken)
	// 获取七牛token和过期时间
	e.GET("/att/token_with_expire", controller.GetUpAttTokenWithExpire)
	// 下载附件
	e.GET("/att/get/:id", controller.AttachmentDownloadUrl)
	// 接七牛callback
	e.POST("/qiniu/upcallback", controller.QiniuCallback)
	// 移动端（无session）下载图片
	e.GET("/image/get/:token/:id", controller.ImageDownloadUrlWithToken)
	// 移动端（无session）下载对象
	e.GET("/att/get/:token/:id", controller.AttachmentDownloadUrlWithToken)
	// 获取图片的下载地址和过期时间
	e.GET("/image/url_with_expire/:id", controller.ImageDownloadUrlWithTime)
	// 获取附件的的下载地址和过期时间
	e.GET("/att/url_with_expire/:id", controller.AttachmentDownloadUrlWithTime)
}
