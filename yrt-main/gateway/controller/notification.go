package controller

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"
	"strings"

	ryzeService "bjyixiu.com/ryze/service"

	"bjyixiu.com/ent/models"

	mob_push_sdk "github.com/saintarion/mobpush-websdkv3-go"

	"github.com/pkg/errors"

	mapset "github.com/deckarep/golang-set"

	"bjyixiu.com/gateway/protocol"
	"github.com/LyricTian/jpush-go"
	"github.com/labstack/echo/v4"
)

const (
	NTFY_TYPE_DELIVERY  = 1
	NTFY_TYPE_APPLY     = 2
	NTFY_TYPE_ORDER     = 3
	NTFY_TYPE_ORG_AUDIT = 4
)

const (
	NTFY_BODY_DELIVERY  = "%s已经发货，编号: %s"
	NTFY_BODY_APPLY     = "%s有新的耗材申领请求，申领单编号: %s"
	NTFY_BODY_ORDER     = "%s向您发送了新的采购订单，请及时处理，订单号: %s"
	NTFY_BODY_ORG_AUDIT = "有新的机构注册等待审核: %s"
)

type NotificationReq struct {
	Type       int
	Title      string
	UIDs       []int
	OID        int
	SystemType int
	Params     []interface{}
	ObjectID   string
}

type Notification struct {
	Type     int         `json:"type"`
	Title    string      `json:"title"`
	Body     string      `json:"body"`
	ObjectID string      `json:"objectID"`
	Extra    interface{} `json:"extra"`
	URI      string      `json:"uri"`
	Sound    string      `json:"sound"`
}

// 消息发送目标
// 组织
type TargetOrg struct {
	SystemType int
	OrgId      int64
}

// 部门
type TargetDepartments struct {
	SystemType   int
	OrgId        int64
	DepartmentId int64
}

// 人员
type TargetEmployees struct {
	SystemType int
	OrgId      int64
	EmployeeId int64
}

// 目标
type Targets struct {
	Orgs             []TargetOrg
	Departments      []TargetDepartments
	Employees        []TargetEmployees
	ExcludeEmployees []TargetEmployees
}

// 接收消息通知发送请求
type NotificationREQ struct {
	Id              string      `json:"id"`
	MetaKey         string      `json:"metaKey"`
	ApplicationName string      `json:"applicationName"`
	Sound           string      `json:"sound"`
	Title           string      `json:"title"`
	Content         string      `json:"content"`
	Uri             string      `json:"uri"`
	Params          interface{} `json:"params"`
	Permissions     []int       `json:"permissions"`
	Targets         Targets     `json:"targets"`
	Terminals       []string    `json:"terminals"`
}

// 注册App
func AppOnlineSignup(c echo.Context) error {
	registrationID := c.Param("id")
	if len(registrationID) == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "推送注册ID不能为空"))
	}

	user, err := sessionStore.GetSessionUser(c)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	user.AppRegID = registrationID

	if err := sessionStore.UpdateSessionUser(c, user); err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.JSON(http.StatusOK, protocol.Success())
}

// 获取有权限的用户 ID
func GetRightUserID(c echo.Context) error {
	nid, err := strconv.Atoi(c.FormValue("nid"))
	if err != nil || nid == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "Node ID couldn't be empty"))
	}
	gid, err := strconv.ParseInt(c.FormValue("oid"), 10, 64)
	if err != nil || gid == 0 {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "Organization ID couldn't be empty"))
	}

	users, err := ent.GetUsersByNodeID(nid, gid)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}

	return c.JSON(http.StatusOK, protocol.SuccessData(users))
}

