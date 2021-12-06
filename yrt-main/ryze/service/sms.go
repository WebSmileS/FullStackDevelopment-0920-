package service

import (
	"bjyixiu.com/ryze/models"
	"github.com/pkg/errors"
)

func SetSMSVerifyCode(mobile string, code string) error {

	key := models.KeySMSCode(mobile)
	if err := kv.Set(key, code); err != nil {
		return errors.Wrap(err, "短信验证码存储出错")
	}

	if err := kv.Expire(key, smsCodeExpire); err != nil {
		return errors.Wrap(err, "短信验证码设置过期时间出错")
	}
	return nil
}

func GetSMSVerifyCode(mobile string) (string, error) {
	key := models.KeySMSCode(mobile)
	code, err := kv.Get(key)
	if err != nil {
		return "", errors.Wrap(err, "读取短信验证码出错")
	}
	return code, nil
}
