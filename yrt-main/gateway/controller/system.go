package controller

import (
	"net/http"

	"github.com/pkg/errors"

	"bjyixiu.com/gateway/config"
	"bjyixiu.com/gateway/protocol"
	"github.com/labstack/echo/v4"
)

func GetAndroidVersion(c echo.Context) error {
	ver, err := config.GetAndroidVersion()
	if err != nil {
		return c.JSON(http.StatusOK, protocol.FlatErrMeg(err))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(ver))
}

func GetIOSVersion(c echo.Context) error {
	ver, err := config.GetIOSVersion()
	if err != nil {
		return c.JSON(http.StatusOK, protocol.FlatErrMeg(err))
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(ver))
}

func GetAppVersion(c echo.Context) error {
	ver, err := config.GetAppVersion()
	if err != nil {

		return c.JSON(http.StatusOK, &ResponseJson{
			Code: 1,
			Msg:  errors.WithStack(err).Error(),
			Data: nil,
		})
	}

	return c.JSON(http.StatusOK, &ResponseJson{
		Code: 0,
		Msg:  "success",
		Data: ver,
	})
}

//TODO 临时处理返回大小写
type ResponseJson struct {
	Code int
	Msg  string
	Data interface{}
}
