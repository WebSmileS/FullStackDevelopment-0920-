package models

import "log"

var idGenerator *Worker

// TODO: 暂时固定节点，用于测试
// 初始化ID生成器
// Params:
//		none
// Return:
//		none
func init() {
	var err error
	idGenerator, err = NewWorker(1)
	if err != nil {
		log.Fatal("ID生成器错误：" + err.Error())
	}
}
