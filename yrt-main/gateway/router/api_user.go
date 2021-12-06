package router

import (
	"bjyixiu.com/gateway/controller"
	"github.com/labstack/echo/v4"
)

func v3UserRouter(e *echo.Group) {
	// v3登录
	e.POST("/login", controller.LoginV3)
	// 获取自身信息
	e.GET("/user/self_info", controller.V3GetSelfInfo)
	// 登出
	e.Any("/logout", controller.Logout)

	// 新建或修改 用户账号
	e.PUT("/account/save/:id", controller.V3SaveAccount)
	// 删除用户账号
	e.DELETE("/account/delete/:id", controller.V3DeleteUser)
	// 获取用户列表
	e.POST("/org/employee/list", controller.V3GetEmployeeList)

	// 设置角色权限
	e.POST("/user/set_role_permissions", controller.SetGroupRolePermissions)

	// 设置组织模版
	e.POST("/org/set_group_template", controller.V3SetGroupTemplate)

	// 获取用户的首页菜单和完整菜单树
	e.GET("/user/get_user_favourite_menu/:terminal", controller.GetUserFavouriteMenu)
	// 获取用户的叶子节点map
	e.GET("/user/get_user_menu_map/:terminal", controller.GetUserMenuMap)

	//	财务导出excel
	//e.POST("/business/finance/grn/export", controller.ExportExcel)
	//e.POST("/business/finance/odo/export", controller.ExportExcel)
}

func userRouter(e *echo.Group) {
	//	登录
	e.POST("/login", controller.Login)
	//	移动端登录
	e.POST("/mobile_login", controller.MobileLogin)
	// 重新获取本用户信息
	e.GET("/user/user_info", controller.GetSelfInfo)
	// 登出
	e.POST("/logout", controller.Logout)
	// 修改自己密码
	e.PUT("/user/change_my_password", controller.ChangeMyPassword)
	// 重置自己密码
	//e.POST("/user/reset_my_password", controller.ResetMyPassword)
	// 管理员重置用户密码
	e.POST("/user/reset_password/:id", controller.ResetPassword)
	// 管理员获取用户列表
	e.GET("/user/get_user_list", controller.GetUserList)
	// 管理员修改用户登录名(手机)
	e.PUT("/user/change_username", controller.ChangeUserName)
	// 管理员删除用户
	e.DELETE("/user/delete_user/:id", controller.DeleteUser)
	// 管理员获取角色列表
	//e.GET("/user/get_role_list", controller.GetRoleList)
	// 管理员添加角色
	//e.POST("/user/add_role", controller.AddRole)
	// 管理员修改角色信息
	//e.PUT("/user/update_role", controller.UpdateRole)
	// 管理员删除角色
	//e.DELETE("/user/delete_role/:id", controller.DeleteRole)
	// 管理员更新(新建)角色权限树
	//e.PUT("/user/set_role_permission", controller.UpdateRoleTree)
	// 管理员获取角色菜单树
	//e.GET("/user/get_role_tree/:id", controller.GetRoleTree)
	// 管理员获取角色权限树(仅ID)
	//e.GET("/user/get_role_tree_ids/:id", controller.GetRoleTreeIDs)
	// 管理员设定用户角色
	//e.PUT("/user/bind_role", controller.BindRole)
	// 管理员踢出用户，限制只能平台用户
	e.POST("/user/kick_user/:id", controller.KickUser)
	// 用户找回密码，发送验证码
	e.GET("/user/retrieve_pwd/code/:mobile", controller.GetBackPwdCode)
	// 用户找回密码，验证验证码
	e.POST("/user/retrieve_pwd/verify_code", controller.GetBackPwdVerifyCode)
	// 用户找回密码，修改密码
	e.POST("/user/retrieve_pwd/change_password", controller.GetBackPwdChangePassword)

	// 管理员获取所有权限树
	e.GET("/user/get_group_all_tree", controller.GetGroupAllTree)
	// 获取组织下所有角色（不包括系统内置角色）
	e.GET("/user/get_group_roles", controller.GetGroupRoles)
	// 管理员新建角色
	e.POST("/user/add_group_role", controller.AddGroupRole)
	// 管理员修改角色名称
	e.POST("/user/update_group_role", controller.UpdateGroupRole)
	// 管理员删除角色
	e.DELETE("/user/del_group_role/:id", controller.DelGroupRole)
	// 管理员设置角色权限树
	e.POST("/user/set_role_permission", controller.SetGroupRolePermission)
	// 用户获取自己的菜单树
	e.GET("/user/get_user_menu/:terminal", controller.GetUserTree)
	// 获取角色权限
	e.GET("/user/get_role_permissions/:id", controller.GetGroupRolePermissions)
	// 设定用户角色
	e.POST("/user/set_user_role", controller.SetUserRole)
	// 获得给定用户列表的对应角色列表
	e.GET("/user/get_users_roles", controller.GetUsersRoles)
	// 删除用户角色
	e.DELETE("/user/del_user_role/:userID", controller.DelUserRole)
	// 获取用户指定端指定节点开始
	e.POST("/user/get_user_submenu", controller.GetUserSubMenu)

	// 清除所有session
	//e.GET("/user/clear_session", controller.ClearSession)
	//e.GET("/user/get_user_settings/:page/:type", controller.GetUserSettings)
	//e.POST("/user/store_user_settings", controller.StoreUserSettings)
}
