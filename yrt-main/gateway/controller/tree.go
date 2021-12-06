package controller

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"

	"github.com/pkg/errors"

	"bjyixiu.com/ent/models"
	"bjyixiu.com/gateway/protocol"
	ryzeModels "bjyixiu.com/ryze/models"
	"github.com/labstack/echo/v4"
)

// 增加端
// swagger:route POST /v1/tree/add_terminal 运维-菜单树 addTerminal
//
// 增加一个端，如: web, weixin, app, yrtweb, yrtapp
//
//		Consumes:
//		- application/json
//		-application/x-protobuf
// 		Responses:
//			200: responseMessage
func AddTerminal(c echo.Context) error {
	t := new(models.Terminal)
	if err := c.Bind(t); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	if err := ent.CreateTerminal(t.Key, t.Name); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, errors.WithStack(err).Error()))
	}

	return c.JSON(http.StatusOK, protocol.Success())
}

// 删除端
// swagger:route DELETE /v1/tree/del_terminal/{key} 运维-菜单树 delTerminal
//
// 删除一个端，传入key值
//
//		Consumes:
//		- application/json
//		-application/x-protobuf
// 		Responses:
//			200: responseMessage
func DelTerminal(c echo.Context) error {

	key := c.Param("key")
	if len(key) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := ent.DelTerminal(key); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, errors.WithStack(err).Error()))
	}

	resp := protocol.SuccessMsg("删除端成功")
	return c.JSON(http.StatusOK, resp)
}

// 更新端（名称）
// swagger:route POST /v1/tree/update_terminal 运维-菜单树 updateTerminal
//
// 更新端名称，传入terminal对象
//
//		Consumes:
//		- application/json
//		-application/x-protobuf
// 		Responses:
//			200: responseMessage
func UpdateTerminalName(c echo.Context) error {

	t := new(models.Terminal)
	if err := c.Bind(t); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	if err := ent.UpdateTerminal(t.Key, t.Name); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, errors.WithStack(err).Error()))
	}

	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取所有terminal
// swagger:route GET /v1/tree/getall_terminal 运维-菜单树 noneParam
//
// 获取所有terminal，无输入参数，返回terminal列表
//
// 		Responses:
//			200: respAllTerminal
func GetAllTerminal(c echo.Context) error {
	terminals, err := ent.GetAllTerminal()
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "获取失败"))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(terminals))
}

// 获取所有terminal

// swagger:route GET /v1/tree/getall_terminal_tree 运维-菜单树 noneTerminalTreeParam
//
// 获取所有terminal，无输入参数，返回terminal列表
//
// 		Responses:
//			200: respTerminalTree
func GetAllTerminalTree(c echo.Context) error {
	tts, err := ent.AllTerminalTree()
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(tts))
}

// 新建母树
// swagger:route POST /v1/tree/add_tree 运维-菜单树 addTree
//
// 新建母树，传入树对象
//
// 		Responses:
//			200: responseMessage
func CreateMotherTree(c echo.Context) error {
	tree := new(models.Tree)
	if err := c.Bind(tree); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	_, err := ent.CreateMotherTree(tree)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, errors.WithStack(err).Error()))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(tree))
}

// 删除母树
//
// 新建母树，传入terminal和key
//
// 		Responses:
//			200: responseMessage
func DeleteMotherTree(c echo.Context) error {
	//terminal := c.Param("terminal")
	idStr := c.Param("id")
	id, err := strconv.Atoi(idStr)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "无效ID请检查"))
	}
	//if id <= 0 || len(terminal) == 0 {
	//	return c.JSON(http.StatusOK, protocol.WrongParams())
	//}
	if err := ent.DelMotherTree(id); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取terminal下所有母树
// swagger:route GET /v1/tree/get_terminal_trees/{key} 运维-菜单树 terminalKey
//
// 获取terminal下所有母树，传入terminal key
//
// 		Responses:
//			200: respTerminalTrees
func GetTreesByTerminal(c echo.Context) error {
	terminalKey := c.Param("terminalKey")
	if len(terminalKey) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	trees, err := ent.GetTreesByTerminal(terminalKey)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(trees))
}

// 根据id获得母树（信息）
// swagger:route GET /v1/tree/get_mother_tree/{id} 运维-菜单树 getMotherTree
//
// 根据id获取完整母树，包括信息和菜单树
//
// 		Responses:
//			200: motherTree
func GetMotherTreeByID(c echo.Context) error {
	idStr := c.Param("id")
	id, err := strconv.Atoi(idStr)
	if err != nil || id <= 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	tree, err := ent.GetMotherTreeByID(id)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(tree))
}

