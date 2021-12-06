package router

import (
	"net/http"
	"net/url"
	"os"

	"bjyixiu.com/gateway/config"

	"github.com/labstack/echo/v4"
)

// 有时静态路由可能不够全面，用于遗漏静态文件的返回，判断如访问的文件存在则返回文件，否则返回root
// 目前没有使用，等待后续可能用在根目录静态文件的处理上，如robot.txt
// 废弃
func root(c echo.Context, basePath string) error {
	path := c.Request().RequestURI
	if fileExist(basePath + path) {
		return c.File(basePath + path)
	}
	return index(basePath)(c)
}

// 注册前端路由
func frontEntRouter(e *echo.Echo, frontends []config.FrontEnd) {
	fes := make(map[string]string)
	defaultRoot := frontends[len(frontends)-1].HomePath
	for _, fe := range frontends {
		fes[fe.Domain] = fe.HomePath
		// 根目录默认index.html
		//e.GET("/", index(fe.HomePath))
	}

	root := func(c echo.Context) error {
		homePath, ok := fes[c.Request().Host]
		if !ok {
			homePath = defaultRoot
		}
		path, err := url.QueryUnescape(c.Request().RequestURI)
		if err != nil {
			return c.String(http.StatusNotFound, "访问的内容不存在")
		}
		if fileExist(homePath + path) {
			return c.File(homePath + path)
		} else {
			// 未匹配的返回Index，由前端处理
			return c.File(homePath + "/index.html")
		}
	}
	// 其它静态资源文件
	e.GET("/*", root)
}

// 前端路由,返回index.html
func index(basePath string) func(echo.Context) error {
	return func(c echo.Context) error {
		return c.File(basePath + "/index.html")
	}
}

// 检查文件是否存在
func fileExist(file string) bool {
	_, err := os.Stat(file)
	if err != nil {
		return false
	}
	return true
}
