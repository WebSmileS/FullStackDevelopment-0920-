package models

import (
	"errors"
	"fmt"
	"sync"
	"time"

	"github.com/rs/xid"
)

//ID（主要为Node ID）生成器，用雪花算法，限制为53bit
//ID生成位数限制为53bit，因为Javascript中最大整数为53位
//单节点每毫秒最大可以生成256个ID
const (
	SNOW_WORKER_BITS uint8 = 5 // 节点（机器）数量，Max=31：最大32个节点
	SNOW_NUMBER_BITS uint8 = 8 // 每节点毫秒内可生成id数量，Max=255/10ms:最大256

	SNOW_WORKER_MAX int64 = -1 ^ (-1 << SNOW_WORKER_BITS) // 节点最大值，防止溢出
	SNOW_NUMBER_MAX int64 = -1 ^ (-1 << SNOW_NUMBER_BITS) // 每节点毫秒内id最大值，防止溢出

	SNOW_TIME_SHIFT   uint8 = SNOW_WORKER_BITS + SNOW_NUMBER_BITS // 时间戳左移量
	SNOW_WORKER_SHIFT uint8 = SNOW_NUMBER_BITS                    // 节点左移量

	SNOW_EPOCH int64 = 1577808000 //开始时间为2020.01.01 00:00:00
)

type Worker struct {
	mu        sync.Mutex // 线程安全
	timestamp int64      // 上一次生成id的时间
	workerID  int64      // 当前节点ID
	number    int64      // 当前毫秒内已经生成的id序列号
}

// 实例化一个节点
// Params:
//		workerID : 节点ID
// Return:
//		*Worker: 节点实例
// 		error: 错误返回
func NewWorker(workerID int64) (*Worker, error) {
	if workerID < 0 || workerID > SNOW_WORKER_MAX {
		return nil, errors.New("invalide workerID")
	}

	return &Worker{
		timestamp: 0,
		workerID:  workerID,
		number:    0,
	}, nil
}

// 生成ID
// Params:
//		none
// Return:
//		ID: int64 ID
func (w *Worker) GenID() int64 {
	// 锁定
	w.mu.Lock()
	defer w.mu.Unlock()

	now := time.Now().UnixNano() * 100 // 10ms
	if w.timestamp == now {
		w.number++
		if w.number > SNOW_NUMBER_MAX {
			for now <= w.timestamp {
				now = time.Now().Unix() * 100 // 10ms
			}
		}
	} else {
		w.number = 0
		w.timestamp = now
	}

	id := now<<SNOW_TIME_SHIFT | (w.workerID << SNOW_WORKER_SHIFT) | (w.number)
	return id
}

// 生成ID根据指定的timestamp和number
// Params:
//		none
// Return:
//		ID: int64 ID
func (w *Worker) DirectGenID() int64 {
	return w.timestamp<<SNOW_TIME_SHIFT |
		(w.workerID << SNOW_WORKER_SHIFT) |
		(w.number)
}

// 生成ID
// Params:
//		none
// Return:
//		ID: int64 ID
func GenID() int64 {
	return idGenerator.GenID()
}

// 为yrt转换的group生成新的ID
// client id被固定为31
// Params:
//		systemType: 组织类型：0.平台 1.医院 2.经销商 3.厂商
//		belong: 组织 Inner_SN
// Return:
//		ID: int64 ID
func YrtGroup2NewID(systempType, belong int64) (int64, error) {
	return GenIDWithGivenValue(31, belong+SNOW_EPOCH*100, systempType)
}

// 为yrt转换的user生成新的ID
// client id被固定为30
// Params:
//		systemType: 组织类型：0.平台 1.医院 2.经销商 3.厂商
//		userID:  God_Inner_SN
// Return:
//		ID: int64 ID
func YrtUser2NewID(systempType, userID int64) (int64, error) {
	return GenIDWithGivenValue(30, userID+SNOW_EPOCH*100, systempType)
}

// 用指定的变量生成ID
// Params:
//		clinetID: 端ID
//		timestamp: 时间戳
//		number: 毫秒内顺序
// Return:
//		ID: int64 ID
//		error: 错误返回
func GenIDWithGivenValue(client, timestamp, number int64) (int64, error) {
	worker, err := NewWorker(client)
	if err != nil {
		return 0, fmt.Errorf("新建ID生成器出错: %w", err)
	}

	worker.timestamp = timestamp
	worker.number = number
	return worker.DirectGenID(), nil
}

// 返回UUID，目前仅用于session
// Params:
//		none
// Return:
//		uuid: session id
func GenUUID() string {
	return xid.New().String()
}