// 根据id获得母树（完整）
// swagger:route GET /v1/tree/get_tree/{id} 运维-菜单树 getMotherTree
//
// 根据id获取完整母树，包括信息和菜单树
//
// 		Responses:
//			200: respTree
func GetTreeByID(c echo.Context) error {
	idStr := c.Param("id")
	id, err := strconv.Atoi(idStr)
	if err != nil || id <= 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	tree, err := ent.GetTreeByID(id, 0)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(tree))
}

// 根据terminal和key获取母树
// swagger:route GET /v1/tree/get_mother_tree/{terminal}/{key} 运维-菜单树 getTree
//
// 获得母树，传入terminal和key
//
// 		Responses:
//			200: respTree
func GetMotherTree(c echo.Context) error {
	terminal := c.Param("terminal")
	key := c.Param("key")
	if len(key) == 0 || len(terminal) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	tree, err := ent.GetMotherTree(terminal, key)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(tree))
}

// 更新母树信息
// swagger:route POST /v1/tree/update_mother_tree 运维-菜单树 updateTree
//
// 更新母树信息，只能更新Name和Desc字段，传入母树信息实体
//
// 		Responses:
//			200: respTree
func UpdateMotherTree(c echo.Context) error {
	tree := new(models.Tree)
	if err := c.Bind(tree); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	if err := ent.UpdateMotherTree(tree); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取所有组织模版
// swagger:route GET /v1/tree/get_all_template 运维-组织模板 noneGetAlltemplate
//
// 获取所有组织模版信息
//
// 		Responses:
//			200: allGroupTemplate
func GetAllTemplates(c echo.Context) error {
	gts, err := ent.GetAllTemplates()
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(gts))
}

// 新建组织模版
// swagger:route POST /v1/tree/add_template 运维-组织模板 addTemplate
//
// 新建组织模版，传入组织模版对象
//
// 		Responses:
//			200: responseMessage
func CreateTemplate(c echo.Context) error {
	template := new(models.Template)
	if err := c.Bind(template); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	id, err := ent.CreateTemplate(template)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	// 指定“starter”为组织的管理员，用于组织新建后，系统创建内置角色给用户用于创建和管理
	// 新增组织模板固定角色“starter”
	// TODO: hardcode 应该用更好的办法来替代
	starter := models.Role{
		ID:       0,
		Name:     "starter",
		Type:     models.ROLE_TYPE_TEMPLATE,
		Belong:   int64(id),
		Internal: true,
	}
	if _, err := ent.CreateRole(&starter); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(id))
}

// 修改组织模版
// swagger:route POST /v1/tree/update_template 运维-组织模板 updateTemplate
//
// 修改组织模版，传入组织模版对象
//
// 		Responses:
//			200: responseMessage
func UpdateTemplate(c echo.Context) error {
	template := new(models.Template)
	if err := c.Bind(template); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := ent.UpdateTemplate(template); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 删除组织模版
// swagger:route DELETE /v1/tree/del_template/{id} 运维-组织模板 reqDelTemplate
//
// 删除组织模版，传入组织模版Key
//
// 		Responses:
//			200: responseMessage
func DelTemplate(c echo.Context) error {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil || id <= 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := ent.DelTemplate(id); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.Success())
}

// 设定组织模版权限
// swagger:route POST /v1/tree/set_template_permission 运维-组织模板 setTemplatePermission
//
// 设定组织模版指定端子树，传入模版key，权限
//
// 		Responses:
//			200: responseMessage
func SetTemplatePermission(c echo.Context) error {
	var permission models.TemplatePermission
	if err := c.Bind(&permission); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := ent.SetTemplatePermission(&permission); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取模板指定端权限
// swagger:route GET /v1/tree/get_template_permission/{template}/{terminal} 运维-组织模板 getTemplatePermission
//
// 获取组织模板指定端权限，传入模版key，端key
//
// 		Responses:
//			200: subtreeIndex
func GetTemplatePermission(c echo.Context) error {
	template := c.Param("template")
	id, err := strconv.Atoi(template)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	terminal := c.Param("terminal")
	if len(template) == 0 || len(terminal) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	permission, err := ent.GetTemplatePermissionByTerminal(id, terminal)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(permission))
}

