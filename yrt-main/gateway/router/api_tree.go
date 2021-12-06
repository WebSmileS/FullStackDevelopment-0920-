package router

import (
	"bjyixiu.com/gateway/controller"
	"github.com/labstack/echo/v4"
)

func treeRouter(e *echo.Group) {
	// add a terminal
	e.POST("/tree/add_terminal", controller.AddTerminal)
	// del a terminal
	e.DELETE("/tree/del_terminal/:key", controller.DelTerminal)
	// update a terminal（name）
	e.POST("/tree/update_terminal", controller.UpdateTerminalName)
	// get all terminals
	e.GET("/tree/getall_terminal", controller.GetAllTerminal)
	// get all trees by terminal
	e.GET("/tree/get_terminal_trees/:terminalKey", controller.GetTreesByTerminal)
	// get all terminals and trees
	e.GET("/tree/getall_terminal_tree", controller.GetAllTerminalTree)
	// add a mother tree
	e.POST("/tree/add_tree", controller.CreateMotherTree)
	// update a mother tree
	e.POST("/tree/update_tree", controller.UpdateMotherTree)
	// del a mother tree
	e.DELETE("/tree/del_tree/:id", controller.DeleteMotherTree)
	// add a node
	e.POST("/tree/add_node", controller.AddNode)
	// update a node
	e.POST("/tree/update_node", controller.UpdateNode)
	// delete a node
	e.DELETE("/tree/del_node/:id", controller.DelNode)
	// get a tree by id
	e.GET("/tree/get_tree/:id", controller.GetTreeByID)
	// get a mother tree by terminal & key
	e.GET("/tree/get_mother_tree/:id", controller.GetMotherTreeByID)
	// get all group template
	e.GET("/tree/get_all_template", controller.GetAllTemplates)
	// add a template
	e.POST("/tree/add_template", controller.CreateTemplate)
	// update a template
	e.POST("/tree/update_template", controller.UpdateTemplate)
	// delete a template
	e.DELETE("/tree/del_template/:id", controller.DelTemplate)
	// set tree to template TODO: 等待删除
	//e.POST("/tree/set_template_tree", controller.SetTemplatePermission)
	// set template permission
	e.POST("/tree/set_template_permission", controller.SetTemplatePermission)
	// get template permissionget_template_all_roles
	e.GET("/tree/get_template_permission/:template/:terminal", controller.GetTemplatePermission)
	// del tree from template
	e.DELETE("/tree/del_template_tree/:templateKey/:terminalKey", controller.DelTreeFromTemplate)
	// get template tree
	e.GET("/tree/get_template_tree/:templateKey/:terminalKey", controller.GetTemplateTree)
	//// get template tree index
	//e.GET("/tree/get_template_tree_index/:templateKey/:terminalKey", controller.GetTemplateTreeIndex)
	// get template trees info
	e.GET("/tree/get_template_all_tree_info/:id", controller.GetTemplateAllTreesInfo)
	// get template trees
	e.GET("/tree/get_template_all_tree/:id", controller.GetTemplateAllTree)
	// add role to template
	e.POST("/tree/add_template_role", controller.AddTemplateRole)
	// del role from template
	e.DELETE("/tree/del_template_role/:templateKey/:roleID", controller.DelTemplateRole)
	// update role'name from template
	e.POST("/tree/update_template_role_name", controller.UpdateTemplateRoleName)
	// get roles list from template
	//e.GET("/tree/get_template_roles/:id", controller.GetTemplateRoles)
	// get all roles from template include internal role
	e.GET("/tree/get_template_all_roles/:templateID", controller.GetAllTemplateRoles)
	// set tree to template role
	e.POST("/tree/set_template_role_tree", controller.SetTemplateRoleTree)
	// set permissions to template role
	e.POST("/tree/set_template_role_permission", controller.SetTemplateRolePermission)
	// get tree index from template role
	e.GET("/tree/get_template_role_tree_index/:roleID/:terminalKey", controller.GetTemplateRoleTreeIndex)
	// get role's permissions
	e.GET("/tree/get_role_permissions/:roleID", controller.GetRolePermissions)
	// get all tree index from role
	e.GET("/tree/get_role_trees_index/:roleID", controller.GetRoleTreesIndex)
	// get group's template key
	e.GET("/tree/get_group_template/:systemType/:gid", controller.GetGroupTemplate)
	// apply template to group
	e.POST("/tree/set_template_to_group", controller.SetTemplate2Group)
	// get mother tree node detail
	e.GET("/tree/get_mtnode/:node", controller.GetMTNode)
	// export subtree from mother tree
	e.GET("/tree/export_tree/:id", controller.ExportTree)
	// import subtree
	e.POST("/tree/import_tree", controller.ImportTree)
	// node move up
	e.POST("/tree/node_moveup/:id", controller.NodeMoveUp)
	// node move down
	e.POST("/tree/node_movedown/:id", controller.NodeMoveDown)
	// node swap
	e.POST("/tree/node_swap", controller.NodeSwapSort)
	// trans v1 nodes to v2 nodes
	//e.GET("/tree/convert_v1_v2", controller.Convertv1tov2)
	//  upgrade catalogue to leaf node
	//e.GET("/tree/convert_cat_leaf", controller.UpgradeCat2Leaf)

	// generate ID
	//e.GET("/tree/new_id", controller.NewID)
}
