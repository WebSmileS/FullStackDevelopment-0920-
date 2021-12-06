package controller

import (
	"net/http"

	"bjyixiu.com/gateway/models"
	"bjyixiu.com/gateway/protocol"

	"github.com/labstack/echo/v4"
)

type LogManager interface {
	Insert(*models.HTTP) error
	GetByID(string) (*models.HTTP, error)
	GetByTime(int64, int64) ([]*models.HTTP, error)
}

type LogController struct {
	manager LogManager
}

type GetByID struct {
	Logid string
}

// @Summary Create a reward transaction
// @Description post a reward transaction
// @Tags 运维_日志
// @Accept  json
// @Produce  json
// @Param id path string true "只需要传入Logid"
// @Success 200 {object} models.HttpLogInfo
// @Router /log/get_by_id/{id} [get]

// 根据log ID获取request/response原始信息
// swagger:route GET /v1/log/get_by_id/{id} 运维-日志 reqHttpLogID
//
// 根据log ID获取 request/response 原始信息，传入Log ID
//
//		Responses:
//			200: respHttpLogInfo
func (ctr *LogController) GetLogByID(c echo.Context) error {
	// TODO REST返回
	// var post models.HttpLogInfo
	// 获取参数
	id := c.Param("id")
	// var post struct {
	// 	Logid string
	// }

	// if err := c.Bind(&post); err != nil {
	// 	c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请求参数错误"))
	// 	return nil
	// }
	// 查询
	hRaw, err := ctr.manager.GetByID(id)
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "未找到"))
		return nil
	}
	// 填充
	hInfo := models.NewHttpLogInfo(hRaw)
	// 返回
	j := protocol.SuccessMsg("成功")
	j.Data = hInfo
	c.JSON(http.StatusOK, j)
	return nil
}

func (ctr *LogController) GetLogByTime(c echo.Context) error {

	var post struct {
		BeginTime int64
		EndTime   int64
	}

	// 获取参数
	if err := c.Bind(&post); err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请求参数错误"))
		return nil
	}
	// TODO 校验是否合法时间，并且时间的跨度值比较小

	// 查询
	hs, err := ctr.manager.GetByTime(post.BeginTime, post.EndTime)
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "未找到"))
		return nil
	}
	// 填充
	var hInfo []*models.HttpLogInfo
	for _, v := range hs {
		h := models.NewHttpLogInfo(v)
		hInfo = append(hInfo, h)
	}
	// 返回
	j := protocol.SuccessMsg("成功")
	j.Data = hInfo
	c.JSON(http.StatusOK, j)

	return nil
}
