package services

import (
	"context"
	"io"
	"log"
	"time"

	protoRyze "bjyixiu.com/proto/ryze"
	"github.com/go-kit/kit/endpoint"
	"github.com/go-kit/kit/sd"
	"github.com/go-kit/kit/sd/consul"
	"github.com/go-kit/kit/sd/lb"
	"google.golang.org/grpc"
)

// Ryze(user service)
type RyzeService struct {
	Instancer *consul.Instancer //consul instance
	Create    endpoint.Endpoint
}

var Ryze *RyzeService

func InitServiceRyze() {
	Ryze = new(RyzeService)
	Ryze.Instancer = consul.NewInstancer(kitClient, logger, "ryze", []string{}, true)

	Ryze.CreateFactory()
}

func (this RyzeService) CreateFactory() {

	factory := func(instanceAddr string) (endpoint.Endpoint, io.Closer, error) {
		return func(ctx context.Context, request interface{}) (interface{}, error) {
			log.Println("请求服务 :", instanceAddr)
			conn, err := grpc.Dial(instanceAddr, grpc.WithInsecure())
			if err != nil {
				logger.Log("Can't connect server :", err)
			}
			defer conn.Close()

			userClient := protoRyze.NewUserServiceClient(conn)

			return userClient.Create(context.Background(), request.(*protoRyze.User))

		}, nil, nil
	}

	endpointer := sd.NewEndpointer(this.Instancer, factory, logger)
	balancer := lb.NewRoundRobin(endpointer)
	this.Create = lb.Retry(3, 3*time.Second, balancer)
}
