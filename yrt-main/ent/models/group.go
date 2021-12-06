package models

import (
	"fmt"
	"strconv"
)

const (
// 全局组织模板列表
//KEY_GROUP_TEMPLATE = "GROUP_TEMPLATE"
// 全局组织列表
//KEY_GROUP_TO_TEMPLATE_HASH = "GROUP_TO_TEMPLATE_HASH"
)

// 组织模版
//type GroupTemp struct {
//	GroupTempInfo
//	// 组织模版具有的所有树，map为：map[<terminalKey>]<SubTree>，每个terminal只能有一种树
//	// 存储为hash中
//	//{
//	//	pc: subtree
//	//	web: subtree
//	//}
//	Trees map[string]Subtree
//}

// 组织模版属性
//type GroupTempInfo struct {
//	Key        string // 组织模版的key，全局唯一
//	Name       string // 组织模版名称
//	Desc       string // 描述
//	TreesCount int    // 组织模版所具有的树数量
//	RolesCount int    // 组织模版所拥有的角色数量
//	GroupCount int    // 使用该组织模版的组织数量
//}

// 组织模版全局索引
//type GroupTemplatIndex struct {
//	Key  string
//	Name string
//}

// 组织模版下子树
//type GroupTemplateTree struct {
//	Key   string
//	Value string
//}

// 组织模板
//func KeyGroupTemplate(template string) string {
//	return "GTEMP_" + template
//}

// 组织模版的树
//func KeyGroupTemplateTrees(template string) string {
//	return KeyGroupTemplate(template) + "_TREES"
//}

// 使用组织模版的组织列表
//func KeyGroupTemplateList(template string) string {
//	return KeyGroupTemplate(template) + "_LIST"
//}

// 组织
func KeyGroup(id int) string {
	return "G_" + strconv.Itoa(id)
}

// 组织对应的模板
type GroupTemplate struct {
	GroupID  int64 `json:"groupID"`
	Template int   `json:"template"`
}

func (GroupTemplate) TableName() string {
	return "permission_group_template"
}

type YrtGroupMap struct {
	GroupID    int64
	SystemType int
	Belong     int64
}

func (YrtGroupMap) TableName() string {
	return "permission_yrt_group_map"
}

func (yg *YrtGroupMap) GenGroupID() (int64, error) {
	id, err := YrtGroup2NewID(int64(yg.SystemType), yg.Belong)
	if err != nil {
		return 0, fmt.Errorf("生成yrt组织的新组织ID出错: %w", err)
	}
	yg.GroupID = id
	return id, nil
}
