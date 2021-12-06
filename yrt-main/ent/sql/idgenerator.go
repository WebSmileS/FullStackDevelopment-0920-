package sql

import (
	"log"
)

func (sql *SQLService) GenNodeID() int {
	max, err := sql.getMaxID()
	if err != nil {
		log.Println("生成node id出错：", err)
		return 0
	}
	return max + 1
}

func (sql *SQLService) GenTreeID() int {
	return 0
}
