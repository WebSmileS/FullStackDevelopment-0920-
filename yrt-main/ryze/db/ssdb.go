package db

import (
	"encoding/json"
	"fmt"
	"os"

	"github.com/seefan/gossdb"
	"github.com/seefan/gossdb/conf"
)

type SSDB struct {
	pool *gossdb.Connectors
}

func NewSSDB() *SSDB {
	ssdb := new(SSDB)

	var err error
	ssdb.pool, err = gossdb.NewPool(&conf.Config{
		Host:             "127.0.0.1",
		Port:             8888,
		Password:         "WE28623xiweuu@s8!hd7swgds!3^%!97*&&&(shuwigrqw)",
		MinPoolSize:      5,
		MaxPoolSize:      50,
		AcquireIncrement: 5,
	})
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
		return nil
	}
	return ssdb
}

// Hash Set
func (ss *SSDB) HSet(name, key, value string) error {
	client, err := ss.pool.NewClient()
	if err != nil {
		return err
	}
	defer client.Close()

	return client.Hset(name, key, value)
}

// Hash Set & encode
func (ss *SSDB) HSetJSON(name string, key string, value interface{}) error {
	client, err := ss.pool.NewClient()
	if err != nil {
		return err
	}
	defer client.Close()

	j, err := json.Marshal(value)
	if err != nil {
		return err
	}

	return client.Hset(name, key, j)
}

// multi-set
func (ss *SSDB) HMSet(name string, keys ...string) error {
	keysCount := len(keys)

	if keysCount == 0 || keysCount%2 != 0 {
		// TODO 返回错误值
		return nil
	}

	client, err := ss.pool.NewClient()
	if err != nil {
		return err
	}
	defer client.Close()

	kv := make(map[string]interface{})
	for i := 0; i < keysCount; i = i + 2 {
		kv[keys[i]] = kv[keys[i+1]]
	}
	return client.MultiHset(name, kv)
}

// Hash Get
func (ss *SSDB) HGet(name, key string) (string, error) {
	client, err := ss.pool.NewClient()
	if err != nil {
		return "", err
	}
	defer client.Close()
	value, err := client.Hget(name, key)
	if err != nil {
		return "", err
	}
	return value.String(), nil
}

// Hash Get & tranpot to JSON
func (ss *SSDB) HGetToJSON(name, key string, json interface{}) error {
	client, err := ss.pool.NewClient()
	if err != nil {
		return err
	}
	defer client.Close()
	value, err := client.Hget(name, key)
	if err != nil {
		return err
	}
	if len(value) > 0 {
		return value.As(json)
	}
	return nil
}

// Key Get
func (ss *SSDB) Get(name string) (string, error) {
	client, err := ss.pool.NewClient()
	if err != nil {
		return "", err
	}
	defer client.Close()

	value, err := client.Get(name)
	if err != nil {
		return "", err
	}
	return value.String(), err
}

// Key Set
func (ss *SSDB) Set(name, key string) error {
	client, err := ss.pool.NewClient()
	if err != nil {
		return err
	}
	defer client.Close()

	return client.Set(name, key)
}

func (ss *SSDB) Del(name string) error {
	client, err := ss.pool.NewClient()
	if err != nil {
		return err
	}
	defer client.Close()

	return client.Del(name)
}

func (ss *SSDB) Exist(name string) (bool, error) {
	client, err := ss.pool.NewClient()
	if err != nil {
		return false, err
	}
	defer client.Close()
	return client.Exists(name)
}
