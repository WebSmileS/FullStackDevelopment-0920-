package sql

import (
	"fmt"

	"github.com/pkg/errors"

	"github.com/jinzhu/gorm"

	"bjyixiu.com/ent/models"
)

// 获取所有模板
// Params：
//		none
// Return:
//		[]template: 所有模板列表
//		error: 错误返回
func (sql *SQLService) GetAllTemplates() ([]models.Template, error) {
	var templates []models.Template
	if err := sql.db.Find(&templates).Error; err != nil {
		return nil, fmt.Errorf("获取所有模板出错: %w", err)
	}
	return templates, nil
}

// 根据ID获取模板
// Params:
//		id: 模板id
// Return:
//		template: 模板
//		error: 错误返回
func (sql *SQLService) GetTemplate(id int) (*models.Template, error) {
	var template models.Template
	if err := sql.db.Take(&template, "id = ?", id).Error; err != nil {
		return nil, fmt.Errorf("获取指定模板出错: %w", err)
	}

	return &template, nil
}

// 新建模板
// Params:
//		template: 模板信息
// Return:
//		id: 模板id
//		error: 错误返回
func (sql *SQLService) CreateTemplate(template *models.Template) (int, error) {
	if err := sql.db.Create(template).Error; err != nil {
		return 0, fmt.Errorf("新建模板出错: %w", err)
	}
	return template.ID, nil
}

// 修改模板
// Params:
//		template: 模板信息
// Return:
//		error: 错误返回
func (sql *SQLService) UpdateTemplate(template *models.Template) error {
	if err := sql.db.Model(template).Select("name", "note").Updates(template).Error; err != nil {
		return fmt.Errorf("更新模板出错: %w", err)
	}
	return nil
}

// 删除模板
// Params:
//		id: 模板id
// Return:
//		error: 错误返回
func (sql *SQLService) DelTemplate(id int) (err error) {
	// 检查模板是否还有对应group使用
	count, err := sql.CountGroupByTemplate(id)
	if err != nil {
		return fmt.Errorf("删除模板:%w", err)
	}
	if count > 0 {
		return errors.New("还有组织使用该模板，请先解除关联")
	}
	// 删除模板角色，非事务性的：
	roles, err := sql.GetAllTemplateRoles(id)
	if err != nil {
		return fmt.Errorf("查找模板所有角色出错:%w", err)
	}
	for _, role := range roles {
		if err := sql.DelRole(role.ID, true); err != nil {
			return fmt.Errorf("删除模板角色出错: %w", err)
		}
	}

	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("删除模板出错: %w", err)
		}
		r := recover()
		if r != nil {
			tx.Rollback()
			err = fmt.Errorf("删除模板出错: %w", r)
		}
	}()
	// 删除模板信息
	if err = tx.Delete(&models.Template{}, "id = ?", id).Error; err != nil {
		return
	}

	// 删除模板树对应
	if err = tx.Delete(&models.TemplateTree{}, "template = ?", id).Error; err != nil {
		return
	}

	// 删除模板权限
	if err = tx.Delete(&models.TemplatePermission{}, "template = ?", id).Error; err != nil {
		return
	}
	// 提交事务
	err = tx.Commit().Error

	return nil
}

// 设置模板的权限(按树设置)
// Params:
//		templatePermission: 模板权限
// Return:
//		error: 错误返回
func (sql *SQLService) SetTemplatePermission(permission *models.TemplatePermission) error {
	// 获取原值权限
	old, err := sql.GetTemplatePermissionByTerminal(permission.Template, permission.Terminal)
	if err != nil {
		return fmt.Errorf("设置模板权限,检查原值: %w", err)
	}
	// 无原值，新建
	if old == nil {
		if err := sql.createTemplatePermission(permission); err != nil {
			return fmt.Errorf("设置模板权限, 增加权限树: %w", err)
		}
		return nil
	}
	// 有原值，是否同一树
	// 是，增改
	if old.Tree == permission.Tree {
		if err := sql.updateTemplatePermission(permission, old); err != nil {
			return fmt.Errorf("设置模板权限, 更新权限: %w", err)
		}
		return nil
	}
	// 否，删除原值，再增加
	if err := sql.changeTemplateTree(permission, old); err != nil {
		return fmt.Errorf("设置模板权限, 更换权限树: %w", err)
	}
	return nil
}

