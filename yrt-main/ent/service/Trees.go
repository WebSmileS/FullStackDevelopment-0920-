package service

import (
	"errors"
	"fmt"
	"sync"

	mapset "github.com/deckarep/golang-set"

	"bjyixiu.com/ent/models"
)

type Trees struct {
	storage Persistence
	//MotherTrees map[int]*models.Tree
	MotherTrees sync.Map
}

// 初始化服务
// Params:
//		Persistence: 数据库服务
// Return:
//		error: 错误返回
func (service *EntService) InitTreesService(persistence Persistence, updateTree, delTree chan int) error {
	//service.MotherTrees = make(map[int]*models.Tree)
	//service.MotherTrees = new(sync.Map)
	service.storage = persistence
	if err := service.LoadMotherTrees(); err != nil {
		return fmt.Errorf("初始化树服务: %w", err)
	}
	go service.updateCache(updateTree, delTree)
	return nil
}

// 等待更新树
// Params:
//		chanTreeID: 更新channel
// Return:
//		error: 错误返回
func (service *Trees) updateCache(updateTree, delTree chan int) {
	for {
		select {
		case id := <-delTree:
			service.unloadMotherTree(id)
		case id := <-updateTree:
			service.LoadMotherTree(id)
		}
	}
}

// 加载所有母树
// Params:
//		none
// Return:
//		error: 错误返回
func (service *Trees) LoadMotherTrees() error {
	// 获取所有端信息
	terminals, err := service.storage.GetAllTerminal()
	if err != nil {
		return fmt.Errorf("缓存母树: %w", err)
	}
	// 获取所有母树id
	trees, err := service.storage.GetAllTrees()
	if err != nil {
		return fmt.Errorf("缓存母树: %w", err)
	}
	// 获取母树实体
	for index, _ := range trees {
		for _, terminal := range terminals {
			if terminal.Key == trees[index].Terminal {
				trees[index].TerminalName = terminal.Name
				break
			}
		}
		trees[index].RootNode, err = service.storage.GetTreeByID(trees[index].Root, -1)
		if err != nil {
			return fmt.Errorf("缓存母树, 读取树实体: %w", err)
		}
		//service.MotherTrees[trees[index].ID] = &trees[index]
		service.MotherTrees.Store(trees[index].ID, &trees[index])
	}
	return nil
}

// 重新加载母树（单颗）
// 从数据库重新加载，指定的母树
// Params:
//		treeID : 母树id
// Return:
//		error: 错误返回
func (service *Trees) LoadMotherTree(treeID int) error {
	tree, err := service.storage.GetMotherTreeByID(treeID)
	if err != nil {
		return fmt.Errorf("加载母树失败:%w", err)
	}
	terminal, err := service.storage.GetTerminalByKey(tree.Terminal)
	if err != nil {
		return fmt.Errorf("加载母树，获取端信息失败: %w", err)
	}
	tree.TerminalName = terminal.Name
	rootNode, err := service.storage.GetTreeByID(tree.Root, 0)
	if err != nil {
		return fmt.Errorf("加载母树，获取母树实体失败:%w", err)
	}
	tree.RootNode = rootNode

	service.MotherTrees.Store(tree.ID, tree)
	//service.MotherTrees[tree.ID] = tree
	return nil
}

// 卸载母树
// 从缓存中卸载母树
// Params:
//		treeID : 母树id
// Return:
//		none
func (service *Trees) unloadMotherTree(treeID int) {
	service.MotherTrees.Delete(treeID)
	//delete(service.MotherTrees, treeID)
}

// 装载母树
// 从缓存中装载母树
// params:
// 		tree : 母树(信息和实体)
// Return:
//		none
//func (service Trees) mountedMotherTree(tree *models.Tree) {
//
//}

