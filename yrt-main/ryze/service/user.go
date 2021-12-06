package service

import (
	"encoding/json"
	"fmt"
	"log"
	"strconv"

	"bjyixiu.com/ryze/models"
	"github.com/go-xorm/xorm"
	"github.com/pkg/errors"
)

// 废弃 用户登录，调用存储过程(c_login)实现
func Userlogin(name, password, ip string) (string, *models.UserInfo, error) {

	passwordMD5 := models.PwdMD5(password)

	sql := `CALL yrt.c_login('` +
		name + `','` +
		passwordMD5 + `','` +
		ip + `',` +
		`@result_o, @error_code_o, @sql_state_o, @message_string_o)`
	result, err := sqlEngin.QueryString(sql)

	if err != nil {
		return "", nil, errors.Wrap(err, "调用存储过程c_login出错")
	}

	if len(result) == 0 {
		return "", nil, errors.New("登录失败，用户名或密码错误")
	}

	return "", nil, nil
}

// 验证用户
func AuthUser(name, password string) (*models.God, error) {
	passwordMD5 := models.PwdMD5(password)
	god := new(models.God)
	// has, err := sqlEngin.Where("desembarco_nombre=?", name).And("senha=?", passwordMD5).Get(god)
	has, err := sqlEngin.Where("desembarco_nombre=?", name).And("senha=?", passwordMD5).Get(god)

	if err != nil {
		return nil, errors.Wrap(err, "数据库错误，请稍后再试")
	}
	if !has {
		return nil, nil
	}

	return god, nil
}

// 登录，并存储session
func UserLogin(name, password, ip string) (*models.RepUserInfo, error) { //passwordMD5 := models.PwdMD5(password)
	////var god models.God
	//god := new(models.God)
	//has, err := sqlEngin.Where("desembarco_nombre=?", name).And("senha=?", passwordMD5).Get(god)
	//if err != nil {
	//	return "", nil, errors.Wrap(err, "数据库错误，请稍后再试")
	//}
	//if !has {
	//	return "", nil, nil
	//}

	god, err := AuthUser(name, password)
	if err != nil {
		return nil, errors.Wrap(err, "根据name和password获取god出错")
	}

	if god == nil {
		return nil, nil
	}

	userInfo, err := GetUserInfo(god)
	if err != nil {
		return nil, errors.Wrap(err, "获取用户信息出错")
	}

	// 设置session过期时间
	//userInfo.SessionExpire = sessionExpire
	//return SetSession(userInfo)
	return userInfo, nil
}

func GetAllUsers() ([]*models.God, error) {
	var users []*models.God

	if err := sqlEngin.Find(&users); err != nil {
		return nil, fmt.Errorf("获取所有用户出错: %w", err)
	}
	return users, nil
}

func GetAllUsersByOrg(systemType, belong int) ([]*models.God, error) {
	var users []*models.God
	if err := sqlEngin.Where("system_type = ? AND belong = ?", systemType, belong).Find(&users); err != nil {
		return nil, fmt.Errorf("获取组织所有用户出错:%w", err)
	}
	return users, nil
}

func GetUserIDByEmpoyee(systemType int, belong, employeeID int64) (int64, error) {
	var userID int64
	switch systemType {
	case models.TYPE_PLATFORM:
		sqlEngin.Select("user_inner_sn").Table("platform_employee").
			Where("employee_inner_sn = ?", employeeID).
			And("user_inner_sn is not null").
			Get(&userID)
	case models.TYPE_HOSPITAL:
		sqlEngin.Select("user_inner_sn").Table("hospital_employee").
			Where("hospital_inner_sn = ?", belong).
			And("employee_inner_sn = ?", employeeID).
			And("user_inner_sn is not null").
			Get(&userID)
	case models.TYPE_DEALER:
		sqlEngin.Select("user_inner_sn").Table("dealer_employee").
			Where("dealer_inner_sn = ?", belong).
			And("employee_inner_sn = ?", employeeID).
			And("user_inner_sn is not null").
			Get(&userID)
	case models.TYPE_FACTORY:
		sqlEngin.Select("user_inner_sn").Table("vendor_employee").
			Where("vendor_inner_sn = ?", belong).
			And("employee_inner_sn = ?", employeeID).
			And("user_inner_sn is not null").
			Get(&userID)
	default:
		return 0, errors.New("用户类型错误")
	}

	return userID, nil
}

