package ent

import (
	"bjyixiu.com/ent/config"
	"bjyixiu.com/ent/service"
)

func NewEnt(conf config.Config, isDev bool) service.Service {
	return service.NewService(conf, isDev)
}