// 删除组织模版指定端树
// swagger:route DELETE /v1/tree/del_template_tree/{templateKey}/{terminalKey} 运维-组织模板 delTemplateTree
//
// 删除组织模版指定端子树，传入模版key，端key
//
// 		Responses:
//			200: responseMessage
func DelTreeFromTemplate(c echo.Context) error {
	templateKey := c.Param("templateKey")
	terminalKey := c.Param("terminalKey")
	if len(templateKey) == 0 || len(terminalKey) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := ent.DelTreeFromGroupTemplate(templateKey, terminalKey); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取组织模板指定端子树
// swagger:route GET /v1/tree/get_template_tree/{templateKey}/{terminalKey} 运维-组织模板 getTemplateTree
//
// 获取组织模板指定端子树实体，传入模版key，端key
//
// 		Responses:
//			200: subtree
func GetTemplateTree(c echo.Context) error {
	templateKey := c.Param("templateKey")
	terminalKey := c.Param("terminalKey")
	if len(templateKey) == 0 || len(terminalKey) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	subtree, err := ent.GetGroupTemplateSubTree(templateKey, terminalKey)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(subtree))
}

// 获取组织模板指定端子树索引
// swagger:route GET /v1/tree/get_template_tree_index/{templateKey}/{terminalKey} 运维-组织模板 getTemplateTreeIndex
//
// 获取组织模板指定端子树索引，传入模版key，端key
//
// 		Responses:
//			200: subtreeIndex
//func GetTemplateTreeIndex(c echo.Context) error {
//	templateKey := c.Param("templateKey")
//	terminalKey := c.Param("terminalKey")
//	if len(templateKey) == 0 || len(terminalKey) == 0 {
//		return c.JSON(http.StatusOK, protocol.WrongParams())
//	}
//
//	subtree, err := ent.GetGroupTemplateSubtreeIndex(templateKey, terminalKey)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
//	}
//
//	return c.JSON(http.StatusOK, protocol.SuccessData(subtree))
//}

// 获取组织模版所有端子树
// swagger:route GET /v1/tree/get_template_all_tree_info/{id} 运维-组织模板 allGroupTemplateTreeInfo
//
// 获取组织模版所有端的子树（无实体），传入模版Key
//
//		Responses:
//			200: subtrees
func GetTemplateAllTreesInfo(c echo.Context) error {
	idStr := c.Param("id")
	//if len(idStr) == 0 {
	//	return c.JSON(http.StatusOK, protocol.WrongParams())
	//}
	id, err := strconv.Atoi(idStr)
	if err != nil || id == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	trees, err := ent.GetTemplateTrees(id)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(trees))
}

// 获取组织模版所有端子树
// swagger:route GET /v1/tree/get_template_all_tree/{id} 运维-组织模板 allGroupTemplateTree
//
// 获取组织模版所有端的子树(包含实体)，传入模版Key
//
//		Responses:
//			200: subtrees
func GetTemplateAllTree(c echo.Context) error {
	idStr := c.Param("id")
	id, err := strconv.Atoi(idStr)
	if err != nil || id == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	subtrees, err := ent.TemplateTrees(id)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(subtrees))
}

// 新增组织模板角色
// swagger:route POST /v1/tree/add_template_role 运维-组织模板 addTemplateRole
//
// 新增组织模板角色，传入模板ID和角色名称
//
//		Responses:
//			200: respAddTemplateRole
func AddTemplateRole(c echo.Context) error {
	var post struct {
		GroupTemplateKey string
		RoleName         string
	}

	if err := c.Bind(&post); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	id, err := ent.AddRoleToTemplate(post.GroupTemplateKey, post.RoleName)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(id))
}

// 删除组织模板角色
// swagger:route DELETE /v1/tree/del_template_role/{role} 运维-组织模板 delTemplateRole
//
// 删除组织模板角色，传入角色id
//
//		Responses:
//			200: responseMessage
func DelTemplateRole(c echo.Context) error {
	//template := c.Param("template")
	role := c.Param("role")
	//tempalteID, err := strconv.Atoi(template)
	//if err != nil || tempalteID <= 0 {
	//	return c.JSON(http.StatusOK, protocol.WrongParams())
	//}
	roleID, err := strconv.Atoi(role)
	if err != nil || roleID <= 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	if err := ent.DelRole(roleID, false); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取所有组织模板角色，包括系统内置角色
// swagger:route GET /v1/tree/get_template_all_roles/{id} 运维-组织模板 reqTemplateID
//
// 获取所有组织模板角色,包括系统内置角色，传入模板ID
//
//		Responses:
//			200: respTemplateRoles
func GetAllTemplateRoles(c echo.Context) error {
	idStr := c.Param("templateID")

	id, err := strconv.Atoi(idStr)
	if err != nil || id == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	roles, err := ent.GetAllTemplateRoles(id)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(roles))
}