func GetAllUsersByDepartment(systemType int, belong, department int64) ([]int64, error) {
	userIDs := make([]int64, 0)
	sql := ""
	switch systemType {
	case models.TYPE_PLATFORM:
		sql = `SELECT pe.user_inner_sn FROM platform_employee pe ` +
			`INNER JOIN platform_employee_platform_department pepd ON pe.employee_inner_sn = pepd.employee_inner_sn ` +
			`AND pe.login_account_status = 1 AND pe.user_inner_sn is not null ` +
			`AND pepd.department_inner_sn = ` + strconv.FormatInt(department, 10)
	case models.TYPE_HOSPITAL:
		sql = `SELECT he.user_inner_sn FROM hospital_employee he ` +
			`INNER JOIN hospital_employee_hospital_department hehd ON he.employee_inner_sn = hehd.employee_inner_sn ` +
			`AND he.login_account_status = 1 AND he.user_inner_sn is not null ` +
			`AND hehd.hospital_inner_sn = ` + strconv.FormatInt(belong, 10) + ` ` +
			`AND hehd.department_inner_sn = ` + strconv.FormatInt(department, 10)
	case models.TYPE_DEALER:
		sql = `SELECT de.user_inner_sn FROM dealer_employee de ` +
			`INNER JOIN dealer_employee_dealer_department dedd ON de.employee_inner_sn = dedd.employee_inner_sn ` +
			`AND de.login_account_status = 1 AND de.user_inner_sn is not null ` +
			`AND dedd.dealer_inner_sn = ` + strconv.FormatInt(belong, 10) + ` ` +
			`AND dedd.department_inner_sn = ` + strconv.FormatInt(department, 10)
	case models.TYPE_FACTORY:
		sql = `SELECT ve.user_inner_sn FROM vendor_employee ve ` +
			`INNER JOIN vendor_employee_vendor_department vevd ON ve.employee_inner_sn = vevd.employee_inner_sn ` +
			`AND ve.login_account_status = 1 AND ve.user_inner_sn is not null ` +
			`AND vevd.vendor_inner_sn = ` + strconv.FormatInt(belong, 10) + ` ` +
			`AND vevd.department_inner_sn = ` + strconv.FormatInt(department, 10)
		sqlEngin.SQL(sql).Find(&userIDs)
	default:
		return nil, errors.New("用户类型错误")
	}
	if err := sqlEngin.SQL(sql).Find(&userIDs); err != nil {
		return nil, fmt.Errorf("获取部门用户错误:%w", err)
	}
	return userIDs, nil
}

