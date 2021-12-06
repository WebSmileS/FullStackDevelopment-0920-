package controller

import (
	"encoding/json"
	"log"
	"net/http"
	"strconv"
	"strings"

	"bjyixiu.com/gateway/models"

	entModels "bjyixiu.com/ent/models"
	"bjyixiu.com/gateway/protocol"
	ryzeModels "bjyixiu.com/ryze/models"
	"bjyixiu.com/ryze/service"

	"github.com/labstack/echo/v4"
	"github.com/pkg/errors"
)

//var sessionName string
//var sessionExpire int
//var sessionWXExpire int

// 用户登录
// @Summary 用户登录
// @Description 用户登录，输入用户名和密码
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param node body models.ReqLoginUser true "用户名和密码"
// @Success 200 {object} models.RepUserInfo "返回用户信息"
// @Router /login [post]
func Login(c echo.Context) error {
	// 登录
	// swagger:route POST /v1/login 用户-账号 loginUser
	//
	// 用户登录，传入用户名和密码
	//
	//		Consumes:
	//		- application/json
	//		-application/x-protobuf
	// 		Responses:
	//			200: responseMessage
	reqBody := new(ryzeModels.ReqLoginUser)

	if err := c.Bind(reqBody); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
	}

	if len(reqBody.UserName) == 0 || len(reqBody.Password) == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请填写用户名或密码"))
	}

	userInfo, err := service.UserLogin(reqBody.UserName, reqBody.Password, c.RealIP())
	if err != nil {
		log.Println(errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "用户名或密码错误"))
	}

	if userInfo == nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "用户名或密码错误"))
	}

	resp := protocol.SuccessMsg("登录成功")
	resp.Data = userInfo

	// 存储session，返回cookie
	sessionUser := SessionUser{
		UID:        0,
		OID:        0,
		RID:        0,
		UserType:   userInfo.UserInfo.Type,
		SystemType: userInfo.UserInfo.System_type,
		GodInnerID: userInfo.UserInfo.User_inner_sn,
		Belong:     int64(userInfo.UserInfo.Belong),
		Version:    1,
		AppRegID:   reqBody.RegistrationID,
	}
	if _, err := sessionStore.SaveSessionUser(c, &sessionUser); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	// app用户加入已登录用户列表
	//if err := ent.AppNotificationSignup(userInfo.UserInfo.User_inner_sn, sid, reqBody.RegistrationID); err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "在线注册失败"))
	//}
	return c.JSON(http.StatusOK, resp)
}

// 用户登录
// @Summary 微信(移动端)用户登录
// @Description 微信用户登录，输入用户名和密码
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param node body models.ReqLoginUser true "用户名和密码"
// @Success 200 {object} models.RepUserInfo "返回用户信息"
// @Router /mobile_login [post]
func MobileLogin(c echo.Context) error {
	reqBody := new(ryzeModels.ReqLoginUser)

	if err := c.Bind(reqBody); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
	}

	if len(reqBody.UserName) == 0 || len(reqBody.Password) == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请填写用户名或密码"))
	}

	userInfo, err := service.UserLogin(reqBody.UserName, reqBody.Password, c.RealIP())
	if err != nil {
		log.Println(errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "用户名或密码错误"))
	}

	if userInfo == nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "用户名或密码错误"))
	}

	resp := protocol.SuccessMsg("登录成功")
	resp.Data = userInfo

	// 设置cookie
	// cookie过期时间设置为24小时，可以考虑再设置长一些（一年）
	//cookie := http.Cookie{
	//	Name:    sessionName,
	//	Value:   sid,
	//	Expires: time.Now().Add(time.Duration(sessionWXExpire)),
	//}
	//c.SetCookie(&cookie)

	sessionUser := SessionUser{
		UID:        0,
		OID:        0,
		RID:        0,
		UserType:   userInfo.UserInfo.Type,
		SystemType: userInfo.UserInfo.System_type,
		GodInnerID: userInfo.UserInfo.User_inner_sn,
		Belong:     int64(userInfo.UserInfo.Belong),
		Version:    1,
		AppRegID:   reqBody.RegistrationID,
	}
	if _, err := sessionStore.SaveSessionUser(c, &sessionUser); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "用户信息缓存失败"))
	}

	return c.JSON(http.StatusOK, resp)
}