// 修改组织模板角色名称
// swagger:route POST /v1/tree/update_template_role_name 运维-组织模板 updateTemplateRole
//
// 修改组织模板角色名称，传入模板Key和角色ID和名称
//
//		Responses:
//			200: responseMessage
func UpdateTemplateRoleName(c echo.Context) error {
	var post struct {
		GroupTemplateKey string
		RoleID           string
		RoleName         string
	}
	if err := c.Bind(&post); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := ent.UpdateTemplateRoleName(post.GroupTemplateKey, post.RoleID, post.RoleName); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.Success())
}

// 设置组织模板角色权限树
// swagger:route POST /v1/tree/set_template_role_tree 运维-组织模板 setTemplateRoleTree
//
// 设置组织模板角色权限树，角色id，端key和子树
//
//		Responses:
//			200: responseMessage
func SetTemplateRoleTree(c echo.Context) error {
	var post struct {
		RoleID string
		Tree   models.Subtree
	}
	if err := c.Bind(&post); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := ent.SetTreeToTemplateRole(post.RoleID, post.Tree); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 设置组织模板角色权限
// swagger:route POST /v1/tree/set_template_role_permission 运维-组织模板 setTemplateRolePermission
//
// 设置组织模板角色权限，传入权限组，角色id，端key，母树id
//
//		Responses:
//			200: responseMessage
func SetTemplateRolePermission(c echo.Context) error {
	var permission models.RolePermission
	if err := c.Bind(&permission); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := ent.SetRolePermission(&permission); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取组织模板角色指定端树（索引）
// swagger:route GET /v1/tree/get_template_role_tree_index/{roleID}/{template}/{terminalKey} 运维-组织模板 1016
//
// 获取组织模板角色指定端树（索引），传入角色id，端key
//
//		Responses:
//			200: respTemplateRoleTreeIndex
// TODO 废弃
func GetTemplateRoleTreeIndex(c echo.Context) error {
	role := c.Param("roleID")
	template := c.Param("template")
	terminal := c.Param("terminalKey")

	if len(role) == 0 || len(terminal) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	treeIndex, err := ent.GetTemplateRoleTreeIndex(role, template, terminal)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(treeIndex))
}

// 获取角色所有端树（索引）
// swagger:route GET /v1/tree/get_role_trees_index/{roleID} 运维-组织模板 getRoleTreesIndex
//
// 获取角色所有端树（索引），角色id
//
//		Responses:
//			200: respRoleTreesIndex
// TODO 废弃
func GetRoleTreesIndex(c echo.Context) error {
	role := c.Param("roleID")

	if len(role) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	treesIndex, err := ent.GetRoleTreesIndex(role)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	if treesIndex == nil {
		treesIndex = []models.Subtree{}
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(treesIndex))
}

// 获取角色所有端权限（索引）
// swagger:route GET /v1/tree/get_role_permissions/{roleID} 运维-组织模板 getRolePermissions
//
// 获取角色所有端树（索引），角色id
//
//		Responses:
//			200: respRolePermissions
func GetRolePermissions(c echo.Context) error {
	roleIDStr := c.Param("roleID")

	roleID, err := strconv.Atoi(roleIDStr)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	permissions, err := ent.GetRolePermissions(roleID)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	//if permissions == nil {
	//	permissions = []*models.RolePermission{}
	//}
	return c.JSON(http.StatusOK, protocol.SuccessData(permissions))
}

// 应用模板到组织
// swagger:route POST /v1/tree/set_template_to_group 运维-组织模板 reqSetTemplate2Group
//
// 应用模板到组织，会清除组织原有的模版连接和所有角色！
//
//		Responses:
//			200: responseMessage
func SetTemplate2Group(c echo.Context) error {
	var t2g struct {
		SystemType   int
		GroupID      int64
		TemplateID   int
		IncludeRoles bool
	}
	if err := c.Bind(&t2g); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	groupMap := models.YrtGroupMap{
		GroupID:    0,
		SystemType: t2g.SystemType,
		Belong:     t2g.GroupID,
	}

	gid, err := groupMap.GenGroupID()
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, protocol.FlatErrMeg(err)))
	}

	if err := ent.SetGroupTemplate(gid, t2g.TemplateID); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	//if err := ent.ClearGroupTemplate(t2g.Group, t2g.IncludeRoles); err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	//}
	//
	//if err := ent.ApplyTemplateToGroup(t2g.TemplateKey, t2g.Group, t2g.IncludeRoles); err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	//}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 应用模板到组织
