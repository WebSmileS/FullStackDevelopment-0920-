package endpoint

import (
	"context"
	"log"

	protoRyze "bjyixiu.com/proto/ryze"
	"bjyixiu.com/ryze/service"
	"github.com/go-kit/kit/endpoint"
	"github.com/pkg/errors"
)

func Create() endpoint.Endpoint {
	return func(ctx context.Context, request interface{}) (interface{}, error) {
		req := request.(*protoRyze.User)
		// 调用服务建立用户
		// user := new(models.User)
		// 校验必要项目和

		if err := service.CreateUser(req); err != nil {
			log.Println(errors.Cause(err))
			return nil, err
		}

		// req.Id = user.ID
		// req.CreateDate = user.CreateAt

		return &protoRyze.UserResponse{
			Code: "000",
			Msg:  "Successed!",
			User: req,
		}, nil
	}
}

func Delete() endpoint.Endpoint {
	return func(ctx context.Context, request interface{}) (interface{}, error) {
		return &protoRyze.UserResponse{
			Code: "000",
			Msg:  "Successed!",
			User: &protoRyze.User{},
		}, nil
	}
}

func GetUserById() endpoint.Endpoint {
	return func(ctx context.Context, request interface{}) (interface{}, error) {
		return &protoRyze.UserResponse{
			Code: "000",
			Msg:  "Successed!",
			User: &protoRyze.User{},
		}, nil
	}
}

func GetUserByName() endpoint.Endpoint {
	return func(ctx context.Context, request interface{}) (interface{}, error) {
		return &protoRyze.UserResponse{
			Code: "000",
			Msg:  "Successed!",
			User: &protoRyze.User{},
		}, nil
	}
}

func Auth() endpoint.Endpoint {
	return func(ctx context.Context, request interface{}) (interface{}, error) {
		return &protoRyze.UserResponse{
			Code: "000",
			Msg:  "Successed!",
			User: &protoRyze.User{},
		}, nil
	}
}

func Modify() endpoint.Endpoint {
	return func(ctx context.Context, request interface{}) (interface{}, error) {
		return &protoRyze.UserResponse{
			Code: "000",
			Msg:  "Successed!",
			User: &protoRyze.User{},
		}, nil
	}
}

func encodeResponse(_ context.Context, req interface{}) (interface{}, error) {
	return req, nil
}

func decodeRequest(_ context.Context, req interface{}) (interface{}, error) {
	return req, nil
}
