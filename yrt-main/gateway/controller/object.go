package controller

import (
	"encoding/json"
	"log"
	"net/http"

	"bjyixiu.com/gateway/models"

	"github.com/pkg/errors"

	"bjyixiu.com/gateway/protocol"
	"bjyixiu.com/twisted"
	"github.com/labstack/echo/v4"
)

// 图片下载
// swagger:route GET /v1/image/get/{id} 对象存储 reqImgID
//
// 传入图片id获取图片，为302跳转
//
// 		Responses:
//			302:
func ImageDownloadUrl(c echo.Context) error {
	// id
	id := c.Param("id")
	if len(id) == 0 {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少图片ID"))
	}
	// 获得params
	v, err := c.FormParams()
	if err != nil {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请求参数错误"))
	}
	s := v.Encode()
	if len(s) > 0 {
		id = id + "?" + s
	}
	// 生成下载链接
	url := twisted.ImgDownloadUrl(RequestScheme(c), id)
	// url = url + s
	// 302跳转
	//return c.Redirect(http.StatusFound, RequestScheme(c)+url)
	return c.Redirect(http.StatusFound, url)
}

// 图片下载
// swagger:route GET /v1/image/url_with_expire/{id} 对象存储 reqImgTimeID
//
// 传入图片id获取图片下载地址和时间（开始时间和过期时间）
//
// 		Responses:
// 			200: respObjectUrlTime
func ImageDownloadUrlWithTime(c echo.Context) error {
	// id
	id := c.Param("id")
	if len(id) == 0 {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少图片ID"))
	}
	// 获得params
	v, err := c.FormParams()
	if err != nil {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请求参数错误"))
	}
	s := v.Encode()
	if len(s) > 0 {
		id = id + "?" + s
	}
	// 生成下载链接
	url, begin, expire := twisted.ImgDonwloadUrlWithTime(RequestScheme(c), id)
	// url = url + s
	data := models.DownloadUrlAndTime{
		URL:    url,
		Begin:  begin,
		Expire: expire,
	}
	c.Response().Header().Set(echo.HeaderContentType, echo.MIMEApplicationJSONCharsetUTF8)
	c.Response().Status = http.StatusOK

	// 避免c.JSON()对html转义
	response := protocol.SuccessData(data)
	encoder := json.NewEncoder(c.Response())
	encoder.SetEscapeHTML(false)
	if err := encoder.Encode(&response); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "JSON序列化错误"))
	}

	return nil
}

// 附件下载
// swagger:route GET /v1/att/get/{id} 对象存储 reqAttID
//
// 传入附件id获取附件，为302跳转
//
// 		Responses:
//			302:
func AttachmentDownloadUrl(c echo.Context) error {
	// id
	id := c.Param("id")
	fileName := c.QueryParam("filename")
	if len(id) == 0 {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少附件ID"))
	}
	// 生成下载链接
	url := twisted.AttDownloadUrl(RequestScheme(c), id)
	url = url + `&attname=` + fileName
	// url = url + s
	// 302跳转
	return c.Redirect(http.StatusFound, url)
}

// 附件下载
// swagger:route GET /v1/att/url_with_expire/{id} 对象存储 reqAttTimeID
//
// 传入附件id获取附件下载地址和时间（开始时间和过期时间）
//
// 		Responses:
// 			200: respObjectUrlTime
func AttachmentDownloadUrlWithTime(c echo.Context) error {
	// id
	id := c.Param("id")
	fileName := c.QueryParam("filename")
	if len(id) == 0 {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少附件ID"))
	}
	// 生成下载链接
	url, begin, expire := twisted.AttDownloadUrlWithTime(RequestScheme(c), id)
	url = url + `&attname=` + fileName
	// url = url + s
	data := models.DownloadUrlAndTime{
		URL:    url,
		Begin:  begin,
		Expire: expire,
	}

	c.Response().Header().Set(echo.HeaderContentType, echo.MIMEApplicationJSONCharsetUTF8)
	c.Response().Status = http.StatusOK

	// 避免c.JSON()对html转义
	response := protocol.SuccessData(data)
	encoder := json.NewEncoder(c.Response())
	encoder.SetEscapeHTML(false)
	if err := encoder.Encode(&response); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "JSON序列化错误"))
	}

	return nil
}

