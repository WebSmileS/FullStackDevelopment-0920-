package models

import (
	"log"

	"github.com/go-xorm/xorm"
)

// Xorm Engine
var engine *xorm.Engine

func init() {
	// 初始化连接，建立引擎
	//db.CreateMysqlEngin()
	// // 同步表
	// log.Println("同步表...")
	// sync()
}

//func SetXormEngine(engin *xorm.Engin){
//	engine = engine
//}

func sync() {
	// 用户表
	if err := engine.Sync2(new(User)); err != nil {
		log.Fatal("同步表失败：", err)
	}
}