// 获取用户信息
// @Summary 获取用户信息
// @Description 获取用户信息
// @Tags 用户
// @Accept  json
// @Produce  json
// @Success 200 {object} models.RepUserInfo "返回用户信息"
// @Router /user/user_info [get]
func GetSelfInfo(c echo.Context) error {
	//sid, _, user, err := getSession(c)
	user, err := sessionStore.GetSessionUser(c)

	if err != nil {

		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
	}

	//url := V1Host + "/userinfo"
	//resultJson, err := HandleResult2SimpleJson(RESTWithResponse(http.MethodGet, url, "", nil, user))
	//log.Println(resultJson)

	god, err := service.GetGodByID(user.GodInnerID)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
	}

	userInfo, err := service.GetUserInfo(god)
	if err != nil {
		log.Println(errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "用户信息获取失败"))
	}

	// 设置session时间
	//userInfo.SessionExpire = user.SessionExpire
	//if err := service.UpdateSession(sid, userInfo); err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "用户信息获取失败"))
	//}

	url := V1Host + SERVICE_URI_V1_USERINFO

	result, err := HandleResult2SimpleJson(RESTWithResponse(http.MethodGet, url, "", nil, user))
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	userInfo.WarehouseList = result.Get("info").Get("warehouseModelList")

	resp := protocol.SuccessMsg("成功")
	resp.Data = userInfo
	return c.JSON(http.StatusOK, resp)
}

// 获得用户菜单
// @Summary 用户菜单
// @Description 获得用户菜单
// @Tags 用户
// @Accept  json
// @Produce  json
// @Success 200 {object} models.Node[]
// @Router /menu/user_menu [get]
//func GetUserMenu(c echo.Context) error {
//	_, _, user, err := getSession(c)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	var menuTree []*models.Node
//	if user.UserInfo.Type == models.USER_TYPE_ADMIN {
//		// 初始管理员用户获取固定角色
//		// 平台管理员获取全权限
//		if user.UserInfo.System_type == models.TYPE_PLATFORM {
//			menuTree, err = service.UserMenu(user)
//		} else {
//			// 其他端管理员获得设定权限
//			menuTree, err = service.GetRoleTreeByID(models.KeyManagerTree(user.UserInfo.System_type))
//		}
//		if err != nil {
//			log.Println(errors.WithStack(err))
//			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "获取菜单失败，请稍后重试"))
//		}
//	} else {
//		menuTree, err = service.GetRoleTree(user.RID)
//		if err != nil {
//			log.Println(errors.WithStack(err))
//			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "获取菜单失败，请稍后重试"))
//		}
//	}
//	resp := protocol.SuccessMsg("获取成功")
//	resp.Data = menuTree
//
//	return c.JSON(http.StatusOK, resp)
//}

// 获得用户所属组织全部菜单
// @Summary 获得用户所属组织全部菜单
// @Description <管理员>获得用户所属组织全部菜单
// @Tags 用户
// @Accept  json
// @Produce  json
// @Success 200 {object} models.Node[]
// @Router /menu/org_menu [get]
//func GetOrgMenu(c echo.Context) error {
//	_, _, userInfo, err := getSession(c)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	strType := c.Param("type")
//	systemTemp := 0
//	if len(strType) > 0 {
//		systemTemp, err = strconv.Atoi(strType)
//		if err != nil {
//			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "参数错误"))
//		}
//	} else {
//		systemTemp = userInfo.UserInfo.System_type
//	}
//
//	menu, err := service.OrgMenu(systemTemp)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "获取菜单失败，请稍后重试"))
//	}
//
//	resp := protocol.SuccessMsg("获取成功")
//	resp.Data = menu.Children
//
//	return c.JSON(http.StatusOK, resp)
//}

// 获取对应组织菜单
// @Summary 获取对应组织菜单
// @Description <平台运维>获取对应组织菜单
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param id path int false "组织ID"
// @Success 200 {object} models.Node[]
// @Router /menu/org_menu/{type} [get]
// 用于swagger，方法在GetOrgMenu
func GetOrgMenuByType() {

}

// Logout 用户登出
// @Summary 用户登出
// @Description 用户登出，调用即可
// @Tags 用户
// @Accept  json
// @Produce  json
// @Success 200 {string} string "成功/失败信息"
// @Router /logout [post]
func Logout(c echo.Context) error {
	// 查找自己的user session
	userSession, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, protocol.FlatErrMeg(err)))
	}
	// 删除user session
	if err := sessionStore.DeleteSession(userSession); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessMsg("安全退出"))
}

// 管理员注销用户
// @Summary 管理员注销用户
// @Description 管理员把用户注销，只能给平台运维使用
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param id path string true "Session ID"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/kick_user/{id} [post]
func KickUser(c echo.Context) error {
	sid := c.Param("id")

	if len(sid) <= 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "参数错误"))
	}

	// 通过 user id 查找到session
	user, err := sessionStore.GetSessionUserByID(sid)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, protocol.FlatErrMeg(err)))
	}

	// 删除用户 session
	if err := sessionStore.DeleteSession(user); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "退出用户出错"))
	}

	return c.JSON(http.StatusOK, protocol.SuccessMsg("已经退出"))
}

// abandoned
// 重置密码
// @Summary 重置密码
// @Description 重置密码
// @Tags 用户
// @Accept  json
// @Produce  json
// @Success 200 {string} string "成功/失败信息"
// @Router /user/reset_my_password [post]
//func ResetMyPassword(c echo.Context) error {
//	_, _, user, err := getSession(c)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "未找到用户"))
//	}
//
//	if err := service.ResetPassword(user.UserInfo.User_inner_sn); err != nil {
//		log.Println("用户重置密码出错", errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "密码重置失败，请稍候再试"))
//	}
//
//	return c.JSON(http.StatusOK, protocol.SuccessMsg("密码重置成功，新密码通过短信已经发送到用户手机"))
//}

