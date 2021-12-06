// Package classification Tree API.
//
// 树，权限配置，用户角色管理相关接口
//
//      Host:
//      Version: 1.0.0
//		Schemes: http, https
// swagger:meta
package controller

import (
	entService "bjyixiu.com/ent/service"
	"bjyixiu.com/gateway/config"
	"bjyixiu.com/gateway/protocol"
	"github.com/LyricTian/jpush-go"
	mob_push_sdk "github.com/saintarion/mobpush-websdkv3-go"
)

type mpushClient struct {
	appName      string
	appKey       string
	client       *mob_push_sdk.PushClient
	activityName string
}

// 角色权限服务
var ent entService.Service

// websocket消息发送接口
var wsSender func(*protocol.WSMessage)

// 环境
var env_runtime_dev bool

// 极光推送

// YRT用MobPush
var mpushYRT mpushClient

// YXK用MobPush
var mpushYXK mpushClient

// 根据配置初始化
func Init(conf config.Configuration) {
	smsHost = conf.SMS.URL
	smsKey = conf.SMS.Key

	//sessionName = conf.Session.CookieName
	//sessionExpire = conf.Session.Expire
	//sessionWXExpire = conf.Session.WXExpire

	wsSender = NewWebsocket()
	env_runtime_dev = conf.IsDev()
	//InitJpush()
	InitMobPush(conf.MPushYRT, conf.MPushYXK)
	// 初始化session
	InitSession(conf.KVDB, conf.Session)
	// 设置v3版本的登录接口地址
	for _, p := range conf.Proxies {
		if p.Prefix == "/v3" {
			V3Host = p.Url
		}

		if p.Prefix == "/v1" {
			V1Host = p.Url
		}
	}
}

//var UserHandler UserController

// 初始化Controllers
// LogController
func NewLogController(m LogManager) *LogController {
	l := new(LogController)
	l.manager = m
	return l
}

func SetEntService(e entService.Service) {
	ent = e
}

func InitJpush() {
	jpush.Init(2,
		jpush.SetAppKey("e6e2354af55e2e4b09791d2e"),
		jpush.SetMasterSecret("a8dd710668b3f2f55a9af85f"))
}

func InitMobPush(yrt, yxk config.MPush) {
	mpushYRT = mpushClient{
		appName:      APPLICATION_NAME_YRT,
		appKey:       yrt.AppKey,
		client:       mob_push_sdk.NewPushClient(yrt.AppKey, yrt.AppSecret),
		activityName: "com.bjyixiu.yrt.MainActivity",
	}

	mpushYXK = mpushClient{
		appName:      APPLICATION_NAME_YXK,
		appKey:       yxk.AppKey,
		client:       mob_push_sdk.NewPushClient(yxk.AppKey, yxk.AppSecret),
		activityName: "com.bjyixiu.yxk.MainActivity",
	}
}