func GetUserInfo(god *models.God) (*models.RepUserInfo, error) {
	// 查询用户的雇员信息，并且获得所属机构的名称
	var employee models.EmployeeInfo
	var orgEmployee models.OrgEmployee
	var err error
	// TODO 系统生成的管理员账户需要单独处理，因为在各机构的表中没有对应的user_inner_sn字段
	switch god.System_type {
	case models.TYPE_PLATFORM:
		// 平台用户没有部门关联
		_, err = sqlEngin.Where("user_inner_sn=?", god.User_inner_sn).
			And("is_delete=0").
			Table("platform_employee").
			Get(&employee)
		orgEmployee.EmployeeInfo = employee
	case models.TYPE_HOSPITAL:
		_, err = sqlEngin.Where("user_inner_sn=?", god.User_inner_sn).
			And("hospital_employee.is_delete=0").
			Table("hospital_employee").
			Join("INNER", "hospital", "hospital.hospital_inner_sn=hospital_employee.hospital_inner_sn").
			Get(&orgEmployee)
	case models.TYPE_DEALER:
		_, err = sqlEngin.Where("user_inner_sn=?", god.User_inner_sn).
			And("dealer_employee.is_delete=0").
			Table("dealer_employee").
			Join("INNER", "dealer", "dealer.dealer_inner_sn=dealer_employee.dealer_inner_sn").
			Get(&orgEmployee)
	case models.TYPE_FACTORY:
		_, err = sqlEngin.Where("user_inner_sn=?", god.User_inner_sn).
			And("vendor_employee.is_delete=0").
			Table("vendor_employee").
			Join("INNER", "vendor", "vendor.vendor_inner_sn=vendor_employee.vendor_inner_sn").
			//Join("LEFT OUTER", "vendor_employee", "vendor.vendor_inner_sn=vendor_employee.vendor_inner_sn").
			Get(&orgEmployee)
	default:
		return nil, errors.New("用户类型错误")
	}

	if err != nil {
		return nil, errors.Wrap(err, "查询用户登录信息错误")
	}

	userInfo := &models.RepUserInfo{
		UserInfo:     god,
		EmployeeInfo: orgEmployee,
		Departments:  make([]models.EmployeeDepartmentName, 0),
	}

	// 查询用户的部门关系
	var department models.EmployeeDepartmentName
	var rows *xorm.Rows
	//departments := make([]models.EmployeeDepartment, 0)
	switch god.System_type {
	case models.TYPE_PLATFORM:
		rows, err = sqlEngin.
			Where("employee_inner_sn=?", employee.Employee_inner_sn).
			Join("INNER", "platform_department", "platform_department.department_inner_sn=platform_employee_platform_department.department_inner_sn").
			Rows(&department)
	case models.TYPE_HOSPITAL:
		rows, err = sqlEngin.
			Where("hospital_employee_hospital_department.employee_inner_sn=?", orgEmployee.Employee_inner_sn).
			And("hospital_employee_hospital_department.hospital_inner_sn=?", god.Belong).
			Table("hospital_employee_hospital_department").
			Join("INNER", "hospital_department", "hospital_department.department_inner_sn=hospital_employee_hospital_department.department_inner_sn").
			And("hospital_department.hospital_inner_sn=hospital_employee_hospital_department.hospital_inner_sn").
			And("hospital_department.status = 1").
			And("hospital_department.is_delete = 0").
			Rows(&department)
		//Find(departments)
	case models.TYPE_DEALER:
		rows, err = sqlEngin.
			Where("dealer_employee_dealer_department.employee_inner_sn=?", orgEmployee.Employee_inner_sn).
			And("dealer_employee_dealer_department.dealer_inner_sn=?", god.Belong).
			Table("dealer_employee_dealer_department").
			Join("INNER", "dealer_department", "dealer_department.department_inner_sn=dealer_employee_dealer_department.department_inner_sn").
			And("dealer_department.dealer_inner_sn=dealer_employee_dealer_department.dealer_inner_sn").
			And("dealer_department.status = 1").
			And("dealer_department.is_delete = 0").
			Rows(&department)
	case models.TYPE_FACTORY:
		rows, err = sqlEngin.
			Where("vendor_employee_vendor_department.employee_inner_sn=?", orgEmployee.Employee_inner_sn).
			And("vendor_employee_vendor_department.vendor_inner_sn=?", god.Belong).
			Table("vendor_employee_vendor_department").
			Join("INNER", "vendor_department", "vendor_department.department_inner_sn=vendor_employee_vendor_department.department_inner_sn").
			And("vendor_department.vendor_inner_sn=vendor_employee_vendor_department.vendor_inner_sn").
			And("vendor_department.status = 1").
			And("vendor_department.is_delete = 0").
			Rows(&department)
	default:
		return nil, errors.New("用户类型错误")
	}

	if err != nil {
		return nil, errors.Wrap(err, "查询用户部门关系出错")
	}

	defer rows.Close()
	for rows.Next() {
		if err := rows.Scan(&department); err != nil {
			return nil, errors.Wrap(err, "填充用户所属部门关系出错")
		}
		userInfo.Departments = append(userInfo.Departments, department)
	}

	// roleID, err := GetRoleIDByUID(userInfo.UserInfo.User_inner_sn)
	// if err != nil {
	// 	return nil, errors.Wrap(err, "用户登录时获取角色出错")
	// }

	// if roleID > 0 {
	// 	userInfo.RID = roleID
	// }

	return userInfo, nil
}

func UserLogout(id string) error {
	if err := kv.Del(id); err != nil {
		return errors.Wrap(err, "删除用户session出错")
	}
	return nil
}

// 暂存用户信息
func SetSession(userinfo *models.RepUserInfo) (string, *models.RepUserInfo, error) {
	//缓存ID，Token / sessionID
	id := models.NewID()

	userByte, err := json.Marshal(userinfo)
	if err != nil {
		return "", userinfo, errors.Wrap(err, "用户session序列化失败")
	}

	if err := kv.Set(id, string(userByte)); err != nil {
		return "", nil, errors.Wrap(err, "缓存用户信息出错")
	}

	//设置过期时间
	if err := RenewalSession(id, userinfo.SessionExpire); err != nil {
		return "", nil, errors.Wrap(err, "设置session过期时间出错")
	}

	return id, userinfo, nil
}

