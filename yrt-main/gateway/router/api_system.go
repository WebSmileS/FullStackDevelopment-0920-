package router

import (
	"bjyixiu.com/gateway/controller"
	"github.com/labstack/echo/v4"
)

func systemRouter(e *echo.Group) {
	e.GET("/sys/android_version", controller.GetAndroidVersion)
	e.GET("/sys/ios_version", controller.GetIOSVersion)
	e.GET("/sys/app_version", controller.GetAppVersion)

	//	财务导出excel
	//e.POST("/business/finance/grn/export", controller.ExportExcel)
	//e.POST("/business/finance/odo/export", controller.ExportExcel)
}
