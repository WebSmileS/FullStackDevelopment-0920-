package sql

import (
	"errors"
	"fmt"

	"github.com/jinzhu/gorm"

	"bjyixiu.com/ent/models"
)

// 新建角色
// Params:
//		role: 角色信息
// Return:
//		id: 角色id
// 		error: 错误返回
func (sql *SQLService) CreateRole(role *models.Role) (int, error) {
	if err := sql.db.Create(role).Error; err != nil {
		return 0, fmt.Errorf("新建角色出错: %w", err)
	}
	return role.ID, nil
}

// 修改角色名称
// Params:
//		role: 角色信息
// Return:
// 		error: 错误返回
func (sql *SQLService) UpdateRole(role *models.Role) error {
	if err := sql.db.Model(role).Where("id = ?", role.ID).Update("name", role.Name).Error; err != nil {
		return fmt.Errorf("修改角色名称出错: %w", err)
	}
	return nil
}

// 删除角色，需要显式指定是否强制删除（删除模版内置角色或删除有关联用户的角色）
// 对于属于组的角色需要先判定是否有关联用户，有关联用户不允许删除
// Params:
//		id: 角色id
//		force: 是否强制删除
// Return:
// 		error: 错误返回
func (sql *SQLService) DelRole(id int, force bool) (err error) {
	// 获取角色
	role, err := sql.GetRole(id)
	if err != nil {
		err = fmt.Errorf("删除角色错误: %w", err)
		return
	}
	// 不是强制删除，检查是否内置，或有用户关联
	if !force {
		if role.Internal {
			err = errors.New("不能删除内置角色")
			return
		}
	}

	count, err := sql.getRoleUserCount(role.ID)
	if err != nil {
		err = fmt.Errorf("删除角色错误: %w", err)
		return
	}

	if count > 0 {
		err = errors.New("还有用户关联到改角色，请先处理所有关联用户")
		return
	}

	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("删除角色出错: %w", err)
		}
		if r := recover(); r != nil {
			err = fmt.Errorf("删除角色出错: %v", r)
		}
	}()
	// 是，则直接删除
	// 删除角色信息
	if err = tx.Delete(&models.Role{}, "id = ?", id).Error; err != nil {
		return
	}
	// 删除角色树
	if err = tx.Delete(&models.RoleTree{}, "role = ?", id).Error; err != nil {
		return
	}
	// 删除角色对应的权限
	if err = tx.Delete(&models.RolePermission{}, "role = ?", id).Error; err != nil {
		return
	}
	// 提交事务
	err = tx.Commit().Error
	return
}

// 获取属于模板/组织的所有角色
// Params:
//		roleType : 模板角色或组织角色
//		belong: 所属对象的id
// Return:
//		[]role: 所有角色列表
// 		error: 错误返回
func (sql *SQLService) GetAllRoles(roleType uint8, belong int64) (roles []models.Role, err error) {
	defer func() {
		if r := recover(); r != nil {
			err = fmt.Errorf("获取所有角色出错: %v", r)
		}
	}()
	if err = sql.db.Find(&roles, "type = ? AND belong = ?", roleType, belong).Error; err != nil {
		if gorm.IsRecordNotFoundError(err) {
			return roles, nil
		}
		return nil, fmt.Errorf("获取所有角色出错: %w", err)
	}

	return roles, nil
}

// 获取角色（信息）
// Params:
//		id : 角色id
// Return:
//		role: 角色信息
// 		error: 错误返回
func (sql *SQLService) GetRole(id int) (*models.Role, error) {
	var role models.Role
	if err := sql.db.Take(&role, "id = ?", id).Error; err != nil {
		return nil, fmt.Errorf("获取角色出错: %w", err)
	}
	return &role, nil
}