// swagger:route POST /ent/org/set_group_template 运维-组织模板 reqSetGroupTemplate
//
// V3应用模板到组织，会清除组织原有的模版连接和所有角色！
//
//		Responses:
//			200: responseMessage
func V3SetGroupTemplate(c echo.Context) error {
	var t2g struct {
		Group        int64
		Template     int
		IncludeRoles bool
	}

	if err := c.Bind(&t2g); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := ent.SetGroupTemplate(t2g.Group, t2g.Template); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取组织下所有端和子树
// swagger:route GET /v1/user/get_group_all_tree 用户-角色权限 noneGetAllTree
//
// 获取组织下所有端和子树(包含实体)，不需要传入值，从session中获取
//
//		Responses:
//			200: respGroupAllTrees
func GetGroupAllTree(c echo.Context) error {
	// 读取用户
	user, err := sessionStore.GetSessionUser(c)

	//user, err := getUserFromSession(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	// 如果为平台用户，走平台处理
	//if user.UserInfo.System_type == 0 {
	//	trees, err := ent.GetTemplateAllSubtrees("management")
	//	if err != nil {
	//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	//	}
	//	return c.JSON(http.StatusOK, protocol.SuccessData(trees))
	//}
	// 如果为其他类型用户
	// group := strconv.Itoa(user.UserInfo.System_type) + strconv.Itoa(user.UserInfo.Belong)
	trees, err := ent.GroupTrees(user.GroupID())
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(trees))
}

// 获取组织下所有角色
// swagger:route GET /v1/user/get_group_roles 用户-角色权限 noneGetGroupRoles
//
// 获取组织下所有角色，不需要传入值，从session中获取
//
//		Responses:
//			200: respGroupAllRoles
func GetGroupRoles(c echo.Context) error {
	//user, err := getUserFromSession(c)
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	roles, err := ent.GetAllRoles(models.ROLE_TYPE_GROUP, user.GroupID())
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(roles))
}

// 增加角色到组织
// swagger:route POST /v1/user/add_group_role 用户-角色权限 reqAddGroupRole
//
// 组织增加角色，传入角色名称，可以重名
//
//		Responses:
//			200: respAddGroupRole
func AddGroupRole(c echo.Context) error {
	var role models.Role
	if err := c.Bind(&role); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	if len(role.Name) == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "请填写角色名称"))
	}

	role.Type = models.ROLE_TYPE_GROUP
	role.Belong = user.GroupID()
	role.ID = 0
	role.Internal = false
	id, err := ent.CreateRole(&role)
	role.ID = id

	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(role))
}

// 修改组织到角色名称
// swagger:route POST /v1/user/update_group_role 用户-角色权限 reqUpdateGroupRole
//
// 修改组织到角色名称，角色名称可以重名
//
//		Responses:
//			200: responseMessage
func UpdateGroupRole(c echo.Context) error {
	var role models.Role
	if err := c.Bind(&role); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := verifySameGroup(c, role.ID); err != nil {
		return err
	}

	if err := ent.UpdateRole(&role); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 删除组织的角色
// swagger:route DELETE /v1/user/del_group_role/{id} 用户-角色权限 noneReqDelGroupRole
//
// 删除组织角色
//
//		Responses:
//			200: responseMessage
func DelGroupRole(c echo.Context) error {
	idStr := c.Param("id")

	id, err := strconv.Atoi(idStr)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := verifySameGroup(c, id); err != nil {
		return err
	}

	if err := ent.DelRole(id, false); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 设置组织角色权限
// swagger:route POST /v1/user/set_role_permission 用户-角色权限 setGroupRolePermission
//
// 设置组织角色权限树，角色id，端key和子树
//
//		Responses:
//			200: responseMessage
func SetGroupRolePermission(c echo.Context) error {
	var permission models.RolePermission

	if err := c.Bind(&permission); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	if err := verifySameGroup(c, permission.Role); err != nil {
		return nil
	}

	// 修改角色权限
	if err := ent.SetRolePermission(&permission); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// SetGroupRolePermissions 批量设置组织角色权限
// swagger:route POST /ent/user/set_role_permissions 用户-角色权限 reqGroupRolePermissions
//
// 设置角色的权限,一次处理多树,最好是有修改的树才上传处理
// 上传Json中Permissions是数组，数组中的 "node" 和 "role" 两个字段不需要传，其它是必填
//
//		Responses:
//			200: responseMessage
func SetGroupRolePermissions(c echo.Context) error {
	var post struct {
		Role        int `json:"role"`
		Permissions []models.RolePermission
	}
	if err := c.Bind(&post); err != nil {
		err = fmt.Errorf("参数错误：%w", err)
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	if err := verifySameGroup(c, post.Role); err != nil {
		return err
	}

	for _, permission := range post.Permissions {
		permission.Role = post.Role
		if err := ent.SetRolePermission(&permission); err != nil {
			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
		}
	}

	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取组织对应的模板key
// swagger:route GET /v1/tree/get_group_template/{systemType}/{gid} 运维-组织模板 reqGetGroupTemplate
//
// 获取组织对应的模板key
//
// 		Responses:
//			200: respGetGroupTemplateKey
func GetGroupTemplate(c echo.Context) error {
	// 获取group
	systemType, err := strconv.Atoi(c.Param("systemType"))
	if err != nil || systemType <= 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "组织类型错误"))
	}
	gid, err := strconv.ParseInt(c.Param("gid"), 10, 64)
	if err != nil || gid <= 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "组织id错误"))
	}
	yrtgroup := models.YrtGroupMap{
		GroupID:    0,
		SystemType: systemType,
		Belong:     gid,
	}
	newGid, err := yrtgroup.GenGroupID()
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, protocol.FlatErrMeg(err)))
	}

	template, err := ent.GetGroupTemplate(newGid)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(template))
}

