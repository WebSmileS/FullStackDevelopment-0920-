package router

import (
	"log"
	"strings"

	"bjyixiu.com/gateway/config"
	"bjyixiu.com/gateway/controller"
	"bjyixiu.com/gateway/proxy"
	"github.com/labstack/echo/v4"
	"github.com/pkg/errors"
	echoSwagger "github.com/swaggo/echo-swagger"
)

// 路由管理
func Router(e *echo.Echo, conf config.Configuration) {

	// swagger 固定写为最后一个，需要区分生产环境和开发环境
	if conf.IsDev() {
		e.GET("/swagger/*", echoSwagger.WrapHandler)
		last := len(conf.Proxies) - 1
		// swagger for java
		javaSwagger(e, conf.Proxies[last].Url)
		// Druid
		springDruid(e, conf.Proxies[last].Url)
	}

	// websocket for message push`
	e.Any("/ws", controller.WebsocketController)
	//v1 路由组
	//检查v1字段下的所有api，并把用户数据加入header
	apiV1 := e.Group("/v1")
	apiEnt := e.Group("/ent")

	//apiV1.Use(Auth)
	e.Use(Auth)
	// 系统路由
	systemRouter(apiV1)
	// 用户管理路由
	userRouter(apiV1)
	v3UserRouter(apiEnt)
	// Tree服务路由
	treeRouter(apiV1)
	// Menu服务路由
	//menuRouter(apiV1)
	// 对象存储服务路由
	objectRouter(apiV1)
	// 消息通知路由
	notificationRouter(apiV1)
	// 临时用路由
	//tempRouter(apiV1)

	for _, p := range conf.Proxies {
		group := e.Group(p.Prefix)
		//建立反向代理
		if p.Type == config.PROXY_TYPE_REVERSE {
			if err := proxy.EchoProxy(group, p.Url); err != nil {
				log.Fatal("建立反向代理错误：", errors.WithStack(err))
			}
		} else if p.Type == config.PROXY_TYPE_CROS {
			if err := proxy.CrosProxy(group, p.Url); err != nil {
				log.Fatal("建立跨域代理错误：", errors.WithStack(err))
			}
		}
	}

	// 前端路由
	if len(conf.FrontEnd) > 0 {
		frontEntRouter(e, conf.FrontEnd)
	}
}

// 静态文件路由
func staticsRouter(e *echo.Echo, basePath string, staticDir ...string) {
	// 没有统一的静态根目录，静态目录平铺的
	if len(basePath) == 0 {
		for _, d := range staticDir {
			e.Static(d, d)
		}
	} else {
		// 有统一的静态根目录
		e.Static(basePath, basePath)
		for _, d := range staticDir {
			e.Static(d, basePath+"/"+d)
		}
	}

	// TODO: 临时处理, 提供大屏显示
	//e.Static("dp", "dp")
}

func springDruid(e *echo.Echo, target string) {
	index := e.Group("/admin/monitor")
	if err := proxy.EchoProxy(index, target); err != nil {
		log.Fatal("建立反向代理错误：", errors.WithStack(err))
	}
}

func javaSwagger(e *echo.Echo, target string) {
	index := e.Group("/doc.html")
	if err := proxy.EchoProxy(index, target); err != nil {
		log.Fatal("建立反向代理错误：", errors.WithStack(err))
	}

	static := e.Group("/webjars")
	if err := proxy.EchoProxy(static, target); err != nil {
		log.Fatal("建立反向代理错误：", errors.WithStack(err))
	}

	static = e.Group("/swagger-resources")
	if err := proxy.EchoProxy(static, target); err != nil {
		log.Fatal("建立反向代理错误：", errors.WithStack(err))
	}

	apiDoc := e.Group("/v2/api-docs")
	if err := proxy.EchoProxy(apiDoc, target); err != nil {
		log.Fatal("建立反向代理错误：", errors.WithStack(err))
	}

	apiDocExt := e.Group("/v2/api-docs-ext")
	if err := proxy.EchoProxy(apiDocExt, target); err != nil {
		log.Fatal("建立反向代理错误：", errors.WithStack(err))
	}
}

func white(uri string) bool {

	if !strings.HasPrefix(uri, "/v1") &&
		!strings.HasPrefix(uri, "/v3") &&
		!strings.HasPrefix(uri, "/msg") &&
		!strings.HasPrefix(uri, "/ent") {
		return true
	}

	if uri == "/ent/login" || uri == "/ent/logout" {
		return true
	}

	if strings.HasPrefix(uri, "/v1/image/get") {
		return true
	}

	if uri == "/v1/login" || uri == "/v1/mobile_login" || uri == "/v1/logout" || uri == "/v1/reg/add" || uri == "/v1/qiniu/upcallback" {
		return true
	}
	// version
	if uri == "/v1/sys/app_version" || uri == "/v1/sys/android_version" || uri == "/v1/sys/ios_version" {
		return true
	}

	if strings.Contains(uri, "/v1/comm/area/city") {
		return true
	}

	if strings.Contains(uri, "/v1/comm/area/county") {
		return true
	}

	if strings.Contains(uri, "/v1/comm/area/list") {
		return true
	}

	if strings.Contains(uri, "/v1/comm/area/province") {
		return true
	}

	// 为Demo App 特殊打开
	if strings.HasPrefix(uri, "/v1/comm/category/list") {
		return true
	}

	if strings.HasPrefix(uri, "/v1/app/product/list") {
		return true
	}

	// 找回密码
	if strings.HasPrefix(uri, "/v1/user/retrieve_pwd") {
		return true
	}

	// 消息推送
	if strings.HasPrefix(uri, "/v1/notify") || strings.HasPrefix(uri, "/v1/notification") {
		return true
	}

	return false
}

// 检查用户是否登录，并向header写入用户数据
func Auth(next echo.HandlerFunc) echo.HandlerFunc {
	return func(c echo.Context) error {
		/// 不需要登录的api
		uri := c.Request().RequestURI

		//例外处理
		//TODO 等待前缀树实现后，再做动态例外处理
		if white(uri) {
			return next(c)
		}

		pass, err := controller.Auth(c)
		if err != nil {
			return err
		}

		if !pass {
			return nil
		}

		return next(c)
	}
}

// TODO 临时网站，佰田和维尔
func tempHost(e *echo.Echo) {
	baitian := e.Host("baitian.com")
	weier := e.Host("weier.com")
	wbaitian := e.Host("www.baitian.com")
	wweier := e.Host("www.weier.com")
	baitian.Static("/", "hosts/baitian")
	wbaitian.Static("/", "hosts/baitian")
	tempIndexRouter(baitian, "hosts/baitian")
	tempIndexRouter(wbaitian, "hosts/baitian")
	weier.Static("/", "hosts/weier")
	wweier.Static("/", "hosts/weier")
	tempIndexRouter(weier, "hosts/weier")
	tempIndexRouter(wweier, "hosts/weier")
}

// TODO 临时网站，前端路由,返回index.html
func tempIndex(basePath string) func(echo.Context) error {
	return func(c echo.Context) error {
		return c.File(basePath + "/index.html")
	}
}

// TODO 临时网站，注册前端路由
func tempIndexRouter(e *echo.Group, basePath string, dir ...string) {
	// 根目录默认index.html
	e.GET("/", index(basePath))
	// 返回前端静态页面，路由
	for _, d := range dir {
		// 子目录
		e.Any("/"+d, index(basePath))
		// 子目录下所有路由
		e.Any("/"+d+"/*", index(basePath))
	}
}