// 发送消息
// swagger:route POST /v1/notification/send 消息-发送消息 NotificationREQ
//
// 发送消息
//
//		Responses:
//			200: none
func SendNotification(c echo.Context) error {
	// 接收发送要求
	var req NotificationREQ
	if err := c.Bind(&req); err != nil {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, protocol.FlatErrMeg(err)))
	}

	// 检查发送内容
	if (len(req.Targets.Employees) + len(req.Targets.Departments) + len(req.Targets.Orgs)) == 0 {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "缺少发送对象"))
	}

	var userIDs mapset.Set
	var err error
	// 获取发送给Yrt的用户ID列表
	if req.ApplicationName == APPLICATION_NAME_YRT {
		userIDs, err = getYrtNotifyUserIds(req)
		if err != nil {
			return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "发送消息到yrt用户出错 :"+protocol.FlatErrMeg(err)))
		}
	} else {
		// 获取发送给yxk的用户ID列表
		userIDs, err = getYxkNotifyUserIds(req)
		if err != nil {
			return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_WRONG_DATA, "发送消息到yxk用户出错 :"+protocol.FlatErrMeg(err)))
		}
	}

	// 发送
	onlineUserIDs, err := sendToClient(req, userIDs)
	if err != nil {
		return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	// 返回接收人列表
	data := struct {
		Uids []int64 `json:"uids"`
	}{Uids: onlineUserIDs}
	return c.JSON(http.StatusOK, protocol.SuccessData(data))
}

// 获取发送消息给yrt用户的id列表
func getYrtNotifyUserIds(req NotificationREQ) (mapset.Set, error) {
	orgIDs := mapset.NewSet()
	userIDs := mapset.NewSet()
	// 如果没有权限节点的要求，直接把所有人员的并集
	// 组织
	for _, org := range req.Targets.Orgs {
		// 找到所有组织
		oid := org.OrgId

		id, err := models.YrtGroup2NewID(int64(org.SystemType), org.OrgId)
		if err != nil {
			return nil, fmt.Errorf("获取发送消息给yrt指定组织用户的id出错: %w", err)
		}
		oid = id

		// 如果没有权限要求，要获取整个组织单位的用户
		if len(req.Permissions) == 0 {
			users, err := ryzeService.GetAllUsersByOrg(org.SystemType, int(org.OrgId))
			if err != nil {
				return nil, fmt.Errorf("获取发送消息给yrt指定组织用户的id出错: %w", err)
			}
			for _, user := range users {
				id, err := models.YrtUser2NewID(int64(user.System_type), user.User_inner_sn)
				if err != nil {
					return nil, fmt.Errorf("获取发送消息给yrt指定组织用户的id出错: %w", err)
				}
				userIDs.Add(id)
			}
		}

		orgIDs.Add(oid)
	}

	// 部门
	for _, org := range req.Targets.Departments {
		oid := org.OrgId

		id, err := models.YrtGroup2NewID(int64(org.SystemType), org.OrgId)
		if err != nil {
			return nil, fmt.Errorf("获取发送消息给yrt指定部门用户的id出错: %w", err)
		}
		oid = id
		//	部门下人员
		uids, err := ryzeService.GetAllUsersByDepartment(org.SystemType, org.OrgId, org.DepartmentId)
		if err != nil {
			return nil, fmt.Errorf("获取发送消息给yrt指定部门用户的id出错: %w", err)
		}
		for _, uid := range uids {
			id, err := models.YrtUser2NewID(int64(org.SystemType), uid)
			if err != nil {
				return nil, fmt.Errorf("获取发送消息给yrt指定部门用户的id出错: %w", err)
			}
			userIDs.Add(id)
		}
		orgIDs.Add(oid)
	}

	// 雇员
	for _, org := range req.Targets.Employees {
		oid := org.OrgId
		newOrgID, err := models.YrtGroup2NewID(int64(org.SystemType), org.OrgId)
		if err != nil {
			return nil, fmt.Errorf("获取发送消息给yrt指定用户的id出错: %w", err)
		}
		oid = newOrgID
		// 雇员转化为userID
		uid, err := ryzeService.GetUserIDByEmpoyee(org.SystemType, org.OrgId, org.EmployeeId)
		if err != nil {
			return nil, fmt.Errorf("获取发送消息给yrt指定用户的id出错: %w", err)
		}
		if uid > 0 {
			id, err := models.YrtUser2NewID(int64(org.SystemType), uid)
			if err != nil {
				return nil, fmt.Errorf("获取发送消息给yrt指定用户的id出错: %w", err)
			}
			userIDs.Add(id)
		}
		orgIDs.Add(oid)
	}
	// 获取排出发送的人员
	for _, org := range req.Targets.ExcludeEmployees {
		//
		uid, err := ryzeService.GetUserIDByEmpoyee(org.SystemType, org.OrgId, org.EmployeeId)
		if err != nil {
			return nil, fmt.Errorf("获取排出发送消息给yrt指定用户的id出错: %w", err)
		}
		if uid > 0 {
			id, err := models.YrtUser2NewID(int64(org.SystemType), uid)
			if err != nil {
				return nil, fmt.Errorf("获取排出发送消息给yrt指定用户的id出错: %w", err)
			}
			// 排出要发送的人员
			userIDs.Remove(id)
		}
	}

	// 如果限定了权限，则查找所有组织有权限的用户
	if len(req.Permissions) > 0 {
		rightUsers := mapset.NewSet()
		it := orgIDs.Iterator()
		for id := range it.C {
			var users []models.UserRole
			var err error
			if len(req.Permissions) == 1 {
				users, err = ent.GetUsersByNodeID(req.Permissions[0], id.(int64))
			} else {
				users, err = ent.GetUsersByNodeIDs(req.Permissions, id.(int64))
			}
			if err != nil {
				return nil, fmt.Errorf("获取有权限接收消息的yrt用户id出错: %w", err)
			}
			for _, u := range users {
				rightUsers.Add(u.User)
			}
		}
		// 获取交集
		userIDs = userIDs.Intersect(rightUsers)
	}
	return userIDs, nil
}

