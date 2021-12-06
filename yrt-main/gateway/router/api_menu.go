package router

import (
	"bjyixiu.com/gateway/controller"
	"github.com/labstack/echo/v4"
)

func menuRouter(e *echo.Group) {
	// 权限菜单相关路由
	// 获取所有端菜单
	e.GET("/menu/getall_tree", controller.GetAllMotherTree)
	// 获取节点详细
	e.GET("/menu/get_node/:id", controller.GetNode)
	// 增加菜单子节点
	e.POST("/menu/add_node", controller.AddChildrenNode)
	// 修改菜单子节点
	e.PUT("/menu/update_node", controller.UpdateNode)
	// 删除子节点，必须没有Children的情况才能删除
	e.DELETE("/menu/delete_node/:id", controller.DeleteNode)
	// 菜单节点排序往上
	e.PUT("/menu/moveup_node", controller.MoveUpNode)
	// 菜单节点排序往下
	e.PUT("/menu/movedown_node", controller.MoveDownNode)
	// 获取用户菜单
	//e.GET("/menu/user_menu", controller.GetUserMenu)
	//// 获取用户所属组织菜单
	//e.GET("/menu/org_menu", controller.GetOrgMenu)
	//// 获取指定组织菜单
	//e.GET("/menu/org_menu/:type", controller.GetOrgMenu)
	// 启用菜单树
	e.PUT("/menu/enable_tree/:id", controller.EnableTree)
	// 导出所有菜单树
	e.GET("/menu/export_all_tree", controller.ExportAllMotherTree)
	// 获取系统固定角色列表
	//e.GET("/menu/get_system_role_list", controller.GetSystemRoleList)
	//// 修改系统固定角色菜单/权限树
	//e.PUT("/menu/update_system_role_tree", controller.UpdateSystemRoleTree)
	//// 获取系统固定角色菜单/权限树，ID
	//e.GET("/menu/get_system_role_tree_ids/:id", controller.GetSystemRoleTreeIDs)
}