// GetUserTree 获取用户菜单实体
// swagger:route GET /v1/user/get_user_menu/{terminal} 用户-角色权限 reqGetUserTree
//
// 获取用户指定端的菜单实体，传入端key
//
// 		Responses:
//			200: respGetUserTree
func GetUserTree(c echo.Context) error {
	// 获取端
	terminal := c.Param("terminal")
	if len(terminal) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	// 获取用户类型
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	//// admin 用户，读取特殊角色: starter
	//var roleTree *models.Tree
	//if user.UserType == ryzeModels.USER_TYPE_ADMIN {
	//	roleTree, err = ent.InternalRoleTreeByName(ADMIN_ROLE_NAME, user.GroupID(), -1, terminal, true)
	//} else {
	//	var roleID int
	//	roleID, err = ent.GetRoleIDByUser(user.UserID())
	//	if err == nil {
	//		roleTree, err = ent.RoleSubtree(roleID, -1, terminal, true)
	//	}
	//}
	//
	//if err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	//}
	roleTree, err := getUserTreeByTerminal(user, terminal)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	// 返回实体树
	return c.JSON(http.StatusOK, protocol.SuccessData(roleTree.RootNode.Children))
}

// GetUserSettings 获取用户菜单实体
// swagger:route GET /v1/user/get_user_settings/{page}/{type} 用户-角色权限 reqGetUserSettings
//
// 获取用户指定端的菜单实体，传入端key
//
// 		Responses:
//			200: respGetUserSettings
func GetUserSettings(c echo.Context) error {
	// 获取端
	page := c.Param("page")
	tab := c.Param("type")
	// 获取用户类型
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	yrtSetting, err := ent.GetUserSettings(user.UserID(), page, tab)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	// 返回实体树
	return c.JSON(http.StatusOK, protocol.SuccessData(yrtSetting))
}

func StoreUserSettings(c echo.Context) error {
	var setting models.YrtUserSetting

	// 获取用户类型
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	if err := c.Bind(&setting); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	setting.UserID = user.UID

	settingID, err := ent.StoreUserSettings(&setting)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	// 返回实体树
	return c.JSON(http.StatusOK, protocol.SuccessData(settingID))
}

// GetUserTree 获取用户菜单实体和首页自定义快捷菜单
// swagger:route GET /ent/user/get_user_favourite_menu/{terminal} 用户-角色权限 reqUserFavouriteMenu
//
// 获取用户指定端的菜单实体，传入端key
//
// 		Responses:
//			200: respUserFavouriteMenu
func GetUserFavouriteMenu(c echo.Context) error {
	// 获取端
	terminal := c.Param("terminal")
	if len(terminal) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	// 获取用户类型
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	//	获取用户菜单树
	roleTree, err := getUserTreeByTerminal(user, terminal)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	//	生成首页快捷菜单
	nodes := getUserFavouriteMenu(roleTree.RootNode)
	//	返回
	//favourite := gatewayModels.FavouriteMenuAndTree{
	//	Home: nodes,
	//	Tree: roleTree.RootNode.Children,
	//}

	return c.JSON(http.StatusOK, protocol.SuccessData(nodes))
}

// GetUserMenuMap 获取用户菜单实体的Map: [content]Node
// swagger:route GET /ent/user/get_user_menu_map/{terminal} 用户-角色权限 reqUserMenuMap
//
// 获取用户指定端的菜单map，传入端key
//
// 		Responses:
//			200: respUserMenuMap
func GetUserMenuMap(c echo.Context) error {
	// 获取端
	terminal := c.Param("terminal")
	if len(terminal) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	// 获取用户类型
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	//	获取用户菜单树
	roleTree, err := getUserTreeByTerminal(user, terminal)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	nodes := treeDFS(roleTree.RootNode, 0)

	nodesMap := make(map[string]*models.Node)
	for _, node := range nodes {
		nodesMap[node.Content] = node
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(nodesMap))
}

