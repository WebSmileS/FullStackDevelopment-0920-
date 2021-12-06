package models

import (
	entModels "bjyixiu.com/ent/models"
	"bjyixiu.com/proto"
	"bjyixiu.com/ryze/models"
)

// 接口访问原始日志参数
//swagger:parameters reqHttpLogID reqTemplateID reqDelTemplate reqExportTree reqGetGroupTemplate
type reqIDInPath struct {
	// Required: true
	// in: path
	id string
}

//swagger:parameters reqGetGroupTemplate
type reqGetGroupTemplateInPath struct {
	// Required: true
	// in: path
	systemType int
	gid        int
}

// 接口访问原始日志返回
// swagger:response respHttpLogInfo
type respHttpLogInfo struct {
	// in: body
	httpLogInfo HttpLogInfo
}

// 错误返回
// swagger:response respHttpError
type respHttpError struct {
	// in: body

}

// 对象ID
// swagger:parameters reqImgID reqAttID reqImgTimeID reqAttTimeID reqSaveAccountID reqDeleteAccount
type requestIDInPath struct {
	//	Required: true
	//	in: path
	id string
}

// 对象Token和ID
// swagger:parameters reqImgWithToken reqAttWithToken
type requestIDInPathWithToken struct {
	//	Required: true
	//	in: path
	token string
	id    string
}

// 对象下载地址和过期时间
// swagger:response respObjectUrlTime
// in: body
type respObjectUrlTime struct {
	//in: body
	objectUrlTime struct {
		proto.ResponseCommon
		Data DownloadUrlAndTime `json:"data"`
	}
}

// 对象上传Token
// swagger:response respObjectUpToken
type respObjectUpToken struct {
	//	in: body
	objectUpToken struct {
		proto.ResponseCommon
		Data string `json:"data"`
	}
}

// 对象上传Token和过期时间
// swagger:response respObjectTokenTime
type respObjectTokenTime struct {
	// in: body
	objectTokenTime struct {
		proto.ResponseCommon
		Data UploadUrlAndTime `json:"data"`
	}
}

// 用户信息
// swagger:response respUserInfo
type respUserInfo struct {
	// in: body
	userInfo struct {
		proto.ResponseCommon
		Data V3UserInfo `json:"userInfo"`
	}
}

// 用户登录
// swagger:parameters reqUserLogin
type reqUserLogin struct {
	//	in: body
	userLogin struct {
		UserLogin
	}
}

// 建立/修改用户账号
// swagger:parameters reqSaveAccount
type reqSaveAccount struct {
	//	in: body
	saveAccount struct {
		V3UserAccount
	}
}

// 建立/修改用户账号
// swagger:response respSaveAccount
type respSaveAccount struct {
	//	in: body
	saveAccount struct {
		proto.ResponseCommon
		Data V3UserAccount `json:"data"`
	}
}

// 建立用户账号成功返回
// swagger:response respCreateUser
type respCreateUser struct {
	//	in: body
	createUser struct {
		proto.ResponseCommon
		Data string `json:"data"`
	}
}

// 删除用户账号
// swagger:response respDeleteUser
type respDeleteUser struct {
	//	in: body
	deleteUser struct {
		proto.ResponseCommon
	}
}

// 组织的树（实体）列表
// swagger:response respGroupAllTrees
type respGroupAllTrees struct {
	//in: body
	groupAllTrees struct {
		proto.ResponseCommon
		Data []entModels.Tree `json:"data"`
	}
}

// 组织下所有角色返回
// swagger:response respGroupAllRoles
type respGroupAllRoles struct {
	//in: body
	groupAllRoles struct {
		proto.ResponseCommon
		Data []entModels.GroupRole `json:"data"`
	}
}

// 增加组织角色返回
// swagger:response respAddGroupRole
type respAddGroupRole struct {
	//in: body
	addGroupRole struct {
		proto.ResponseCommon
		Data entModels.Role `json:"data"`
	}
}

// 角色所有端权限
// swagger:response respRolePermissions
type respRolePermissions struct {
	//in: body
	rolePermissions struct {
		proto.ResponseCommon
		Data []entModels.RolePermission `json:"data"`
	}
}

// 角色所有端权限
// swagger:parameters getRolePermissions getGroupRolePermissions
type getRolePermissions struct {
	//in: path
	// Required: true
	RoleID string
}

// 设置用户角色
// swagger:parameters reqSetUserRole
type reqSetUserRole struct {
	//in: body
	setUserRole struct {
		UserID int64 `json:"userId"`
		RoleID int   `json:"roleId"`
	}
}

// 修改组织角色名称
// swagger:parameters reqUpdateGroupRole
type reqUpdateGroupRole struct {
	//in: body
	updateGroupRole struct {
		ID   string `json:"id"`
		Name string `json:"name"`
	}
}

// 组织模版角色列表
// swagger:response respTemplateRoles
type respTemplateRoles struct {
	//in: body
	templateRoles struct {
		proto.ResponseCommon
		Data []entModels.Role
	}
}

// 母树实体
// swagger:response respExportTree
type respExportTree struct {
	//in: body
	exportTree struct {
		proto.ResponseCommon
		Data entModels.Tree
	}
}

// 导入母树
// swagger:parameters reqImportTree
type reqImportTree struct {
	// in: body
	importTree struct {
		entModels.Tree
	}
}

// 设置角色的权限（多树）
// swagger:parameters reqGroupRolePermissions
type reqGroupRolePermissions struct {
	//	in: body
	groupRolePermissions struct {
		Role        int
		Permissions []entModels.RolePermission
	}
}

// 设置组织的模板
// swagger:parameters reqSetGroupTemplate
type reqSetGroupTemplate struct {
	//	in: body
	setGroupTemplate struct {
		Group        int64
		Template     int
		IncludeRoles bool
	}
}

// 母树菜单上的节点顺序对调
type reqNodeSwapSort struct {
	// in: body
	nodeSwapSort struct {
		ID1 int
		ID2 int
	}
}

// 端参数
// swagger:parameters reqGetUserTree reqUserFavouriteMenu reqUserMenuMap
type TerminalInPath struct {
	//in: path
	//Required: true
	Terminal string
}

// 获取用户菜单实体返回
// swagger:response respGetUserTree
type respGetUserTree struct {
	//in: body
	getUserTree struct {
		proto.ResponseCommon
		Data models.Node
	}
}

// 获取用户菜单实体和首页自定义快捷菜单 返回
// swagger:response respUserFavouriteMenu
type respUserFavouriteMenu struct {
	//in: body
	getUserFavouriteMenu struct {
		proto.ResponseCommon
		Data []*entModels.Node
	}
}

// 获取用户菜单实体和首页自定义快捷菜单 返回
// swagger:response respUserMenuMap
type respUserMenuMap struct {
	//in: body
	getUserMenuMap struct {
		proto.ResponseCommon
		Data map[string]*entModels.Node
	}
}
