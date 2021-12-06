package service

import (
	protoRyze "bjyixiu.com/proto/ryze"
	"bjyixiu.com/ryze/models"
	"github.com/golang/protobuf/ptypes"
	"github.com/pkg/errors"
)

// 建立用户
func CreateUser(protoUser *protoRyze.User) error {
	//
	user := new(models.User)
	user.Name = protoUser.Name
	user.Pwd = models.Ecode(protoUser.Name, protoUser.Pwd)
	if err := models.Insert(user); err != nil {
		return errors.Wrap(err, "建立用户失败")
	}
	createAt, err := ptypes.TimestampProto(user.CreateAt)
	if err != nil {
		return errors.Wrap(err, "用户建立时间转换错误")
	}
	protoUser.Id = user.Id
	protoUser.CreateAt = createAt
	return nil
}

// 删除用户（逻辑删除, 设置deleted_at字段
func RemoveUserByID(id int32) error {
	if err := models.RemoveByID(id); err != nil {
		return errors.Wrap(err, "删除(逻辑)用户失败")
	}
	return nil
}

// 用户认证
//func Auth(name string, pwd string) (*models.User, string, error) {
//	// 尝试查询用户
//	has, user, err := GetUserByName(name)
//	if err != nil {
//		return nil, "", errors.Wrap(err, "按用户名获取用户错误")
//	}
//	// 没有找到用户
//	if !has || user == nil || len(user.Name) <= 0 {
//		return nil, models.MSG_STATUS_NO_USER, nil
//	}
//	// 比对密码
//	if user.Pwd == models.Ecode(user.Name, pwd) {
//		user.Pwd = ""
//		return user, models.MSG_STATUS_SUCCESS, nil
//	}
//
//	return nil, models.MSG_STATUS_PWD_WRONG, nil
//}

// 获取所有用户，没分页，谨慎使用
func GetAll() ([]models.User, error) {
	return models.GetAll()
}

func RemoveUser(user *models.User) {

}

// 删除用户（物理）
func DeleteUserByID(id int32) error {
	if err := models.DeleteByID(id); err != nil {
		return errors.Wrap(err, "删除(物理)用户失败")
	}
	return nil
}

//func GetUserById(id int32, includeRemoved ...bool) (bool, *models.User, error) {
//	return models.GetUserByID(id, includeRemoved...)
//}

//func GetUserByName(name string, includeRemoved ...bool) (bool, *models.User, error) {
//	return models.GetUserByName(name, includeRemoved...)
//}
