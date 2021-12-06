// service 包， 用于定义服务接口和返回服务实体
// Auth: Arion
// 2019.11.21
package service

import (
	"log"

	"github.com/jinzhu/gorm"

	"bjyixiu.com/ent/config"
	"bjyixiu.com/ent/db"
	"bjyixiu.com/ent/models"
	"bjyixiu.com/ent/sql"
)

type Persistence interface {
	// 初始化服务
	InitSqlService(db *gorm.DB) (chan int, chan int)

	// Terminal
	// 新建Terminal
	CreateTerminal(key, name string) error
	// 修改terminal名称
	UpdateTerminal(key, name string) error
	// 删除terminal
	DelTerminal(key string) error
	// 获取所有terminal
	GetAllTerminal() ([]models.Terminal, error)
	// 获取指定的terminal
	GetTerminalByKey(key string) (*models.Terminal, error)

	// Tree
	// 新增母树
	CreateMotherTree(tree *models.Tree) (id int, err error)
	// 直接插入母树
	InsertTree(tree *models.Tree) error
	// 修改母树信息，不能修改Terminal和Root节点
	UpdateMotherTree(tree *models.Tree) error
	// 删除母树, 包括母树信息和所有节点
	DelMotherTree(id int) (err error)
	// 根据母树ID获取母树,不包含树结构
	GetMotherTreeByID(id int) (*models.Tree, error)
	// 根据节点（一般为根节点）获取树结构
	GetTreeByID(rootID int, depth int, states ...int) (*models.Node, error)
	// 设置tree key 到 tree id的映射
	InsertTreeKeyID(terminal, tree string, id int) error
	// 获得Tree 对应的 id
	GetTreeIDByKey(terminal, tree string) (int, error)
	// 获取所有母树(信息)
	GetAllTrees() ([]models.Tree, error)
	// 同步母树
	SyncTree(newTree, oldTree *models.Tree) (err error)

	// Node
	// 新增母树上的节点，根节点不在此新增
	AddNode(node *models.Node) error
	// 获取母树节点
	GetNodeByID(id int) (models.Node, error)
	// 删除节点，包括节点的子节点
	DelNode(id int, forces bool) error
	// 保存节点Key -- SqlID对应表，从pika迁移到mysql用
	InsertKeyID(key string, id int) error
	// 获取节点Key -- SqlID对应关系，从pika迁移到mysql用
	GetKeyIDbyKey(key string) (int, error)
	// 交换节点顺序
	SwapNode(id1, id2 int) (err error)

	// Template
	// 获取所有组织模版
	GetAllTemplates() ([]models.Template, error)
	// 根据ID获取模板
	GetTemplate(id int) (*models.Template, error)
	// 新增组织模版
	CreateTemplate(*models.Template) (int, error)
	// 修改组织模版
	UpdateTemplate(*models.Template) error
	// 删除组织模版∑
	DelTemplate(int) error
	// 设置模板的权限(按树设置)
	SetTemplatePermission(permission *models.TemplatePermission) error
	// 获取模板的权限(按树获取)
	GetTemplatePermission(templateID, treeID int) (*models.TemplatePermission, error)
	// 获取模板的权限(按端获取)
	GetTemplatePermissionByTerminal(templateID int, terminalKey string) (*models.TemplatePermission, error)
	// 建立旧templateKey到新templateID到映射
	InsertTemplateKeyID(key string, id int) error
	// 根据旧template key获取新template id
	GetTemplateIDByKey(key string) (int, error)
	// 获取模板下所有树（信息）
	GetTemplateTrees(id int) ([]models.TemplateTree, error)
	// 获得组织模板下所有角色，包括组织管理员这种固定角色
	GetAllTemplateRoles(templateID int) ([]models.Role, error)

	// group
	// 设置组织对应的模板
	SetGroupTemplate(groupID int64, templateID int) error
	// 获取组织对应的模板
	GetGroupTemplate(groupID int64) (*models.Template, error)

	// Role
	// 设置角色权限
	SetRolePermission(permission *models.RolePermission) error
	// 获取角色指定树的权限
	GetRolePermission(roleID, treeID int) (*models.RolePermission, error)
	// 获取角色指定端的权限
	GetRolePermissionByTerminal(roleID int, terminalKey string) (*models.RolePermission, error)
	// 设置用户角色
	SetUserRole(userID int64, roleID int) error
	// 删除用户角色
	DeleteUserRole(userID int64) error
	// 新建角色
	CreateRole(role *models.Role) (int, error)
	// 获取属于模板/组织的所有角色
	GetAllRoles(roleType uint8, belong int64) ([]models.Role, error)
	// 获取角色（信息）
	GetRole(id int) (*models.Role, error)
	// 删除角色
	DelRole(id int, force bool) (err error)
	// 修改角色名称
	UpdateRole(role *models.Role) error
	// 获取角色拥有权限的端&树
	GetRoleTrees(roleID int) ([]models.RoleTree, error)
	// 获取角色拥有所有权限
	GetRolePermissions(roleID int) ([]*models.RolePermission, error)
	//// 获取角色对应的Tree (信息)
	//GetRoleTree(roleID int, treeID int) (*models.RoleTree, error)
	//// 获取角色指定端的Tree（信息）
	//GetRoleTreeByTerminal(roleID int, terminal string) (*models.RoleTree, error)
	// 获取角色指定端的树id
	GetRoleTerminalTreeID(roleID int, terminalKey string) (int, error)
	// 按照角色名称获取组织对应模板的内置角色
	GetInternalRoleByName(roleName string, groupID int64) (*models.Role, error)
	// 根据用户id获取角色id
	GetRoleIDByUser(userID int64) (int, error)
	// 建立旧角色key到新角色id的映射
	InsertRoleKeyID(key string, id int) error
	// 根据旧角色key获取新角色id
	GetRoleIDByKey(key string) (int, error)
	// 批量获取用户角色ID
	GetUsersRoleIDs(uIDs []int64) ([]models.UserRole, error)
	// 批量获取用户角色
	GetUsersRoles(uIDs []int64) ([]models.UserNRole, error)
	// 查询角色是否有对应的权限
	CheckRolePermissions(roleID int, permissions []int) (bool, error)

	// User
	// 获取有对应权限的用户id
	GetUsersByNodeID(nodeID int, groupID int64) ([]models.UserRole, error)
	// 获取有对应权限组的用户id
	GetUsersByNodeIDs(nodeIDs []int, group int64) ([]models.UserRole, error)
	// get settings
	GetUserSettings(userID int64, pageName string, pageType string) (*models.YrtUserSetting, error)
	// store settings
	StoreUserSettings(setting *models.YrtUserSetting) (int, error)
}

