package models

import (
	"bjyixiu.com/proto"
)

// 增加/更新端
// swagger:parameters addTerminal updateTerminal
type terminalInfo struct {
	// Required: true
	// in: body
	terminal Terminal
}

// 删除端
// swagger:parameters delTerminal terminalKey
type terminalKey struct {
	// Required: true
	// in: path
	key string
}

// 获取/删除树
// swagger:parameters getMotherTree
type treeKey struct {
	// Required: true
	// in: path
	id string
}

// 新建/修改母树
// swagger:parameters motherTree
type motherTree struct {
	// Required: true
	// in: body
	tree Tree
}

// swagger:response respTree
type respMotherTree struct {
	// Required: true
	// in: body
	node Node
}

// 所有terminal
// swagger:response respAllTerminal
type respAllTerminal struct {
	//in: body
	allTerminal struct {
		proto.ResponseCommon
		Data []Terminal
	}
}

// 所有terminal--tree的树
// swagger:response respTerminalTree
type respTerminalTree struct {
	// in: body
	//allTree []TerminalTree
}

// 新建/修改母树
// swagger:parameters addTree updateTree treeInfo
type reqTreeInfo struct {
	// Required: true
	// in: body
	//tree TreeInfo
}

// 删除母树
// swagger:parameters delTree getTree
type delTree struct {
	// Required: true
	// in: path
	terminal string
	// Required: true
	// in: path
	key string
}

// 所有母树
// swagger:response respTerminalTrees
type respTerminalTrees struct {
	//in: body
	trees struct {
		proto.ResponseCommon
		//Data []TreeInfo
	}
}

// 新增Node
// swagger:parameters addNode
type nodeInfo struct {
	// Required: true
	// in: body
	node Node
}

// 新增Node，返回新Node的id值
// swagger:response respAddNode
type respAddNode struct {
	//in: body
	addNode struct {
		proto.ResponseCommon
		// 返回新增节点id，如：'bnkrr97budgp29vdern0'
		Data string
	}
}

// 修改Node
// swagger:parameters updateNode
type updateNode struct {
	//in: body
	node struct {
		// Required: true
		// node id
		ID string
		// Required: true
		Name string
		// Required: true
		Icon string
		// Required: true
		URI string
		// Required: true
		Desc string
		// Required: true
		State int
	}
}

// 删除Node
// swagger:parameters delNode
type delNode struct {
	// Required: true
	//in: path
	id string
}

// 所有组织模版
// swagger:response allGroupTemplate
type allGroupTemplate struct {
	//in: body
	templates []Template
}

// 组织模版
// swagger:parameters addTemplate updateTemplate
type groupTemplateInfo struct {
	//in: body
	template Template
}

// 组织模版key
// swagger:parameters delGroupTemplate allGroupTemplateTree allGroupTemplateTreeInfo
type groupTemplateKey struct {
	//in: path
	key string
}

// 子树索引
// swagger:parameters delTemplateTree getTemplateTree getTemplateTreeIndex
type subtreeIndex struct {
	//in: path
	// Required: true
	templateKey string
	//in: path
	// Required: true
	terminalKey string
}

// 子树索引
// swagger:parameters getTemplatePermission
type getTemplatePermission struct {
	//in: path
	// Required: true
	template string
	// Required: true
	terminal string
}

// 设置子树索引d
// swagger:parameters setTemplateTree
type templateTree struct {
	//in: body
	templateTerminalTree struct {
		GroupTemplateKey string
		TerminalKey      string
		Subtree          struct {
			Mother string
			Tree   []Node
		}
	}
}

// 子树实体/索引
// swagger:response subtree
type respSubtree struct {
	//in: body
	templateTree struct {
		proto.ResponseCommon
		Data Subtree
	}
}

// 子树索引
// swagger:response subtreeIndex
type respSubtreeIndex struct {
	//in: body
	templateTree struct {
		proto.ResponseCommon
		Data Subtree
	}
}

// 增加组织模板角色
// swagger:parameters addTemplateRole
type addTemplateRole struct {
	//in: body
	templateRole struct {
		GroupTemplateKey string
		RoleName         string
	}
}

// 指定组织模板角色
// swagger:parameters delTemplateRole
type reqTemplateRole struct {
	//in: path
	// Required: true
	GroupTemplateKey string
	// Required: true
	RoleID string
}