// 清除所有session
func ClearSession() error {

	keys, err := kv.Keys("*")
	if err != nil {
		return errors.Wrap(err, "清除session，获取所有key出错")
	}

	count := 0
	for _, key := range keys {
		ss, err := GetSession(key)
		if err != nil || ss == nil {
			continue
		} else {
			if err := kv.Del(key); err != nil {
				return errors.Wrap(err, "清除session，删除key"+key+"出错")
			}
			count++
			log.Println(count, "del session:", ss.UserInfo.UserName)
		}
	}

	return nil
}

// 更新Session
func UpdateSession(sid string, userInfo *models.RepUserInfo) error {
	userByte, err := json.Marshal(userInfo)
	if err != nil {
		return errors.Wrap(err, "用户session序列化失败")
	}

	if err := kv.Set(sid, string(userByte)); err != nil {
		return errors.Wrap(err, "缓存用户信息出错")
	}

	return nil
}

//获取用户session
func GetSession(sid string) (*models.RepUserInfo, error) {

	// userInfo := new(models.RepUserInfo)
	var userInfo models.RepUserInfo
	var god models.God
	userInfo.UserInfo = &god

	userStr, err := kv.Get(sid)
	if err != nil {
		return nil, errors.Wrap(err, "获取session出错")
	}

	if len(userStr) == 0 {
		return nil, nil
	}

	if err := json.Unmarshal([]byte(userStr), &userInfo); err != nil {
		return nil, errors.Wrap(err, "获取session unmarshal 出错")
	}

	if userInfo.UserInfo.User_inner_sn == 0 {
		return nil, nil
	}

	return &userInfo, nil
}

// session延期
func RenewalSession(sid string, expire int) error {
	return kv.Expire(sid, expire)
}

// 修改用户密码
func ChangePassword(user_inner_sn int64, plaintext string) error {
	pwdMD5 := models.PwdMD5(plaintext)
	//user := &models.God{
	//	Passowrd: pwdMD5,
	//}
	//result, err := sqlEngin.ID(user_inner_sn).Update(user)

	sql := "UPDATE god SET senha='" + pwdMD5 + "' WHERE god_inner_sn=" + strconv.FormatInt(user_inner_sn, 10)
	//result, err := sqlEngin.Prepare().Exec("UPDATE god SET senha = ? WHERE god_inner_sn = ?", pwdMD5, user_inner_sn)
	result, err := sqlEngin.Exec(sql)
	if err != nil {
		return errors.Wrap(err, "修改用户密码出错")
	}

	affected, err := result.RowsAffected()
	if err != nil {
		return errors.New("修改employee表登录关系不成功")
	}

	// TODO 与原值相同，考虑做更多处理
	if affected == 0 {
		return errors.New("修改不成功或与原值相同")
	}

	//if result == 0 {
	//
	//	return nil
	//}

	return nil
}

// 重置用户密码
// abandoned 随机重置密码，短信提醒
//func ResetPassword(user_inner_sn int64) error {
//	return ChangePassword(user_inner_sn, "a1234567")
//}

// 删除系统登录账号
func DeleteUser(user *models.God) error {
	// 修改employee表，再删除god表
	var employee models.EmployeeGod
	employee.User_inner_sn = 0
	employee.Login_account_status = 0

	tableName := "platform_employee"
	switch user.System_type {
	case models.TYPE_PLATFORM:
		tableName = "platform_employee"
	case models.TYPE_HOSPITAL:
		tableName = "hospital_employee"
	case models.TYPE_DEALER:
		tableName = "dealer_employee"
	case models.TYPE_FACTORY:
		tableName = "vendor_employee"
	}

	sql := `update ` + tableName + ` set user_inner_sn=null, login_account_status=0 where user_inner_sn=` + strconv.FormatInt(user.User_inner_sn, 10)

	result, err := sqlEngin.Exec(sql)
	if err != nil {
		return errors.Wrap(err, "修改employee表登录关系出错")
	}

	affected, err := result.RowsAffected()
	if err != nil {
		return errors.New("修改employee表登录关系不成功")
	}

	sql = `DELETE FROM god WHERE god_inner_sn = ` + strconv.FormatInt(user.User_inner_sn, 10)
	//affected, err = sqlEngin.ID(user.User_inner_sn).Unscoped().Delete(user)
	result, err = sqlEngin.Exec(sql)
	if err != nil {
		return errors.Wrap(err, "删除God表记录出错")
	}

	affected, err = result.RowsAffected()
	if err != nil {
		return errors.New("删除God表记录不成功")
	}

	if affected == 0 {
		return errors.New("删除God表记录不成功")
	}

	// TODO 如无影响行，则要做判断，数据是否还存在
	_, err = DeleteUserRole(user.User_inner_sn)
	if err != nil {
		return errors.Wrap(err, "删除用户角色出错")
	}

	return nil
}

