package sql

import (
	"fmt"

	"bjyixiu.com/ent/models"
	"github.com/jinzhu/gorm"
)

// 获取组织对应的模板ID
// 如果组织没有对应的模板则返回 -1
// Params:
//		groupID : 组织id
// Return:
//		templateID: 模板id
// 		error: 错误返回
func (sql *SQLService) getGroupTemplateID(groupID int64) (int, error) {
	var groupTemplate models.GroupTemplate
	if err := sql.db.Take(&groupTemplate, "group_id = ?", groupID).Error; err != nil {
		if gorm.IsRecordNotFoundError(err) {
			return -1, nil
		}
		return 0, fmt.Errorf("获取组织对应模板ID: %w", err)
	}
	return groupTemplate.Template, nil
}

// 获取组织对应的模板
// Params:
//		groupID : 组织id
// Return:
//		template: 模板信息
// 		error: 错误返回
func (sql SQLService) GetGroupTemplate(groupID int64) (*models.Template, error) {
	id, err := sql.getGroupTemplateID(groupID)
	if err != nil {
		return nil, fmt.Errorf("获取组织对应模板: %w", err)
	}
	if id > 0 {
		template, err := sql.GetTemplate(id)
		if err != nil {
			return nil, fmt.Errorf("获取组织对应模板: %w", err)
		}
		return template, nil
	}
	return nil, nil
}

// 设置组织对应的模板
// Params:
//		groupID : 组织id
//		templateID: 模板id
// Return:
// 		error: 错误返回
func (sql *SQLService) SetGroupTemplate(groupID int64, templateID int) error {
	// 获取原值
	old, err := sql.getGroupTemplateID(groupID)
	if err != nil {
		return fmt.Errorf("设置组织模板: %w", err)
	}
	groupTemplate := models.GroupTemplate{
		GroupID:  groupID,
		Template: templateID,
	}
	// 有值更新
	if old > 0 {
		if err := sql.db.Model(&groupTemplate).Where("group_id = ?", groupTemplate.GroupID).Update("template", groupTemplate.Template).Error; err != nil {
			return fmt.Errorf("设置组织模板: %w", err)
		}
		//if err := sql.db.Save(&groupTemplate).Error; err != nil {
		//
		//}
		return nil
	}
	// 无值增加
	if err := sql.db.Create(&groupTemplate).Error; err != nil {
		return fmt.Errorf("设置组织模板: %w", err)
	}
	return nil
}

// 获取组织对应端权限（组织对应模板权限）
// Params:
//		groupID : 组织id
//		terminal: 端key
// Return:
// 		error: 错误返回
func (sql *SQLService) GetGroupPermissionByTerminal(groupID int64, terminal string) (*models.TemplatePermission, error) {
	// 获取组织对应的template
	templateID, err := sql.getGroupTemplateID(groupID)
	if err != nil {
		return nil, fmt.Errorf("获取组织模板: %w", err)
	}
	permission, err := sql.GetTemplatePermissionByTerminal(templateID, terminal)
	if err != nil {
		return nil, fmt.Errorf("获取组织的模板: %w", err)
	}
	return permission, nil
}

// 获取组织所有的Tree（实体）
// Params:
//		groupID : 组织id
// Return:
//		[]Tree: 树实体列表
// 		error: 错误返回
func (sql *SQLService) GetGroupTrees(groupID int) ([]models.Tree, error) {
	// 获取组织对应模板
	// 模板所有权限
	// 获取母树
	// 剪裁后
	return nil, nil
}

// 获取使用指定模板的机构数量
// Params:
//		templateID: 模板id
// Return:
//		count: 使用模板的机构数量
//		error: 错误返回
func (sql *SQLService) CountGroupByTemplate(templateID int) (int, error) {
	var count int
	if err := sql.db.Model(&models.GroupTemplate{}).Where("template = ?", templateID).Count(&count).Error; err != nil {
		return 0, fmt.Errorf("获取使用模板的组织数量出错")
	}

	return count, nil
}

// 获取组织内具有某节点权限的所有用户id
// Params:
//		nodeID: 节点id
//		group: 组织id
// Return:
//		userIDs: 具有权限的所有用户
//		error: 错误返回
func (sql *SQLService) GetUsersByNodeID(nodeID int, group int64) ([]models.UserRole, error) {
	var users []models.UserRole
	if err := sql.db.Table(models.UserRole{}.TableName()).
		Select("permission_user_role.*").
		Joins("INNER JOIN permission_role pr ON pr.belong = ? AND pr.id = permission_user_role.role", group).
		Joins("INNER JOIN permission_role_permission prp ON prp.node = ? AND pr.id = prp.role", nodeID).
		Find(&users).Error; err != nil {
		return nil, fmt.Errorf("批量获取用户对应角色出错: %w", err)
	}

	return users, nil
}

// 获取组织内具有某些节点权限的所有用户id
// Params:
//		nodeIDs: 节点id数组
//		group: 组织id
// Return:
//		userIDs: 具有权限的所有用户
//		error: 错误返回
func (sql *SQLService) GetUsersByNodeIDs(nodeIDs []int, group int64) ([]models.UserRole, error) {
	var users []models.UserRole
	if err := sql.db.Table(models.UserRole{}.TableName()).
		Select("permission_user_role.*").
		Joins("INNER JOIN permission_role pr ON pr.belong = ? AND pr.id = permission_user_role.role", group).
		Joins("INNER JOIN permission_role_permission prp ON  pr.id = prp.role AND prp.node IN (?)", nodeIDs).
		Find(&users).Error; err != nil {
		return nil, fmt.Errorf("批量获取用户对应角色出错: %w", err)
	}

	return users, nil
}
