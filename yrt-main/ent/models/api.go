package models

// RESTful API
type API struct {
	ID    int    `gorm:"PRIMARY_KEY;AUTO_INCREMENT" json:"id"`
	API   string `gorm:"UNIQUE；SIZE:500" json:"api"` // api本身
	Count int    `json:"count"`                      // api被用到多少节点上
	State uint8  `json:"state"`                      // 状态
}

func (api API) TableName() string {
	return "permission_api"
}

type NodeAPI struct {
	APIID  int `gorm:"UNIQUE" json:"apiid"`
	NodeID int `json:"nodeid"`
}

func (nodeAPI NodeAPI) TableName() string {
	return "permission_node_rel_api"
}

func KeyAPI(api string) string {
	return "A_" + api
}

func KeyAPIRoles(api string) string {
	return KeyAPI(api) + "_ROLES"
}

func KeyAPIFeatures(api string) string {
	return KeyAPI(api) + "_FS"
}