// 获取模板所有树（实体）
// Params:
//		templateID: 模板id
// Return:
//		[]tree: 树实体列表
//		error: 错误返回
func (service *Trees) TemplateTrees(templateID int) ([]*models.Tree, error) {
	// 获取模板所有权限
	templateTrees, err := service.storage.GetTemplateTrees(templateID)
	if err != nil {
		return nil, fmt.Errorf("获取模板树: %w", err)
	}
	// 获取每一个权限对应的母树
	var trees []*models.Tree
	for _, templateTree := range templateTrees {
		permission, err := service.storage.GetTemplatePermission(templateID, templateTree.Tree)
		if err != nil {
			return nil, fmt.Errorf("获取模板树: %w", err)
		}

		//motherTree, ok := service.MotherTrees[templateTree.Tree]
		value, ok := service.MotherTrees.Load(templateTree.Tree)
		if ok {
			motherTree := value.(*models.Tree)
			// 渲染树实体
			rootNode := service.Rendering(motherTree.RootNode, -1, true, permission.Nodes)
			tree := &models.Tree{
				ID:           motherTree.ID,
				Name:         motherTree.Name,
				Terminal:     motherTree.Terminal,
				TerminalName: motherTree.TerminalName,
				Note:         motherTree.Note,
				Root:         motherTree.Root,
				RootNode:     rootNode,
			}
			trees = append(trees, tree)
		}
	}
	// 返回
	return trees, nil
}

// 获取组织所有树（实体）
// 组织树其实就是组织对应的模板树
// Params:
//		groupID: 组织id
// Return:
//		[]tree: 树实体列表
//		error: 错误返回
// TODO 应该用值拷贝获得树
func (service *Trees) MotherTree(id int) *models.Tree {
	//tree, ok := service.MotherTrees[id]
	tree, ok := service.MotherTrees.Load(id)
	if ok {
		return tree.(*models.Tree)
	}
	return nil
}

// 获取组织所有树（实体）
// 组织树其实就是组织对应的模板树
// Params:
//		groupID: 组织id
// Return:
//		[]tree: 树实体列表
//		error: 错误返回
func (service *Trees) GroupTrees(groupID int64) ([]*models.Tree, error) {
	// 组织对应的模板
	groupTemplate, err := service.storage.GetGroupTemplate(groupID)
	if err != nil {
		return nil, fmt.Errorf("获取组织所有树: %w", err)
	}
	if groupTemplate == nil {
		return nil, errors.New("组织未指定模板")
	}
	trees, err := service.TemplateTrees(groupTemplate.ID)
	if err != nil {
		return nil, fmt.Errorf("获取组织所有树: %w", err)
	}
	return trees, nil
}

// 获取角色的权限树（实体）
// 从指定端节点开始的子树，未指定节点的返回整颗树
// Params:
//		roleID: 角色id
//		nodeID: 开始节点id
//		terminalKey: 端key
//		includeFeature: 是否包含feature
// Return:
//		tree: 树实体
//		error: 错误返回
func (service *Trees) RoleSubtree(roleID, nodeID int, terminalKey string, includeFeature bool) (*models.Tree, error) {
	// 获取角色对应端的树
	rolePermission, err := service.storage.GetRolePermissionByTerminal(roleID, terminalKey)
	if err != nil {
		return nil, fmt.Errorf("获取菜单: %w", err)
	}
	if rolePermission == nil {
		return nil, errors.New("无可用菜单")
	}
	// 对应的mother tree
	value, ok := service.MotherTrees.Load(rolePermission.Tree)
	//motherTree, ok := service.MotherTrees[rolePermission.Tree]
	if ok {
		motherTree := value.(*models.Tree)
		tree := &models.Tree{
			ID:       motherTree.ID,
			Name:     motherTree.Name,
			Terminal: motherTree.Terminal,
			Note:     motherTree.Note,
			Root:     motherTree.Root,
			RootNode: &models.Node{},
		}
		rootNode, ok := service.subtree(motherTree.RootNode, nodeID)
		if !ok {
			return tree, nil
		}
		// 获取模版权限（防止权限溢出）
		// 获取角色对应组织
		role, err := service.storage.GetRole(roleID)
		if err != nil {
			return nil, fmt.Errorf("获取菜单: %w", err)
		}
		// 获取组织对应模板
		groupTemplate, err := service.storage.GetGroupTemplate(role.Belong)
		if err != nil {
			return nil, fmt.Errorf("获取菜单: %w", err)
		}
		// 获取模板对应权限
		templatePermission, err := service.storage.GetTemplatePermission(groupTemplate.ID, rolePermission.Tree)
		if err != nil {
			return nil, fmt.Errorf("获取菜单: %w", err)
		}
		// 获取角色权限
		// 模板和角色权限的交集
		// 裁剪树
		subtreeRoot := service.Rendering(rootNode,
			-1,
			includeFeature,
			templatePermission.Nodes,
			rolePermission.Nodes)

		tree.RootNode = subtreeRoot
		return tree, nil
	}

	return nil, errors.New("请求的权限树不存在")
}

