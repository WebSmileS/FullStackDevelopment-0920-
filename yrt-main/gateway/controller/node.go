package controller

import (
	"net/http"
	"strconv"

	"bjyixiu.com/ent/models"
	"bjyixiu.com/gateway/protocol"
	"github.com/labstack/echo/v4"
)

// 在树上增加节点
// swagger:route POST /v1/tree/add_node 运维-菜单树 addNode
//
// 在母树上新增子节点，传入节点实体
//
// 		Responses:
//			200: respAddNode
func AddNode(c echo.Context) error {
	var node models.Node
	if err := c.Bind(&node); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	// TODO 检查

	// 处理
	err := ent.AddNode(&node)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	// 返回
	resp := protocol.SuccessMsg("新增节点成功")
	resp.Data = node
	return c.JSON(http.StatusOK, resp)
}

// 更新节点信息
// swagger:route POST /v1/tree/update_node 运维-菜单树 updateNode
//
// 在母树上新增子节点，传入节点实体
//
// 		Responses:
//			200: responseMessage
func UpdateNode(c echo.Context) error {
	updateNode := new(models.Node)
	if err := c.Bind(updateNode); err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}

	// TODO 检查

	// 处理
	err := ent.UpdateNode(updateNode)
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
		return nil
	}
	// 返回
	return c.JSON(http.StatusOK, protocol.Success())
}

// 删除节点
// swagger:route DELETE /v1/tree/del_node/{id} 运维-菜单树 delNode
//
// 删除母树节点，不允许删除根节点和有子节点的节点
//
// 		Responses:
//			200: responseMessage
func DelNode(c echo.Context) error {
	idStr := c.Param("id")
	id, err := strconv.Atoi(idStr)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	if id <= 0 {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	if err := ent.DelNode(id, false); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// TODO：测试用，后面关闭
// 生成新的节点ID（test）
// swagger:route GET /v1/tree/new_id 运维-菜单树 noneParam
//
// 生成新的节点ID，用于测试
//
// 		Responses:
//			200: responseMessage
//func NewID(c echo.Context) error {
//	id := ent.GenID()
//	return c.JSON(http.StatusOK, protocol.SuccessData(id))
//}
