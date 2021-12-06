package service

import (
	"bjyixiu.com/ryze/config"
	"bjyixiu.com/ryze/db"
	"github.com/go-xorm/xorm"
)

var kv KV
var sqlEngin *xorm.Engine
var smsCodeExpire int

func InitService(confPika config.Pika, confMysql config.Mysql, confSMS config.SMS) error {
	redis := db.NewPikaDB(confPika)

	if err := setKV(redis); err != nil {
		return err
	}

	engine, err := db.CreateMysqlEngine(confMysql)
	if err != nil {
		return err
	}

	setSMSCodeExpire(confSMS.Expire)

	return setXormEngine(engine)
}

// 设置KV引擎，并检查和初始化必要数据
func setKV(kvdb KV) error {
	kv = kvdb
	// 检查节点根树，没有则初始化建立
	//if err := CheckMotherTree(); err != nil {
	//	return errors.Wrap(err, "检查和建立节点根数时出错")
	//}
	return nil
}

// 设置xorm引擎
func setXormEngine(engin *xorm.Engine) error {
	sqlEngin = engin
	return nil
}

// 设置验证码过期时间
func setSMSCodeExpire(expire int) {
	smsCodeExpire = expire
}
