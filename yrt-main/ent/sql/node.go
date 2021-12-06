package sql

import (
	"errors"
	"fmt"
	"strconv"
	"time"

	"github.com/jinzhu/gorm"

	"bjyixiu.com/ent/models"
)

// 新增母树上的节点，根节点不在此新增
// Params:
//		node: 节点
// Return:
// 		error: 错误返回
func (sql *SQLService) AddNode(node *models.Node) error {
	// 计算路径
	// 获得父节点
	parent, err := sql.GetNodeByID(node.ParentID)
	if err != nil {
		return fmt.Errorf("新建节点，获取父节点出错：%w", err)
	}
	node.ID = sql.GenNodeID()
	node.Path = parent.Path + "/" + strconv.Itoa(node.ID)
	max, err := sql.getChildrenSortMax(parent.ID)
	if err != nil {
		return fmt.Errorf("新建节点错误 %w", err)
	}
	node.Sort = max + 1
	node.Level = parent.Level + 1
	//node.UpdateTime = time.Now()
	if err := sql.db.Create(node).Error; err != nil {
		return fmt.Errorf("新建节点错误 %w", err)
	}

	sql.notifyUpdateNode(node)

	return nil
}

// 更新母树上的节点
// Params:
//		node: 节点
// Return:
// 		error: 错误返回
func (sql *SQLService) UpdateNode(node *models.Node) error {
	// TODO: 要增加api的处理
	node.Updated = time.Now()
	if err := sql.db.Model(node).
		Select("name", "type", "flag", "runes", "content", "note", "state", "free", "updated").
		Updates(node).Error; err != nil {
		return fmt.Errorf("更新节点: %w", err)
	}
	sql.notifyUpdateNode(node)
	return nil
}

// 直接更新节点，用于数据导入或者同步
// Params:
//		node: 节点
// Return:
// 		error: 错误返回
func (sql *SQLService) SaveNode(node *models.Node) error {
	// TODO: 要增加api的处理
	if err := sql.db.Save(node); err != nil {
		return fmt.Errorf("更新节点出错: %w", err)
	}
	sql.notifyUpdateNode(node)
	return nil
}

// 获取母树节点
// Params:
//		id: 节点ID
// Return:
//		node: 节点
// 		error: 错误返回
func (sql *SQLService) GetNodeByID(id int) (models.Node, error) {
	var node models.Node
	if err := sql.db.Take(&node, "id = ?", id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return node, fmt.Errorf("节点未找到")
		}
		return node, fmt.Errorf("获取节点出错 %w", err)
	}
	return node, nil
}

// 根据出入的id数组获取获取母树节点
// Params:
//		ids: 节点ID数组
// Return:
//		nodes: 节点数组
// 		error: 错误返回
func (sql SQLService) GetNodesByIDs(ids ...int) ([]models.Node, error) {
	var nodes []models.Node
	if err := sql.db.Where("id IN (?)", ids).Find(&nodes).Error; err != nil {
		return nil, fmt.Errorf("批量获取节点出错: %w", err)
	}
	return nodes, nil
}

// 查找子节点序号最大值
// Params:
//		id: 节点ID
// Return:
//		sortMax: 序号最大值
// 		error: 错误返回
func (sql *SQLService) getChildrenSortMax(id int) (int, error) {
	max := 0

	rows, err := sql.db.Table(models.Node{}.TableName()).Select("MAX(sort) AS max").
		Where("parent_id = ?", id).Rows()
	defer rows.Close()

	if err != nil {
		return 0, fmt.Errorf("查询子节点最大排序出错：%w", err)
	}

	if rows.Next() {
		err := rows.Scan(&max)
		if err != nil {
			// 在无内容情况下会返回：sql: Scan error on column index 0, name "max": converting NULL to int is unsupported
			// 当作无值返回
			return 0, nil
		}
		return max, nil
	}

	return 0, nil
}

// 查找节点ID最大值（自增）
// Params:
//		none
// Return:
//		max: id 最大值
//		error: 错误返回
func (sql *SQLService) getMaxID() (int, error) {
	max := 0

	rows, err := sql.db.Table(models.Node{}.TableName()).Select("MAX(id) AS max").Rows()
	defer rows.Close()

	if err != nil {
		return 0, fmt.Errorf("查询节点最大id值出错：%w", err)
	}

	if rows.Next() {
		err := rows.Scan(&max)
		if err != nil {
			// 在无内容情况下会返回：sql: Scan error on column index 0, name "max": converting NULL to int is unsupported
			// 当作无值返回
			return 0, nil
		}
		return max, nil
	}

	return 0, nil
}

