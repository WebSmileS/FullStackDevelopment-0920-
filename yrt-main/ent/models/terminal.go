package models

// 终端对应的类型, hash
// terminal: {
//  key : 名称
//  -----------
// 	web : 网页端
// 	app : 手机App
// 	weixin: 微信小程序
// 	pad: 平板电脑
//  yrtweb: 医润通网页
//  yxkweb: 医修库网页
//  yrtapp: 医润通app
// }
const (
	KEY_TERMINAL = "TERMINAL"
)

// 端key和name
// swagger:model terminal
type Terminal struct {
	// 端key，如：web
	Key string `gorm:"PRIMARY_KEY;Column:id" json:"key"`
	// 端名称，如：网页
	Name string `gorm:"NOT NULL" json:"name"`
}

func (Terminal) TableName() string {
	return "permission_terminal"
}

// terminal--motherTree
// swagger:model terminalTree
type TerminalTree struct {
	Terminal
	Trees []*Tree `json:"trees"`
}

// 终端母树列表, hash
// T_web_MTLIST : {
// 	key : 名称
// 	hospital : 医院
// 	dealer : 经销商
// }
func KeyTerminalTree(terminal string) string {
	return "T_" + terminal + "_MTLIST"
}