// 重置密码
// @Summary 重置密码
// @Description <管理员>重置其他用户密码
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param id path int true "用户id"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/reset_password/{id} [post]
func ResetPassword(c echo.Context) error {
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "未找到用户"))
	}

	// 用户是否存在
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "参数错误"))
	}

	targetUser, err := service.GetGodByID(id)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "系统错误，请稍后重试"))
	}

	if targetUser == nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "未找到用户"))
	}
	// 用户是否与当前用户同一机构
	if targetUser.Belong != int(user.Belong) || targetUser.System_type != user.SystemType {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "未找到用户"))
	}

	password := genResetPwd(8)

	if err := service.ChangePassword(id, password); err != nil {
		log.Println("重置密码出错;", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "密码重置失败，请稍候再试"))
	}

	if err := sendSMSResetPwd(targetUser.UserName, password); err != nil {
		log.Println("发送重置密码短信出错:", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_EXTERNAL, "发送密码短信失败"))
	}

	return c.JSON(http.StatusOK, protocol.SuccessMsg("密码重置成功，新密码通过短信已经发送到用户手机"))
}

// 修改密码
// @Summary 修改密码
// @Description 用户修改自己修改密码
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param JSON body models.ChangePassword true "新密码"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/change_my_password [put]
func ChangeMyPassword(c echo.Context) error {
	//TODO 检查密码强度
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
	}

	var changePassword ryzeModels.ChangePassword
	//var requestJson struct {
	//	Password string
	//}

	if err := c.Bind(&changePassword); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
	}

	god, err := service.GetGodByID(user.GodInnerID)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "修改密码错误，请稍后再试"))
	}
	if god == nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "用户未找到"))
	}

	if god.Passowrd != ryzeModels.PwdMD5(changePassword.Old) {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "旧密码错误"))
	}

	// 验证用户
	//god, err := service.AuthUser(god.UserName, changePassword.Old)
	if err != nil {
		log.Println("修改密码，验证用户旧密码出错：", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "修改密码错误，请稍后再试"))
	}

	if err := service.ChangePassword(user.GodInnerID, changePassword.Password); err != nil {
		log.Println("修改密码出错：", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "修改密码错误，请稍后再试"))
	}

	return c.JSON(http.StatusOK, protocol.SuccessMsg("成功修改密码"))
}

// 删除系统账号
// @Summary 删除系统账号
// @Description <管理员>删除系统账号
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param id path int true "用户ID"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/delete_user/{id} [delete]
func DeleteUser(c echo.Context) error {
	//验证要删除的用户是否与当前用户处于同一机构，防止猜测删除
	oprator, err := sessionStore.GetSessionUser(c)
	if err != nil {
		log.Println(errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
	}

	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		log.Println(errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
	}

	user, err := service.GetGodByID(id)
	if err != nil {
		log.Println(errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
	}

	if user == nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "用户未找到"))
	}

	if oprator.SystemType != user.System_type || oprator.Belong != int64(user.Belong) {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "用户未找到"))
	}

	if err := service.DeleteUser(user); err != nil {
		log.Println("删除用户出错", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
	}

	userMap := entModels.YrtUserMap{
		UserID:     0,
		SystemType: user.System_type,
		GodInnerSN: int64(user.Belong),
	}

	uid, err := userMap.GenUserID()
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	if err := ent.DeleteUserRole(uid); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessMsg("成功删除登录账号"))
}

// 修改登录电话
// @Summary 修改登录电话
// @Description <管理员>修改登录电话
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param node body models.EmployeeGod true "修改电话号码"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/change_username [put]
func ChangeUserName(c echo.Context) error {
	var employeeGod ryzeModels.EmployeeGod
	if err := c.Bind(&employeeGod); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "错误的请求数据"))
	}

	//	检查电话是否已经存在
	user, err := service.GetUserByName(employeeGod.Phone)
	if err != nil {
		log.Println("读取用户出错", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
	}

	if user != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_EXIST, "电话已存在，请先注销"))
	}

	//验证要删除的用户是否与当前用户处于同一机构，防止猜测删除
	oprator, err := sessionStore.GetSessionUser(c)

	if err != nil {
		log.Println(errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
	}

	user, err = service.GetGodByID(employeeGod.User_inner_sn)
	if err != nil {
		log.Println(errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
	}

	if user == nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "用户未找到"))
	}

	// 检查操作用户是否与指定用户为统一机构
	if user.System_type != oprator.SystemType || int64(user.Belong) != oprator.Belong {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "用户未找到"))
	}

	if err := service.ChangePhone(user, employeeGod.Phone); err != nil {
		log.Println("修改电话出错", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
	}

	return c.JSON(http.StatusOK, protocol.SuccessMsg("成功修改登录电话"))

}

