package service

type KV interface {
	// 设置hash中的key值
	HSet(string, string, interface{}) error
	// 设置hash中的key值
	HSetJSON(string, string, interface{}) error
	// 批量设置hask key值
	HMSet(string, map[string]string) error
	//存入全部hasi值
	HMSetAll(interface{}, string) error
	// 批量获取hash key值
	HMGet(interface{}, string, ...string) error
	// 获取所有hash key值
	HGetAll(interface{}, string) error
	// 获取hash中的key值
	HGet(string, string) (string, error)
	// 获取hash中的key值，并填充给结构体
	HGetToJSON(string, string, interface{}) error
	// 获取key值
	Get(string) (string, error)
	// 设置key值
	Set(string, string) error
	// 删除key/hash
	Del(string) error
	// 检查key是否存在
	Exist(string) (bool, error)
	// 设置过期时间
	Expire(string, int) error
	// 获取所有key
	Keys(match string) ([]string, error)
}
