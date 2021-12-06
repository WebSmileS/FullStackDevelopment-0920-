package sql

import (
	"fmt"
	"strconv"

	"github.com/pkg/errors"

	"bjyixiu.com/ent/models"
)

// 新增母树
// Params:
// 		tree : 母树
// Return:
//		id : 母树的ID
// 		error: 错误返回
func (sql *SQLService) CreateMotherTree(tree *models.Tree) (id int, err error) {

	// 检查
	if tree.ID > 0 {
		return 0, fmt.Errorf("TreeID自动生成，应为空")
	}

	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("新增母树出错 %w", err)
		}
		r := recover()
		if r != nil {
			tx.Rollback()
			err = fmt.Errorf("新增母树出错 %w", r)
		}
	}()

	// 建立Root节点
	rootNode := models.NewRoot(sql.GenNodeID())
	if err = tx.Create(&rootNode).Error; err != nil {
		return
	}
	tree.Root = rootNode.ID
	tree.RootNode = &rootNode

	// 建立母树
	if err = tx.Create(&tree).Error; err != nil {
		return
	}

	if err = tx.Commit().Error; err != nil {
		return
	}
	id = tree.ID
	sql.notifyUpdateTree(id)

	return
}

// 新增母树，直接新增，不生成新的ID
// 也不需要建立Root节点
// Params:
// 		tree : 母树
// Return:
// 		error: 错误返回
func (sql *SQLService) InsertTree(tree *models.Tree) error {
	if tree.ID <= 0 {
		return errors.New("树ID不能为空")
	}

	// 建立母树
	if err := sql.db.Create(&tree).Error; err != nil {
		return fmt.Errorf("新增母树出错: %w", err)
	}
	sql.notifyUpdateTree(tree.ID)
	return nil
}

// 同步树
// 也不需要建立Root节点
// Params:
//		newTree: 新树
// 		oldTree : 母树
// Return:
// 		error: 错误返回
func (sql *SQLService) SyncTree(newTree, oldTree *models.Tree) (err error) {

	newTreeNodesMap := make(map[int]*models.Node)
	oldTreeNodesMap := make(map[int]*models.Node)
	if newTree != nil {
		tree2NodeMap(newTree.RootNode, newTreeNodesMap)
	}
	if oldTree != nil {
		tree2NodeMap(oldTree.RootNode, oldTreeNodesMap)
	}
	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("同步母树出错 %w", err)
		}
		r := recover()
		if r != nil {
			tx.Rollback()
			err = fmt.Errorf("同步母树出错 %w", r)
		}
		if err == nil && r == nil {
			sql.notifyUpdateTree(newTree.ID)
		}
	}()

	for _, newTreeNode := range newTreeNodesMap {
		_, ok := oldTreeNodesMap[newTreeNode.ID]
		if ok {
			//if oldNode.Updated != newTreeNode.Updated {
			//	 更新
			if err = tx.Save(newTreeNode).Error; err != nil {
				return
			}
			//}
			delete(oldTreeNodesMap, newTreeNode.ID)
		} else {
			//	增加
			if err = tx.Create(&newTreeNode).Error; err != nil {
				return
			}
		}

	}
	// 剩下为删除的
	for _, delTreeNode := range oldTreeNodesMap {
		if err = tx.Delete(delTreeNode).Error; err != nil {
			return
		}
	}

	err = tx.Commit().Error
	return
}

// 递归把树转为节点map,传入空map，进行填满
// Params：
//		Root: 根节点
//		NodesMap: 节点map
// Return:
//		None
func tree2NodeMap(root *models.Node, nodesMap map[int]*models.Node) {
	nodesMap[root.ID] = root
	for _, node := range root.Children {
		tree2NodeMap(node, nodesMap)
	}
}

// 修改母树信息，不能修改Terminal和Root节点
// Params:
// 		tree : 母树
// Return:
// 		error: 错误返回
func (sql *SQLService) UpdateMotherTree(tree *models.Tree) error {
	// Root节点是不会更新的
	if err := sql.db.Model(tree).
		Select("name", "note").
		Updates(tree).Error; err != nil {
		return fmt.Errorf("更新母树信息出错: %w", err)
	}
	sql.notifyUpdateTree(tree.ID)
	return nil
}