// 根据ID查询用户
func GetGodByID(id int64) (*models.God, error) {
	user := new(models.God)
	has, err := sqlEngin.Where("god_inner_sn = ?", id).Get(user)
	if err != nil {
		return nil, errors.Wrap(err, "根据ID查询God数据出错")
	}

	if !has {
		return nil, nil
	}

	return user, nil
}

// 修改登录电话
func ChangePhone(user *models.God, phone string) error {
	var employee models.EmployeeGod
	employee.Phone = phone

	tableName := "platform_employee"
	switch user.System_type {
	case models.TYPE_PLATFORM:
		tableName = "platform_employee"
	case models.TYPE_HOSPITAL:
		tableName = "hospital_employee"
	case models.TYPE_DEALER:
		tableName = "dealer_employee"
	case models.TYPE_FACTORY:
		tableName = "vendor_employee"
	}

	sql := "UPDATE " + tableName + " SET phone = '" + employee.Phone + "' WHERE user_inner_sn = " + strconv.FormatInt(user.User_inner_sn, 10)

	//affected, err := sqlEngin.Prepare().Table(tableName).
	//	Where("user_inner_sn=?", user.User_inner_sn).
	//	Cols("phone").
	//	Update(&employee)

	result, err := sqlEngin.Exec(sql)

	if err != nil {
		return errors.Wrap(err, "修改employee表手机号出错")
	}

	_, err = result.RowsAffected()
	if err != nil {
		return errors.Wrap(err, "修改employee表手机号出错")
	}
	// TODO 检查是否有修改（影响行数）
	//if affected == 0 {
	//	return errors.New("修改employee表手机号不成功")
	//}
	//
	//updatePhone := models.God{
	//	UserName: phone,
	//	Mobile:   phone,
	//}

	sql = "UPDATE god SET telepon = '" + employee.Phone + "'" +
		" , desembarco_nombre = '" + employee.Phone + "'" +
		" WHERE god_inner_sn = " + strconv.FormatInt(user.User_inner_sn, 10)

	//affected, err = sqlEngin.Table("god").
	//	Where("god_inner_sn=?", user.User_inner_sn).
	//	Update(&updatePhone)

	result, err = sqlEngin.Exec(sql)

	if err != nil {
		return errors.Wrap(err, "修改god表登录名/手机号出错")
	}

	_, err = result.RowsAffected()
	if err != nil {
		return errors.Wrap(err, "修改god表登录名/手机号出错")
	}
	// TODO 检查是否有修改（影响行数）
	//if affected == 0 {
	//	return errors.New("修改god表登录名/手机号不成功")
	//}

	return nil
}

// 根据登录名获得用户
func GetUserByName(phone string) (*models.God, error) {
	var god models.God

	has, err := sqlEngin.Where("desembarco_nombre=?", phone).Get(&god)
	if err != nil {
		return nil, errors.Wrap(err, "根据登录名查询用户出错")
	}

	if !has {
		return nil, nil
	}

	return &god, nil
}

// 用户名是否存在
func ExistUserName(phone string) (bool, error) {
	var god models.God
	exist, err := sqlEngin.Where("desembarco_nombre=?", phone).Exist(&god)
	if err != nil {
		return false, errors.Wrap(err, "查询用户是否存在出错")
	}
	return exist, nil
}