// 新设置模板权限
// Params:
//		permission: 旧权限
// Return:
//		error: 错误返回
func (sql SQLService) createTemplatePermission(permission *models.TemplatePermission) (err error) {
	// 进入事务
	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("新建模板权限出错: %w", err)
		}
		r := recover()
		if r != nil {
			tx.Rollback()
			err = fmt.Errorf("新建板权限出错: %w", r)
		}
	}()

	if err = tx.Error; err != nil {
		return
	}
	// 增加模板-树关联
	templateTree := &models.TemplateTree{
		Template: permission.Template,
		Terminal: permission.Terminal,
		Tree:     permission.Tree,
	}
	if err = tx.Create(&templateTree).Error; err != nil {
		return
	}
	// 添加模板-节点关联
	for _, id := range permission.Nodes {
		addNode := models.TemplatePermission{
			Template: permission.Template,
			Tree:     permission.Tree,
			Node:     id,
		}
		if err = tx.Create(&addNode).Error; err != nil {
			return
		}
	}
	// 提交事务
	err = tx.Commit().Error
	return
}

// 模板树不变的情况下节点更新
// Params:
//		oldPermission: 旧权限
//		newPermission: 新权限
// Return:
//		error: 错误返回
func (sql *SQLService) updateTemplatePermission(new, old *models.TemplatePermission) (err error) {
	// 与原权限对比
	add, abandoned := diff(new.Nodes, old.Nodes)
	// 更新
	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("更新模板权限出错: %w", err)
		}
		r := recover()
		if r != nil {
			tx.Rollback()
			err = fmt.Errorf("更新模板权限出错: %w", r)
		}
	}()
	//defer func() {
	//	if err != nil || recover() != nil {
	//		tx.Rollback()
	//		err = fmt.Errorf("保存模板权限出错: %w", err)
	//	}
	//}()
	if err = tx.Error; err != nil {
		return
	}
	// 删除的
	for _, id := range abandoned {
		if err = tx.Where("template = ? AND node = ?", new.Template, id).Delete(&new).Error; err != nil {
			return
		}
	}

	// 增加的
	for _, id := range add {
		addNode := models.TemplatePermission{
			Template: new.Template,
			Tree:     new.Tree,
			Node:     id,
		}
		if err = tx.Create(&addNode).Error; err != nil {
			return
		}
	}

	err = tx.Commit().Error
	return
}

// 更换模板权限树
// Params:
//		oldPermission: 旧权限
//		newPermission: 新权限
// Return:
//		error: 错误返回
func (sql *SQLService) changeTemplateTree(new, old *models.TemplatePermission) (err error) {
	// 进入事务
	tx := sql.db.Begin()
	if err = tx.Error; err != nil {
		return
	}
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("更新模板树出错:%w", err)
		}
		r := recover()
		if r != nil {
			tx.Rollback()
			err = fmt.Errorf("更新模板树出错:%w", r)
		}
	}()
	// 修改模板-树关联
	templateTree := &models.TemplateTree{
		Template: old.Template,
		Terminal: old.Terminal,
		Tree:     new.Tree,
	}
	if err = tx.Model(templateTree).
		Where("template = ? AND terminal = ?", templateTree.Template, templateTree.Terminal).
		Update("tree", templateTree.Tree).Error; err != nil {
		return
	}
	// 删除所有的旧节点关联
	if err = tx.Delete(&models.TemplatePermission{}, "template = ? AND tree = ?", old.Template, old.Tree).Error; err != nil {
		return
	}
	// 增加新的节点关联
	for _, id := range new.Nodes {
		addNode := models.TemplatePermission{
			Template: new.Template,
			Tree:     new.Tree,
			Node:     id,
		}
		if err = tx.Create(&addNode).Error; err != nil {
			return
		}
	}
	// 提交
	err = tx.Commit().Error
	return
}

// 获取模板的权限(按树获取)
// Params:
//		templateID: 模板id
//		treeID: 树id
// Return:
//		templatePermission: 模板权限
//		error: 错误返回
func (sql *SQLService) GetTemplatePermission(templateID, treeID int) (*models.TemplatePermission, error) {
	var ids []int
	rows, err := sql.db.Table(models.TemplatePermission{}.TableName()).
		Select("node").
		Where("template = ? AND tree = ?", templateID, treeID).
		Rows()
	if err != nil {
		return nil, fmt.Errorf("获取模板权限出错：%w", err)
	}

	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			return nil, fmt.Errorf("获取模板权限，读取权限出错: %w", err)
		}
		ids = append(ids, id)
	}
	permission := &models.TemplatePermission{
		Template: templateID,
		Tree:     treeID,
		Terminal: "",
		Nodes:    ids,
	}
	return permission, nil
}

