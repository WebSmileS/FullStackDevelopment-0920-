package controller

import (
	"fmt"
	"log"
	"net/http"
	"strconv"

	"bjyixiu.com/gateway/protocol"
	"bjyixiu.com/ryze/models"
	"bjyixiu.com/ryze/service"
	"github.com/labstack/echo/v4"
	"github.com/pkg/errors"
)

func Auth(c echo.Context) (bool, error) {
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		// 获取不到Cookie或 Session已经过期
		c.JSON(http.StatusUnauthorized, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, err.Error()))
		return false, nil
	}

	// 多一层保险：获取用户信息失败
	if user == nil {
		c.JSON(http.StatusUnauthorized, protocol.ErrMsg(protocol.CODE_ERR_AUTH_FAILED, "未知异常，请稍后重试"))
		return false, nil
	}

	// TODO: 应该记录用户IP，用于判断，减少Cookie泄漏带来的安全风险

	// 更新session过期时间 TODO: 应该异步channel来处理
	//if userInfo.SessionExpire <= 0 {
	//	userInfo.SessionExpire = sessionExpire
	//}

	if err := sessionStore.ExtendSession(c); err != nil {

		//if err := service.RenewalSession(sid, userInfo.SessionExpire); err != nil {
		log.Println("续期session失败", errors.WithStack(err))
	}

	// 设置头信息
	SetHeader(user, c.Request())

	//if user.Version == 1 {
	//	// 用户id，对应god表id
	//	c.Request().Header.Add("uid", strconv.FormatInt(user.GodInnerID, 10))
	//	// 用户类型，0:平台，1:医院，2:经销商/维修商，3:厂商
	//	c.Request().Header.Add("systemType", strconv.Itoa(user.SystemType))
	//	// 用户组织id，平台id为0
	//	c.Request().Header.Add("oid", strconv.FormatInt(user.Belong, 10))
	//} else {
	//	// 用户id，对应god表id
	//	c.Request().Header.Add("uid", strconv.FormatInt(user.UID, 10))
	//	// 用户组织id，平台id为0
	//	c.Request().Header.Add("oid", strconv.FormatInt(user.OID, 10))
	//}

	//// 用户id，对应god表id
	//c.Request().Header.Add("uid", strconv.FormatInt(userInfo.UserInfo.User_inner_sn, 10))
	//// 用户类型，0:平台，1:医院，2:经销商/维修商，3:厂商
	//c.Request().Header.Add("systemType", strconv.Itoa(userInfo.UserInfo.System_type))
	//// 用户组织id，平台id为0
	//c.Request().Header.Add("oid", strconv.Itoa(userInfo.UserInfo.Belong))

	return true, nil
}

// 根据用户版本设置请求头
// Params:
//		user: session user
//		c : echo context
// Return:
//		none
func SetHeader(user *SessionUser, req *http.Request) {
	if user == nil || user.UID == 0 {
		return
	}

	if user.Version == 1 {
		// 用户id，对应god表id
		req.Header.Add("uid", strconv.FormatInt(user.GodInnerID, 10))
		// 用户类型，0:平台，1:医院，2:经销商/维修商，3:厂商
		req.Header.Add("systemType", strconv.Itoa(user.SystemType))
		// 用户组织id，平台id为0
		req.Header.Add("oid", strconv.FormatInt(user.Belong, 10))
	} else {
		// 用户id，对应user表id
		req.Header.Add("uid", strconv.FormatInt(user.UID, 10))
		// 用户组织id，平台id为0
		req.Header.Add("oid", strconv.FormatInt(user.OID, 10))

	}
}

// 验证登录情况，并返回sessionID
// 一般用于Websocket连接的验证，不做转发，不往Header注入用户信息
// 不更新session过期时间
// Params:
//		c : echo context
// Return:
//		sessionID: session id
//		error: 错误返回
func AuthConnection(c echo.Context) (string, error) {
	sessionID := c.FormValue("token")
	if len(sessionID) == 0 {
		return "", errors.New("参数错误")
	}
	user, err := sessionStore.GetSessionUserByID(sessionID)
	if err != nil {
		return "", fmt.Errorf("验证登录失败: %w", err)
	}
	//exist, userInfo, err := getSessionByID(sessionID)
	////sid, msg, userInfo, err := getSession(c)
	//if err != nil {
	//	// 后端错误，如数据库错误，或值错误
	//	return "", fmt.Errorf("读取session出错：%w", err)
	//}

	//if !exist {
	//	return "", errors.New("session不存在")
	//}

	// 获取用户信息失败
	if user == nil {
		return "", fmt.Errorf("用户信息异常")
	}

	// TODO: 应该记录用户IP，用于判断，减少Cookie泄漏带来的安全风险
	// 已经成功的加入在线用户注册表
	//if err := ent.WebNotificationSignup(user.UID, sessionID); err != nil {
	//	return "", fmt.Errorf("验证用户信息，注册在线用户出错: %w", err)
	//}
	return sessionID, nil
}

// TODO 准备删除
//func getSession(c echo.Context) (string, string, *models.RepUserInfo, error) {
//	cookie, err := c.Cookie(sessionStore.CookieName)
//	if err != nil {
//		return "", "请先登录", nil, nil
//	}
//
//	if len(cookie.Value) == 0 {
//		return "", "本网站需要允许Cookie才能访问", nil, nil
//	}
//
//	userInfo, err := service.GetSession(cookie.Value)
//	if err != nil {
//		return cookie.Value, "", nil, errors.Wrap(err, "获取session出错")
//	}
//	if userInfo == nil {
//		return cookie.Value, "登录过期请重新登录", nil, nil
//	}
//
//	return cookie.Value, "", userInfo, nil
//}

func getSessionByID(sid string) (bool, *models.RepUserInfo, error) {
	userInfo, err := service.GetSession(sid)
	if err != nil {
		return false, nil, errors.Wrap(err, "获取session出错")
	}

	if userInfo == nil {
		return false, nil, nil
	}
	return true, userInfo, nil
}

//func getUserFromSession(c echo.Context) (*models.RepUserInfo, error) {
//	_, str, user, err := getSession(c)
//	if len(str) > 0 {
//		return nil, errors.Wrap(err, "未找到用户")
//	}
//	if err != nil {
//		return nil, errors.Wrap(err, "数据出错")
//	}
//	return user, nil
//}

// TODO abandoned 验证是否操作同组织内角色，避免安全漏洞
//func verifyGroupRole(user *models.RepUserInfo, roleID int) error {
//	// 安全性验证：判断该角色是否属于该组织
//	role, err := ent.GetRole(roleID)
//	if err != nil {
//		return err
//	}
//	if role.Belong != user.UserInfo.GroupKey() {
//		return errors.New("角色未找到")
//	}
//	return nil
//}

func verifySameGroup(c echo.Context, roleID int) error {
	//user, err := getUserFromSession(c)
	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
		return err
	}

	r, err := ent.GetRole(roleID)
	if err != nil {
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
		return err
	}

	if r.Belong != user.GroupID() {
		err = errors.New("角色未找到")
		c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_FORBIDDEN, err.Error()))
		return err
	}
	return nil
}
