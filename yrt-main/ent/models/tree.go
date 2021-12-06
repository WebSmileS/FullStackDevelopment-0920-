package models

import "strconv"

// 母树Key, EX:MT_hospital_web
func KeyMotherTree(terminal string, id int) string {
	return "MT_" + terminal + "_" + strconv.Itoa(id)
}

// 模板树列表 hash
// MT_hospital_web_GTLIST : {
// 	key : 描述
// 	sj : 通用三甲医院
// 	wsy : 小型卫生院
// }
func KeyGroupTreeList(motherTreeKey string) string {
	return motherTreeKey + "_GTLIST"
}

// 母树
//type Tree struct {
//	TreeInfo
//	RootID string // 不持久化，计算所得
//	Root   *Node
//}

// 母树
type Tree struct {
	ID           int    `gorm:"PRIMARY_KEY" json:"id"`
	Name         string `json:"name"`
	Terminal     string `json:"terminal"`
	TerminalName string `gorm:"-" json:"terminalName"` // 端名称，方便前端显示用
	Note         string `json:"note"`
	Root         int    `json:"root"`
	RootNode     *Node  `gorm:"-" json:"rootNode"` // 不在mysql存储
}

func (tree Tree) TableName() string {
	return "permission_tree"
}

//子树
type Subtree struct {
	Mother       string   // 母树ID
	Terminal     string   // 端key，不持久化
	TerminalName string   // 端名称, 不持久化
	Root         *Node    // 树实体，不持久化
	Tree         []string // 子树索引，持久化
}

// 树key与ID映射
type TreeKeyIDMap struct {
	Terminal string
	Tree     string
	ID       int
}

func (TreeKeyIDMap) TableName() string {
	return "permission_tree_map"
}

// 母树基本信息，不包含树
//type TreeInfo struct {
//	// Required: true
//	Key      string
//	ParentID string
//	// Required: true
//	Name string
//	// Required: true
//	// terminal key, 见 terminal.go
//	Terminal string
//	Desc     string
//}
