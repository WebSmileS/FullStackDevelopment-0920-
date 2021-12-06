package controller

import (
	"encoding/json"
	"net/http"

	"bjyixiu.com/gateway/protocol"
	"github.com/pkg/errors"

	"bjyixiu.com/ryze/models"
	"bjyixiu.com/ryze/service"
	"github.com/labstack/echo/v4"
)

// 获取所有母树
// @Summary 获取所有菜单母树
// @Description 获取所有完整母树
// @Tags 维护_菜单管理
// @Accept  json
// @Produce  json
// @Success 200 {object} models.Node[]
// @Router /menu/getall_tree [get]
func GetAllMotherTree(c echo.Context) error {
	tree, err := service.AllMotherTree()
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
		return nil
	}
	resp := protocol.SuccessMsg("查询成功")
	resp.Data = tree

	c.JSON(http.StatusOK, resp)
	return nil
}

// 获取节点和节点Feature
// @Summary 获取节点和节点Feature
// @Description 获取节点和节点Feature,用于leaf节点
// @Tags 维护_菜单管理
// @Accept  json
// @Produce  json
// @Param id path string true "节点ID"
// @Success 200 {string} models.Node ""
// @Router /menu/get_node/{id} [get]
func GetNode(c echo.Context) error {
	id := c.Param("id")
	if len(id) == 0 {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少节点ID"))
		return nil
	}

	node, err := service.GetNodeDetailByID(id)
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
		return nil
	}

	// 返回
	resp := protocol.SuccessMsg("成功")
	resp.Data = node
	return c.JSON(http.StatusOK, resp)
}

// 增加子节点
// @Summary 在母树上新增加子节点
// @Description 在母树上新增加子节点
// @Tags 维护_菜单管理
// @Accept  json
// @Produce  json
// @Param node body models.AddNode true "菜单子节点"
// @Success 200 {string} id "增加的节点ID"
// @Router /menu/add_node [post]
func AddChildrenNode(c echo.Context) error {
	var post models.AddNode
	if err := c.Bind(&post); err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
		return nil
	}

	// TODO检查

	// 处理
	id, err := service.AddNode(&post)
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
		return nil
	}
	// 返回
	resp := protocol.SuccessMsg("新增节点成功")
	resp.Data = id
	c.JSON(http.StatusOK, resp)
	return nil
}

// 修改节点
// @Summary 修改节点
// @Description 更新已经存在的节点，只更新Name(菜单名),URI(API),Icon(图标)
// @Tags 维护_菜单管理
// @Accept  json
// @Produce  json
// @Param node body models.UpdateNode true "菜单子节点"
// @Success 200 {object} models.UpdateNode
// @Router /menu/update_node [put]
func Update_Node(c echo.Context) error {
	var post models.UpdateNode
	if err := c.Bind(&post); err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
		return nil
	}

	// TODO检查

	// 处理
	err := service.UpdateNode(&post)
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
		return nil
	}
	// 返回
	resp := protocol.SuccessMsg("更新节点成功")
	resp.Data = post
	c.JSON(http.StatusOK, resp)
	return nil
}

// 删除节点
// @Summary 删除节点
// @Description 删除子节点，提交子节点ID，若节点下还有字节则不允许删除
// @Tags 维护_菜单管理
// @Accept  json
// @Produce  json
// @Param id path string true "子节点ID"
// @Success 200 {string} id "已删除的节点ID"
// @Router /menu/delete_node/{id} [delete]
func DeleteNode(c echo.Context) error {
	id := c.Param("id")
	if len(id) == 0 {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少节点ID"))
		return nil
	}

	if err := service.RemoveNode(id); err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
		return nil
	}

	// 返回
	resp := protocol.SuccessMsg("删除节点成功")
	resp.Data = id
	c.JSON(http.StatusOK, resp)
	return nil
}

// 上移节点
// @Summary 上移节点
// @Description 上移子节点
// @Tags 维护_菜单管理
// @Accept  json
// @Produce  json
// @Param ids body models.UpdateChildrenOrder true "菜单节点ID和要上移的子节点ID"
// @Success 200 {string} string "ok"
// @Router /menu/moveup_node [put]
func MoveUpNode(c echo.Context) error {
	ids := new(models.UpdateChildrenOrder)
	if err := c.Bind(ids); err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
		return nil
	}

	if err := service.ChildrenMoveUp(ids); err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
		return nil
	}

	resp := protocol.SuccessMsg("上移节点成功")
	c.JSON(http.StatusOK, resp)
	return nil
}

