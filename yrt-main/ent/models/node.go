package models

import (
	"strconv"
	"time"
)

const (
	TYPE_NODE_ROOT   = "r" // 根节点
	TYPE_NODE_LEAF   = "l" // 叶子节点
	TYPE_NODE_CAT    = "c" // 目录节点（暂弃使用）
	TYPE_NODE_NORMAL = "n" // 普通节点 (暂未使用)
)

const (
	STATE_NODE_DISABLE = 0
	STATE_NODE_ENABLE  = 1
)

const (
	NODE_TYPE_ROOT    uint8 = 0
	NODE_TYPE_MENU    uint8 = 1
	NODE_TYPE_FEATURE uint8 = 2
)

const (
	NODE_STATE_RELEASE uint8 = 1
	NODE_STATE_DEV     uint8 = 2
	NODE_STATE_DISABLE uint8 = 3
)

// 菜单树节点
//type Node struct {
//	NodeInfo
//	Index       string        // 用于前端计算用的索引，格式为：0.1.1, 0.1.2, 1.0.1
//	ChildrenIDs []string      // 子节点数组，持久化用
//	Children    []*Node       `json:"children"` // 子节点实体，不持久化
//	FeatureInfo []FeatureInfo // 功能信息
//	Features    []Feature     // 功能列表
//}

// 菜单节点索引，用于子树持久化
//type NodeIndex struct {
//	ID       string
//	Children []NodeIndex `json:"children"`
//	Features []FeatureInfo
//}

// 权限节点
//type NodeIDs struct {
//	ID    string   // 节点ID
//	FKeys []string // 节点下feature flag列表
//}

// Node 基本信息，不包括Children实体和计算内容
//type NodeInfo struct {
//	ID     string `gorm:"PRIMARY_KEY" json:"id"`
//	Type   string // node类型，l：菜单 c: 文件夹 r: Root节点
//	Parent string // 父节点id
//	Name   string // 节点名称
//	Icon   string // 节点图标
//	URI    string // 节点前端路径，cat类型节点无
//	Desc   string // 描述
//	State  int    // node状态是否禁用， 0：禁用 1：启用
//}

type Node struct {
	ID          int       `gorm:"PRIMARY_KEY" json:"id"` // 节点ID，自增ID
	Path        string    `gorm:"SIZE:1000" json:"path"` // 节点路径
	Level       uint8     `json:"level"`                 // 节点深度
	Type        uint8     `gorm:"" json:"type"`          // 节点种类， 0: Root 1: Menu 2: Feature
	ParentID    int       `gorm:"" json:"parentID"`      // 父节点ID
	Name        string    `json:"name"`                  // 名称
	Flag        string    `json:"flag"`                  // 标识
	Runes       string    `json:"runes"`                 // 对应原来的Icon
	Content     string    `json:"content"`               // 对应原来的URI
	Note        string    `json:"note"`                  // 备注，说明
	Sort        int       `json:"sort"`                  // 顺序，序号用于排序
	State       uint8     `json:"state"`                 // 节点状态 1: 正常 2: 开发中
	Free        bool      `json:"free"`                  // 是否免费节点
	ChildrenIDs []int     `gorm:"-" json:"childrenIDs"`  // 子节点列表，KV数据库中需要
	Children    []*Node   `gorm:"-" json:"children"`     // 子节点
	APIs        []string  `gorm:"-" json:"apis"`         // API数组
	Updated     time.Time `json:"updated"`               // 节点更新时间, 没有使用gorm默认的更新时间，因为要自己完全控制
}

func (node Node) TableName() string {
	return "permission_node"
}

// 调整子节点的顺序
type UpdateChildrenOrder struct {
	ID         int
	ChildrenID int
}

//func NewID() string {
//
//	return xid.New().String()
//}

//func NewNode() *Node {
//	n := new(Node)
//	n.ID = xid.New().String()
//	return n
//}

func NewRoot(id int) Node {
	strID := strconv.Itoa(id)
	return Node{
		ID:       id,
		Path:     "/" + strID,
		Level:    0,
		Type:     NODE_TYPE_ROOT,
		ParentID: 0,
		Name:     "Root",
		Flag:     "",
		Runes:    "",
		Content:  "/",
		Note:     "根节点",
		Sort:     0,
		Free:     true,
		State:    NODE_STATE_RELEASE,
		Updated:  time.Now(),
	}
}

func NewNode(id int) Node {
	//strID := strconv.Itoa(id)
	//strParentID := strconv.Itoa(parentID)
	return Node{
		ID:       id,
		Path:     "",
		Level:    0,
		Type:     NODE_TYPE_MENU,
		ParentID: 0,
		Name:     "",
		Flag:     "",
		Runes:    "",
		Content:  "",
		Note:     "",
		Sort:     0,
		State:    NODE_STATE_DEV,
		Free:     true,
		Children: []*Node{},
		Updated:  time.Now(),
	}
}

func KeyNode(id int) string {
	return "N_" + strconv.Itoa(id)
}

// EX: yrthospital_ROOT
// func KeyMotherTreeRootNode(motherKey string) string {
// 	return motherKey + "_ROOT"
// }

type NodeKeyIDMap struct {
	KVKey string `gorm:"PRIMARY_KEY"`
	ID    int
}

func (nodeMap NodeKeyIDMap) TableName() string {
	return "permission_node_map"
}
