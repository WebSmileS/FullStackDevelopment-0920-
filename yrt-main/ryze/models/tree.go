package models

import (
	"github.com/rs/xid"
)

// 菜单树节点
type Node struct {
	ID          string
	Type        string    // node类型，leaf：菜单 cat: 文件夹
	Parent      string    // 父节点id
	Name        string    // 节点名称
	Icon        string    // 节点图标
	URI         string    // 节点前端路径，cat类型节点无
	Index       string    // 用于前端计算用的索引，格式为：0.1.1, 0.1.2, 1.0.1
	ChildrenIDs []string  // 自节点数组，持久化用
	Children    []*Node   `json:"children"` // 自节点实体，不持久化
	Features    *Features // 菜单功能，见Feature
	Desc        string    // 描述
	State       int       // node状态是否禁用， 0：禁用 1：启用
}

// 菜单功能
type Feature struct {
	ID     string
	NodeID string   // 所属菜单节点id
	Name   string   // 功能名称
	Flag   string   // 功能代号，前端用
	Desc   string   // 描述
	APIs   []string // 实现功能所用api数组
}

// 母树
type Tree struct {
	ID       string
	ParentID string
	Name     string
	Type     string
	Desc     string
	RootID   string
	Root     *Node
}

// 子树
type SubTree struct {
	ID     string
	Name   string
	Mother string
	Type   string
	Desc   string
	Tree   []*RoleTreeNode
}

// 组织模版，母树的子树
type Group struct {
	ID      string   // id
	Name    string   // 模版树名称
	Type    string   // 类型
	State   int      // 状态 0：禁用 1：启用
	Desc    string   // 描述
	Trees   []*Tree  // 菜单树，可以配置多棵树，不持久化
	TreesID []string // 菜单树id数组，持久化
}

type RoleTreeNode struct {
	ID          string
	ChildrenIDs []RoleTreeNode
	FeaturesIDs []string
}

type Features struct {
	Internal   *Feature
	IDs        []string
	Additional []*Feature
}

type Feature2Role struct {
	ID string
}

func NewID() string {
	return xid.New().String()
}

func NewNode() *Node {
	n := new(Node)
	n.ID = xid.New().String()
	return n
}

func NewFeature() *Feature {
	guid := xid.New()
	return &Feature{
		ID: guid.String(),
	}
}

// 通讯用struct
// 增加Node
type AddNode struct {
	Type     string
	Parent   string //分类的父级，只能是分类
	Name     string
	Icon     string
	URI      string
	State    int
	Features *Features
}

// 增加分类
type AddCatalog struct {
	Parent string //分类的父级，只能是分类
	Name   string
	Icon   string
}

// 增加菜单
type AddLeaf struct {
	Parent string //菜单的父级，只能是分类，菜单下不能有子节点
	Name   string
	Icon   string
	URI    string
}

// 增加Feature
// 修改Node
type UpdateNode struct {
	ID       string
	Name     string
	Icon     string
	URI      string
	State    int
	Features *Features
}

// 修改分类
type UpdateCatalog struct {
	ID   string
	Name string
	Icon string
}

// 修改菜单
type UpdateLeaf struct {
	ID   string
	Name string
	Icon string
	URI  string
}

// 修改子节点
type UpdateChildren struct {
	ID          string
	ChildrenIDs []string
}

// 调整子节点的顺序
type UpdateChildrenOrder struct {
	ID         string
	ChildrenID string
}

// 用户菜单
type UserMenu struct {
}

type UpdateGroupTree struct {
	GID  string
	Tree []*RoleTreeNode
}

// 角色树
type UpdateRoleTree struct {
	RID  int
	Tree []*RoleTreeNode
}

// 系统角色树
type UpdateSystemRoleTree struct {
	RID  string
	Tree []*RoleTreeNode
}
