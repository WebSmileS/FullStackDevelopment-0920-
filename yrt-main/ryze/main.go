package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"os"
	"strconv"
	"time"

	protoRyze "bjyixiu.com/proto/ryze"
	"bjyixiu.com/ryze/db"
	"bjyixiu.com/ryze/endpoint"
	"bjyixiu.com/ryze/models"
	"bjyixiu.com/ryze/service"

	"google.golang.org/grpc"

	consulapi "github.com/hashicorp/consul/api"

	"google.golang.org/grpc/health/grpc_health_v1"

	kitlog "github.com/go-kit/kit/log"
	"github.com/go-kit/kit/sd/consul"
)

func main() {

	test()
	os.Exit(1)

	log.Println("Ryze Start...")
	// 建立数据库连接
	service.NewService(db.NewSSDB())
	// 获得可用端口
	port, err := GetFreePort()
	if err != nil {
		log.Println("没找到合适的端口:", err)
	} else {
		log.Println("可用端口：", port)
	}

	ls, err := net.Listen("tcp", ":"+strconv.Itoa(port))
	if err != nil {
		log.Fatal(err)
	}

	gs := grpc.NewServer()
	// 主gRpc服务
	protoRyze.RegisterUserServiceServer(gs, endpoint.UserServer)
	// gRpc健康检查
	grpc_health_v1.RegisterHealthServer(gs, &HealthImpl{})
	// 注册服务，服务发现工具为consul
	registConsul(port)
	// 启动服务
	gs.Serve(ls)
}

func test() {
	service.NewService(db.NewPikaDB())
	platformTree, err := service.PlatformMotherTree()
	if err != nil {
		log.Fatal("获取平台树错误:", err)
	}

	fmt.Println("增加节点")
	since := time.Now()
	// 第一级增加一个node
	var n1 models.Node
	n1.Parent = platformTree.ID
	n1.Name = time.Now().Format("0102T15:04:05")
	n1.Url = "/n1"
	if _, err := service.AddNode(&n1); err != nil {
		log.Fatal("增加节点错误")
	}
	// 第二级增加一个node
	var n2 models.Node
	n2.Parent = n1.ID
	n2.Name = time.Now().Format("0102T15:04:05")
	n2.Url = "/n1/n2"
	if _, err := service.AddNode(&n2); err != nil {
		log.Fatal("增加节点错误")
	}
	fmt.Println("增加完成：")
	fmt.Println(time.Now().Sub(since))

	since = time.Now()
	tree, err := service.PlatformMotherTree()
	if err != nil {
		log.Println("获取平台树出错：", err)
	}
	fmt.Println("耗时:", time.Now().Sub(since))
	fmt.Println("节点数：", len(tree.Children))
	fmt.Println("树：", tree)
}

func registConsul(port int) {
	// 文本Port
	strPort := strconv.Itoa(port)

	// 建立默认的consul client
	config := consulapi.DefaultConfig()
	config.Address = "127.0.0.1:8500"
	client, err := consulapi.NewClient(config)

	if err != nil {
		log.Fatal("注册服务错误 :", err)
	}

	// 注册内容
	id := "ryze" + strPort
	reg := &consulapi.AgentServiceRegistration{
		ID:      id,
		Name:    "ryze",
		Tags:    []string{"ryze", "userService"},
		Port:    port,
		Address: "127.0.0.1",
		Check: &consulapi.AgentServiceCheck{
			Interval:                       "10s",
			GRPC:                           "127.0.0.1:" + strPort + "/Check",
			DeregisterCriticalServiceAfter: "40s",
		},
	}
	// go-kit的服务器注册方法
	kitClient := consul.NewClient(client)
	register := consul.NewRegistrar(kitClient, reg, kitlog.NewNopLogger())
	register.Register()
	// TODO: 缺少检查服务注册是否成功的方法
	log.Println("服务注册成功,servie id:", id)
}

// 提供给consul的健康检查服务
type HealthImpl struct{}

// 调用gRpc健康检查
func (this *HealthImpl) Check(ctx context.Context, req *grpc_health_v1.HealthCheckRequest) (*grpc_health_v1.HealthCheckResponse, error) {
	return &grpc_health_v1.HealthCheckResponse{
		Status: grpc_health_v1.HealthCheckResponse_SERVING,
	}, nil
}

// 要求实现的空方法
func (this *HealthImpl) Watch(_ *grpc_health_v1.HealthCheckRequest, _ grpc_health_v1.Health_WatchServer) error {
	return nil
}

// 得到一个可用的端口.
func GetFreePort() (port int, err error) {
	listener, err := net.Listen("tcp", "127.0.0.1:0")
	if err != nil {
		return 0, err
	}
	defer listener.Close()

	addr := listener.Addr().String()
	_, portString, err := net.SplitHostPort(addr)
	if err != nil {
		return 0, err
	}

	return strconv.Atoi(portString)
}
