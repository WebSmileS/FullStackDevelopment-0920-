package db

import (
	"log"

	"github.com/jinzhu/gorm"

	"bjyixiu.com/ent/config"
	_ "github.com/jinzhu/gorm/dialects/mysql"
)

func NewMySQLDB(conf config.MySQL, logEnable bool) *gorm.DB {
	log.Print("连接数据库...")
	connect := conf.User + ":" +
		conf.Password + "@(" +
		conf.Host + ":" +
		conf.Port + ")/" +
		conf.Database + "?charset=utf8mb4&parseTime=True&loc=Local"

	db, err := gorm.Open("mysql", connect)

	if err != nil {
		log.Fatal("连接Mysql数据库出错", err)
	}
	db.LogMode(logEnable)
	return db
}
