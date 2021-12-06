package controller

const (
	ONLINE_USERS = "online_users"

	ADMIN_ROLE_NAME = "starter"

	USER_TYPE_NORMAL = 0
	USER_TYPE_ADMIN  = 1

	SESSION_KEY_PREFIX = "SS_"

	USER_VER_1 = 1
	USER_VER_3 = 3

	SERVICE_URI_V3_LOGIN           = "/v3/user/login"
	SERVICE_URI_V3_GET_USER_INFO   = "/v3/user/info/"
	SERVICE_URI_V3_CREATE_ACCOUNT  = "/v3/account/create/"
	SERVICE_URI_V3_CHANGE_USER     = "/v3/account/change/"
	SERVICE_URI_V3_DELETE_ACCOUNT  = "/v3/account/delete/"
	SERVICE_URI_V3_EMPLOYEE_LIST   = "/v3/org/employee/list"
	SERVICE_URI_V3_EMPLOYEE_LIST_X = "/v3/org/employee/list/x"
	SERVICE_URI_V3_USER_LISTUID    = "/v3/user/listuid"

	SERVICE_URI_V1_COMM_DOWNLOAD = "/v1/comm/download"
	SERVICE_URI_V1_USERINFO      = "/userinfo"

	MSG_TYPE_NOTIFICATION = "notification"

	APPLICATION_NAME_YRT = "YRT"
	APPLICATION_NAME_YXK = "YXK"
)

var V1Host string // 医润通v2版本服务器
var V3Host string // 医修库v3版本服务器