// 获取模板的权限(按端获取)
// 无记录返回nil
// Params:
//		terminalKey: 端key
// Return:
//		templatePermission: 模板权限
//		error: 错误返回
func (sql *SQLService) GetTemplatePermissionByTerminal(templateID int, terminalKey string) (*models.TemplatePermission, error) {
	// 获取端对应的树
	treeID, err := sql.GetTemplateTerminalTreeID(templateID, terminalKey)
	if err != nil {
		return nil, fmt.Errorf("获取模板权限出错:%w", err)
	}
	// 无记录
	if treeID == -1 {
		return nil, nil
	}
	permission, err := sql.GetTemplatePermission(templateID, treeID)
	if err != nil {
		return nil, fmt.Errorf("获取模板权限: %w", err)
	}
	// 获取权限
	permission.Terminal = terminalKey
	return permission, nil
}

// 获取模板指定端对应的树ID
// 无记录则返回id值为-1
// Params:
//		templateID: 模板id
//		terminalKey: 端key
// Return:
//		treeID: 树id
//		error: 错误返回
func (sql *SQLService) GetTemplateTerminalTreeID(templateID int, terminalKey string) (int, error) {
	var templateTree models.TemplateTree
	if err := sql.db.Take(&templateTree, "template = ? AND terminal = ?", templateID, terminalKey).Error; err != nil {
		if gorm.IsRecordNotFoundError(err) {
			return -1, nil
		}
		return 0, fmt.Errorf("获取模板指定端树id出错: %w", err)
	}
	return templateTree.Tree, nil
}

// 设定模板指定端的树
// Params:
//		templateTree: 模板-端-树关联struct
// Return:
//		error: 错误返回
func (sql *SQLService) SetTemplateTerminalTree(templateTree *models.TemplateTree) error {
	// 查找是否有原值：每terminal只能有一棵树
	treeID, err := sql.GetTemplateTerminalTreeID(templateTree.Template, templateTree.Terminal)
	if err != nil {
		return fmt.Errorf("设置模板树关联: %w", err)
	}
	// 存在并且与现值相同，不做处理
	if treeID == templateTree.Tree {
		return nil
	}
	// 不存在或存在并且与现值不同，更新
	if err := sql.db.Save(templateTree).Error; err != nil {
		return fmt.Errorf("设置模板树关联保存失败：%w", err)
	}

	return nil
}

// 建立旧templateKey到新templateID到映射
// Params:
//		key: 旧templateKey
//		id: 新templateID
// Return:
//		error: 错误返回
func (sql *SQLService) InsertTemplateKeyID(key string, id int) error {
	keyID := models.TemplateKeyIDMap{
		KVKey: key,
		ID:    id,
	}
	if err := sql.db.Create(&keyID).Error; err != nil {
		return fmt.Errorf("建立template key id 映射出错: %w", err)
	}
	return nil
}

// 根据旧template key获取新template id
// Params:
//		key: 旧templateKey
// Return:
//		id: 新templateID
//		error: 错误返回
func (sql SQLService) GetTemplateIDByKey(key string) (int, error) {
	var templateKeyID models.TemplateKeyIDMap
	if err := sql.db.Take(&templateKeyID, "kv_key = ?", key).Error; err != nil {
		return 0, fmt.Errorf("获取tempalte id出错: %w", err)
	}
	return templateKeyID.ID, nil
}

// 获取模板下所有树（信息）
// Params:
//		templateID: 模板id
// Return:
//		[]TemplateTree: 模板所有的树
//		error: 错误返回
func (sql *SQLService) GetTemplateTrees(id int) ([]models.TemplateTree, error) {
	var templateTrees []models.TemplateTree
	if err := sql.db.Find(&templateTrees, "template = ?", id).Error; err != nil {
		return nil, fmt.Errorf("获取模板所有树信息出错: %w", err)
	}
	return templateTrees, nil
}

// 获取模板下所有角色,包括组织管理员这种固定角色
// Params:
//		templateID: 模板id
// Return:
//		[]roles: 模板所有的角色
//		error: 错误返回
func (sql *SQLService) GetAllTemplateRoles(templateID int) ([]models.Role, error) {
	//var roles []models.Role
	return sql.GetAllRoles(models.ROLE_TYPE_TEMPLATE, int64(templateID))

	//if err := sql.db.Find(&roles, "type = ? AND belong = ?", models.ROLE_TYPE_TEMPLATE, templateID).Error; err != nil {
	//	return nil, fmt.Errorf("获取模板角色出错: %w", err)
	//}
	//return roles, nil
}
