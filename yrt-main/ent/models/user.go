package models

import (
	"fmt"
)

// 在线用户列表
// ONLINE_USERS : {
//	<user id> : <user session struct>
// }
// Ex:
// ONLINE_USERS : {
// 232 : {name: "用户A", "App":["82375hbhs", "8723ybvffr"], "Web":["djhu2342b", "288723gh1"]}
// }
func KeyOnlineUsers() string {
	return "ONLINE_USERS"
}

// 在线用户列表
//type UserSession  {
//
//	Name string
//	App  []string
//	Web  []string
//}

type YrtUserMap struct {
	UserID     int64
	SystemType int
	GodInnerSN int64
}

func (YrtUserMap) TableName() string {
	return "permission_yrt_user_map"
}

func (yu YrtUserMap) GenUserID() (int64, error) {
	id, err := YrtUser2NewID(int64(yu.SystemType), yu.GodInnerSN)
	if err != nil {
		return 0, fmt.Errorf("生成yrt用户的新ID出错: %w", err)
	}
	yu.UserID = id
	return id, nil
}

type YrtUserSetting struct {
	ID       int    `json:"id"`
	UserID   int64  `json:"user_id"`
	PageName string `json:"page_name"`
	PageType string `json:"page_type"`
	Settings string `json:"settings"`
}

func (YrtUserSetting) TableName() string {
	return "permission_yrt_user_settings"
}
