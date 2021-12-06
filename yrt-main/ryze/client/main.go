package main

import (
	"context"
	"io"
	"log"
	"time"

	protoRyze "bjyixiu.com/proto/ryze"

	"github.com/go-kit/kit/endpoint"
	kitlog "github.com/go-kit/kit/log"
	"github.com/go-kit/kit/sd"
	"github.com/go-kit/kit/sd/consul"
	"github.com/go-kit/kit/sd/lb"
	"github.com/golang/protobuf/ptypes/timestamp"
	consulapi "github.com/hashicorp/consul/api"
	"google.golang.org/grpc"
)

func main() {
	kitLogger := kitlog.NewNopLogger()
	kitLogger.Log("开始...")
	config := consulapi.DefaultConfig()
	config.Address = "127.0.0.1:8500"
	client, err := consulapi.NewClient(config)

	kitClient := consul.NewClient(client)

	instancer := consul.NewInstancer(kitClient, kitLogger, "ryze", []string{}, true)

	log.Println("服务实例:", instancer)

	endpointer := sd.NewEndpointer(instancer, reqFacotry, kitLogger)
	// balancer := lb.NewRandom(endpointer, 323)
	balancer := lb.NewRoundRobin(endpointer)

	reqEndpoint := lb.Retry(3, 3*time.Second, balancer)

	lbDuration := time.Now()
	tick := time.Tick(time.Second * 5)
	for {
		<-tick
		// go func() {
		var createdAT timestamp.Timestamp

		createdAT.Seconds = time.Now().Unix()

		if rep, err := reqEndpoint(context.Background(), &protoRyze.User{
			Id:         2234,
			Name:       "balanceUser",
			CreateDate: &createdAT,
		}); err != nil {
			log.Println("调用服务错误:", err)
		} else {
			log.Println(rep)
		}
		// }()

	}
	log.Println("负载均衡消耗 :", time.Since(lbDuration))

	services, err := client.Agent().Services()
	if err != nil {
		log.Println("发现服务错误 :", err)
	}
	for key, service := range services {
		log.Println("key :", key)
		log.Println("Address :", service.Address, ":", service.Port)
		log.Println(service.Meta)
		log.Println(service.Kind)
		log.Println(service.Service)
		log.Println(service.Tags)
	}

	if err != nil {
		log.Fatal("注册服务错误 :", err)
	}

	begin := time.Now()
	conn, err := grpc.Dial("127.0.0.1:12345", grpc.WithInsecure())
	if err != nil {
		log.Fatal("Can't connect server :", err)
	}

	defer conn.Close()
	var ts timestamp.Timestamp

	ts.Seconds = time.Now().Unix()
	userClient := protoRyze.NewUserServiceClient(conn)

	log.Println("connection spend :", time.Since(begin))
	newUser, err := userClient.Create(context.Background(), &protoRyze.User{
		Id:         1234,
		Name:       "testUser",
		CreateDate: &ts,
	})
	if err != nil {
		log.Println(err)
	}
	log.Println("call spend :", time.Since(begin))
	log.Println(newUser)
}

func reqFacotry(instanceAddr string) (endpoint.Endpoint, io.Closer, error) {
	return func(ctx context.Context, request interface{}) (interface{}, error) {
		log.Println("请求服务 :", instanceAddr)
		conn, err := grpc.Dial(instanceAddr, grpc.WithInsecure())
		if err != nil {
			log.Fatal("Can't connect server :", err)
		}
		defer conn.Close()

		userClient := protoRyze.NewUserServiceClient(conn)

		return userClient.Create(context.Background(), request.(*protoRyze.User))

	}, nil, nil
}