// 设置角色权限
// Params:
//		permission : 角色权限
// Return:
// 		error: 错误返回
func (sql *SQLService) SetRolePermission(permission *models.RolePermission) error {
	// 获取原值权限
	//var old *models.RolePermission
	//var err error
	//if permission.Tree > 0 {
	//	old, err = sql.GetRolePermission(permission.Role, permission.Tree)
	//} else {
	if len(permission.Terminal) == 0 {
		return errors.New("未指定端")
	}
	old, err := sql.GetRolePermissionByTerminal(permission.Role, permission.Terminal)

	//}

	if err != nil {
		return fmt.Errorf("设置角色权限,检查原值: %w", err)
	}
	// 无原值，新建
	if old == nil {
		if err := sql.createRolePermission(permission); err != nil {
			return fmt.Errorf("设置角色权限,新增权限: %w", err)
		}
		return nil
	}
	// 有原值，是否为同一树
	// 是，更改
	if old.Tree == permission.Tree {
		if err := sql.updateRolePermission(permission, old); err != nil {
			return fmt.Errorf("设置角色权限, 更新权限: %w", err)
		}
		return nil
	}
	// 否，删除原值，再增加
	if err := sql.changeRoleTree(permission, old); err != nil {
		return fmt.Errorf("设置角色权限，更换权限树: %w", err)
	}
	return nil
}

// 获取角色指定端对应的树ID
// 无记录则返回id值为-1
// Params:
//		roleID: 角色id
//		terminalKey: 端key
// Return:
//		treeID: 树id
//		error: 错误返回
func (sql *SQLService) GetRoleTerminalTreeID(roleID int, terminalKey string) (int, error) {
	var roleTree models.RoleTree
	if len(terminalKey) == 0 {
		return 0, errors.New("未指定端")
	}
	if err := sql.db.Take(&roleTree, "role = ? AND terminal = ?", roleID, terminalKey).Error; err != nil {
		if gorm.IsRecordNotFoundError(err) {
			return -1, nil
		}
		return 0, fmt.Errorf("获取角色指定端树id出错: %w", err)
	}
	return roleTree.Tree, nil
}

// GetRolePermission 获取角色指定树的权限
// Params:
//		roleID: 角色id
//		treeID: 树id
// Return:
//		permission: 角色权限
//		error: 错误返回
func (sql *SQLService) GetRolePermission(roleID, treeID int) (*models.RolePermission, error) {
	var ids []int
	rows, err := sql.db.Table(models.RolePermission{}.TableName()).
		Select("node").
		Where("role = ? AND tree = ?", roleID, treeID).
		Rows()
	if err != nil {
		return nil, fmt.Errorf("获取角色权限出错：%w", err)
	}

	for rows.Next() {
		var id int
		if err := rows.Scan(&id); err != nil {
			return nil, fmt.Errorf("获取角色权限，读取权限出错: %w", err)
		}
		ids = append(ids, id)
	}
	permission := &models.RolePermission{
		Role:     roleID,
		Tree:     treeID,
		Terminal: "",
		Nodes:    ids,
	}
	return permission, nil
}

// 获取角色指定端的权限
// Params:
//		roleID: 角色id
//		terminalKey: 端Key
// Return:
//		permission: 角色权限
//		error: 错误返回
func (sql *SQLService) GetRolePermissionByTerminal(roleID int, terminalKey string) (*models.RolePermission, error) {
	// 获取端对应的树
	treeID, err := sql.GetRoleTerminalTreeID(roleID, terminalKey)
	if err != nil {
		return nil, fmt.Errorf("获取角色权限出错:%w", err)
	}
	// 无记录
	if treeID == -1 {
		return nil, nil
	}
	permission, err := sql.GetRolePermission(roleID, treeID)
	if err != nil {
		return nil, fmt.Errorf("获取角色权限: %w", err)
	}
	// 获取权限
	permission.Terminal = terminalKey
	return permission, nil
}

// 新建角色权限
// Params:
//		permission: 旧权限
// Return:
//		error: 错误返回
func (sql SQLService) createRolePermission(permission *models.RolePermission) (err error) {
	// 事务
	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("保存角色权限出错: %w", err)
		}
		if r := recover(); r != nil {
			tx.Rollback()
			err = fmt.Errorf("保存角色权限出错: %v", r)
		}
	}()
	if err = tx.Error; err != nil {
		return
	}
	// 增加角色-树关联
	roleTree := &models.RoleTree{
		Role:     permission.Role,
		Terminal: permission.Terminal,
		Tree:     permission.Tree,
	}
	if err = tx.Create(&roleTree).Error; err != nil {
		return
	}
	// 添加模板-节点关联
	for _, id := range permission.Nodes {
		addNode := models.RolePermission{
			Role: permission.Role,
			Tree: permission.Tree,
			Node: id,
		}
		if err = tx.Create(&addNode).Error; err != nil {
			return
		}
	}
	// 提交事务
	err = tx.Commit().Error
	return
}