// 获取用户列表
// @Summary 获取用户列表
// @Description <管理员>获取用户列表
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param pageNum query int true "页码"
// @Param pageSize query int true "每页条数"
// @Success 200 {object} models.RepUserList[]
// @Router /user/get_user_list [get]
func GetUserList(c echo.Context) error {
	oprator, err := sessionStore.GetSessionUser(c)
	if err != nil {
		log.Println(errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
	}

	pageNum := 0
	pageSize := 10
	pageNumStr := c.QueryParam("pageNum")
	pageSizeStr := c.QueryParam("pageSize")
	keyword := c.QueryParam("keyword")

	pn, err := strconv.Atoi(pageNumStr)
	if err == nil {
		pageNum = pn
	}

	ps, err := strconv.Atoi(pageSizeStr)
	if err == nil {
		pageSize = ps
	}

	// 获取用户信息
	userList, total, err := service.GetUsers(oprator.SystemType, int(oprator.Belong), pageNum, pageSize, keyword)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
	}

	// 获取角色信息
	var uIDs []int64
	for _, u := range userList {
		// 转换旧版UserID 为新的
		su := SessionUser{
			UID:        0,
			OID:        0,
			RID:        0,
			UserType:   USER_TYPE_NORMAL,
			SystemType: u.System_type,
			GodInnerID: u.User_inner_sn,
			Belong:     int64(u.Belong),
			Version:    USER_VER_1,
		}

		uIDs = append(uIDs, su.UserID())
	}
	usersRoles, err := ent.GetUsersRoleIDs(uIDs)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
	}
	uID2rID := make(map[int64]entModels.UserRole)
	for _, ur := range usersRoles {
		uID2rID[ur.User] = ur
	}

	for i, _ := range userList {
		userList[i].RoleID = uID2rID[uIDs[i]].Role
	}

	// 获取角色信息
	//for i, user := range userList {
	//	// userID = <system_type> + <user_inner_sn>
	//	// userID := strconv.Itoa(user.System_type) + strconv.FormatInt(user.User_inner_sn, 10)
	//	// TODO: 有出错暂不处理，应写入日志
	//	roleID, _ := ent.GetUserRoleKey(strconv.FormatInt(user.User_inner_sn, 10))
	//	userList[i].RoleID = roleID
	//}

	resp := protocol.SuccessMsg("获取成功")
	resp.Data = ryzeModels.RepUserList{
		UserList: userList,
		Total:    total,
	}
	return c.JSON(http.StatusOK, resp)
}

// 获取角色列表
// @Summary 获取角色列表
// @Description <管理员>获取角色列表
// @Tags 用户
// @Accept  json
// @Produce  json
// @Success 200 {object} models.Role[]
// @Router /user/get_role_list [get]
//func GetRoleList(c echo.Context) error {
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	roles, err := service.GetRoles(oprator.UserInfo.System_type, oprator.UserInfo.Belong)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	resp := protocol.SuccessMsg("获取成功")
//	resp.Data = roles
//
//	return c.JSON(http.StatusOK, resp)
//}

// 添加角色
// @Summary 添加角色
// @Description <管理员>添加角色
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param node body models.ReqAddRole true "新添角色"
// @Success 200 {object} models.Role
// @Router /user/add_role [post]
//func AddRole(c echo.Context) error {
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	req := new(models.ReqAddRole)
//	if err := c.Bind(req); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "错误的请求数据"))
//	}
//
//	role := new(models.Role)
//	role.Name = req.Name
//	role.Description = req.Description
//	role.Belong = oprator.UserInfo.Belong
//	role.System_type = oprator.UserInfo.System_type
//
//	// 检查是否有同名Role
//	has, err := service.RoleNameExist(role)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	if has {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_EXIST, "角色已经存在"))
//	}
//
//	if err := service.AddRole(role); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	resp := protocol.SuccessMsg("成功")
//	resp.Data = role
//	return c.JSON(http.StatusOK, resp)
//}

// 修改角色信息
// @Summary 修改角色信息
// @Description <管理员>修改角色信息
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param node body models.ReqUpdateRole true "修改名称和描述"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/update_role [put]
//func UpdateRole(c echo.Context) error {
//	req := new(models.ReqUpdateRole)
//	if err := c.Bind(req); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "错误的请求数据"))
//	}
//
//	//验证要删除的用户是否与当前用户处于同一机构，防止猜测修改
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//	role, err := service.GetRoleByID(req.Role_inner_sn)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//	if role == nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "角色未找到"))
//	}
//	if role.System_type != oprator.UserInfo.System_type && role.Belong != oprator.UserInfo.Belong {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "角色未找到"))
//	}
//	// 修改
//	updateRole := &models.Role{
//		Role_inner_sn: req.Role_inner_sn,
//		Description:   req.Description,
//		Name:          req.Name,
//	}
//	ok, err := service.UpdateRole(updateRole)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	if ok {
//		return c.JSON(http.StatusOK, protocol.SuccessMsg("修改成功"))
//	}
//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "无修改"))
//}

