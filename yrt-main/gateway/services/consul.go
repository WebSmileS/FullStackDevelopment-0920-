package services

import (
	"github.com/go-kit/kit/sd/consul"
	"github.com/hashicorp/consul/api"
)

var kitClient consul.Client

// 获取Consul客户端，给各个Service用
func CreateConsulClient() bool {
	logger.Log("开始...")
	config := api.DefaultConfig()
	config.Address = "127.0.0.1:8500"
	client, err := api.NewClient(config)
	if err != nil {
		logger.Log("建立consul client失败:", err)
		return false
	}

	kitClient = consul.NewClient(client)
	return true
}