type TreesService interface {
	// 初始化服务
	InitTreesService(persistence Persistence, updateTree, delTree chan int) error
	// 获取模板所有树（实体）
	TemplateTrees(templateID int) ([]*models.Tree, error)
	// 获取组织所有树（实体）
	GroupTrees(groupID int64) ([]*models.Tree, error)
	// 获取角色的权限树（实体）
	RoleSubtree(roleID, nodeID int, terminalKey string, includeFeature bool) (*models.Tree, error)
	// 按照角色名称获取特殊角色（模板特殊角色）的权限树（实体）
	InternalRoleTreeByName(roleName string, groupID int64, nodeID int, terminalKey string, includeFeature bool) (*models.Tree, error)
	// 获取所有terminal tree信息
	AllTerminalTree() ([]*models.TerminalTree, error)
	// 获取指定id的母树
	MotherTree(id int) *models.Tree
}

type TireService interface {
}

// Service 接口定义
type Service interface {
	Persistence
	TreesService
	TireService
	// 获取terminal下所有母树(不包括树)
	GetTreesByTerminal(terminalKey string) ([]*models.Tree, error)

	// 获取节点详细
	GetNodeDetailByID(id string) (*models.Node, error)

	// 修改母树节点
	UpdateNode(node *models.Node) error

	// 节点顺序变动
	MoveNode(id string, side bool) error

	// 启用节点
	// 停用节点

	// 根据termina和tree key获取母树
	GetMotherTree(terminal string, tree string) (*models.Tree, error)

	// 设定模版指定端的树
	SetTreeToTemplate(string, string, models.Subtree) error
	// 获取组织模版指定端子树（填充）
	GetGroupTemplateSubTree(groupKey, terminalKey string) (*models.Subtree, error)
	// 获取组织模板指定端子树（索引）
	GetGroupTemplateSubtreeIndex(groupKey, terminalKey string) (*models.Subtree, error)
	// 删除模版指定端树
	DelTreeFromGroupTemplate(string, string) error
	// 获取指定组织模版下所有子树（信息）
	GetTemplateAllSubtreesInfo(groupKey string) ([]models.Subtree, error)
	// 获取指定组织模版下所有子树（包括实体）
	GetTemplateAllSubtrees(groupKey string) ([]models.Subtree, error)

	// 新增模板角色
	AddRoleToTemplate(templateID string, name string) (string, error)
	// 删除角色
	DelRolePermission(id int) error
	// 删除组织角色
	DelGroupRolePermission(id, gid int) error
	// 修改组织模版角色名称
	UpdateTemplateRoleName(templateKey, roleID, name string) error
	// 设置组织模版角色树 TODO: 等待删除
	SetTreeToTemplateRole(roleID string, subtree models.Subtree) error
	// 删除组织模板角色指定端树
	DelTreeFromTemplateRole(roleID, terminalKey string) error
	// 获取组织模板角色指定端树索引
	GetTemplateRoleTreeIndex(roleID, template string, terminalKey string) (models.Subtree, error)
	// 获取角色所有端树索引
	GetRoleTreesIndex(roleID string) ([]models.Subtree, error)
	// 应用模板到组织
	ApplyTemplateToGroup(templateKey, groupKey string, includeRoles bool) error
	// 获取组织对应的模板id
	GetGroupTemplateID(gid int) (int, error)
	// 清除组织的模板和角色
	ClearGroupTemplate(groupKey string, clearRoles bool) error
	// 获取组织下所有端和子树
	GetGroupAllSubtrees(groupKey string) ([]models.Subtree, error)
	// 获取组织下所有角色，包括系统内置角色
	GetAllGroupRoles(groupKey string) ([]models.GroupRole, error)
	// 获取组织下所有角色，不包括系统内置角色
	GetGroupRoles(groupKey string) ([]models.GroupRole, error)
	// 增加角色到组织
	AddRoleToGroup(groupKey, name string) (string, error)
	// 修改组织角色名称
	UpdateGroupRoleName(groupKey, roleID, name string) error
	// 删除组织角色
	DelRoleFromGroup(groupKey, roleID string) error
	// 设置（更新）组织角色权限树
	SetTreeToRole(roleID, groupID string, subtree models.Subtree) error

	//SetTreeToRole(roleID string, subtree models.Subtree) error
	// 获取组织角色信息
	GetGroupRole(groupKey, roleID string) (*models.GroupRole, error)
	// 设定某角色为特殊角色
	SetRole2Special(template, name, specialKey string) error
	// 获取模板中的特殊角色
	GetSpecRoleFromTemplate(template, roleName string) (*models.TemplateRole, error)
	// 获取组织中的特殊角色
	GetSpecRoleFromGroup(group, roleName string) (*models.GroupRole, error)
	// 获取组织对应的模板key
	GetTemplateKeyFromGroup(group string) (template string, err error)

	// 设置用户角色
	//SetRole2User(userID, role string) error
	// 获得用户角色key
	GetUserRoleKey(userID string) (string, error)
	// 获取角色指定端，指定节点下子树
	GetRoleSubtree(roleID, group, terminalKey, parentID string) ([]*models.Node, error)
	// 导出母树节点，包括节点下子节点（子树）
	GetSubtree(nodeID string) (*models.Node, error)
	// 插入子树到母树，一般配合导出使用
	InsertSubtree(parentNodeID string, node *models.Node) error
	// 检查节点是否有重复
	NodeExist(node *models.Node, level int) (bool, error)
	//关联角色-用户列表
	LinkRoleUser(roleID, userID string) error
	// 更新节点-组织-角色的关联
	//UpdateNodeRoleLink(roleID, groupID string, oldTree, newTree []models.NodeIndex) error

	// 消息推送相关
	// 	注册web端对应的通知id
	WebNotificationSignup(uid int64, sid string) error
	// 注册app端对应的通知id
	AppNotificationSignup(uid int64, sid, regid string) error
	// 获取session和appID(registration id)
	GetNotificationIDs(uid string) (map[string]string, error)
	// 生成ID
	//GenID() int64
}

type EntService struct {
	sql.SQLService
	Trees
	//Tire
}

// 新Service, 建立新的服务实体
// Params:
// 		conf : 配置
// Return:
// 		服务实体
func NewService(conf config.Config, isDev bool) Service {
	var service EntService
	updateTree, delTree := service.InitSqlService(db.NewMySQLDB(conf.MySQL, isDev))
	if err := service.InitTreesService(&service.SQLService, updateTree, delTree); err != nil {
		log.Fatal(err)
	}
	return &service
}