// 删除角色
// @Summary 删除角色
// @Description <管理员>删除角色
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param id path int true "角色ID"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/delete_role/{id} [delete]
//func DeleteRole(c echo.Context) error {
//	rid, err := strconv.Atoi(c.Param("id"))
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//	//验证要删除的用户是否与当前用户处于同一机构，防止猜测修改
//	//_, _, oprator, err := getSession(c)
//	oprator, err := sessionStore.GetSessionUser(c)
//
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//	role, err := ent.GetRole(rid)
//	//role, err := service.GetRoleByID(rid)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//	if role == nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "角色未找到"))
//	}
//	if role.Belong != oprator.GroupID() {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "角色未找到"))
//	}
//	// 查找角色是否与用户有对应
//	//has, err := service.ExistRoleUser(rid)
//	//if err != nil {
//	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	//}
//	//if has {
//	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "还有用户绑定该角色，请先解除角色绑定"))
//	//}
//	// 删除角色
//	if err := ent.DelRole(rid, false); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//	return c.JSON(http.StatusOK, protocol.SuccessMsg("删除角色成功"))
//}

// 更新角色权限(菜单/功能)树
// @Summary 更新角色权限(菜单/功能)树
// @Description <管理员>更新角色权限(菜单/功能)树
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param node body models.UpdateRoleTree true "菜单/功能树"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/update_role_tree [put]
//func UpdateRoleTree(c echo.Context) error {
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	roleTree := new(models.UpdateRoleTree)
//	if err := c.Bind(roleTree); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "数据错误"))
//	}
//
//	role, err := service.GetRoleByID(roleTree.RID)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	if role == nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "未找到角色"))
//	}
//
//	// 校验，只能处理本组织角色
//	if role.System_type != oprator.UserInfo.System_type || role.Belong != oprator.UserInfo.Belong {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "未找到角色"))
//	}
//
//	if err := service.UpdateRoleTreeNode(roleTree.RID, roleTree.Tree); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	return c.JSON(http.StatusOK, protocol.SuccessMsg("修改成功"))
//}

// 获取角色权限树
// @Summary 获取角色权限树
// @Description <管理员>获取角色权限树
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param id path int true "角色id"
// @Success 200 {object} models.RoleTreeNode
// @Router /user/get_role_tree/{id} [get]
//func GetRoleTree(c echo.Context) error {
//	rid, err := strconv.Atoi(c.Param("id"))
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "参数错误"))
//	}
//
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	role, err := service.GetRoleByID(rid)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	// 用户是否与当前用户同一机构
//	if role.Belong != oprator.UserInfo.Belong || role.System_type != oprator.UserInfo.System_type {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "未找到用户"))
//	}
//
//	tree, err := service.GetRoleTree(rid)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	resp := protocol.SuccessMsg("成功")
//	resp.Data = tree
//	return c.JSON(http.StatusOK, resp)
//}

// 获取角色权限树(仅ID)
// @Summary 获取角色权限树(仅ID)
// @Description <管理员>获取角色权限树(仅ID)
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param id path int true "角色id"
// @Success 200 {object} models.RoleTreeNode
// @Router /user/get_role_tree_ids/{id} [get]
//func GetRoleTreeIDs(c echo.Context) error {
//	rid, err := strconv.Atoi(c.Param("id"))
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "参数错误"))
//	}
//
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	role, err := service.GetRoleByID(rid)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	// 用户是否与当前用户同一机构
//	if role.Belong != oprator.UserInfo.Belong || role.System_type != oprator.UserInfo.System_type {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "未找到用户"))
//	}
//
//	root, err := service.GetRoleTreeIDs(rid)
//	if err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
//	}
//
//	resp := protocol.SuccessMsg("成功")
//	resp.Data = root
//	return c.JSON(http.StatusOK, resp)
//}

// 设定账号角色
// @Summary 设定账号角色
// @Description <管理员>设定账号角色
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param node body models.UserRole true "用户绑定角色"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/bind_role [put]
//func BindRole(c echo.Context) error {
//	userRole := new(models.UserRole)
//	if err := c.Bind(userRole); err != nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	_, _, oprator, err := getSession(c)
//	if err != nil {
//		log.Println(errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
//	}
//
//	// 验证 user 和 role都是本组织的
//	user, err := service.GetGodByID(userRole.User_inner_sn)
//	if err != nil {
//		log.Println("读取用户出错", errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
//	}
//	if user == nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "用户未找到"))
//	}
//
//	role, err := service.GetRoleByID(userRole.Role_inner_sn)
//	if err != nil {
//		log.Println("读取角色", errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
//	}
//
//	if role == nil {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "角色未找到"))
//	}
//
//	if oprator.UserInfo.System_type != role.System_type ||
//		oprator.UserInfo.Belong != role.Belong ||
//		oprator.UserInfo.System_type != user.System_type ||
//		oprator.UserInfo.Belong != user.Belong {
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "用户或角色未找到"))
//	}
//
//	// 增加或修改
//	// 检查关系是否存在
//	exist, err := service.ExistUserRole(userRole.User_inner_sn)
//	if err != nil {
//		log.Println("绑定用户角色出错", errors.WithStack(err))
//		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
//	}
//
//	if exist {
//		//	修改
//		_, err := service.UpdateUserRole(userRole)
//		if err != nil {
//			log.Println("修改用户绑定角色出错", errors.WithStack(err))
//			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
//		}
//
//	} else {
//		//	增加
//		_, err := service.AddUserRole(userRole)
//		if err != nil {
//			log.Println("增加用户绑定角色出错", errors.WithStack(err))
//			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "数据错误，请稍后再试"))
//		}
//	}
//
//	return c.JSON(http.StatusOK, protocol.SuccessMsg("设定成功"))
//}

