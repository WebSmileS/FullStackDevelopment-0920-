package models

import "strconv"

// 机构类型
const (
	TYPE_PLATFORM = iota
	TYPE_HOSPITAL
	TYPE_DEALER
	TYPE_FACTORY
)

// 用户密码加盐
const (
	salt1 string = "@#S$"
	salt2 string = ")&*BF"
)

// 母树标识
const (
	KEY_PLATFORM_TREE = "platform_tree"
	KEY_DEALER_TREE   = "dealer_tree"
	KEY_HOSPITAL_TREE = "hospital_tree"
	KEY_FACTORY_TREE  = "facotry_tree"
)

// 信息
const (
	MSG_STATUS_SUCCESS   string = "成功"
	MSG_STATUS_NO_USER   string = "没有找到用户"
	MSG_STATUS_PWD_WRONG string = "密码错误"
)

// 树类型
const (
	TYPE_TERMINAL_WEB = iota
	TYPE_TERMINAL_MOBILE
	TYPE_TERMINAL_WEIXIN
	TYPE_TERMINAL_PAD
)

// 用户类型
const (
	USER_TYPE_NORMAL = 0
	USER_TYPE_ADMIN  = 1
)

// 节点类型
const (
	TYPE_NODE_LEAF = "leaf"
	TYPE_NODE_CAT  = "cat"
)

// 节点状态
const (
	STATE_NODE_DISABLE = 0
	STATE_NODE_ENABLE  = 1
)

// 组织模板树列表
const (
	KEY_MOTHER_TREE_LIST string = "mother_tree_list"
	KEY_GROUP_TREE_LIST  string = "group_tree_list"
	KEY_ROLE_TREE_LIST   string = "role_tree_list"
)

// 终端列表
/*
{
	1: "Web端"
	2: "APP端"
	3: "微信小程序端"
}*/
const (
	KEY_TERMINAL_HASH string = "terminal"
)

// 母树
func KeyMotherTree(terminal string) string {
	return "MT" + " " + terminal
}

// 组织子树(模板)
func KeyGroupTree(groupTemplate int) string {
	return "GT" + strconv.Itoa(groupTemplate)
}

// 角色子树
func KeyRoleTree(rid int) string {
	return "RT" + strconv.Itoa(rid)
}

// 除平台端其他端管理员（固定）角色树
func KeyManagerTree(systemType int) string {
	return "RT_Manager_" + strconv.Itoa(systemType)
}

// 节点对应Feature
func KeyNodeFeatures(nid string) string {
	return nid + "_FS"
}

// Internal Feature
func KeyInternalFeatures(nid string) string {
	return nid + "_IF"
}

// Feature 对应 Role
func KeyFeature2Role(fid string) string {
	return "F2R" + fid
}

// API 对应 Features -> zset
func KeyAPI2Features(aid string) string {
	return "A2FS" + aid
}

// Role 对应 Features -> zset
func KeyRole2Features(rid string) string {
	return "R2FS" + rid
}

// 短信验证
func KeySMSCode(mobile string) string {
	return "SMSCODE" + mobile
}
