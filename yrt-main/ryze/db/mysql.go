package db

import (
	"log"

	"bjyixiu.com/ryze/config"

	_ "github.com/go-sql-driver/mysql"
	"github.com/go-xorm/xorm"
	"github.com/pkg/errors"
)

// 尝试连接数据库，失败则直接退出
func CreateMysqlEngine(conf config.Mysql) (*xorm.Engine, error) {
	log.Print("连接数据库...")
	connect := conf.User + ":" +
		conf.Password + "@tcp(" +
		conf.Host + ":" +
		conf.Port + ")/" +
		conf.Database + "?charset=utf8mb4&parseTime=True&loc=Local"

	engine, err := xorm.NewEngine("mysql", connect)

	if err != nil {
		return nil, errors.Wrap(err, "连接Mysql数据库出错")
	}

	log.Println("Ping数据库...")
	if err := engine.Ping(); err != nil {
		log.Println("Ping数据库 error: ", err)
		return nil, errors.Wrap(err, "测试(Ping)Mysql数据库出错")
	}

	return engine, nil
}
