package models

import "strconv"

// type Role struct {
// 	RoleInfo
// 	Trees []Subtree
// }

// type RoleInfo struct {
// 	Name       string
// 	Desc       string
// 	TreesCount int
// 	Status     int
// 	Group      string
// }

const (
	ROLE_TYPE_TEMPLATE = 0
	ROLE_TYPE_GROUP    = 1
)

type Role struct {
	ID       int    `json:"id"`       // 角色ID
	Name     string `json:"name"`     // 角色名称
	Type     uint8  `json:"type"`     // 角色类型: 0: template; 1: group
	Belong   int64  `json:"belong"`   // 角色所属对象的id
	Internal bool   `json:"internal"` // 是否内置角色
}

func (Role) TableName() string {
	return "permission_role"
}

type GroupRole struct {
	ID    int    `json:"id"`    // 不持久化
	Name  string `json:"name"`  // 角色名称
	Group int    `json:"group"` // 组织key
}

func (GroupRole) TableName() string {
	return "permission_group_role"
}

type TemplateRole struct {
	ID       int    `json:"id"`       // 不持久化
	Template int    `json:"template"` // 模板
	Name     string `json:"name"`     // 角色名称
	Internal bool   `json:"internal"` // 内置角色，一般不能删除和修改
}

func (TemplateRole) TableName() string {
	return "permission_template_role"
}

type RoleTree struct {
	Role     int    `json:"role"`     // 角色id
	Terminal string `json:"terminal"` // 对应的端Key
	Tree     int    `json:"tree"`     // 端对应端树id
}

func (RoleTree) TableName() string {
	return "permission_role_tree"
}

type UserRole struct {
	User int64 `gorm:"unique" json:"user"` // 用户id
	Role int   `json:"role"`               // 角色id
}

func (UserRole) TableName() string {
	return "permission_user_role"
}

type User2Role struct {
	UserID int64 `json:"userID"`
	Role   `json:"role"`
}

// 角色权限key
// R_<role id>
// Ex:
// R_32
func keyRole(id int) string {
	return "R_" + strconv.Itoa(id)
}

//角色权限，权限节点以平铺方式存储
// 以Hash形式存储：
//  <role key>_PER{
//		<terminal key> : <role permission struct>
//  }
// Ex:
// R_32_PER {
//		"ptmweb" : {"RoleID" : <role id>,"Mother" : <mother tree id>,"Terminal" : <terminal key>}
// }
// mysql 角色权限表
type RolePermission struct {
	Role     int    `json:"role"`              // 角色ID
	Tree     int    `json:"tree"`              // 对应的母树
	Terminal string `gorm:"-" json:"terminal"` // 对应的端，mysql不存储
	Node     int    `json:"node"`              // 节点id
	Nodes    []int  `gorm:"-" json:"nodes"`    // 节点IDs列表，mysql不存储
}

func (RolePermission) TableName() string {
	return "permission_role_permission"
}

type TemplateRoleKeyIDMap struct {
	KVKey string // 原kv系统role key
	ID    int    // 新sql系统role id
}

func (TemplateRoleKeyIDMap) TableName() string {
	return "permission_template_role_map"
}

func KeyRolePermission(id int) string {
	return keyRole(id) + "_PER"
}

type GroupRoleKeyIDMap struct {
	KVKey string // 原kv系统role key
	ID    int    // 新sql系统role id
}

func (GroupRoleKeyIDMap) TableName() string {
	return "permission_group_role_map"
}

type UserNRole struct {
	User int64  `json:"user"` //用户id
	ID   int64  `json:"id"`   //角色id
	Name string `json:"name"` //角色名称
}

//func KeyRoleTrees(role string) string {
//	return role + "_TREES"
//}

// 生产角色ID
//func GentRoleID() string {
//	//return "R_" + NewID()
//	return ""
//}

func KeyGroupRoles(group int) string {
	return KeyGroup(group) + "_ROLES"
}

// 组织角色树
//func KeyGroupRoleTrees(groupRole string) string {
//	return groupRole + "_TREES"
//}

// 组织模版角色列表,未包含系统固定角色，如：系统管理员
//func KeyGroupTemplateRoles(id int) string {
//	return KeyGroupTemplate(template) + "_ROLES"
//}

// 组织模版系统管理员角色
//func KeyGroupTemplateSpecialRoles(template string) string {
//	return KeyGroupTemplate(template) + "_R_SP"
//}

// 组织模版角色ID
//func GenGroupTemplateRoleID() string {
//	return GentRoleID()
//}

// 组织模版角色树
//func KeyGroupTemplateRoleTrees(templateRole string) string {
//	return templateRole + "_TREES"
//}

// 用户-角色关联键值
// UR_<user id> : <role id>
// Ex:
// UR_872 : 997
func KeyUserRole(id int64) string {
	return "UR_" + strconv.FormatInt(id, 10)
}

// 角色-用户关联列表
// RU_<role id> {
//		<user id> : <update time>
// }
// Ex:
//	RU_3 {
//		312 : 2020-09-17
//		337 : 2023-05-07
// }
func KeyRoleUser(roleID int) string {
	return "RU_" + strconv.Itoa(roleID)
}

// 节点-组织-角色关联列表
// NR_<node id>_<group id> {
//		<role id> : null
// }
// Ex:
// NR_33_861 {
//		238 : "['123','332','316']"
// }
func KeyNodeRole(nodeID, groupID int) string {
	return "NR_" + strconv.Itoa(nodeID) + "_" + strconv.Itoa(groupID)
}

// 节点-组织-角色关联列表
// NR_<node id>_<group id> {
//		<role id> : null
// }
// Ex:
// NR_33_861 {
//		238 : null
// }
//func KeyNodeRole(node, group string) string {
//	return "NR_" + node + "_" + group
//}

// 角色-用户关联列表
// RU_<role id> {
//		<user id> : <update time>
// }
// Ex:
//	RU_3 {
//		312 : 2020-09-17
//		337 : 2023-05-07
// }
//func KeyRoleUser(roleID string) string {
//	return "RU_" + roleID
//}