// getUserFavouriteMenu 获取指定端首页自定义快捷菜单
// 生成用户首页快捷菜单，目前是返回用户菜单树的前7个菜单（深度遍历）
// Params:
//		tree: 用户权限树
// Return:
// 		nodes: 权限节点实体
func getUserFavouriteMenu(root *models.Node) []*models.Node {
	return treeDFS(root, 7)
}

// treeDFS 对树进行深度遍历
// 对给定的树做深度遍历，返回扁平化的结果，只返回菜单节点，
// 功能节点作为菜单节点的属性不认为是独立节点
// Params:
//		root: 用户权限树
//		max: 最多返回多少节点，0: 表示无限
//		nodes: 空节点数组，会装载符合条件的节点
// Return:
//		null
func treeDFS(root *models.Node, max int) []*models.Node {
	nodes := make([]*models.Node, 0)
	if root.Type == models.NODE_TYPE_FEATURE {
		return nodes
	}
	// 判断是否有子节点
	if len(root.Children) > 0 {
		// 有，继续
		isLeaf := true
		for _, node := range root.Children {
			// 子节点为功能节点的跳过

			//
			child := treeDFS(node, max-len(nodes))
			// 返回后节点是否小于最大值，是继续
			if len(child) > 0 {
				isLeaf = false
				nodes = append(nodes, child...)
			}
			// 否，直接返回
			if max > 0 && len(nodes) == max {
				break
			}
		}
		if isLeaf {
			if max <= 0 || len(nodes) < max {
				// 是，加入列表，并返回
				nodes = append(nodes, root)
			}
		}
	} else {
		// 无节点为叶子节点，并且数量没超过max
		if max <= 0 || len(nodes) < max {
			// 是，加入列表，并返回
			nodes = append(nodes, root)
		}
	}
	// 否，直接返回
	return nodes
}

// getUserTreeByTerminal 根据用户和指定端，返回用户对应端的全树
// Params：
//		user : session user
//		terminal: 指定端
// Return:
//		tree: 用户权限树（指定端母树的子树）
//		error: 错误返回
func getUserTreeByTerminal(user *SessionUser, terminal string) (*models.Tree, error) {
	var roleTree *models.Tree
	var err error
	// admin 用户，读取特殊角色: starter
	if user.UserType == ryzeModels.USER_TYPE_ADMIN {
		roleTree, err = ent.InternalRoleTreeByName(ADMIN_ROLE_NAME, user.GroupID(), -1, terminal, true)
	} else {
		var roleID int
		roleID, err = ent.GetRoleIDByUser(user.UserID())
		if err == nil {
			roleTree, err = ent.RoleSubtree(roleID, -1, terminal, true)
		}
	}
	if err != nil {
		return nil, err
	}
	return roleTree, nil
}

// 获取节点详细信息
// swagger:route GET /v1/tree/get_mtnode/{node} 运维-菜单树 reqGetNode
//
// 获取节点详细信息，包括feature等，传入node ID
//
// 		Responses:
// 			200: respGetNode
func GetMTNode(c echo.Context) error {

	nodeID := c.Param("node")
	if len(nodeID) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	node, err := ent.GetNodeDetailByID(nodeID)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(node))
}