// 找回密码发送验证码
// @Summary 找回密码发送验证码
// @Description 用户找回密码，发送验证码
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param mobile path string true "用户名/手机号"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/retrieve_pwd/code/{mobile} [get]
func GetBackPwdCode(c echo.Context) error {
	mobile := c.Param("mobile")
	if len(mobile) != 11 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "电话号码错误请重新输入"))
	}

	// 检查用户是否存在
	exist, err := service.ExistUserName(mobile)
	if err != nil {
		log.Println("用户找回密码，查找用户出错", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "系统错误请稍后再试"))
	}

	if !exist {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "用户不存在"))
	}

	code := genSMSVerifyCode()
	if err := service.SetSMSVerifyCode(mobile, code); err != nil {
		log.Println("用户找回密码，保存短信验证码失败", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "验证码发送失败，请稍后再试"))
	}

	if err := sendSMSVerifyCode(mobile, code); err != nil {
		log.Println("用户找回密码, 发送验证码失败", errors.WithStack(err))
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_EXTERNAL, "验证码发送失败，请稍后再试"))
	}

	return c.JSON(http.StatusOK, protocol.SuccessMsg("发送成功"))
}

// API用JSON
type ReqGetBackPassowrd struct {
	Name       string
	Password   string
	VerifyCode string
}

// 找回密码，验证短信验证码
// @Summary 找回密码，验证短信验证码
// @Description 用户找回密码时，验证短信验证码
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param JSON body ReqGetBackPassowrd true "验证码和手机号"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/retrieve_pwd/verify_code [post]
func GetBackPwdVerifyCode(c echo.Context) error {
	req := new(ReqGetBackPassowrd)
	if err := c.Bind(req); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "数据错误"))
	}

	if len(req.Name) != 11 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "手机号码错误"))
	}

	code, err := service.GetSMSVerifyCode(req.Name)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "系统错误请稍后再试"))
	}

	if len(code) == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "验证码过期"))
	}

	if code != req.VerifyCode {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "验证码错误"))
	}

	return c.JSON(http.StatusOK, protocol.SuccessMsg("验证成功"))
}

// 找回密码，修改密码
// @Summary 找回密码，修改密码
// @Description 用户找回密码，修改密码，要再次验证验证码
// @Tags 用户
// @Accept  json
// @Produce  json
// @Param Json body ReqGetBackPassowrd true "验证码，手机号和密码"
// @Success 200 {string} string "成功/失败信息"
// @Router /user/retrieve_pwd/change_password [post]
func GetBackPwdChangePassword(c echo.Context) error {
	req := new(ReqGetBackPassowrd)
	if err := c.Bind(req); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "数据错误"))
	}

	if len(req.Name) != 11 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "手机号码错误"))
	}

	code, err := service.GetSMSVerifyCode(req.Name)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "系统错误请稍后再试"))
	}

	if len(code) == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "验证码过期"))
	}

	if code != req.VerifyCode {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "验证码错误"))
	}

	user, err := service.GetUserByName(req.Name)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "系统错误请稍后再试"))
	}

	if user == nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "用户不存在"))
	}

	if err := service.ChangePassword(user.User_inner_sn, req.Password); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "修改密码失败请稍后再试"))
	}

	return c.JSON(http.StatusOK, protocol.SuccessMsg("修改成功"))
}

// 验证账号机构是否一致
func CheckConformity(oprator *ryzeModels.God, target *ryzeModels.God) bool {
	if oprator.System_type == target.System_type && oprator.Belong == target.Belong {
		return true
	}
	return false
}

// 获取角色所有端权限（索引）
// swagger:route GET /v1/user/get_role_permissions/{roleID} 用户-角色权限 getGroupRolePermissions
//
// 组织（管理员）获取组织内角色所有端树（索引），角色id
//
//		Responses:
//			200: respRolePermissions
func GetGroupRolePermissions(c echo.Context) error {
	roleIDStr := c.Param("id")

	roleID, err := strconv.Atoi(roleIDStr)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	//	TODO 判断角色是否属于改组织

	permissions, err := ent.GetRolePermissions(roleID)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(permissions))
}

