package db

import (
	"fmt"

	"github.com/siddontang/ledisdb/config"
	"github.com/siddontang/ledisdb/ledis"
)

type LedisDB struct {
	*ledis.DB
	l *ledis.Ledis
}

var ledisdb *LedisDB

// var ledisReadonlyDB *LedisDB

func (this *LedisDB) Close() {
	this.l.Close()
	this.l = nil
}

func GetDB() (*LedisDB, error) {
	if ledisdb != nil {
		return ledisdb, nil
	}

	ledisdb = new(LedisDB)
	db := new(ledis.DB)
	cfg := config.NewConfigDefault()
	cfg.DataDir = "db_ledis"
	l, err := ledis.Open(cfg)
	if err != nil {
		fmt.Println(err.Error())
		return ledisdb, err
	}

	db, err = l.Select(0)
	ledisdb.DB = db
	ledisdb.l = l
	return ledisdb, err
}
