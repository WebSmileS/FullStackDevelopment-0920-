package sql

import (
	"log"

	"bjyixiu.com/ent/models"
	"github.com/jinzhu/gorm"
)

type SQLService struct {
	db             *gorm.DB
	updateTreeChan chan int
	delTreeChan    chan int
}

func (sql *SQLService) GetGroupTemplateSubTree(groupKey, terminalKey string) (*models.Subtree, error) {
	panic("implement me")
}

func (sql *SQLService) GetGroupTemplateSubtreeIndex(groupKey, terminalKey string) (*models.Subtree, error) {
	panic("implement me")
}

func (sql *SQLService) DelRolePermission(id int) error {
	panic("implement me")
}

func (sql *SQLService) DelGroupRolePermission(id, gid int) error {
	panic("implement me")
}

func (sql *SQLService) GetGroupTemplateID(gid int) (int, error) {
	panic("implement me")
}

func (sql *SQLService) SetTreeToRole(roleID, groupID string, subtree models.Subtree) error {
	panic("implement me")
}

func (sql *SQLService) LinkRoleUser(roleID, userID string) error {
	panic("implement me")
}

//func (sql *SQLService) GetUsersByNodeID(nodeID, groupID string) ([]string, error) {
//	panic("implement me")
//}

func (sql *SQLService) WebNotificationSignup(uid int64, sid string) error {
	//panic("implement me")
	return nil
}

func (sql *SQLService) AppNotificationSignup(uid int64, sid, regid string) error {
	//panic("implement me")
	//log.Println("App notification signup waiting for implement")
	return nil
}

func (sql *SQLService) GetNotificationIDs(uid string) (map[string]string, error) {
	panic("implement me")
}

// 事物开始
// Params:
//		none
// Return:
// 		sqlService: 带事务的Service
func (sql *SQLService) Begin() *SQLService {
	db := sql.db.Begin()
	return &SQLService{
		db: db,
	}
}

// 事务提交
// Params:
// 		none
// Return:
//		error: 错误返回
func (sql *SQLService) Commit() error {
	return sql.db.Commit().Error
}

func (sql *SQLService) GenID() int64 {
	return models.GenID()
}

// 初始化服务
// Params:
//		db: gorm 数据库引擎
// Return:
//		error: 错误返回
func (sql *SQLService) InitSqlService(db *gorm.DB) (chan int, chan int) {
	migrate(db)
	sql.db = db
	sql.updateTreeChan = make(chan int)
	sql.delTreeChan = make(chan int)
	return sql.updateTreeChan, sql.delTreeChan
}

func NewSQLService(db *gorm.DB) *SQLService {
	//TODO: 暂时建表用
	migrate(db)
	return &SQLService{db: db}
}

//TODO: 暂时建表用
func migrate(db *gorm.DB) {
	if err := db.AutoMigrate(
		&models.Terminal{},
		&models.Node{},
		&models.Tree{},
		&models.TreeKeyIDMap{},
		&models.NodeKeyIDMap{},
		&models.Template{},
		&models.TemplateTree{},
		&models.TemplatePermission{},
		&models.TemplateKeyIDMap{},
		&models.Role{},
		&models.RoleTree{},
		&models.RolePermission{},
		&models.GroupTemplate{},
		&models.GroupRoleKeyIDMap{},
		&models.UserRole{}).Error; err != nil {
		log.Fatal("建表出错:" + err.Error())
	}
}

// 临时实现所有接口

// 获取节点详细
func (sql *SQLService) GetNodeDetailByID(id string) (*models.Node, error) {
	return nil, nil
}

// 节点顺序变动
func (sql *SQLService) MoveNode(id string, side bool) error {
	return nil
}

// 根据termina和tree key获取母树
func (sql *SQLService) GetMotherTree(terminal string, tree string) (*models.Tree, error) {
	return nil, nil
}

// 新增组织模版
func (sql *SQLService) AddTemplate(*models.Template) error {
	return nil
}

// 设定模版指定端的树
func (sql *SQLService) SetTreeToTemplate(string, string, models.Subtree) error {
	return nil
}

// 获取组织模版指定端子树（填充）
func (sql *SQLService) GetTemplateSubTree(groupKey, terminalKey string) (*models.Subtree, error) {
	return nil, nil
}

// 获取组织模板指定端子树（索引）
func (sql *SQLService) GetTemplateSubtreeIndex(groupKey, terminalKey string) (*models.Subtree, error) {
	return nil, nil
}

// 删除模版指定端树
func (sql *SQLService) DelTreeFromGroupTemplate(string, string) error {
	return nil
}

