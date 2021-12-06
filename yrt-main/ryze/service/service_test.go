package service

import (
	"log"
	"testing"

	protoRyze "bjyixiu.com/proto/ryze"
	"bjyixiu.com/ryze/models"
	"github.com/pkg/errors"
)

func TTest_CreateAndRemove(t *testing.T) {
	// 建立用户
	protoUser, err := TCreateUser()
	if err != nil {
		t.Fatal("建立用户失败：", err)
	}

	if protoUser.Id <= 0 {
		t.Fatal("建立用户失败，返回ID为：", protoUser.Id)
	}
	t.Log("建立用户成功：", protoUser.Id)

	// 逻辑删除
	if err = RemoveUserByID(protoUser.Id); err != nil {
		t.Fatal("删除(逻辑)用户错误：", errors.Cause(err))
	}
	// 查询逻辑删除
	if has, _, err := GetUserById(protoUser.Id); err != nil {
		t.Fatal("查询删除用户错误：", err)
	} else if has {
		t.Fatal("查询到用户没被删除：", protoUser.Id)
	}
	t.Log("删除(逻辑)用户成功：", protoUser.Id)

	// 物理删除
	if err = DeleteUserByID(protoUser.Id); err != nil {
		t.Fatal("删除(物理)用户错误：", err)
	}

	// 查询物理删除
	if has, _, err := GetUserById(protoUser.Id, true); err != nil {
		t.Fatal("查询物理删除用户错误：", err)
	} else if has {
		t.Fatal("删除(物理)用户失败:", protoUser.Id)
	}
	t.Log("删除(物理)用户成功：", protoUser.Id)
}

func TTest_AllUser(t *testing.T) {
	//  读取所有用户
	users, err := GetAll()
	if err != nil {
		t.Fatal("获取所有用户错误：", err)
	}
	t.Log("获取所有用户成功：", len(users))
}

func TCreateUser() (*protoRyze.User, error) {
	var protoUser protoRyze.User
	protoUser.Name = "testName"
	protoUser.Pwd = "testPwd"
	err := CreateUser(&protoUser)
	return &protoUser, err
}

// 测试验证用户(登录)
func TTest_Auth(t *testing.T) {
	// 建立用户
	name := "JustForTest"
	pwd := "a1234567"
	protoUser := protoRyze.User{
		Name: name,
		Pwd:  pwd,
	}
	err := CreateUser(&protoUser)
	if err != nil {
		t.Fatal("新建用户失败：", err)
	}

	// 验证用户，检查返回用户
	user, status, err := Auth(name, pwd)
	if err != nil {
		t.Fatal("用户认证错误：", err)
	}

	if status == models.MSG_STATUS_NO_USER {
		t.Fatal("未找到用户")
	} else if status == models.MSG_STATUS_PWD_WRONG {
		t.Fatal("用户验证失败")
	}

	if user.Id <= 0 {
		t.Fatal("用户认证后未返回")
	}

	// 删除测试数据
	defer func() {
		if err = DeleteUserByID(user.Id); err != nil {
			t.Fatal("删除(物理)用户错误：", err)
		}
	}()

	t.Log("用户验证成功：", user.Id, user.Name)
}

func Benchmark_CreateUser(b *testing.B) {
	b.StopTimer()

	b.StartTimer()
	for i := 0; i < b.N; i++ {
		protoUser, err := TCreateUser()
		if err != nil {
			log.Println("增加用户错误:", err)
		} else {
			log.Println("增加用户成功:", protoUser)
		}
	}
}
