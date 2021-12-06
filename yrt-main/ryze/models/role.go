package models

type Role struct {
	Role_inner_sn int `xorm:"autoincr pk"`
	Name          string
	System_type   int
	Belong        int
	Status        int
	Is_delete     int `json:"-"`
	Description   string
}

func (Role) TableName() string {
	return "role"
}

type SystemRole struct {
	Role_inner_sn string
	Name          string
	System_type   int
	Belong        int
	Status        int
	Is_delete     int `json:"-"`
	Description   string
}

type UserRole struct {
	Role_inner_sn int
	User_inner_sn int64
}

func (UserRole) TableName() string {
	return "user_role"
}

type ReqAddRole struct {
	Name        string
	Description string
}

type ReqUpdateRole struct {
	Role_inner_sn int
	Name          string
	Description   string
}