// 把Node按父子结构关联起来
// Params:
//		allNode： 所有node数组
//		node: 节点
//		m : 已经映射好的结构
// Return:
//		none
func linkNode(allNode []*models.Node, node *models.Node, m map[int]*models.Node) {
	m[node.ID] = node
	parent, ok := m[node.ParentID]
	if !ok {
		for _, n := range allNode {
			if n.ID == node.ParentID {
				parent = n
				m[n.ID] = parent

			}
		}
	}
	// 插入并排序
	insertSort(parent, node)
}

// node按照顺序插入到父节点到children
// Params:
//		parent：父节点
//		child: 节点
// Return:
//		none
func insertSort(parent, child *models.Node) {
	if len(parent.Children) == 0 {
		parent.Children = append(parent.Children, child)
		return
	}

	if parent.Children[0].Sort > child.Sort {
		parent.Children = append([]*models.Node{child}, parent.Children...)
		return
	}

	if parent.Children[len(parent.Children)-1].Sort < child.Sort {
		parent.Children = append(parent.Children, child)
		return
	}

	for i := 0; i < len(parent.Children)-1; i++ {
		if child.Sort > parent.Children[i].Sort && child.Sort <= parent.Children[i+1].Sort {
			tail := make([]*models.Node, 0)
			tail = append(tail, parent.Children[i+1:]...)
			parent.Children = append(parent.Children[:i+1], child)
			parent.Children = append(parent.Children, tail...)
			return
		}
	}
}

// 保存节点Key -- SqlID对应表
// Params:
//		key: 节点key
//		id: 节点ID
// Return:
// 		error: 错误返回
func (sql *SQLService) InsertKeyID(key string, id int) error {
	keyID := models.NodeKeyIDMap{
		KVKey: key,
		ID:    id,
	}

	if err := sql.db.Create(&keyID).Error; err != nil {
		return fmt.Errorf("key - id 对应关系存储出错：%w", err)
	}
	return nil
}

// 获取节点Key -- SqlID对应关系
// Params:
//		key: 节点key
// Return:
// 		error: 错误返回
func (sql *SQLService) GetKeyIDbyKey(key string) (int, error) {
	keyID := models.NodeKeyIDMap{}
	if err := sql.db.First(&keyID, "kv_key = ?", key).Error; err != nil {
		return 0, fmt.Errorf("获取Key - id对应关系出错：%w", err)
	}
	return keyID.ID, nil
}

// 删除节点，包括节点的子节点
// Params:
//		ID: 节点ID
//		force: 是否强制删除,否会判断是否有子节点，有子节点的不能删除
// Return:
// 		error: 错误返回
func (sql *SQLService) DelNode(id int, forces bool) error {
	node, err := sql.GetNodeByID(id)
	if err != nil {
		return fmt.Errorf("删除节点，查询节点出错: %w", err)
	}
	if !forces {
		if len(node.Children) > 0 {
			return fmt.Errorf("节点下有子节点，请先删除子节点")
		}
	}
	// 防止path为""或"/"时，删除整张表
	if len(node.Path) < 2 {
		return errors.New("错误的节点")
	}
	// TODO: 缺少对API的处理

	if err := sql.db.Delete(&models.Node{}, "id = ?", id).Error; err != nil {
		return fmt.Errorf("删除节点出错 : %w", err)
	}

	sql.notifyUpdateNode(&node)

	return nil
}

// 交换节点顺序
// Params:
//		ID1: 节点ID1
//		ID2: 节点ID2
// Return:
// 		error: 错误返回
func (sql *SQLService) SwapNode(id1, id2 int) (err error) {
	nodes, err := sql.GetNodesByIDs(id1, id2)
	if err != nil {
		return fmt.Errorf("更改节点顺序:%w", err)
	}

	if len(nodes) != 2 {
		return errors.New("节点未找到")
	}
	node1 := nodes[0]
	node2 := nodes[1]
	tx := sql.db.Begin()
	defer func() {
		if err != nil {
			tx.Rollback()
			err = fmt.Errorf("更改节点顺序出错 %w", err)
		}
		r := recover()
		if r != nil {
			tx.Rollback()
			err = fmt.Errorf("更改节点顺序出错 %w", r)
		}
		sql.notifyUpdateNode(&node1)
	}()
	now := time.Now()
	node1.Updated = now
	node2.Updated = now
	tempSort := node1.Sort
	node1.Sort = node2.Sort
	node2.Sort = tempSort
	if err = sql.db.Model(&node1).
		Select("sort", "updated").
		Updates(node1).Error; err != nil {
		return
	}

	if err = sql.db.Model(&node2).
		Select("sort", "updated").
		Updates(node2).Error; err != nil {
		return
	}

	err = tx.Commit().Error
	return
}