// 角色权限树不变的情况下节点更新
// Params:
//		oldPermission: 旧权限
//		newPermission: 新权限
// Return:
//		error: 错误返回
func (sql *SQLService) updateRolePermission(new, old *models.RolePermission) (err error) {
	// 与原权限对比
	add, abandoned := diff(new.Nodes, old.Nodes)
	// 更新
	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("保存模板权限出错: %w", err)
		}
		if r := recover(); r != nil {
			tx.Rollback()
			err = fmt.Errorf("保存模板权限出错: %v", r)
		}
	}()
	if err = tx.Error; err != nil {
		return
	}
	// 删除的
	for _, id := range abandoned {
		if err = tx.Where("role = ? AND node = ?", new.Role, id).Delete(new).Error; err != nil {
			return
		}
	}
	// 增加的
	for _, id := range add {
		addNode := models.RolePermission{
			Role: new.Role,
			Tree: new.Tree,
			Node: id,
		}
		if err = tx.Create(&addNode).Error; err != nil {
			return
		}
	}

	err = tx.Commit().Error
	return
}

// 角色权限树变更
// Params:
//		oldPermission: 旧权限
//		newPermission: 新权限
// Return:
//		error: 错误返回
func (sql *SQLService) changeRoleTree(new, old *models.RolePermission) (err error) {
	// 进入事务
	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("保存模板权限出错: %w", err)
		}
		if r := recover(); r != nil {
			tx.Rollback()
			err = fmt.Errorf("保存模板权限出错: %v", r)
		}
	}()
	if err = tx.Error; err != nil {
		return
	}
	// 修改模板-树关联
	roleTree := &models.RoleTree{
		Role:     old.Role,
		Terminal: old.Terminal,
		Tree:     new.Tree,
	}
	if err = tx.Model(roleTree).Update("tree").Error; err != nil {
		return
	}
	// 删除所有的旧节点关联
	if err = tx.Delete(&models.RoleTree{}, "role = ? AND tree = ?", old.Role, old.Tree).Error; err != nil {
		return
	}
	// 增加新的节点关联
	for _, id := range new.Nodes {
		addNode := models.RolePermission{
			Role: new.Role,
			Tree: new.Tree,
			Node: id,
		}
		if err = tx.Create(&addNode).Error; err != nil {
			return
		}
	}

	// 提交
	err = tx.Commit().Error
	return
}

// 设置用户角色
// Params:
//		userID: 用户id
//		roleID: 角色id
// Return:
//		error: 错误返回
func (sql *SQLService) SetUserRole(userID int64, roleID int) error {
	userRole := models.UserRole{
		User: userID,
		Role: roleID,
	}
	// 关联是否存在
	oldID, err := sql.getUserRoleID(userID)
	if err != nil {
		return fmt.Errorf("设定用户角色出错: %w", err)
	}

	if oldID > 0 {
		// 存在，更新
		if err := sql.db.Model(&userRole).Select("role").Where("user = ?", userRole.User).Updates(&userRole).Error; err != nil {
			return fmt.Errorf("用户角色保存错误: %w", err)
		}
	} else {
		// 不存在，新建
		if err := sql.db.Create(&userRole).Error; err != nil {
			return fmt.Errorf("用户角色保存错误: %w", err)
		}
	}
	return nil
}

// 删除用户关联的角色
// Params:
//		userID: 用户id
// Return:
//		error: 错误返回
func (sql *SQLService) DeleteUserRole(userID int64) error {
	if err := sql.db.Delete(&models.UserRole{}, "user = ?", userID).Error; err != nil {
		return fmt.Errorf("删除用户角色出错：%w", err)
	}
	return nil
}

