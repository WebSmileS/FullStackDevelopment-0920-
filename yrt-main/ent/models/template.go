package models

import (
	"strconv"
	"time"
)

const (
	// 全局组织模板列表
	KEY_TEMPLATES = "TEMPLATES"
	// 全局组织列表
)

// 模板信息
// TMPL_<template id>{
//		ID: <id>
//		Name: <name>
//		Note: <note>
// }
// EX:
//	TMPL_1{
//		ID : 1
//		Name : 医润通经销商
//		Note : 医润通单独经销商端
// }
//
// mysql 模板表(信息)
type Template struct {
	ID   int    `gorm:"primaryKey;autoIncrement" json:"id"` // 模版ID
	Name string `json:"name"`                               // 模版名称
	Note string `json:"note"`                               // 模版描述
}

func (Template) TableName() string {
	return "permission_template"
}

// 模板-树关联表
// mysql
type TemplateTree struct {
	Template int    `json:"template"` // 模板id
	Terminal string `json:"terminal"` // 对应的端key
	Tree     int    `json:"tree"`     // 端对应的树id
}

func (TemplateTree) TableName() string {
	return "permission_template_tree"
}

// 模板key
func KeyTemplate(id int) string {
	return "TMPL_" + strconv.Itoa(id)
}

// 模版角色列表
// TMPL_<template id>_ROLES{
//		<roleID> : <role information>
// }
// Ex:
// TMPL_1_ROLES{
//		32 : "{"id":"32","Name":"starter","Internal":true}"
//		12 : "{"id":"12","Name":"设备科主任","Internal":false}"
//	}
type TemplateRoles struct {
	RID  int          // 角色ID
	Role TemplateRole // 角色
}

func KeyTemplateRoles(id int) string {
	return KeyTemplate(id) + "_ROLES"
}

// 应用该模板的组织列表
// TMPL_<template id>_GROUPS{
//		<group id> : <update time>
//	}
// Ex:
// TMPL_1_GROUPS{
//		121 : 2020-09-09
//	}
type TemplateGroups struct {
	GID  int // 组ID
	Date time.Time
}

func KeyTemplateGroups(id int) string {
	return KeyTemplate(id) + "_GROUPS"
}

// 组织模版子树（组织模版权限）,与RolePermission相同
// TMPL_<template id>_PER {
//		<terminal id> : <template permission struct>
// }
// Ex:
// TMPL_1_PER {
//		"yrtweb" : "{Mohter:1,Template:1,Terminal:yrtweb,Nodes:[1,3,55..]"
//}
// mysql 模板权限表
type TemplatePermission struct {
	Template int    `json:"template"`          // 模板key
	Tree     int    `json:"tree"`              // 对应的母树
	Terminal string `gorm:"-" json:"terminal"` // 对应的端, mysql不存储
	Node     int    `json:"node"`              // 节点id
	Nodes    []int  `gorm:"-" json:"nodes"`    // 节点ID列表, mysql不存储
}

func (TemplatePermission) TableName() string {
	return "permission_template_permission"
}

func KeyTemplatePermission(id int) string {
	return KeyTemplate(id) + "_PER"
}

type TemplateKeyIDMap struct {
	KVKey string `gorm:"PRIMARY_KEY"`
	ID    int
}

func (templateMap TemplateKeyIDMap) TableName() string {
	return "permission_template_map"
}