// 按照角色名称获取特殊角色（模板特殊角色）的权限树（实体）
// 从指定端节点开始的子树，未指定节点的返回整颗树
// Params:
//		roleName: 角色名称
//		groupID: 组织id
//		nodeID: 开始节点id
//		terminalKey: 端key
//		includeFeature: 是否包含feature
// Return:
//		tree: 树实体
//		error: 错误返回
func (service *Trees) InternalRoleTreeByName(roleName string, groupID int64, nodeID int, terminalKey string, includeFeature bool) (*models.Tree, error) {
	// 按照角色名称获取模板角色
	role, err := service.storage.GetInternalRoleByName(roleName, groupID)
	if err != nil {
		return nil, fmt.Errorf("获取内置角色树: %w", err)
	}
	// 角色权限
	rolePermission, err := service.storage.GetRolePermissionByTerminal(role.ID, terminalKey)
	if err != nil {
		return nil, fmt.Errorf("获取内置角色树: %w", err)
	}
	if rolePermission == nil {
		return nil, errors.New("无权限访问")
	}
	// 模板权限
	templatePermission, err := service.storage.GetTemplatePermissionByTerminal(int(role.Belong), terminalKey)
	if err != nil {
		return nil, fmt.Errorf("获取内置角色树: %w", err)
	}
	// 渲染
	// 对应的mother tree
	//motherTree, ok := service.MotherTrees[rolePermission.Tree]
	value, ok := service.MotherTrees.Load(rolePermission.Tree)
	if ok {
		motherTree := value.(*models.Tree)
		tree := &models.Tree{
			ID:       motherTree.ID,
			Name:     motherTree.Name,
			Terminal: motherTree.Terminal,
			Note:     motherTree.Note,
			Root:     motherTree.Root,
			RootNode: &models.Node{},
		}
		rootNode, ok := service.subtree(motherTree.RootNode, nodeID)
		if !ok {
			return tree, nil
		}
		// 裁剪树

		subtreeRoot := service.Rendering(rootNode,
			-1,
			includeFeature,
			templatePermission.Nodes,
			rolePermission.Nodes)

		tree.RootNode = subtreeRoot
		return tree, nil
	}
	return nil, errors.New("请求的权限树不存在")
}

// 获取母树的子树
// 从指定节点开始的子树，未指定节点的返回整颗树
// Params:
//		treeID: 母树Id
//		nodeID: 开始节点id
// Return:
//		tree: 树实体
//		error: 错误返回
func (service *Trees) motherSubtree(treeID, nodeID int) (*models.Node, bool) {
	//motherTree, ok := service.MotherTrees[treeID]
	value, ok := service.MotherTrees.Load(treeID)
	if ok {
		return service.subtree(value.(*models.Tree).RootNode, nodeID)
	}

	return nil, false
}

// 传入树实体，获取子树
// 从指定节点开始的子树，未指定节点的返回整颗树
// Params:
//		tree: 树实体
//		nodeID: 开始节点id
// Return:
//		tree: 树实体
//		error: 错误返回
func (service *Trees) subtree(tree *models.Node, nodeID int) (*models.Node, bool) {
	if nodeID <= 0 || tree.ID == nodeID {
		return tree, true
	}

	if len(tree.Children) > 0 {
		for _, node := range tree.Children {
			if root, found := service.subtree(node, nodeID); found {
				return root, true
			}
		}
	}
	return nil, false
}

// 获取所有terminal tree信息
// 从缓存中获取
// 按照terminal分组tree(信息)： models.TerminalTree
// Params:
//		none
// Return:
//		terminalTree: 按照terminal分组的tree(信息)
//		error: 错误返回
func (service *Trees) AllTerminalTree() ([]*models.TerminalTree, error) {
	// 获取所有terminal信息
	terminals, err := service.storage.GetAllTerminal()
	if err != nil {
		return nil, fmt.Errorf("获取所有端-树: %w", err)
	}
	// 建立terminalTree map
	terminalsMap := make(map[string]*models.TerminalTree)
	for _, terminal := range terminals {
		terminalsMap[terminal.Key] = &models.TerminalTree{
			Terminal: terminal,
			Trees:    []*models.Tree{},
		}
	}
	// 找到tree和terminal对应
	service.MotherTrees.Range(func(key, value interface{}) bool {
		tree := value.(*models.Tree)
		terminal, ok := terminalsMap[tree.Terminal]
		if ok {
			terminal.Trees = append(terminal.Trees, tree)
		}
		return true
	})

	//for _, tree := range service.MotherTrees {
	//	terminal, ok := terminalsMap[tree.Terminal]
	//	if ok {
	//		terminal.Trees = append(terminal.Trees, tree)
	//	}
	//}
	// 转为list
	var terminalTreeArr []*models.TerminalTree
	for _, terminalTree := range terminalsMap {
		terminalTreeArr = append(terminalTreeArr, terminalTree)
	}
	return terminalTreeArr, nil
}