// 删除用户对应的角色
// swagger:route DELETE /v1/user/del_user_role/{userID} 用户-账号 noneReqDelUserRole
//
// v3版本用户登录
//
// 		Responses:
//			200: responseMessage
func DelUserRole(c echo.Context) error {
	// 生成要删除的账号
	strID := c.Param("userID")
	id, err := strconv.ParseInt(strID, 10, 64)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.WrongParams())
	}
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	// TODO 比对操作用户、角色、被操作用户是属于同一组织
	if user.Version == USER_VER_1 {
		id, err = entModels.YrtUser2NewID(int64(user.SystemType), id)
		if err != nil {
			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
		}
	}
	// TODO 检查账号是否同一组织

	// 删除账号
	if err := ent.DeleteUserRole(id); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "删除用户角色失败"))
	}

	return c.JSON(http.StatusOK, protocol.Success())
}

// 新版本的用户登录
// swagger:route POST /ent/login 用户-账号 noneReqV3Login
//
// v3版本用户登录
//
// 		Responses:
//			200: respUserInfo
func LoginV3(c echo.Context) error {
	var post models.UserLogin
	if err := c.Bind(&post); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请填写用户名和密码"))
	}

	content, err := json.Marshal(post)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, err.Error()))
	}
	url := V3Host + SERVICE_URI_V3_LOGIN
	//var userInfo models.V3UserInfo
	//var user SessionUser
	//user.UID = 0
	//if err := REST(http.MethodPost, url, "application/json", strings.NewReader(string(content)), &userInfo, HandleResult2Json, nil); err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "用户名或密码错误"))
	//}

	resultJson, err := HandleResult2SimpleJson(
		RESTWithResponse(http.MethodPost,
			url,
			"application/json",
			strings.NewReader(string(content)),
			nil))

	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "用户名或密码错误"))
	}

	//if resultJson.Get()

	// 是否admin用户
	isAdmin, err := resultJson.Get("data").Get("user").Get("isAdmin").Bool()
	if err != nil {
		log.Println(err)
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "内部错误，请联系管理员"))
	}
	userType := 0
	if isAdmin {
		userType = USER_TYPE_ADMIN
	}
	// 用户id
	userID, err := resultJson.Get("data").Get("user").Get("id").Int64()
	if err != nil {
		log.Println(err)
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "内部错误，请联系管理员"))
	}
	// 组织id
	oid, err := resultJson.Get("data").Get("user").Get("orgId").Int64()
	if err != nil {
		log.Println(err)
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "内部错误，请联系管理员"))
	}

	user := SessionUser{
		UID:        userID,
		OID:        oid,
		AppRegID:   post.RegistrationID,
		RID:        0,
		UserType:   userType,
		SystemType: 0, // 新版本不再区分系统类型
		Version:    3, // 版本，用于区分旧版
	}

	if _, err := sessionStore.SaveSessionUser(c, &user); err != nil {
		if err == ErrRoleUnassigned {
			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, "用户尚未分配角色，请联系管理员"))
		} else {
			// TODO 真正的错误应该记录
			log.Println(err.Error())
			//return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "出现内部错误请稍后再试"))
			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
		}
	}
	return c.JSON(http.StatusOK, resultJson)
}

// 获取用户自身信息
// swagger:route GET /ent/user/self_info 用户-账号 noneReqGetUserSelfInfo
//
// 获取用户自身信息, 不需要传入参数
//
// 		Responses:
//			200: respUserInfo
func V3GetSelfInfo(c echo.Context) error {
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, protocol.FlatErrMeg(err)))
	}

	//var userInfo models.V3UserInfo
	url := V3Host + SERVICE_URI_V3_GET_USER_INFO + strconv.FormatInt(user.UID, 10)
	//if err := REST(http.MethodGet, url, "", nil, &userInfo, HandleResult2Json, user); err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, protocol.FlatErrMeg(err)))
	//}

	resultJson, err := HandleResult2SimpleJson(
		RESTWithResponse(http.MethodGet,
			url,
			"application/json",
			c.Request().Body,
			nil))

	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "内部错误，请联系管理员"))
	}

	return c.JSON(http.StatusOK, resultJson)
}