// 设定用户角色
// swagger:route POST /v1/user/set_user_role 用户-角色权限 reqSetUserRole
//
// 设置用户角色，传入用户ID和角色ID
//
// 		Responses:
// 			200: responseMessage
func SetUserRole(c echo.Context) error {
	var ur struct {
		UserID int64
		RoleID int
	}
	if err := c.Bind(&ur); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	// 检查参数
	// 安全检查，用户是否该组织所属，角色是否该组织所属
	//role, err := ent.GetRole(ur.RoleID)
	//if err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	//}
	//user, err := service.GetGodByID(ur.UserID)
	//if err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	//}
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	// TODO 比对操作用户、角色、被操作用户是属于同一组织
	var id int64
	if user.Version == USER_VER_1 {
		id, err = models.YrtUser2NewID(int64(user.SystemType), ur.UserID)
		if err != nil {
			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
		}
	} else {
		id = ur.UserID
	}

	// 获取操作者
	// 获取角色
	// 获取被操作用户

	//if role.Belong != user.GroupKey() {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "未找到角色"))
	//}

	//if err := ent.SetRole2User(strconv.FormatInt(ur.UserID, 10), ur.RoleID); err != nil {
	if err := ent.SetUserRole(id, ur.RoleID); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 删除用户角色
// swagger:route DELETE /v1/user/del_user_role/{userID} 用户-角色权限 noneDelUserRole
//
// 删除用户角色，传入用户ID
//
// 		Responses:
// 			200: responseMessage
//func DelUserRole(c echo.Context) error {
//	userID := c.Param("userID")
//	if len(userID) == 0 {
//		return c.JSON(http.StatusOK, protocol.WrongParams())
//	}
//	// TODO: 安全检查，用户是否属于该组织
//
//	if err := ent.DelUserRole(userID); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
//	}
//	return c.JSON(http.StatusOK, protocol.Success())
//}

// 获取给定用户列表的对应角色列表
// swagger:route GET /v1/user/get_users_roles 用户-角色权限 reqGetUsersRoles
//
// 获取给定用户列表的对应角色列表，传入用户ID列表
//
// 		Responses:
// 			200: respGetUsersRoles
func GetUsersRoles(c echo.Context) error {
	var ids struct {
		UserIDs []string
	}
	if err := c.Bind(&ids); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	type UserRole struct {
		UserID string
		RoleID string
	}

	var urs []UserRole
	for _, id := range ids.UserIDs {
		roleID, err := ent.GetUserRoleKey(id)
		if err != nil {
			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
		}
		ur := UserRole{
			UserID: id,
			RoleID: roleID,
		}
		urs = append(urs, ur)
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(urs))
}

// 获取角色指定端从指定节点开始的子树
// swagger:route POST /v1/user/get_user_submenu 用户-角色权限 reqGetUserSubmenu
//
// 获取角色指定端从指定节点开始的子树，传入端key和开始节点id，不需要传入角色
//
// 		Responses:
// 			200: respGetUserSubmenu
func GetUserSubMenu(c echo.Context) error {
	var post struct {
		Terminal string
		NodeID   int
	}

	if err := c.Bind(&post); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "用户不存在"))
	}

	node, err := ent.RoleSubtree(user.RID, post.NodeID, post.Terminal, true)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(node))
}

// 导出母树
// swagger:route GET /v1/tree/export_tree/{id} 运维-菜单树 reqExportTree
//
// 导出指定母树，包含所有子节点，用于导入更新，传入母树id，导出为json格式文件
//
// 		Responses:
// 			200: responseMessage
func ExportTree(c echo.Context) error {
	strId := c.Param("id")

	id, err := strconv.Atoi(strId)
	if err != nil || id <= 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	tree := ent.MotherTree(id)

	if tree == nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "未找到树"))
	}

	treeFile, err := json.Marshal(tree)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	c.Response().Header().Add("Content-Disposition", "attachment; filename=tree.json")
	c.Response().Write(treeFile)

	return nil
}

// 导入母树节点（包含子节点）
// swagger:route POST /v1/tree/import_tree 运维-菜单树 reqImportTree
//
// 同步母树
//
// 		Responses:
// 			200: responseMessage
func ImportTree(c echo.Context) error {
	var tree models.Tree
	if err := c.Bind(&tree); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, protocol.FlatErrMeg(err)))
	}
	// 检查是否已有树
	// 无，新建树
	oldTree := ent.MotherTree(tree.ID)
	if oldTree == nil {
		ent.InsertTree(&tree)
	}
	if err := ent.SyncTree(&tree, oldTree); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 母树节点上升一位
// swagger:route POST /v1/tree/node_moveup/{id} 运维-菜单树 reqNodeMoveUp
//
// 母树节点排序上升，只需要传入节点id
//
// 		Responses:
// 			200: responseMessage
func NodeMoveUp(c echo.Context) error {
	id := c.Param("id")
	if len(id) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	if err := ent.MoveNode(id, true); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 母树节点下降一位
// swagger:route POST /v1/tree/node_movedown/{id} 运维-菜单树 reqNodeMoveDown
//
// 母树节点排序下降，只需要传入节点id
//
// 		Responses:
// 			200: responseMessage
func NodeMoveDown(c echo.Context) error {
	id := c.Param("id")
	if len(id) == 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	if err := ent.MoveNode(id, false); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 母树节点顺序对调
// swagger:route POST /v1/tree/node_swap_sort 运维-菜单树 reqNodeSwapSort
//
// 菜单母树的节点顺序对调
//
// 		Responses:
// 			200: responseMessage
func NodeSwapSort(c echo.Context) error {
	var swap struct {
		ID1 int
		ID2 int
	}

	if err := c.Bind(&swap); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, protocol.FlatErrMeg(err)))
	}

	if err := ent.SwapNode(swap.ID1, swap.ID2); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.Success())
}
