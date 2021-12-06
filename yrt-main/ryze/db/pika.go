package db

import (
	"encoding/json"
	"time"

	"bjyixiu.com/ryze/config"

	"github.com/gomodule/redigo/redis"
	"github.com/pkg/errors"
)

type PikaDB struct {
	pool *redis.Pool
}

func NewPikaDB(conf config.Pika) *PikaDB {
	pika := new(PikaDB)
	pika.pool = &redis.Pool{
		MaxIdle:     50,
		IdleTimeout: 240 * time.Second,
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

func (p *PikaDB) HMSet(name string, values map[string]string) error {
	conn := p.pool.Get()
	defer conn.Close()

	if len(values) == 0 {
		return nil
	}

	_, err := conn.Do("HMSET", redis.Args{}.Add(name).AddFlat(values)...)

	return err
}

func (p *PikaDB) HMSetAll(value interface{}, name string) error {
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

func (p *PikaDB) HGetAll(result interface{}, name string) error {
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

// func (p *PikaDB) HSetObject(name string, key string, value interface{}) error {
// 	conn := p.pool.Get()
// 	defer conn.Close()

// 	_, err := conn.Do("SET", name, value)
// 	if err != nil {
// 		return errors.Wrap(err, "数据库设置值出错")
// 	}

// 	return nil
// }