// 新增或修改用户账号
// swagger:route PUT /ent/account/save/{id} 用户-账号 reqSaveAccount
//
//	新增或者修改用户账号，新增账号传入的AccountID为空，返回修改/新建后的账号信息
//
//		Responses:
//			200: respSaveAccount
func V3SaveAccount(c echo.Context) error {
	// 检查数据

	// 获取employee id
	strEid := c.Param("id")
	employeeID, err := strconv.ParseInt(strEid, 10, 64)
	if err != nil || employeeID <= 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "人员ID错误"))
	}

	var account models.V3UserAccount
	if err := c.Bind(&account); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请求参数错误"))
	}

	account.EmployeeID = employeeID

	// TODO 检查角色是否存在
	//	新建账号
	v3Account := models.V3Account{
		Phone:    account.Username,
		RoleId:   account.RoleID,
		Username: account.Username,
	}
	if account.AccountID <= 0 && account.EmployeeID > 0 {
		// TODO 检查Employee 是否与操作者为同一组织
		url := V3Host + SERVICE_URI_V3_CREATE_ACCOUNT + "/" + strconv.FormatInt(account.EmployeeID, 10)

		content, err := json.Marshal(&v3Account)
		if err != nil {
			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请求参数序列化错误"))
		}

		//var resp models.V3YxkResponseJson
		newAccount := struct {
			ID int64 `json:"id"`
		}{}
		// 调用新建
		if err := REST(http.MethodPost, url, "", strings.NewReader(string(content)), &newAccount, HandleResult2Json, nil); err != nil {
			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
		}

		// 角色关联
		if err := ent.SetUserRole(newAccount.ID, account.RoleID); err != nil {
			//	TODO 失败回滚,删除账号
			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "角色设置失败"))
		}
		account.AccountID = newAccount.ID
		return c.JSON(http.StatusOK, protocol.SuccessData(&account))

	}

	// 修改
	//	修改账号
	// TODO 检查用户账号是否与操作者为同组织
	url := V3Host + SERVICE_URI_V3_CHANGE_USER + strconv.FormatInt(account.AccountID, 10)

	content, err := json.Marshal(&v3Account)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "请求参数序列化错误"))
	}

	if err := REST(http.MethodPut, url, "", strings.NewReader(string(content)), nil, HandleResult2Json, nil); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
	}
	//	修改角色关联
	// 角色关联
	if err := ent.SetUserRole(account.AccountID, account.RoleID); err != nil {
		//	TODO 失败回滚,删除账号
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "角色设置失败"))
	}
	// 返回
	return c.JSON(http.StatusOK, protocol.SuccessData(&account))
}

// 删除账号
// swagger:route DELETE /ent/account/delete/{id} 用户-账号 reqDeleteAccount
//
// 删除用户账号
//
// 		Responses:
//			200: respDeleteUser
func V3DeleteUser(c echo.Context) error {
	// 检查是否有账号
	// 否，返回错误
	// 是
	// 删除
	strAccountID := c.Param("id")
	accountID, err := strconv.ParseInt(strAccountID, 10, 64)
	if err != nil || accountID == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "账号ID错误"))
	}
	// 返回
	url := V3Host + SERVICE_URI_V3_DELETE_ACCOUNT + strAccountID
	if err := REST(http.MethodDelete, url, "", nil, nil, HandleResult2Json, nil); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
	}

	if err := ent.DeleteUserRole(accountID); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// V3GetEmployeeList 人力资源列表
// swagger:route POST /ent/org/employee/list 用户-账号 noneReqEmployeeList
//
// 获取人力资源列表
//
//		Responses:
//			200: respEmployeeList
func V3GetEmployeeList(c echo.Context) error {
	//	调用
	//	获取角色

	user, err := sessionStore.GetSessionUser(c)
	//postBody, err := simplejson.NewFromReader(c.Request().Body)
	//if err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "内部错误，请稍后再试"))
	//}
	//
	//postBody.Set("orgId", user.GroupID())
	//post, err := postBody.MarshalJSON()
	//if err != nil {
	//	return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "内部错误，请稍后再试"))
	//}
	//
	//r := bytes.NewReader(post)

	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "内部错误，请稍后再试"))
	}
	//resultJson := simplejson.New()
	url := V3Host + SERVICE_URI_V3_EMPLOYEE_LIST_X + "?" + c.QueryString()
	//	填入角色名称
	//if err := REST(http.MethodPost, url, "", c.Request().Body, resultJson, HandleResult2SimpleJson, user.UID); err != nil {
	resultJson, err := HandleResult2SimpleJson(RESTWithResponse(http.MethodPost, url, "", c.Request().Body, user))
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
	}

	i := 0
	next := true
	accountIds := make([]int64, 0)
	accountIdMap := make(map[int64]int)
	for next {
		record := resultJson.Get("data").GetIndex(i)
		_, exist := record.CheckGet("id")
		if !exist {
			next = false
			continue
		} else {
			id, err := record.Get("accountId").Int64()
			if err == nil && id > 0 {
				accountIds = append(accountIds, id)
				accountIdMap[id] = i
			}
			record.Set("roleId", 0)
			record.Set("roleName", "")
		}

		//id, err := record.Get("accountId").Int64()
		//if err != nil {
		//	next = false
		//} else {
		//	if id > 0 {
		//		accountIds = append(accountIds, id)
		//		accountIdMap[id] = i
		//	}
		//	record.Set("roleId", 0)
		//	record.Set("roleName", nil)
		//}

		i++
	}

	userRoles, err := ent.GetUsersRoles(accountIds)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, err.Error()))
	}

	for _, role := range userRoles {
		index, ok := accountIdMap[role.User]
		if ok {
			resultJson.Get("data").GetIndex(index).Set("roleId", role.ID)
			resultJson.Get("data").GetIndex(index).Set("roleName", role.Name)
		}
	}
	return c.JSON(http.StatusOK, resultJson)
}