// 图片下载
// swagger:route GET /v1/image/get/{token}/{id} 对象存储 reqImgWithToken
//
// path中传入token和图片id获取图片，为302跳转
//
// 		Responses:
//			302:
func ImageDownloadUrlWithToken(c echo.Context) error {
	// id
	id := c.Param("id")
	if len(id) == 0 {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少图片ID"))
	}
	// token
	sid := c.Param("token")
	if len(sid) == 0 {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "请先登录"))
	}

	//auth
	pass, _, err := getSessionByID(sid)
	if err != nil {
		log.Println("下载图片出错", errors.WithStack(err))
	}
	if !pass {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "请先登录"))
	}

	// 获得params
	v, err := c.FormParams()
	if err != nil {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请求参数错误"))
	}
	s := v.Encode()
	if len(s) > 0 {
		id = id + "?" + s
	}
	// 生成下载链接
	url := twisted.ImgDownloadUrl(RequestScheme(c), id)
	// url = url + s
	// 302跳转
	return c.Redirect(http.StatusFound, url)
}

// 附件下载
// swagger:route GET /v1/att/get/{token}/{id} 对象存储 reqAttWithToken
//
// path中传入token和附件id获取附件，为302跳转
//
// 		Responses:
//			302:
func AttachmentDownloadUrlWithToken(c echo.Context) error {
	// id
	id := c.Param("id")
	fileName := c.QueryParam("filename")
	if len(id) == 0 {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少附件ID"))
	}

	// token
	sid := c.Param("token")
	if len(sid) == 0 {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "请先登录"))
	}

	//auth
	pass, _, err := getSessionByID(sid)
	if err != nil {
		log.Println("下载图片出错", errors.WithStack(err))
	}
	if !pass {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "请先登录"))
	}

	// 生成下载链接
	url := twisted.AttDownloadUrl(RequestScheme(c), id)
	url = url + `&attname=` + fileName
	// url = url + s
	// 302跳转
	return c.Redirect(http.StatusFound, url)
}

// 获取图片上传Token
// swagger:route GET /v1/image/token 对象存储 noneReqGetImagetoken
//
// 无参数，获取图片上传Token
//
// 		Responses:
//			200: respObjectUpToken
func GetUpImageToken(c echo.Context) error {
	// 返回
	token, _, _ := twisted.UploadImageToken()
	return c.JSON(http.StatusOK, protocol.SuccessData(token))

}

// 获取图片上传Token和时间
// swagger:route GET /v1/image/token_with_expire 对象存储 noneReqGetImagetokenTime
//
// 无参数，获取图片上传Token和时间
//
// 		Responses:
//			200: respObjectUpToken
func GetUpImageTokenWithExpire(c echo.Context) error {
	token, begin, expire := twisted.UploadImageToken()
	resp := models.UploadUrlAndTime{
		Token:  token,
		Begin:  begin,
		Expire: expire,
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(resp))
}

// 获取附件上传Token
// swagger:route GET /v1/att/token 对象存储 noneReqGetAtttoken
//
// 无参数，获取附件上传Token
//
// 		Responses:
//			200: respObjectUpToken
func GetUpAttToken(c echo.Context) error {
	// 返回
	token, _, _ := twisted.UploadAttToken()
	return c.JSON(http.StatusOK, protocol.SuccessData(token))
}

// 获取附件上传Token和时间
// swagger:route GET /v1/att/token_with_expire 对象存储 noneReqGetAtttokenTime
//
// 无参数，获取附件上传Token和时间
//
// 		Responses:
//			200: respObjectUpToken
func GetUpAttTokenWithExpire(c echo.Context) error {
	token, bengin, expire := twisted.UploadAttToken()
	resp := models.UploadUrlAndTime{
		Token:  token,
		Begin:  bengin,
		Expire: expire,
	}
	return c.JSON(http.StatusOK, protocol.SuccessData(resp))
}

func QiniuCallback(c echo.Context) error {
	var back twisted.CallBack
	if err := c.Bind(&back); err != nil {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请求参数错误"))
	}
	resp := protocol.SuccessMsg("上传成功")
	resp.Data = back
	return c.JSON(http.StatusOK, resp)
}

func RequestScheme(c echo.Context) string {
	if c.IsTLS() {
		return "https://"
	}
	return "http://"
}