// 获取用户列表
func GetUsers(systemType, orgID, pageNum, pageSize int, keyword string) ([]models.UserList, int, error) {
	//var god models.God
	users := make([]models.UserList, 0)
	//total, err := sqlEngin.Where("belong=?", orgID).
	//	And("system_type=?", systemType).
	//	And("type=?", models.USER_TYPE_NORMAL).
	//	Count(&god)
	//if err != nil {
	//	return nil, 0, errors.Wrap(err, "查询用户总数时出错")
	//}
	//
	//if total == 0 {
	//	return users, 0, nil
	//}

	queryKeyword := " "
	if len(keyword) > 0 {
		queryKeyword = " WHERE e.name LIKE '%" + keyword + "%' OR g.desembarco_nombre LIKE '%" + keyword + "%' "
	}

	sql := ""
	totalSql := ""
	switch systemType {
	case models.TYPE_PLATFORM:
		sql = `SELECT g.god_inner_sn AS user_inner_sn, 
					g.system_type, 
					g.belong, 
					g.desembarco_nombre AS user_name, 
					e.name, 
					e.position,					
					GROUP_CONCAT(d.name) AS department_name, 
					GROUP_CONCAT(d.department_inner_sn) AS department_inner_sn
				FROM god g 
				INNER JOIN platform_employee e ON e.user_inner_sn=g.god_inner_sn AND g.belong=` + strconv.Itoa(orgID) + ` AND g.system_type=` + strconv.Itoa(systemType) +
			` LEFT JOIN platform_employee_platform_department pd ON pd.employee_inner_sn=e.employee_inner_sn				
				LEFT JOIN platform_department d ON d.department_inner_sn=pd.department_inner_sn AND d.is_delete=0` +
			queryKeyword +
			`GROUP BY g.god_inner_sn  LIMIT ? OFFSET ?`

		totalSql = `SELECT count(DISTINCT g.god_inner_sn)	FROM god g 
				INNER JOIN platform_employee e ON e.user_inner_sn=g.god_inner_sn AND g.belong=` + strconv.Itoa(orgID) + ` AND g.system_type=` + strconv.Itoa(systemType) +
			` LEFT JOIN platform_employee_platform_department pd ON pd.employee_inner_sn=e.employee_inner_sn				
				LEFT JOIN platform_department d ON d.department_inner_sn=pd.department_inner_sn AND d.is_delete=0` +
			queryKeyword

	case models.TYPE_HOSPITAL:
		sql = `SELECT g.god_inner_sn AS user_inner_sn, 
					g.system_type, 
					g.belong, 
					g.desembarco_nombre AS user_name, 
					e.name, 
					e.position,					
					GROUP_CONCAT(d.name) AS department_name, 
					GROUP_CONCAT(d.department_inner_sn) AS department_inner_sn
				FROM god g 
				INNER JOIN hospital_employee e ON e.user_inner_sn=g.god_inner_sn AND g.belong=` + strconv.Itoa(orgID) + ` AND g.system_type=` + strconv.Itoa(systemType) +
			` LEFT JOIN hospital_employee_hospital_department hd ON hd.hospital_inner_sn=e.hospital_inner_sn AND hd.employee_inner_sn=e.employee_inner_sn
				LEFT JOIN hospital_department d ON d.hospital_inner_sn=hd.hospital_inner_sn AND d.department_inner_sn=hd.department_inner_sn AND d.is_delete=0` +
			queryKeyword +
			`GROUP BY g.god_inner_sn LIMIT ? OFFSET ?`

		totalSql = `SELECT count(DISTINCT g.god_inner_sn)
				FROM god g 
				INNER JOIN hospital_employee e ON e.user_inner_sn=g.god_inner_sn AND g.belong=` + strconv.Itoa(orgID) + ` AND g.system_type=` + strconv.Itoa(systemType) +
			` LEFT JOIN hospital_employee_hospital_department hd ON hd.hospital_inner_sn=e.hospital_inner_sn AND hd.employee_inner_sn=e.employee_inner_sn
				LEFT JOIN hospital_department d ON d.hospital_inner_sn=hd.hospital_inner_sn AND d.department_inner_sn=hd.department_inner_sn AND d.is_delete=0` +
			queryKeyword

	case models.TYPE_DEALER:
		sql = `SELECT g.god_inner_sn AS user_inner_sn, 
					g.system_type, 
					g.belong, 
					g.desembarco_nombre AS user_name, 
					e.name, 
					e.position,					
					GROUP_CONCAT(d.name) AS department_name, 
					GROUP_CONCAT(d.department_inner_sn) AS department_inner_sn
				FROM god g 
				INNER JOIN dealer_employee e ON e.user_inner_sn=g.god_inner_sn AND g.belong=` + strconv.Itoa(orgID) + ` AND g.system_type=` + strconv.Itoa(systemType) +
			` LEFT JOIN dealer_employee_dealer_department dd ON dd.dealer_inner_sn=e.dealer_inner_sn AND dd.employee_inner_sn=e.employee_inner_sn
				LEFT JOIN dealer_department d ON dd.dealer_inner_sn=d.dealer_inner_sn AND dd.department_inner_sn=d.department_inner_sn AND d.is_delete=0` +
			queryKeyword +
			`GROUP BY g.god_inner_sn  LIMIT ? OFFSET ?`

		totalSql = `SELECT count(DISTINCT g.god_inner_sn)
				FROM god g 
				INNER JOIN dealer_employee e ON e.user_inner_sn=g.god_inner_sn AND g.belong=` + strconv.Itoa(orgID) + ` AND g.system_type=` + strconv.Itoa(systemType) +
			` LEFT JOIN dealer_employee_dealer_department dd ON dd.dealer_inner_sn=e.dealer_inner_sn AND dd.employee_inner_sn=e.employee_inner_sn
				LEFT JOIN dealer_department d ON dd.dealer_inner_sn=d.dealer_inner_sn AND dd.department_inner_sn=d.department_inner_sn AND d.is_delete=0` +
			queryKeyword

	case models.TYPE_FACTORY:
		sql = `SELECT g.god_inner_sn AS user_inner_sn, 
					g.system_type, 
					g.belong, 
					g.desembarco_nombre AS user_name, 
					e.name, 
					e.position,					
					GROUP_CONCAT(d.name) AS department_name, 
					GROUP_CONCAT(d.department_inner_sn) AS department_inner_sn
				FROM god g 
				INNER JOIN vendor_employee e ON e.user_inner_sn=g.god_inner_sn AND g.belong=` + strconv.Itoa(orgID) + ` AND g.system_type=` + strconv.Itoa(systemType) +
			` LEFT JOIN vendor_employee_vendor_department vd ON vd.vendor_inner_sn=e.vendor_inner_sn AND vd.employee_inner_sn=e.employee_inner_sn
				LEFT JOIN vendor_department d ON vd.vendor_inner_sn=d.vendor_inner_sn AND vd.department_inner_sn=d.department_inner_sn AND d.is_delete=0` +
			queryKeyword +
			`GROUP BY g.god_inner_sn  LIMIT ? OFFSET ?`

		totalSql = `SELECT count(DISTINCT g.god_inner_sn)
				FROM god g 
				INNER JOIN vendor_employee e ON e.user_inner_sn=g.god_inner_sn AND g.belong=` + strconv.Itoa(orgID) + ` AND g.system_type=` + strconv.Itoa(systemType) +
			` LEFT JOIN vendor_employee_vendor_department vd ON vd.vendor_inner_sn=e.vendor_inner_sn AND vd.employee_inner_sn=e.employee_inner_sn
				LEFT JOIN vendor_department d ON vd.vendor_inner_sn=d.vendor_inner_sn AND vd.department_inner_sn=d.department_inner_sn AND d.is_delete=0` +
			queryKeyword

	}

	if err := sqlEngin.SQL(sql, pageSize, (pageNum-1)*pageSize).Find(&users); err != nil {
		return nil, 0, errors.Wrap(err, "查询用户列表出错")
	}

	total := 0
	values, err := sqlEngin.QueryString(totalSql)
	if err != nil {
		return nil, 0, errors.Wrap(err, "查询用户总数出错")
	}
	if len(values) > 0 {
		strTotal, ok := values[0]["count(DISTINCT g.god_inner_sn)"]
		if ok {
			if i, err := strconv.Atoi(strTotal); err != nil {
				return users, 0, errors.Wrap(err, "计算用户总数出错")
			} else {
				total = i
			}
		}
	}

	return users, total, nil
}

