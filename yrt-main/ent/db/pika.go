package db

import (
	"encoding/json"
	"fmt"
	"time"

	"bjyixiu.com/ent/config"
	"github.com/gomodule/redigo/redis"
	"github.com/pkg/errors"
)

type PikaDB struct {
	pool *redis.Pool
}

func NewPikaDB(conf config.Pika) *PikaDB {
	pika := new(PikaDB)
	pika.pool = &redis.Pool{
		MaxIdle:     20,
		MaxActive:   50,
		IdleTimeout: 30 * time.Second,
		Dial: func() (redis.Conn, error) {
			c, err := redis.Dial("tcp", conf.Host)
			if err != nil {
				return nil, errors.Wrap(err, "连接pika数据库出错")
			}

			if len(conf.Password) > 0 {
				if _, err := c.Do("AUTH", conf.Password); err != nil {
					c.Close()
					return nil, errors.Wrap(err, "Auth pika数据库出错")
				}
			}

			return c, nil
		},
		TestOnBorrow: func(c redis.Conn, t time.Time) error {
			if time.Since(t) < time.Minute {
				return nil
			}
			_, err := c.Do("PING")
			return errors.Wrap(err, "pika数据库健康检查失败")
		},
	}
	return pika
}

func (p *PikaDB) HSet(name string, key string, value interface{}) error {
	conn := p.pool.Get()
	defer conn.Close()

	_, err := conn.Do("HSET", name, key, value)
	if err != nil {
		return errors.Wrap(err, "数据库设置Hash值出错")
	}

	return nil
}

func (p *PikaDB) HSetJSON(name string, key string, value interface{}) error {
	j, err := json.Marshal(value)
	if err != nil {
		return err
	}

	return p.HSet(name, key, string(j))
}

func (p *PikaDB) HMSet(name string, values map[string]interface{}) error {
	conn := p.pool.Get()
	defer conn.Close()

	if len(values) == 0 {
		return nil
	}

	_, err := conn.Do("HMSET", redis.Args{}.Add(name).AddFlat(values)...)

	return err
}

func (p *PikaDB) HMSetAll(name string, value interface{}) error {
	conn := p.pool.Get()
	defer conn.Close()

	if value == nil {
		// TODO 返回错误值
		return nil
	}

	if _, err := conn.Do("HMSET", redis.Args{}.Add(name).AddFlat(value)...); err != nil {
		return errors.Wrap(err, "保存Hash出错")
	}

	return nil
}

func (p *PikaDB) HMGet(result interface{}, name string, keys ...string) error {
	conn := p.pool.Get()
	defer conn.Close()

	if len(keys) == 0 {
		return nil
	}

	v, err := redis.Values(conn.Do("HMGET", redis.Args{}.Add(name).AddFlat(keys)...))

	if err != nil {
		return errors.Wrap(err, "获取多个Hash值出错")
	}

	if err := redis.ScanSlice(v, result, keys...); err != nil {
		return errors.Wrap(err, "获取Hash值与预期不符")
	}
	return nil
}

func (p *PikaDB) HGetAll(name string, result interface{}) error {
	conn := p.pool.Get()
	defer conn.Close()

	v, err := redis.Values(conn.Do("HGETALL", name))
	if err != nil {
		return errors.Wrap(err, "获取所有Hash值出错")
	}

	if err := redis.ScanStruct(v, result); err != nil {
		return errors.Wrap(err, "获取Hash值与预期不符")
	}
	return nil
}

func (p *PikaDB) HGetAllSlice(name string, result interface{}) error {
	conn := p.pool.Get()
	defer conn.Close()

	v, err := redis.Values(conn.Do("HGETALL", name))
	if err != nil {
		return errors.Wrap(err, "获取所有Hash(Slice)值出错")
	}

	if err := redis.ScanSlice(v, result); err != nil {
		return errors.Wrap(err, "获取Hash(Slice)值与预期不符")
	}
	return nil
}

func (p *PikaDB) HGetAllMap(name string) (map[string]string, error) {
	conn := p.pool.Get()
	defer conn.Close()

	v, err := redis.Values(conn.Do("HGETALL", name))
	if err != nil {
		return nil, errors.Wrap(err, "获取所有Hash(map)值出错")
	}

	hashMap := make(map[string]string)

	for i := 0; i < len(v); i = i + 2 {
		key := fmt.Sprintf("%s", v[i])
		value := fmt.Sprintf("%s", v[i+1])
		hashMap[key] = value
	}

	return hashMap, nil
}

// 获取哈希下所有值，第一个返回值为key slice，第二个为value slice
func (p *PikaDB) HGetAllOrderly(name string) ([]string, []string, error) {
	conn := p.pool.Get()
	defer conn.Close()

	v, err := redis.Values(conn.Do("HGETALL", name))
	if err != nil {
		return nil, nil, errors.Wrap(err, "获取所有Hash(map)值出错")
	}

	var keys []string
	var values []string

	for i := 0; i < len(v); i = i + 2 {
		key := fmt.Sprintf("%s", v[i])
		value := fmt.Sprintf("%s", v[i+1])
		keys = append(keys, key)
		values = append(values, value)
	}
	return keys, values, nil
}

