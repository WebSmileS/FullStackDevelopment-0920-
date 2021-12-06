package models

type God struct {
	User_inner_sn  int64 `xorm:"autoincr pk 'god_inner_sn'"`
	System_type    int
	Belong         int
	Password_cycle int
	Status         int
	Is_delete      int    `json:"-"`
	UserName       string `xorm:"desembarco_nombre"`
	Mobile         string `xorm:"telepon"`
	Passowrd       string `xorm:"senha" json:"-"`
	Type           int
}

func (God) TableName() string {
	return "god"
}

func (user *God) GroupKey() int {
	//return user.System_type * 100
	//return strconv.Itoa(user.System_type) + strconv.Itoa(user.Belong)
	digits := user.Belong / 10
	id := user.System_type
	if digits > 0 {
		id = id*digits + user.Belong
	}
	if id == 0 {
		id = 1
	}
	return id
}

type EmployeeInfo struct {
	Employee_inner_sn int
	Sex               int
	Status            int
	Name              string
	Employee_sn       string
	Position          string
	Photo_url         string
	Description       string
}

func (EmployeeInfo) TableName() string {
	return "platform_employee"
}

type EmployeeDepartment struct {
	Employee_inner_sn   int
	Department_inner_sn int
}

func (EmployeeDepartment) TableName() string {
	return "platform_employee_platform_department"
}

type EmployeeDepartmentName struct {
	EmployeeDepartment `xorm:"extends"`
	Name               string
}

type UserInfo struct {
	God
	EmployeeInfo
	Departments []EmployeeDepartment
}

type RepUserInfo struct {
	UserInfo     *God
	EmployeeInfo OrgEmployee
	Departments  []EmployeeDepartmentName
	RID          int // v1版角色key值
	// Role          string // v2版角色key值
	SessionExpire int
	WarehouseList interface{} `json:"warehouseList"` // 关联仓库列表
}

type OrgEmployee struct {
	EmployeeInfo `xorm:"extends"`
	OrgName      string `xorm:"name"`
}

type EmployeeGod struct {
	User_inner_sn        int64
	Login_account_status int
	Phone                string
}

func (EmployeeGod) TableName() string {
	return "platform_employee"
}

type GodEmpoyeeDepartmentName struct {
	God            `xorm:"extends"`
	EmployeeInfo   `xorm:"extends"`
	DepartmentID   string `xorm:"department_id"`
	DepartmentName string `xorm:"department_name"`
}

func (GodEmpoyeeDepartmentName) TableName() string {
	return "god"
}

type RepUserList struct {
	Total    int
	UserList []UserList
}

type UserList struct {
	User_inner_sn   int64
	System_type     int
	Belong          int
	User_name       string
	Name            string
	Position        string
	Department_name string
	Role_inner_sn   string
	RoleID          int // v2版角色key值
}

func (UserList) TableName() string {
	return "god"
}
