package models

import (
	"log"

	"github.com/go-xorm/xorm"
	_ "github.com/lib/pq"
)

// 尝试连接数据库，失败则直接退出
func CreatePostgresEngine() {
	log.Print("连接数据库...")
	connstr := "postgres://postgres:123456@localhost:5432/ryze?sslmode=disable"
	var err error
	engine, err = xorm.NewEngine("postgres", connstr)
	if err != nil {
		log.Fatal("Database connection error :", err)
	}
	log.Println("Ping数据库...")
	if err := engine.Ping(); err != nil {
		log.Println("Ping数据库 error: ", err)
	} else {
		log.Println("Ping数据库 OK")
	}
}
