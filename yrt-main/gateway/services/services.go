package services

import "os"

// 微服务管理
func init() {
	// 初始化logger,使用go-kit的logger，用于建立instance
	initLogger()
	// 建立consul client
	if CreateConsulClient() {
		os.Exit(1)
	}
}