// 获取用户角色
// Params:
//		userID: 用户id
// Return:
//		role: 角色
//		error: 错误返回
func (sql *SQLService) GetUserRole(userID int64) (*models.Role, error) {
	// 获取用户对应的角色id
	rid, err := sql.getUserRoleID(userID)
	if err != nil {
		return nil, fmt.Errorf("获取用户角色: %w", err)
	}
	// 有对应id，返回角色
	var role models.Role
	if rid > 0 {
		if err := sql.db.Take(&role, "id = ?", rid).Error; err != nil {
			return nil, fmt.Errorf("获取用户角色出错: %w", err)
		}
	}
	// 无对应id，返回空值
	return &role, nil
}

// 批量获取用户角色ID
// Params:
//		[]userIDs: 列表
// Return:
//		[]usersRoles: 用户id - 角色id 对应列表
//		error: 错误返回
func (sql SQLService) GetUsersRoleIDs(uIDs []int64) ([]models.UserRole, error) {
	var usersRoles []models.UserRole
	if err := sql.db.Where("user IN (?)", uIDs).Find(&usersRoles).Error; err != nil {
		return nil, fmt.Errorf("获取用户角色id出错: %w", err)
	}

	return usersRoles, nil
}

// 批量获取用户角色
// Params:
//		[]userIDs: 列表
// Return:
//		[]userRole: 用户ID - 角色 对应
//		error: 错误返回
func (sql *SQLService) GetUsersRoles(uIDs []int64) ([]models.UserNRole, error) {
	var usersRoles []models.UserNRole
	if err := sql.db.Table(models.UserRole{}.TableName()).
		Select("permission_user_role.user, permission_role.id, permission_role.name").
		Where("user IN (?)", uIDs).
		Joins("INNER JOIN permission_role ON permission_user_role.role = permission_role.id").
		Find(&usersRoles).Error; err != nil {
		return nil, fmt.Errorf("批量获取用户对应角色出错: %w", err)
	}
	return usersRoles, nil
}

// 获取用户角色id
// Params:
//		userID: 用户id
// Return:
//		roleID: 角色id
//		error: 错误返回
func (sql *SQLService) getUserRoleID(userID int64) (int, error) {
	var userRole models.UserRole
	if err := sql.db.Take(&userRole, "user = ?", userID).Error; err != nil {
		if gorm.IsRecordNotFoundError(err) {
			return -1, nil
		}
		return 0, fmt.Errorf("获取用户角色id出错: %w", err)
	}
	return userRole.Role, nil
}

// 获取角色拥有权限的端&树
// Params:
//		roleID: 角色id
// Return:
//		[]treeTree: 角色树数组
//		error: 错误返回
func (sql *SQLService) GetRoleTrees(roleID int) ([]models.RoleTree, error) {
	var roleTrees []models.RoleTree
	if err := sql.db.Find(&roleTrees, "role = ?", roleID).Error; err != nil {
		return nil, fmt.Errorf("获取角色拥有的权限树: %w", err)
	}
	return roleTrees, nil
}

// 获取角色拥有所有权限
// Params:
//		roleID: 角色id
// Return:
//		[]rolePermission: 角色所有权限
//		error: 错误返回
func (sql *SQLService) GetRolePermissions(roleID int) ([]*models.RolePermission, error) {
	// 获取角色所有端/树
	roleTree, err := sql.GetRoleTrees(roleID)
	if err != nil {
		return nil, fmt.Errorf("获取角色所有权限: %w", err)
	}

	var permissions []*models.RolePermission
	for _, tree := range roleTree {
		permission, err := sql.GetRolePermission(roleID, tree.Tree)
		if err != nil {
			return nil, fmt.Errorf("获取角色所有权限: %w", err)
		}
		permission.Terminal = tree.Terminal
		permissions = append(permissions, permission)
	}

	return permissions, nil
}

