package controller

//func ExportExcel(c echo.Context) error {
//	user, err := sessionStore.GetSessionUser(c)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "内部错误，请稍后再试"))
//	}
//	// 调用第一接口
//	//remote := V1Host + c.Request().RequestURI
//	remote := V1Host + "/v1/business/finance/grn/export"
//	resultJSON, err := HandleResult2SimpleJson(
//		RESTWithResponse(http.MethodPost,
//			remote,
//			"",
//			c.Request().Body, user))
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//	fileName, err := resultJSON.Get("msg").String()
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//	// 调用第二接口获取文件
//	remote = V1Host + SERVICE_URI_V1_COMM_DOWNLOAD +
//		"?delete=true&fileName=" + url.QueryEscape(fileName)
//	//log.Println(fileName)
//	//url = V1Host + SERVICE_URI_V1_COMM_DOWNLOAD +
//	//	"?delete=true&fileName=1111.txt"
//
//	response, err := RESTWithResponse(http.MethodGet,
//		remote,
//		"",
//		nil, user)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//	file, err := ioutil.ReadAll(response.Body)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//	c.Response().Write(file)
//	c.Response().Header().Set("Content-Type", "application/octet-stream")
//	return nil
//}