// 下移节点
// @Summary 下移节点
// @Description 下移子节点
// @Tags 维护_菜单管理
// @Accept  json
// @Produce  json
// @Param ids body models.UpdateChildrenOrder true "菜单节点ID和要下移的子节点ID"
// @Success 200 {string} string "ok"
// @Router /menu/movedown_node [put]
func MoveDownNode(c echo.Context) error {
	ids := new(models.UpdateChildrenOrder)
	if err := c.Bind(ids); err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
		return nil
	}

	if err := service.ChildrenMoveDown(ids); err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
		return nil
	}

	resp := protocol.SuccessMsg("下移节点成功")
	return c.JSON(http.StatusOK, resp)
}

// 启用所有子节点（包括自己）
// @Summary 启用所有子节点（包括自己）
// @Description <运维>启用所有子节点（包括自己）
// @Tags 维护_菜单管理
// @Accept  json
// @Produce  json
// @Param id path string true "节点ID"
// @Success 200 {string} string "ok"
// @Router /menu/enable_tree/{id} [put]
func EnableTree(c echo.Context) error {
	id := c.Param("id")
	if len(id) == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少节点ID"))
	}

	root, err := service.GetNodeByID(id)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, errors.WithStack(err).Error()))
	}

	if err := service.EnableChildrenNode(root); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, errors.WithStack(err).Error()))
	}

	resp := protocol.SuccessMsg("成功")
	return c.JSON(http.StatusOK, resp)
}

// 导出菜单母树
// @Summary 导出菜单母树
// @Description 导出菜单母树
// @Tags 维护_菜单管理
// @Accept  json
// @Produce  json
// @Success 200 {object} models.Node[]
// @Router /menu/export_all_tree [get]
func ExportAllMotherTree(c echo.Context) error {
	forest, err := service.AllMotherTree()
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
		return nil
	}

	file, _ := json.MarshalIndent(forest, "", "")
	//_ = ioutil.WriteFile("forest.json", file, 0644)

	//resp := protocol.SuccessMsg("查询成功")
	//resp.Data = forest
	return c.Blob(http.StatusOK, "text/csv", file)
	//c.JSON(http.StatusOK, resp)
}

// 获取系统固定角色列表
// @Summary 获取系统固定角色列表
// @Description <平台运维>获取系统固定角色列表
// @Tags 运维_机构管理
// @Accept  json
// @Produce  json
// @Success 200 {object} models.Role[]
// @Router /menu/get_system_role_list [get]
//func GetSystemRoleList(c echo.Context) error {
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//	if oprator.UserInfo.System_type != models.TYPE_PLATFORM {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "无权访问"))
//	}
//
//	resp := protocol.SuccessMsg("获取成功")
//	resp.Data = service.GetSystemManagerRoles()
//
//	return c.JSON(http.StatusOK, resp)
//}

// 更新系统设定角色权限(菜单/功能)树
// @Summary 更新系统设定角色权限(菜单/功能)树
// @Description <平台运维>更新系统设定角色权限(菜单/功能)树
// @Tags 运维_机构管理
// @Accept  json
// @Produce  json
// @Param node body models.UpdateSystemRoleTree true "菜单/功能树"
// @Success 200 {string} string "成功/失败信息"
// @Router /menu/update_system_role_tree [put]
//func UpdateSystemRoleTree(c echo.Context) error {
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	roleTree := new(models.UpdateSystemRoleTree)
//	if err := c.Bind(roleTree); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	// 校验，只有平台能够操作
//	if oprator.UserInfo.System_type != models.TYPE_PLATFORM {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "未找到角色"))
//	}
//
//	if err := service.UpdateSystemManagerRoleTree(roleTree.RID, roleTree.Tree); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	return c.JSON(http.StatusOK, protocol.SuccessMsg("修改成功"))
//}

// 获取系统固定角色角色权限树(仅ID)
// @Summary 获取系统固定角色权限树(仅ID)
// @Description <平台运维>获取系统固定角色权限树(仅ID)
// @Tags 运维_机构管理
// @Accept  json
// @Produce  json
// @Param id path int true "角色id"
// @Success 200 {object} models.RoleTreeNode
// @Router /menu/get_system_role_tree_ids/{id} [get]
//func GetSystemRoleTreeIDs(c echo.Context) error {
//	rid := c.Param("id")
//	if len(rid) == 0 {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "参数错误"))
//	}
//
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	if oprator.UserInfo.System_type != models.TYPE_PLATFORM {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "无权操作"))
//	}
//
//	root, err := service.GetRoleTreeIDsByID(rid)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	resp := protocol.SuccessMsg("成功")
//	resp.Data = root
//	return c.JSON(http.StatusOK, resp)
//}
