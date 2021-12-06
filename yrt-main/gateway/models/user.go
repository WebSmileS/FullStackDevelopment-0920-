package models

type V3YxkResponseJson struct {
	Code          int         `json:"code"`
	ExtData       interface{} `json:"extData"`
	InternalError string      `json:"internalError"`
	Msg           string      `json:"msg"`
	Data          interface{} `json:"data"`
}

type V3LoginResponse struct {
	Code          int         `json:"code"`
	ExtData       interface{} `json:"extData"`
	InternalError string      `json:"internalError"`
	Msg           string      `json:"msg"`
	Data          V3UserInfo  `json:"data"`
}

// swagger:model
type UserLogin struct {
	UserName       string `json:"username"`
	Password       string `json:"password"`
	RegistrationID string `json:"registrationID"`
}

// swagger:model
type V3UserInfo struct {
	Employee          V3Employee     `json:"employee"`
	Org               V3Org          `json:"org"`
	PrimaryDepartment V3Department   `json:"primaryDepartment"`
	RelDepartments    []V3Department `json:"relDepartments"`
	User              V3User         `json:"user"`
}

type V3Employee struct {
	ID                    int64  `json:"id"`
	Name                  string `json:"name"`
	OrgId                 int64  `json:"orgId"`
	AccountId             int64  `json:"accountId"`
	CertificateNo         string `json:"certificateNo"`
	Description           string `json:"description"`
	email                 string `json:"email"`
	EmployeeNo            string `json:"employeeNo"`
	ParentEmployeeId      int64  `json:"parentEmployeeId"`
	ParentEmployeeName    string `json:"parentEmployeeName"`
	Phone                 string `json:"phone"`
	Position              string `json:"position"`
	PrimaryDepartmentId   int64  `json:"primaryDepartmentId"`
	PrimaryDepartmentName string `json:"primaryDepartmentName"`
	Sex                   string `json:"sex"`
	CreatedTime           string `json:"createdTime"`
}

type V3Org struct {
	ID          int64  `json:"id"`
	Name        string `json:"name"`
	accountId   int64  `json:"accountId"`
	Address     string `json:"address"`
	Eescription string `json:"description"`
	Email       string `json:"email"`
	OrgType     string `json:"orgType"`
	ParentOrgId int64  `json:"parentOrgId"`
	Phone       string `json:"phone"`
	RegionCode  string `json:"regionCode"`
	Status      string `json:"status"`
	Uscc        string `json:"uscc"`
	CreatedTime string `json:"createdTime"`
}

type V3Department struct {
	ID                 int64  `json:"id"`
	Name               string `json:"name"`
	DepartmentNo       string `json:"departmentNo"`
	Description        string `json:"description"`
	Keywords           string `json:"keywords"`
	LeaderId           int64  `json:"leaderId"`
	LeaderName         string `json:"leaderName"`
	OrgId              int64  `json:"orgId"`
	ParentDepartmentId int64  `json:"parentDepartmentId"`
	ChildrenNumber     int    `json:"childrenNumber"`
	Phone              string `json:"phone"`
}

type V3User struct {
	ID               int64  `json:"id"`
	Username         string `json:"username"`
	IsAdmin          bool   `json:"isAdmin"`
	IsDisabled       bool   `json:"isDisabled"`
	LastPasswordTime string `json:"lastPasswordTime"`
	PasswordCycle    int    `json:"passwordCycle"`
	Phone            string `json:"phone"`
	RoleId           int64  `json:"roleId"`
	OrgId            int64  `json:"orgId"`
}

// 新建用户
// swagger:model
type V3Account struct {
	//IsDisabled    bool   `json:"isDisabled"`
	//Password      string `json:"password"`
	//PasswordCycle int    `json:"passwordCycle"`
	Phone    string `json:"phone"`
	RoleId   int    `json:"roleId"`
	Username string `json:"username"`
}

// 用户账号信息
// swagger:model
type V3UserAccount struct {
	EmployeeID int64  `json:"employeeId"`
	AccountID  int64  `json:"accountId"`
	Username   string `json:"username"`
	RoleID     int    `json:"roleId"`
}

// 分页
type Page struct {
}
