package manager

import (
	"encoding/json"
	"fmt"
	"os"
	"time"

	"bjyixiu.com/gateway/db"
	"bjyixiu.com/gateway/models"
	"github.com/pkg/errors"
	"github.com/siddontang/ledisdb/ledis"
)

const ()

type LedisManager struct {
	KEY_TIMELIME []byte
	db           *db.LedisDB
}

func NewLedisManager() *LedisManager {

	db, err := db.GetDB()
	if err != nil {
		fmt.Println("Ledis数据库链接失败：", err)
		os.Exit(1)
	}
	m := new(LedisManager)
	m.db = db

	// 设置Key
	m.KEY_TIMELIME = []byte("timeline")

	return m
}

func (m *LedisManager) Insert(h *models.HTTP) error {
	// TODO: 事务化
	// 存储Hash对象
	ids := []byte(h.ID)
	value, err := json.Marshal(h)
	if err != nil {
		return errors.Wrap(err, "序列化日志对象出错")
	}
	if err := m.db.Set(ids, value); err != nil {
		return errors.Wrap(err, "存储日志Hash出错")
	}
	// 设置过期时间
	expireAt := time.Now().Add(3 * 30 * 24 * time.Hour).Unix()
	m.db.ExpireAt(ids, expireAt)
	// 加入timeline

	sp := ledis.ScorePair{
		Score:  h.BeginTime.UnixNano(),
		Member: ids,
	}
	m.db.ZAdd(m.KEY_TIMELIME, sp)
	// 设置过期时间
	return nil
}

func (m *LedisManager) GetByID(key string) (*models.HTTP, error) {
	b, err := m.db.Get([]byte(key))
	if err != nil {
		return nil, errors.Wrap(err, "读取日志Hash出错")
	}
	h := new(models.HTTP)
	if err = json.Unmarshal(b, h); err != nil {
		return nil, errors.Wrap(err, "解析日志Hash出错")
	}

	return h, nil
}

func (m *LedisManager) GetByTime(begin int64, end int64) ([]*models.HTTP, error) {
	// 转为nanosecond
	// TODO：默认前端返回为millisecond, 应该
	beginTime := begin * 1000000
	endTime := end * 1000000

	sp, err := m.db.ZRangeByScore(m.KEY_TIMELIME, beginTime, endTime, 0, -1)
	if err != nil {
		return nil, errors.Wrap(err, "获取Key=>timeline出错")
	}

	keys := [][]byte{}
	for _, v := range sp {
		keys = append(keys, v.Member)
	}
	bs, err := m.db.MGet(keys...)
	if err != nil {
		return nil, errors.Wrap(err, "获取多Key => 日志Hash出错")
	}

	var hs []*models.HTTP

	for _, v := range bs {
		h := new(models.HTTP)
		if err = json.Unmarshal(v, h); err != nil {
			return nil, errors.Wrap(err, "解析日志Hash出错")
		}
		hs = append(hs, h)
	}
	return hs, nil
}