// 设置哈希下所有值，第一个参数为HashId 第二个参数为key， 第三个为值
func (p *PikaDB) HSetAllOrderly(name string, keys []string, values []string) error {
	if len(keys) == 0 || len(values) == 0 {
		return nil
	}
	conn := p.pool.Get()
	defer conn.Close()

	if len(keys) != len(values) {
		return errors.New("key 和 value 长度不符")
	}

	var args []interface{}
	args = append(args, name)
	for i, key := range keys {
		args = append(args, key)
		args = append(args, values[i])
	}

	if _, err := conn.Do("HMSET", args...); err != nil {
		return errors.Wrap(err, "保存所有Hash出错")
	}
	return nil
}

func (p *PikaDB) HGet(name string, key string) (string, error) {
	conn := p.pool.Get()
	defer conn.Close()

	r, err := conn.Do("HGET", name, key)
	if err != nil {
		return "", errors.Wrap(err, "数据库获取Hash值出错")
	}
	if r == nil {
		return "", nil
	}
	str, err := redis.String(r, nil)
	if err != nil {
		return "", errors.Wrap(err, "获取Hash值后转化为string出错")
	}
	return str, nil
}

func (p *PikaDB) HDel(name string, key string) error {
	conn := p.pool.Get()
	defer conn.Close()

	_, err := conn.Do("HDEL", name, key)
	if err != nil {
		return errors.Wrap(err, "数据库获取Hash值出错")
	}
	return nil
}

func (p *PikaDB) HLen(name string) (int, error) {
	conn := p.pool.Get()
	defer conn.Close()

	r, err := conn.Do("HLEN", name)
	if err != nil {
		return 0, errors.Wrap(err, "查询Hash长度出错")
	}
	if r == nil {
		return 0, nil
	}
	count, err := redis.Int(r, nil)
	if err != nil {
		return 0, errors.Wrap(err, "查询Hash长度出错")
	}
	return count, nil
}

func (p *PikaDB) HGetToJSON(name string, key string, j interface{}) error {

	str, err := p.HGet(name, key)
	if err != nil {
		return errors.Wrap(err, "数据库获取Hash值出错")
	}

	if len(str) == 0 {
		return nil
	}

	if err := json.Unmarshal([]byte(str), j); err != nil {
		return errors.Wrap(err, "获取Hash后转化为json出错")
	}
	return nil

}

func (p *PikaDB) HIncrby(name string, key string, incr int) (int, error) {
	conn := p.pool.Get()
	defer conn.Close()

	replay, err := conn.Do("HINCRBY", name, key, incr)
	if err != nil {
		return 0, errors.Wrap(err, "Hash整数增量出错")
	}

	count := replay.(int)
	return count, nil
}

func (p *PikaDB) Get(name string) (string, error) {
	conn := p.pool.Get()
	defer conn.Close()

	r, err := conn.Do("GET", name)
	if err != nil {
		return "", errors.Wrap(err, "数据库获取值出错")
	}

	if r == nil {
		return "", nil
	}

	str, err := redis.String(r, nil)
	if err != nil {
		return "", errors.Wrap(err, "转化值时出错")
	}
	return str, nil
}

func (p *PikaDB) Set(name string, value string) error {
	conn := p.pool.Get()
	defer conn.Close()

	_, err := conn.Do("SET", name, value)
	if err != nil {
		return errors.Wrap(err, "数据库设置值出错")
	}

	return nil
}

func (p *PikaDB) Del(name string) error {
	conn := p.pool.Get()
	defer conn.Close()

	_, err := conn.Do("DEL", name)
	if err != nil {
		return errors.Wrap(err, "删除节点失败")
	}
	return nil
}

func (p *PikaDB) Exist(name string) (bool, error) {
	conn := p.pool.Get()
	defer conn.Close()

	i, err := redis.Int(conn.Do("EXISTS", name))
	if err != nil {
		return false, errors.Wrap(err, "检查节点失败")
	}
	if i > 0 {
		return true, nil
	}
	return false, nil
}

func (p *PikaDB) Expire(name string, second int) error {
	conn := p.pool.Get()
	defer conn.Close()

	if _, err := conn.Do("EXPIRE", redis.Args{}.Add(name).Add(second)...); err != nil {
		return errors.Wrap(err, "设置过期时间出错")
	}
	return nil
}

// 获取所有key
func (p *PikaDB) Keys(match string) ([]string, error) {
	conn := p.pool.Get()
	defer conn.Close()

	keys, err := redis.Strings(conn.Do("KEYS", redis.Args{}.Add(match)...))
	if err != nil {
		return nil, errors.Wrap(err, "获取所有key出错")
	}

	return keys, nil
}

func (p *PikaDB) TestHGet(name string, key string, conn redis.Conn) (string, redis.Conn, error) {
	if conn == nil {
		conn = p.pool.Get()
	}

	r, err := conn.Do("HGET", name, key)
	if err != nil {
		return "", conn, errors.Wrap(err, "数据库获取Hash值出错")
	}
	if r == nil {
		return "", conn, nil
	}
	str, err := redis.String(r, nil)
	if err != nil {
		return "", conn, errors.Wrap(err, "数据库获取Hash值后转化为string出错")
	}
	return str, conn, nil
}

// func (p *PikaDB) HSetObject(name string, key string, value interface{}) error {
// 	conn := p.pool.Get()
// 	defer conn.Close()

// 	_, err := conn.Do("SET", name, value)
// 	if err != nil {
// 		return errors.Wrap(err, "数据库设置值出错")
// 	}

// 	return nil
// }
