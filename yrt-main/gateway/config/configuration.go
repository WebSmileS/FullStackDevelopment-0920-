package config

import (
	"encoding/json"
	"os"

	ent "bjyixiu.com/ent/config"
	ryze "bjyixiu.com/ryze/config"
	"bjyixiu.com/twisted"

	"github.com/pkg/errors"
)

const (
	PROXY_TYPE_REVERSE string = "reverse"
	PROXY_TYPE_CROS    string = "cros"

	RUNTIME_TYPE_PROD string = "prod"
	RUNTIME_TYPE_DEV  string = "dev"
)

type Configuration struct {
	Runtime  string
	Port     string
	GZip     bool
	Cros     bool
	TLSCert  TLSCert
	Views    Views
	FrontEnd []FrontEnd
	Proxies  []Proxy
	Logger   Logger
	Object   twisted.Object
	Pika     ryze.Pika
	Mysql    ryze.Mysql
	MysqlEnt ent.MySQL
	SMS      ryze.SMS
	MPushYRT MPush
	MPushYXK MPush
	Session  Session
	EntConf  ent.Config
	KVDB     KVDB // session缓存
}

func (c Configuration) IsDev() bool {
	if c.Runtime == RUNTIME_TYPE_DEV {
		return true
	}
	return false
}

func (c Configuration) IsProd() bool {
	if c.Runtime == RUNTIME_TYPE_PROD {
		return true
	}
	return false
}

// 证书配置
// Enable置为false，不会监听443端口
type TLSCert struct {
	Enable bool
	Key    string
	Pem    string
}

// 静态文件配置和页面配置
// StaticDir列出所有的静态目录，路由优先匹配静态目录
type Views struct {
	Path      string
	Dir       []string
	StaticDir []string
}

// 前端文件路径
type FrontEnd struct {
	Domain   string // 域名
	HomePath string // 根路径
}

// 反向代理配置
// 除首页，静态目录，定义的Api段，其他所有的请求都转到代理
type Proxy struct {
	Enable bool
	Type   string //reverse 反向代理 cros 跨域代理
	Prefix string //代理的前缀
	Url    string
}

// 访问日志配置
// Output标识输出，支持tty和mongodb两种输出模式，tty为简单输出
type Logger struct {
	Enable bool
	Output string //tty , mongodb
}

// MobTech推送
type MPush struct {
	AppKey    string
	AppSecret string
}

// Session
type Session struct {
	CookieName string
	Expire     int //Web过期时间，单位为秒
	WXExpire   int //微信过期时间，单位为秒
}

// kv cache
type KVDB struct {
	Host     string // 服务器地址
	Password string // 密码
}

// 根据输入读取配置文件
func LoadConfiguration(file string) (Configuration, error) {
	var conf Configuration

	if len(file) > 0 {
		ConfigFile = file
	}

	configFile, err := os.Open(ConfigFile)
	defer configFile.Close()

	if err != nil {
		return conf, errors.Wrap(err, "配置文件读取错误")
	}

	decoder := json.NewDecoder(configFile)
	if err := decoder.Decode(&conf); err != nil {
		return conf, errors.Wrap(err, "配置解析错误")
	}

	// 校验配置
	if err := validateConfiguration(conf); err != nil {
		return conf, errors.Wrap(err, "配置校验失败")
	}

	// 设置pika为Ent服务 TODO 合并为一个，不需要再重新赋值
	conf.EntConf.Pika.Host = conf.Pika.Host
	conf.EntConf.Pika.Password = conf.Pika.Password
	conf.EntConf.MySQL.Database = conf.MysqlEnt.Database
	conf.EntConf.MySQL.Host = conf.MysqlEnt.Host
	conf.EntConf.MySQL.Port = conf.MysqlEnt.Port
	conf.EntConf.MySQL.User = conf.MysqlEnt.User
	conf.EntConf.MySQL.Password = conf.MysqlEnt.Password
	return conf, nil
}

// TODO: 校验配置
func validateConfiguration(conf Configuration) error {

	return nil
}