// 获取发送消息给yxk用户的id列表
//
func getYxkNotifyUserIds(req NotificationREQ) (mapset.Set, error) {
	//userIDs := mapset.NewSet()
	// 如果有权限节点的要求，先用权限节点过滤人员，然后直接求交集
	//if len(req.Permissions) > 0 {
	//	//	是否有组织，有组织的单独提出来查询
	//	if len(req.Targets.Orgs) > 0 {
	//		for _, org := range req.Targets.Orgs {
	//			uids, err := ent.GetUsersByNodeIDs(req.Permissions, org.OrgId)
	//			if err != nil {
	//				return nil, fmt.Errorf("查询组织中有权限的用户出错: %w", err)
	//			}
	//			for _, id := range uids {
	//				userIDs.Add(id)
	//			}
	//
	//		}
	//	}
	//	//	其余的先查询uid，然后查询对应的角色，合并角色，再查询角色是否有权限
	//	return nil, nil
	//}

	//查询出所有用户
	userIDs := mapset.NewSet()
	orgIds := make([]int64, 0)
	departmentIds := make([]int64, 0)
	employeeIds := make([]int64, 0)
	excludeEmployeeIds := make([]int64, 0)

	for _, org := range req.Targets.Orgs {
		orgIds = append(orgIds, org.OrgId)
	}
	for _, department := range req.Targets.Departments {
		departmentIds = append(departmentIds, department.DepartmentId)
	}
	for _, employee := range req.Targets.Employees {
		employeeIds = append(employeeIds, employee.EmployeeId)
	}
	for _, excludeEmployee := range req.Targets.ExcludeEmployees {
		excludeEmployeeIds = append(excludeEmployeeIds, excludeEmployee.EmployeeId)
	}
	//	无权限节点要求，直接查询uid就行
	ids, err := getUidsFromYxk(orgIds,
		departmentIds,
		employeeIds,
		excludeEmployeeIds)
	if err != nil {
		return nil, fmt.Errorf("调用yxk接口，获取对应uid出错: %w", err)
	}
	//没有权限要求的，直接返回uids
	if len(req.Permissions) == 0 {
		for _, id := range ids {
			userIDs.Add(id)
		}

		return userIDs, nil
	}

	//有权限节点要求的，查询用户角色，合并角色，排出无权限的用户
	//方法比较暴力，如发现有执行效率问题，可以再细分处理，先把组织拿出来单独处理

	// 批量查找用户角色
	userRoles, err := ent.GetUsersRoleIDs(ids)
	if err != nil {
		return nil, fmt.Errorf("获取要发送的yxk用户出错：%w", err)
	}
	// 合并相同的role
	role2user := make(map[int][]int64)
	for _, userRole := range userRoles {
		users, ok := role2user[userRole.Role]
		if !ok {
			users = make([]int64, 0)
		}
		users = append(users, userRole.User)
		role2user[userRole.Role] = users
	}

	// 剔除没有权限的角色
	for role, users := range role2user {
		having, err := ent.CheckRolePermissions(role, req.Permissions)
		if err != nil {
			return nil, fmt.Errorf("获取要发送的有权限的yxk用户出错: %w", err)
		}
		if having {
			for _, userID := range users {
				userIDs.Add(userID)
			}
		}
	}

	return userIDs, nil
}