// 获取指定组织模版下所有子树（信息）
func (sql *SQLService) GetTemplateAllSubtreesInfo(groupKey string) ([]models.Subtree, error) {
	return nil, nil
}

// 获取指定组织模版下所有子树（包括实体）
func (sql *SQLService) GetTemplateAllSubtrees(groupKey string) ([]models.Subtree, error) {
	return nil, nil
}

//// 新增模板系统固定角色
//func (sql *SQLService) AddInternalRoleToTemplate(templateKey string, name string) (string, error) {
//	return "", nil
//}

// 新增模板角色
func (sql *SQLService) AddRoleToTemplate(templateID string, name string) (string, error) {
	return "", nil
}

// 删除模板角色
func (sql *SQLService) DelRoleFromTemplate(templateID string, roleID string, force bool) error {
	return nil
}

// 修改组织模版角色名称
func (sql *SQLService) UpdateTemplateRoleName(templateKey, roleID, name string) error {
	return nil
}

// 设置组织模版角色树 TODO: 等待删除
func (sql *SQLService) SetTreeToTemplateRole(roleID string, subtree models.Subtree) error {
	return nil
}

// 删除组织模板角色指定端树
func (sql *SQLService) DelTreeFromTemplateRole(roleID, terminalKey string) error {
	return nil
}

// 获取组织模板角色指定端树索引
func (sql *SQLService) GetTemplateRoleTreeIndex(roleID, template string, terminalKey string) (models.Subtree, error) {
	return models.Subtree{}, nil
}

// 获取角色所有端树索引
func (sql *SQLService) GetRoleTreesIndex(roleID string) ([]models.Subtree, error) {
	return nil, nil
}

// 应用模板到组织
func (sql *SQLService) ApplyTemplateToGroup(templateKey, groupKey string, includeRoles bool) error {
	return nil
}

// 清除组织的模板和角色
func (sql *SQLService) ClearGroupTemplate(groupKey string, clearRoles bool) error {
	return nil
}

// 获取组织下所有端和子树
func (sql *SQLService) GetGroupAllSubtrees(groupKey string) ([]models.Subtree, error) {
	return nil, nil
}

// 获取组织下所有角色，包括系统内置角色
func (sql *SQLService) GetAllGroupRoles(groupKey string) ([]models.GroupRole, error) {
	return nil, nil
}

// 获取组织下所有角色，不包括系统内置角色
func (sql *SQLService) GetGroupRoles(groupKey string) ([]models.GroupRole, error) {
	return nil, nil
}

// 增加角色到组织
func (sql *SQLService) AddRoleToGroup(groupKey, name string) (string, error) {
	return "", nil
}

// 修改组织角色名称
func (sql *SQLService) UpdateGroupRoleName(groupKey, roleID, name string) error {
	return nil
}

// 删除组织角色
func (sql *SQLService) DelRoleFromGroup(groupKey, roleID string) error {
	return nil
}

// 获取组织角色信息
func (sql *SQLService) GetGroupRole(groupKey, roleID string) (*models.GroupRole, error) {
	return nil, nil
}

// 设定某角色为特殊角色
func (sql *SQLService) SetRole2Special(template, name, specialKey string) error {
	return nil
}

// 获取模板中的特殊角色
func (sql *SQLService) GetSpecRoleFromTemplate(template, roleName string) (*models.TemplateRole, error) {
	return nil, nil
}

// 获取组织中的特殊角色
func (sql *SQLService) GetSpecRoleFromGroup(group, roleName string) (*models.GroupRole, error) {
	return nil, nil
}

// 获取组织对应的模板key
func (sql *SQLService) GetTemplateKeyFromGroup(group string) (template string, err error) {
	return "", nil
}

// 获得用户角色key
func (sql *SQLService) GetUserRoleKey(userID string) (string, error) {
	return "", nil
}

// 获取角色指定端，指定节点下子树
func (sql *SQLService) GetRoleSubtree(roleID, group, terminalKey, parentID string) ([]*models.Node, error) {
	return nil, nil
}

// 导出母树节点，包括节点下子节点（子树）
func (sql *SQLService) GetSubtree(nodeID string) (*models.Node, error) {
	return nil, nil
}

// 插入子树到母树，一般配合导出使用
func (sql *SQLService) InsertSubtree(parentNodeID string, node *models.Node) error {
	return nil
}

// 检查节点是否有重复
func (sql *SQLService) NodeExist(node *models.Node, level int) (bool, error) {
	return false, nil
}
