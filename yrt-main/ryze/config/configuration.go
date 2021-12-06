package config

type Pika struct {
	Host     string
	Password string
}

type Mysql struct {
	Host     string
	Port     string
	User     string
	Password string
	Database string
}

// 短信接口,
type SMS struct {
	URL    string
	Key    string
	Expire int //验证码过期时间，以秒为单位
}