// 发送消息通知给客户端
// 目前只发送给在线的人, 返回在线用户名单
func sendToClient(req NotificationREQ, userIDs mapset.Set) ([]int64, error) {
	// 封装要发送的消息
	notification := Notification{
		Title:    req.Title,
		Body:     req.Content,
		ObjectID: "",
		Extra:    req.Params,
		URI:      req.Uri,
		Sound:    req.Sound,
	}

	onlineUsers := make([]int64, 0)
	var appRegistration []string
	var webSession []string
	// 获取所有在线用户
	it := userIDs.Iterator()
	for elment := range it.C {
		id := elment.(int64)
		sess, err := sessionStore.GetOnlineUserByUid(id)
		if err != nil {
			return nil, fmt.Errorf("发送消息，获取在线用户出错: %w", err)
		}
		onlineUsers = append(onlineUsers, id)
		for _, session := range sess {
			if len(session.AppRegID) > 0 {
				appRegistration = append(appRegistration, session.AppRegID)
			} else {
				webSession = append(webSession, session.SID)
			}
		}
	}

	// 发送web
	if len(webSession) > 0 {
		var msg protocol.WSMessage
		msg.To = webSession
		msg.Type = MSG_TYPE_NOTIFICATION
		msg.Data = notification
		sendNotification2Web(&msg)
	}

	// 发送APP
	if len(appRegistration) > 0 {
		sendNotification2App(notification, appRegistration, req.ApplicationName)
	}
	return onlineUsers, nil
}