// 获取角色
func GetRoleByID(rid int) (*models.Role, error) {
	role := new(models.Role)
	has, err := sqlEngin.ID(rid).Get(role)
	if err != nil {
		return nil, errors.Wrap(err, "读取角色出错")
	}
	if has {
		return role, nil
	}
	return nil, nil
}

// 获取角色
func GetRoleByUID(uid int64) (*models.Role, error) {

	return nil, nil
}

// 获取角色ID
func GetRoleIDByUID(uid int64) (int, error) {
	userRole := new(models.UserRole)
	has, err := sqlEngin.Where("user_inner_sn=?", uid).Get(userRole)
	if err != nil {
		return 0, errors.Wrap(err, "查询用户角色关系出错")
	}

	if has {
		return userRole.Role_inner_sn, nil
	}

	return 0, nil
}

// 角色列表
func GetRoles(systemType, orgID int) ([]models.Role, error) {
	roles := make([]models.Role, 0)
	if err := sqlEngin.Where("system_type=? AND belong=? AND is_delete=0", systemType, orgID).Find(&roles); err != nil {
		return nil, errors.Wrap(err, "查询角色列表出错")
	}

	return roles, nil
}

// 新建角色
func AddRole(role *models.Role) error {
	affected, err := sqlEngin.Insert(role)
	if err != nil {
		return errors.Wrap(err, "新建角色，插入数据出错")
	}

	if affected > 0 {
		return nil
	}
	return errors.New("新建角色，插入数据失败")
}