// 修改组织模板角色
// swagger:parameters templateRole updateTemplateRole
type reqUpdateTemplateRole struct {
	//in: body
	templateRole struct {
		GroupTemplateKey string
		RoleID           string
		RoleName         string
	}
}

// 增加组织模板角色
// swagger:response respAddTemplateRole
type respAddTemplateRole struct {
	//in: body
	addTemplateRole struct {
		proto.ResponseCommon
		Data string
	}
}

// 组织模版ID
// swagger:parameters templateID getAllTemplateRoles
type templateID struct {
	//in: path
	groupTemplateKey string
}

// 组织模板角色树
// swagger:parameters setTemplateRoleTree
type setTemplateRoleTree struct {
	//in: body
	templateRoleTree struct {
		RoleID  string
		Subtree struct {
			Mother string
			Tree   []string
		}
	}
}

// 指定组织模板角色指定端树索引
// swagger:parameters getTemplateRoleTreeIndex
type getTemplateRoleTreeIndex struct {
	//in: path
	// Required: true
	RoleID string
	// Required: true
	TerminalKey string
}

// 指定组织模板角色指定端树索引返回
// swagger:response respTemplateRoleTreeIndex
type respTemplateRoleTreeIndex struct {
	//in: body
	templateRoleTreeIndex struct {
		proto.ResponseCommon
		Data Subtree
	}
}

// 角色所有端树索引
// swagger:parameters getRoleTreesIndex
type getRoleTreesIndex struct {
	//in: path
	// Required: true
	RoleID string
}

// 角色所有端树索引返回
// swagger:response respRoleTreesIndex
type respRoleTreesIndex struct {
	//in: body
	roleTreesIndex struct {
		proto.ResponseCommon
		Data []Subtree
	}
}

// 应用模板到组织
// swagger:parameters reqSetTemplate2Group
type setTemplate2Group struct {
	//in: body
	template2Group struct {
		Group        string
		TemplateKey  string
		IncludeRoles bool
	}
}

// 增加组织角色
// swagger:parameters reqAddGroupRole
type reqAddGroupRole struct {
	//in: body
	addGroupRole struct {
		// Required: true
		Name string
	}
}

// 设置模板权限
// swagger:parameters setTemplatePermission
type setTemplatePermission struct {
	//in: body
	TemplatePermission TemplatePermission
}

// 设置模板角色权限
// swagger:parameters setTemplateRolePermission
type setTemplateRolePermission struct {
	//in: body
	RolePermission RolePermission
}

// 设置组织角色权限
// swagger:parameters setGroupRolePermission
type setGroupRolePermission struct {
	//in: body
	RolePermission RolePermission
}

// 获取组织对应的模板key返回
// swagger:response respGetGroupTemplateKey
type respGetGroupTemplateKey struct {
	//in: body
	getGroupTemplateKey struct {
		proto.ResponseCommon
		Data string
	}
}

// 获取节点详细信息
// swagger:parameters reqGetNode
type reqGetNode struct {
	//in: path
	//Required: true
	Node string
}

// 获取节点详细信息返回
// swagger:response respGetNode
type respGetNode struct {
	//in: body
	getNode struct {
		proto.ResponseCommon
		Data Node
	}
}

// 获取给定用户列表的对应角色列表
// swagger:parameters reqGetUsersRoles
type reqGetUsersRoles struct {
	//in: body
	getUsersRoles struct {
		UserIDs []string
	}
}

// 获取给定用户列表的对应角色列表返回
// swagger:response respGetUsersRoles
type respGetUsersRoles struct {
	//in: body
	getUsersRoles struct {
		proto.ResponseCommon
		Data []struct {
			UserID int64
			RoleID string
		}
	}
}

// 获取角色指定端从指定节点开始的子树
// swagger:parameters reqGetUserSubmenu
type reqGetUserSubmenu struct {
	// in: body
	getUserSubmenu struct {
		Terminal string
		NodeID   string
	}
}

// 获取角色指定端从指定节点开始的子树返回
// swagger:response respGetUserSubmenu
type respGetUserSubmenu struct {
	// in: body
	getUserSubmenu struct {
		proto.ResponseCommon
		Data []Node
	}
}

// 母树节点排序上升一位
// swagger:parameters reqNodeMoveUp
type reqNodeMoveUp struct {
	// in: path
	// Required: true
	id string
}

// 母树节点排序上升一位
// swagger:parameters reqNodeMoveDown
type reqNodeMoveDown struct {
	// in: path
	// Required: true
	id string
}