// 按照角色名称获取组织对应模板的内置角色
// 如果有重名则只返回第一个结果
// Params:
//		roleName: 角色名称
//		groupID: 组织id
// Return:
//		role: 角色
//		error: 错误返回
func (sql *SQLService) GetInternalRoleByName(roleName string, groupID int64) (*models.Role, error) {
	var role models.Role
	if err := sql.db.Table(role.TableName()).Joins(`INNER JOIN 
		permission_group_template on 
		permission_role.name = ? AND 
		permission_group_template.group_id = ? AND 
		permission_role.belong = permission_group_template.template AND
		permission_role.internal = TRUE`, roleName, groupID).Take(&role).Error; err != nil {
		return nil, fmt.Errorf("根据角色名称获取角色: %w", err)
	}
	return &role, nil
}

// 根据用户id获取角色id
// Params:
//		userID: 用户id
// Return:
//		roleID: 角色id
//		error: 错误返回
func (sql *SQLService) GetRoleIDByUser(userID int64) (int, error) {
	var userRole models.UserRole
	if err := sql.db.Take(&userRole, "user = ?", userID).Error; err != nil {
		if gorm.IsRecordNotFoundError(err) {
			return 0, err
		}
		return 0, fmt.Errorf("获取用户角色: %w", err)
	}
	return userRole.Role, nil
}

// 建立旧角色key到新角色id的映射
// Params:
//		key: 旧角色key
//		id: 新角色id
// Return:
//		error: 错误返回
func (sql *SQLService) InsertRoleKeyID(key string, id int) error {
	keyID := models.GroupRoleKeyIDMap{
		KVKey: key,
		ID:    id,
	}
	if err := sql.db.Create(&keyID).Error; err != nil {
		return fmt.Errorf("存储角色key--ID映射出错: %w", err)
	}
	return nil
}

// 根据旧角色key获取新角色id
// Params:
//		key: 旧角色key
// Return:
//		id: 新角色id
//		error: 错误返回
func (sql *SQLService) GetRoleIDByKey(key string) (int, error) {
	keyID := models.GroupRoleKeyIDMap{}
	if err := sql.db.Take(&keyID, "kv_key = ?", key).Error; err != nil {
		return 0, fmt.Errorf("获取新角色id出错: %w", err)
	}
	return keyID.ID, nil
}

// 获取角色关联的用户数量
// Params:
//		roleID: 角色id
// Return:
//		count: 关联用户的数量
//		error: 错误返回
func (sql *SQLService) getRoleUserCount(roleID int) (int64, error) {
	var count int64
	if err := sql.db.Model(&models.UserRole{}).Where("role = ?", roleID).Count(&count).Error; err != nil {
		return 0, fmt.Errorf("获取角色用户数量出错：%w", err)
	}

	return count, nil
}

// 查询角色是否有对应的权限
// Params:
//		roleID: 角色id
//		permissionNodes: 权限node id 数组
// Return:
//		bool: 有对应权限返回true,反之false
//		error: 错误返回
func (sql *SQLService) CheckRolePermissions(roleID int, permissions []int) (bool, error) {
	var rolePermission models.RolePermission
	if err := sql.db.Where("role = ? AND node IN (?)", roleID, permissions).Take(&rolePermission).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return false, nil
		}
		return false, fmt.Errorf("检查角色是否有权限出错：%w", err)
	}
	return true, nil
}

// 获取角色（信息）
// Params:
//		userID : user_id
//		pageName : page_name
//		pageType : page_type
// Return:
//		setting: 角色信息
// 		error: 错误返回
func (sql *SQLService) GetUserSettings(userID int64, pageName string, pageType string) (*models.YrtUserSetting, error) {
	var setting models.YrtUserSetting
	if err := sql.db.Where("user_id = ? AND page_name = ? AND page_type = ?", userID, pageName, pageType).Take(&setting).Error; err != nil {
		return nil, fmt.Errorf("根据角色名称获取角色: %w", err)
	}
	return &setting, nil
}

// 设置角色权限
// Params:
//		setting : YrtUserSetting
// Return:
// 		error: 错误返回
func (sql *SQLService) StoreUserSettings(setting *models.YrtUserSetting) (int, error) {
	sql.db.Where("user_id = ? AND page_name = ? AND page_type = ?", setting.UserID, setting.PageName, setting.PageType).Delete(setting)
	if err := sql.db.Create(setting).Error; err != nil {
		return 0, fmt.Errorf("新建角色出错: %w", err)
	}

	return 1, nil
}
