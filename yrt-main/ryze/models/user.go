package models

import (
	"crypto/md5"
	"fmt"
	"io"
	"time"
)

// 用户对象，CreateAt和DeleteAt为符合xorm规范的建立时间和软删除字段
type User struct {
	Id        int32     `xorm:"autoincr pk 'id'"` //自增主键
	Name      string    `xorm:"index"`            //登录名
	Pwd       string    //MD5+salt
	CreateAt  time.Time `xorm:"created"` //建立时间
	DeletedAt time.Time `xorm:"deleted"` //删除时间，xorm逻辑删除
}

//type UserInfo struct {
//	Belong                     string
//	Module_function_inner_sn_s string
//	Password_cycle             string
//	System_type                string
//	User_inner_sn              string
//}

// swagger:model ReqLoginUser
type ReqLoginUser struct {
	UserName       string
	Password       string
	RegistrationID string
}

// swagger:parameters loginUser
type loginUser struct {
	// in: body
	reqLoginUser ReqLoginUser
}

type ResLoginUser struct {
	UserInfo *UserInfo
	God      *God
	SID      string
}

type ChangePassword struct {
	Old      string
	Password string
}

// 密码MD5+加盐
func Ecode(name string, origin string) string {
	m := md5.New()
	io.WriteString(m, origin)

	pwdmd5 := fmt.Sprintf("%x", m.Sum(nil))

	io.WriteString(m, salt1)
	io.WriteString(m, name)
	io.WriteString(m, salt2)
	io.WriteString(m, pwdmd5)

	return fmt.Sprintf("%x", m.Sum(nil))
}

// 密码MD5
func PwdMD5(origin string) string {
	m := md5.New()
	io.WriteString(m, origin)

	return fmt.Sprintf("%x", m.Sum(nil))
}

// 验证用户密码
// func (u *User) Auth(pwd string) bool {
// 	user := new(User)
// 	return user, nil
// }

// 根据id返回user，includeRemoved标识是否查询已经逻辑删除的用户，如未找到返回 （false, nil， nil),
func GetUserByID(id int32, includeRemoved ...bool) (bool, *User, error) {
	// 默认不查询删除的用户
	unscoped := false
	if len(includeRemoved) > 0 {
		unscoped = includeRemoved[0]
	}
	user := new(User)

	has := false
	var err error
	if unscoped {
		has, err = engine.Id(id).Unscoped().Get(user)
	} else {
		has, err = engine.Id(id).Get(user)
	}

	if err != nil {
		return false, nil, err
	}
	return has, user, nil
}

// 根据登录名返回user，includeRemoved标识是否查询已经逻辑删除的用户，如未找到返回 （false, nil， nil)
func GetUserByName(name string, includeRemoved ...bool) (bool, *User, error) {
	// 默认不查询删除的用户
	unscoped := false
	if len(includeRemoved) > 0 {
		unscoped = includeRemoved[0]
	}
	user := new(User)

	has := false
	var err error
	if unscoped {
		has, err = engine.Where("name=?", name).Unscoped().Get(user)
	} else {
		has, err = engine.Where("name=?", name).Get(user)
	}

	if err != nil {
		return false, nil, err
	}
	return has, user, nil
}

// 增加用户，成功后填充，返回用户对象，对象包括填充的ID和CreateAt
func Insert(user *User) error {
	_, err := engine.Insert(user)
	return err
}

// 删除用户（逻辑删除）
func RemoveByID(id int32) error {
	user := new(User)
	user.Id = id
	_, err := engine.Id(id).Delete(user)
	return err
}

// 删除用户（逻辑删除）
func Remove(user *User) error {
	_, err := engine.Delete(user)
	return err
}

// 删除用户（物理删除）
func DeleteByID(id int32) error {
	user := new(User)
	user.Id = id
	_, err := engine.Id(id).Unscoped().Delete(user)
	return err
}

// 删除用户（物理删除）
func Delete(user *User) error {
	_, err := engine.Unscoped().Delete(user)
	return err
}

// 检查用户名是否存在
func Exist(user *User) bool {
	return false
}

// 获取所有用户
func GetAll() ([]User, error) {
	users := make([]User, 0)
	err := engine.Find(&users)
	return users, err
}