// 删除母树, 包括母树信息和所有节点
// Params:
// 		terminal : terminal key
//		id : 母树ID
// Return:
// 		error: 错误返回
func (sql *SQLService) DelMotherTree(id int) (err error) {
	tree, er := sql.GetMotherTreeByID(id)
	if er != nil {
		err = fmt.Errorf("删除母树，检查母树出错: %w", er)
		return
	}

	tx := sql.Begin()
	defer func() {
		if err != nil {
			tx.db.Rollback()
			err = fmt.Errorf("删除母树出错: %w", err)
		}
		r := recover()
		if r != nil {
			tx.db.Rollback()
			err = fmt.Errorf("删除母树出错: %w", r)
		}
	}()

	if err := tx.db.Delete(&models.Tree{ID: id}).Error; err != nil {
		return err
	}

	// 删除节点
	if err = tx.DelNode(tree.Root, true); err != nil {
		return
	}

	if err = tx.Commit(); err != nil {
		return
	}
	return nil
}

// 获取所有母树(信息)
// Params:
// 		none
// Return:
//		[]tree: 母树数组
// 		error: 错误返回
func (sql *SQLService) GetAllTrees() ([]models.Tree, error) {
	var trees []models.Tree
	if err := sql.db.Find(&trees).Error; err != nil {
		return nil, fmt.Errorf("获取所有母树出错: %w", err)
	}
	return trees, nil
}

// 根据母树ID获取母树,包含树结构
// Params:
//		id: 母树ID
// Return:
//		Tree: 母树
// 		error: 错误返回
func (sql *SQLService) GetMotherTreeByID(id int) (*models.Tree, error) {
	// 拿到树信息
	var tree models.Tree
	//if err := sql.db.Where("id = ?", id).Find(&tree).Error; err != nil {
	//	return nil, fmt.Errorf("获取母树，获取母树信息出错: %w", err)
	//}
	if err := sql.db.Take(&tree, "id = ?", id).Error; err != nil {
		return nil, fmt.Errorf("获取母树，获取母树信息出错: %w", err)
	}
	return &tree, nil
}

// 根据节点（一般为根节点）获取树结构
// Params:
//		rootID: 节点ID
// Return:
//		Node: 树结构
// 		error: 错误返回
func (sql *SQLService) GetTreeByID(rootID int, depth int, states ...int) (*models.Node, error) {
	var allNodes []*models.Node
	if depth <= 0 {
		depth = 9999
	}

	root, err := sql.GetNodeByID(rootID)
	if err != nil {
		return nil, fmt.Errorf("读取根节点出错: %w", err)
	}

	stateQuery := ""

	if len(states) > 0 {
		stateQuery = " AND ( "
		for i, state := range states {
			if i > 0 {
				stateQuery = stateQuery + " OR "
			}
			stateQuery = stateQuery + "state = " + strconv.Itoa(state)
		}
		stateQuery = " )"
	}

	query := `SELECT * ` +
		`FROM permission_node` +
		` WHERE path LIKE '` + root.Path + `/%'` +
		` AND level <= ` + strconv.Itoa(int(root.Level)+depth) + stateQuery
	//` ORDER BY parent_id`

	if err := sql.db.Raw(query).Scan(&allNodes).Error; err != nil {
		return nil, fmt.Errorf("读取Tree失败: %w", err)
	}

	m := make(map[int]*models.Node)
	m[rootID] = &root
	for _, node := range allNodes {
		if node.Children == nil {
			node.Children = make([]*models.Node, 0)
		}
		linkNode(allNodes, node, m)
	}
	return &root, nil
}

// 设置tree key 到 tree id的映射
// Params:
//		terminalKey: terminal key
//		treeKey: tree key
//		id: tree ID
// Return:
// 		error: 错误返回
func (sql *SQLService) InsertTreeKeyID(terminal, tree string, id int) error {
	keyID := models.TreeKeyIDMap{
		Terminal: terminal,
		Tree:     tree,
		ID:       id,
	}
	if err := sql.db.Create(&keyID).Error; err != nil {
		return fmt.Errorf("保存Tree Key id映射失败: %w", err)
	}
	return nil
}

// 获得Tree 对应的 id
// Params:
//		Terminal: terminal key
//		Tree: tree key
// Return:
//		id: tree ID
// 		error: 错误返回
func (sql SQLService) GetTreeIDByKey(terminal, tree string) (int, error) {
	keyID := models.TreeKeyIDMap{}
	if err := sql.db.Take(&keyID, "terminal = ? AND tree = ?", terminal, tree).Error; err != nil {
		return 0, fmt.Errorf("获取Tree key - id对应关系出错: %w", err)
	}
	return keyID.ID, nil
}
