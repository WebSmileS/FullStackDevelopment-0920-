package sql

import (
	"strconv"
	"strings"

	"bjyixiu.com/ent/models"
)

// 同步（channel 消息）节点的更改
// Params:
//		node: 节点
// Return:
// 		none
func (sql *SQLService) notifyUpdateNode(node *models.Node) {
	// 查找node是否包含完整的path
	if len(node.Path) <= 1 {
		n, err := sql.GetNodeByID(node.ID)
		if err != nil {
			// TODO 错误日志
			return
		}
		node = &n
	}

	//	找到对应的树id
	ids := strings.Split(node.Path, "/")
	if len(ids) < 2 {
		// TODO 错误日志
		return
	}
	rootID, err := strconv.Atoi(ids[1])
	if err != nil {
		// TODO 错误日志
		return
	}
	var tree models.Tree
	if err := sql.db.Take(&tree, "root = ?", rootID).Error; err != nil {
		// TODO 错误日志
		return
	}
	//	更新
	sql.updateTreeChan <- tree.ID
}

// 同步（channel 消息）树的更改
// Params:
//		id: 树id
// Return:
// 		none
func (sql *SQLService) notifyUpdateTree(id int) {
	sql.updateTreeChan <- id
}

// 同步（channel 消息）树的删除
// Params:
//		id: 树id
// Return:
// 		none
func (sql *SQLService) notifyDelTree(id int) {
	sql.delTreeChan <- id
}

// 同步（channel 消息）端的修改
// Params:
//		terminal: 端实体
// Return:
// 		none
func (sql *SQLService) notifyTerminal(terminal models.Terminal) {

}
