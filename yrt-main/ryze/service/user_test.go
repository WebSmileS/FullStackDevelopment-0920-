package service

import (
	"log"
	"testing"
	"bjyixiu.com/ryze/models"
	"github.com/pkg/errors"
)

var SID = ""

func initDB() {
	if err := InitService(); err != nil {
		log.Fatal("数据库连接错误", errors.WithStack(err))
	}
}

func TestUserLogin(t *testing.T) {
	initDB()
	name := "test"

	pwd := "a1234567"
	pwdmd5 := models.PwdMD5(pwd)

	if pwdmd5 != "5690dddfa28ae085d23518a035707282" {
		t.Fatal("密码MD5加密出错：",pwdmd5 )
	}

	// password := "5690dddfa28ae085d23518a035707282"
	ip := "123.456.789.0"

	id, info, err := UserLogin(name, pwdmd5, ip)
	if  err != nil {
		t.Fatal("登录功能错误", errors.WithStack(err))
	} else {
		t.Log("登录正常", info)
	}

	SID = id
}

func TestGetSession(t *testing.T){
	userInfo, err := GetSession(SID)
	if err != nil {
		t.Fatal("从session中获取用户信息出错", errors.WithStack(err))
	}
	t.Log("从session中获得用户信息", userInfo)
}