// 修改角色属性：名称,描述
func UpdateRole(role *models.Role) (bool, error) {
	// 更新
	affected, err := sqlEngin.ID(role.Role_inner_sn).Cols("name", "description").Update(role)
	if err != nil {
		return false, errors.Wrap(err, "修改角色属性出错")
	}
	if affected > 0 {
		return true, nil
	}

	return false, nil
}

// 判断角色名称是否已经存在
func RoleNameExist(role *models.Role) (bool, error) {
	sqlWhere := `name = '` + role.Name + "'" +
		` AND is_delete = 0` +
		` AND system_type = ` + strconv.Itoa(role.System_type) +
		` AND belong = ` + strconv.Itoa(role.Belong)
	if role.Role_inner_sn > 0 {
		sqlWhere = sqlWhere + ` AND role_inner_sn != ` + strconv.Itoa(role.Role_inner_sn)
	}

	has, err := sqlEngin.Where(sqlWhere).Exist(role)
	if err != nil {
		return false, errors.Wrap(err, "检查角色名称是否重复出错")
	}
	// 角色存在
	if has {
		return true, nil
	}

	return false, nil
}

// 删除角色
// TODO 删除
func DeleteRole(rid int) (bool, error) {
	role := &models.Role{
		Role_inner_sn: rid,
	}

	affected, err := sqlEngin.ID(rid).Unscoped().Delete(role)
	if err != nil {
		return false, errors.Wrap(err, "删除角色出错")
	}

	if affected > 0 {
		if err := DeleteRoleTree(rid); err != nil {
			return true, errors.Wrap(err, "删除角色树出错")
		}
		return true, nil
	}

	return false, nil
}

// 是否有关联角色
func ExistRoleUser(rid int) (bool, error) {
	var userRole models.UserRole

	has, err := sqlEngin.Where("role_inner_sn = ?", rid).Exist(&userRole)
	if err != nil {
		return false, errors.Wrap(err, "检查用户-角色关系是否存在出错")
	}

	if has {
		return true, nil
	}
	return false, nil
}

// 是否有关联角色
func ExistUserRole(uid int64) (bool, error) {
	var userRole models.UserRole

	has, err := sqlEngin.Where("user_inner_sn = ?", uid).Exist(&userRole)
	if err != nil {
		return false, errors.Wrap(err, "检查用户-角色关系是否存在出错")
	}

	if has {
		return true, nil
	}
	return false, nil
}

// 增加关联用户角色
func AddUserRole(userRole *models.UserRole) (bool, error) {
	affected, err := sqlEngin.Insert(userRole)
	if err != nil {
		return false, errors.Wrap(err, "新建用户角色关联，插入数据出错")
	}

	if affected > 0 {
		return true, nil
	}
	return false, nil
}

// 修改关联用户角色
func UpdateUserRole(userRole *models.UserRole) (bool, error) {

	affected, err := sqlEngin.Where("user_inner_sn=?", userRole.User_inner_sn).Cols("role_inner_sn").Update(userRole)
	if err != nil {
		return false, errors.Wrap(err, "修改角色属性出错")
	}
	if affected > 0 {
		return true, nil
	}

	return false, nil
}

// 删除用户角色关联
func DeleteUserRole(uid int64) (bool, error) {
	userRole := &models.UserRole{
		User_inner_sn: uid,
	}
	affected, err := sqlEngin.Where("user_inner_sn=?", userRole.User_inner_sn).Unscoped().Delete(userRole)
	if err != nil {
		return false, errors.Wrap(err, "删除用户角色关联出错")
	}

	if affected > 0 {
		return true, nil
	}

	return false, nil
}

// TODO: 增加Role与Feature的对应关系
func AddMappingRole2Features() {

}

// TODO：删除Role与Feature的对应关系
func DelMappingRole2Features() {

}