// 发送消息推送
// swagger:route POST /v1/notify 消息-发送消息 reqNotification
//
// 发送消息推送
//
// 		Responses:
//			200: none
func Notify(c echo.Context) error {
	// 接收信息
	var req NotificationReq
	if err := c.Bind(&req); err != nil {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	var ntfy Notification
	var webNID int
	var appNID int
	switch req.Type {
	case NTFY_TYPE_DELIVERY:
		ntfy.Title = "已发货"
		ntfy.URI = "/commonView/godownEntry"
		ntfy.Body = fmt.Sprintf(NTFY_BODY_DELIVERY, req.Params...)
		appNID = 104 // app验货单节点
		webNID = 178 // web采购入库
	case NTFY_TYPE_APPLY:
		ntfy.Title = "新的耗材申领"
		ntfy.URI = "/hospital/SuppliesClaim"
		ntfy.Body = fmt.Sprintf(NTFY_BODY_APPLY, req.Params...)
		webNID = 169 // web科室申领节点
		appNID = 93  // app申领单节点
	case NTFY_TYPE_ORDER:
		ntfy.URI = "/commonView/salesOrder"
		ntfy.Title = "新的订单"
		ntfy.Body = fmt.Sprintf(NTFY_BODY_ORDER, req.Params...)
		webNID = 150 // web销售订单节点
		appNID = 65  // app销售订单节点
	case NTFY_TYPE_ORG_AUDIT:
		ntfy.URI = "/ptm/agencyManage/RegistAudit"
		ntfy.Title = "新机构注册审核"
		ntfy.Body = fmt.Sprintf(NTFY_BODY_ORG_AUDIT, req.Params...)
		name := ""
		if len(req.Params) > 0 {
			name = req.Params[0].(string)
		}
		extra := struct {
			System_type string `json:"system_type"`
			Org_name    string `json:"org_name"`
		}{req.ObjectID, name}
		webNID = 26 // web 平台 注册审核节点
		appNID = 0  // 无app端
		ntfy.Extra = extra
	default:
		return c.JSON(http.StatusNotFound, protocol.ErrMsg(protocol.CODE_ERR_NOTFOUND, "未找到对应的消息类型"))
	}
	ntfy.Type = req.Type
	ntfy.ObjectID = req.ObjectID
	//group := strconv.Itoa(req.SystemType) + strconv.Itoa(req.OID)
	group, err := models.YrtGroup2NewID(int64(req.SystemType), int64(req.OID))
	if err != nil {
		return c.String(http.StatusOK, err.Error())
	}
	if err := send(ntfy, webNID, appNID, group); err != nil {
		return c.JSON(http.StatusBadRequest, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	}
	return c.String(http.StatusOK, "ok")
}

// 旧接口准备废弃
// 发送消息通知
func send(ntfy Notification, webNID int, appNID int, gid int64) error {
	web, err := ent.GetUsersByNodeID(webNID, gid)
	if err != nil {
		return fmt.Errorf("查找要发送的用户出错: %w", err)
	}
	app, err := ent.GetUsersByNodeID(appNID, gid)
	if err != nil {
		return fmt.Errorf("查找要发送的用户出错: %w", err)
	}
	// web端用户和app端用户做并集，获得所有要推送的用户
	webSet := mapset.NewSet()
	for _, u := range web {
		webSet.Add(u.User)
	}

	appSet := mapset.NewSet()
	for _, u := range app {
		appSet.Add(u.User)
	}

	all := webSet.Union(appSet)
	allSlice := all.ToSlice()
	if len(allSlice) == 0 {
		return nil
	}

	webSession := make([]string, 0)
	appRegistration := make([]string, 0)
	for _, id := range allSlice {
		users, err := sessionStore.GetOnlineUserByUid(id.(int64))
		if err != nil {
			return fmt.Errorf("发送消息，获取在线用户出错: %w", err)
		}
		hasWeb := webSet.Contains(id)
		hasAPP := appSet.Contains(id)

		for _, user := range users {
			if hasWeb && len(user.AppRegID) == 0 {
				webSession = append(webSession, user.SID)
			}
			if hasAPP && len(user.AppRegID) > 0 {
				appRegistration = append(appRegistration, user.AppRegID)
			}
		}

	}

	// 发送Web
	if len(webSession) > 0 {
		var msg protocol.WSMessage
		msg.To = webSession
		msg.Type = MSG_TYPE_NOTIFICATION
		msg.Data = ntfy
		sendNotification2Web(&msg)
	}

	// 发送APP
	if len(appRegistration) > 0 {
		sendNotification2App(ntfy, appRegistration, APPLICATION_NAME_YRT)
	}

	return nil
}

// 向web端推送消息
func sendNotification2Web(msg *protocol.WSMessage) {
	wsSender(msg)
}

// 向App端推送消息
func sendNotification2App(notify Notification, registrationIDs []string, appName string) {
	//log.Println("regid: ", registrationIDs)

	// 极光推送
	//notifyJPush(notify, registrationIDs)

	// mobTech
	notifyMobPush(notify, registrationIDs, appName)
}

// 极光推送
// Params:
//		notify: 消息提醒
//		registrationIDs: 推送的ID列表
// Return:
//		none
func notifyJPush(notify Notification, registrationIDs []string) {
	notification := jpush.NewNotification().SetAlert(notify.Body)
	notification.SetIOSNotification(jpush.
		NewIOSNotification().
		SetSound("default").
		SetAlert(notify.Body))
	notification.SetAndroidNotification(jpush.
		NewAndroidNotification().SetTitle(notify.Title).
		SetAlert(notify.Body))

	payload := &jpush.Payload{
		Platform:     jpush.NewPlatform().All(),
		Audience:     jpush.NewAudience().SetRegistrationID(registrationIDs...),
		Notification: notification,
		Options:      jpush.NewOptions().SetApnsProduction(!env_runtime_dev),
	}

	err := jpush.Push(context.Background(),
		payload,
		func(cxt context.Context, result *jpush.PushResult, err error) {
			if err != nil {
				log.Println("消息推送失败", err.Error())
			}
			//log.Println(registrationIDs)
			if env_runtime_dev {
				log.Println("jpush result:", result.String())
			}

		})

	if err != nil {
		log.Println("jPush推送失败", err.Error())
	}
}

// mobTech 推送
// Params:
//		notify: 消息提醒内容
//		registrationIDs: 推送的ID列表
// Return:
//		none
func notifyMobPush(notify Notification, registrationIDs []string, appName string) {
	mpush := mpushYRT
	if appName == mpushYXK.appName {
		mpush = mpushYXK
	}

	extras := make([]mob_push_sdk.PushMap, 0)
	extras = append(extras, mob_push_sdk.PushMap{
		Key:   "androidBadgeClass", // 华为特别设置, value值为activity.name否则不能显示角标
		Value: mpush.activityName,
	})
	//0： 开发，1：生产
	runtime := 0
	if !env_runtime_dev {
		runtime = 1
	}
	androidSounds := make([]string, 0)
	androidSounds = append(androidSounds, notify.Sound)
	push := mob_push_sdk.Push{
		Source: "webapi",
		Appkey: mpush.appKey,
		PushTarget: mob_push_sdk.PushTarget{
			Target: int(mob_push_sdk.TARGET_RIDS),
			Rids:   registrationIDs,
		},
		PushNotify: mob_push_sdk.PushNotify{
			Type:  1,           // 1 ：通知，2 ：自定义
			Plats: []int{1, 2}, // 1: android， 2：iOS
			//Plats:         []int{2}, // 1: android， 2：iOS
			ExtrasMapList: extras,
			Title:         notify.Title,
			Content:       notify.Body,
			IosProduct:    runtime,
			IosNotify: &mob_push_sdk.IosNotify{
				Sound:     notify.Sound,
				Badge:     "1",
				BadgeType: 2,
			},
			AndroidNotify: &mob_push_sdk.AndroidNotify{
				Style: 0,
				Warn:  "12",
				//Sound: androidSounds,
			},
		},
	}
	result, err := mpush.client.Push(push)

	if err != nil {
		log.Println("mPush 推送失败: ", errors.WithStack(err).Error())
	}
	if env_runtime_dev {
		log.Print("mPush推送结果:", string(result))
	}

}

// 调用yxk接口获取对应人员的Uid
// Params:
//		orgs: 组织机构id列表
//		departments: 部门id列表
//		employees: 雇员id列表
//		exclueEmployees: 推送的ID列表
// Return:
//		ids: user id 数组
//		error: 错误返回
func getUidsFromYxk(orgs, departments, employees, exclueEmployees []int64) ([]int64, error) {
	url := V3Host + SERVICE_URI_V3_USER_LISTUID
	post := struct {
		Orgs            []int64 `json:"orgs"`
		Departments     []int64 `json:"departments"`
		Employees       []int64 `json:"employees"`
		ExclueEmployees []int64 `json:"exclueEmployees"`
	}{Orgs: orgs, Departments: departments, Employees: employees, ExclueEmployees: exclueEmployees}
	content, err := json.Marshal(post)
	if err != nil {
		return nil, fmt.Errorf("请求内容序列化出错%w", err)
	}

	uids := make([]int64, 0)

	if err := REST(http.MethodPost,
		url,
		"application/json",
		strings.NewReader(string(content)),
		&uids,
		HandleResult2Json,
		nil); err != nil {
		return nil, fmt.Errorf("获取对应的uid出错%w", err)
	}

	return uids, nil
}