// 渲染实体树
// 根据传入的权限节点，求交集后返回渲染的树实体
// Params:
//		root: 树的根节点
//		level: 渲染的层数, -1为无限
//		includeFeature: 是否包含Feature
//		[]nodes: 有权限的节点id
// Return:
//		[]Node: 树实体
func (service *Trees) Rendering(root *models.Node, level int, includeFeature bool, nodes ...[]int) *models.Node {
	// 根节点以下剪裁
	node := &models.Node{
		ID:          root.ID,
		Path:        root.Path,
		Level:       root.Level,
		Type:        root.Type,
		ParentID:    root.ParentID,
		Name:        root.Name,
		Flag:        root.Flag,
		Runes:       root.Runes,
		Content:     root.Content,
		Note:        root.Note,
		Sort:        root.Sort,
		State:       root.State,
		Free:        root.Free,
		ChildrenIDs: root.ChildrenIDs,
		Children:    []*models.Node{},
		APIs:        root.APIs,
		Updated:     root.Updated,
	}

	// 无任何权限返回空子节点的根节点
	if len(nodes) == 0 {
		return node
	}

	nodeSet := service.nodesIntersect(nodes...)
	node.Children = service.trimTree(root.Children, level, includeFeature, nodeSet)
	return node
}

// 剪裁实体
// 根据传入的权限节点，剪裁实体树
// Params:
//		[]Node: 树实体
//		level: 渲染的层数, -1为无限
//		includeFeature: 是否包含Feature
//		nodeSet: 权限集合
// Return:
//		[]Node: 树实体
func (service *Trees) trimTree(nodes []*models.Node, level int, includeFeature bool, nodeSet mapset.Set) []*models.Node {
	survivor := make([]*models.Node, 0)
	// 深度到达，返回空列表，方便前端处理
	if level == 0 || len(nodes) == 0 {
		return survivor
	}

	for _, node := range nodes {
		// 是否包含feature
		if node.Type == models.NODE_TYPE_FEATURE && !includeFeature {
			continue
		}

		n := &models.Node{
			ID:          node.ID,
			Path:        node.Path,
			Level:       node.Level,
			Type:        node.Type,
			ParentID:    node.ParentID,
			Name:        node.Name,
			Flag:        node.Flag,
			Runes:       node.Runes,
			Content:     node.Content,
			Note:        node.Note,
			Sort:        node.Sort,
			State:       node.State,
			Free:        node.Free,
			ChildrenIDs: node.ChildrenIDs,
			Children:    []*models.Node{}, // 返回为初始化的空数组，减少前端判断难度
			APIs:        node.APIs,
			Updated:     node.Updated,
		}

		// 深度遍历
		if len(node.Children) > 0 {
			children := service.trimTree(node.Children, level-1, includeFeature, nodeSet)
			// 有子节点权限，本身自然有权限
			if len(children) > 0 {
				n.Children = children
				survivor = append(survivor, n)
			} else {
				//无子节点权限，但是本身有权限
				if nodeSet.Contains(node.ID) {
					survivor = append(survivor, n)
				}
			}
		} else {
			// 无子节点，但是本身有权限
			if nodeSet.Contains(node.ID) {
				survivor = append(survivor, n)
			}
		}

	}

	return survivor
}

// 求交集
// Params:
//		nodes: []int
// Return:
//		nodeSet: 交集
func (service *Trees) nodesIntersect(nodes ...[]int) mapset.Set {
	// 节点转为集合
	//nodeSets := make([]mapset.Set, len(nodes))
	nodeSet := mapset.NewSet()
	for i, ns := range nodes {
		tempSet := mapset.NewSet()
		for _, n := range ns {
			tempSet.Add(n)
			//nodeSets[i].Add(n)
		}
		// 求交集
		if i == 0 {
			nodeSet = tempSet
		} else {
			nodeSet = nodeSet.Intersect(tempSet)
		}
	}
	return nodeSet
}
