package main

import (
	"fmt"
	"log"
	"time"

	"bjyixiu.com/ent"
	"bjyixiu.com/gateway/manager"
	"bjyixiu.com/twisted"

	"bjyixiu.com/ryze/service"

	"bjyixiu.com/gateway/config"
	"bjyixiu.com/gateway/controller"

	// _ "bjyixiu.com/gateway/docs"
	"bjyixiu.com/gateway/logger"
	"bjyixiu.com/gateway/router"
	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
	"github.com/pkg/errors"
)

// @title 医润通API
// @version 1.0
// @description 医润通自定义菜单和权限角色相关API
// @termsOfService http://swagger.io/terms/

// @contact.name arion
// @contact.url http://yrt.bjyixiu.com
// @contact.email tao.yang@bjyixiu.com

// @license.name Apache 2.0
// @license.url http://www.apache.org/licenses/LICENSE-2.0.html

// @host
// @BasePath /v1
func main() {
	e := echo.New()
	// 开启错误恢复
	e.Use(middleware.Recover())
	// 读取配置
	configuration, err := config.LoadConfiguration("")
	if err != nil {
		log.Fatal("加载配置错误：", errors.WithStack(err))
	}

	// 启动GZip
	// 代理模式为跨域代理时不能进行Gzip压缩!!
	if configuration.GZip {
		e.Use(middleware.Gzip())
	}
	// 是否允许跨域
	if configuration.Cros {
		e.Use(middleware.CORS())
	}
	// 启动服务
	CreateService(configuration)

	// Controoler配置
	controller.Init(configuration)

	// 路由配置
	router.Router(e, configuration)

	// 启动日志
	if configuration.Logger.Enable {
		// 启动持久化
		m := manager.NewLedisManager()
		// access日志
		handleLog(e, configuration.Logger, m)
	}

	if configuration.TLSCert.Enable {
		//e.Use(middleware.HTTPSRedirect())
		go func() {
			e.Logger.Fatal(e.StartTLS(":443", configuration.TLSCert.Pem, configuration.TLSCert.Key))
		}()
	}

	// 监听80端口
	e.Logger.Fatal(e.Start(":" + configuration.Port))

}

func test(e *echo.Echo) {

	e.GET("/test/:method/r/:id", func(c echo.Context) error {
		fmt.Println("GET | /test/:method/r/:id")
		return c.String(200, c.Param("method")+" "+c.Param("id"))
	})

}

func ttyLog(c echo.Context, reqBody, resBody []byte) {
	go func() {
		// 开始
		fmt.Println("--------------------------------------------------")
		fmt.Println(time.Now())
		fmt.Println("URI", c.Request().RequestURI, "METHOD", c.Request().Method)
		if len(reqBody) > 1000 {
			fmt.Println("Request too large")
		} else if len(reqBody) > 0 {
			fmt.Println("Request Body", string(reqBody))
		}
		if len(resBody) > 1000 {
			fmt.Println("Response too large")
		} else if len(resBody) > 0 {
			fmt.Println("Response Body", string(resBody))
		}
		fmt.Println("--------------------------------------------------")
	}()
}

func handleLog(e *echo.Echo, logConfig config.Logger, m controller.LogManager) {
	logController := controller.NewLogController(m)
	e.GET("/v1/log/get_by_id/:id", logController.GetLogByID)
	e.POST("/v1/log/get_by_time", logController.GetLogByTime)

	// 启动日志服务
	// 建立日志即时消息
	send := controller.NewWebsocket()

	// 日志handler
	// var recoder = &logger.DefaultRecoder{}
	// handler := logger.DefaultHandler(send, recoder)
	handler := logger.LedisHandler(send, m.Insert)
	e.Use(logger.HttpLogger(handler))
}

func CreateService(conf config.Configuration) {
	//	启动ryze服务
	if err := service.InitService(conf.Pika, conf.Mysql, conf.SMS); err != nil {
		log.Fatal("初始化Ryze服务出错", errors.WithStack(err))
	}

	// 启动twisted服务
	// TODO 把twisted服务初始化从router中拿出来，twiste应该作为一个独立的服务
	twisted.InitService(conf.Object)
	//	启动ent服务
	ent := ent.NewEnt(conf.EntConf, conf.IsDev())
	controller.SetEntService(ent)
}
