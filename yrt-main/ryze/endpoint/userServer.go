package endpoint

import (
	"context"

	protoRyze "bjyixiu.com/proto/ryze"

	"github.com/go-kit/kit/transport/grpc"
)

type UserService struct {
	CreateHandler        grpc.Handler
	DeleteHandler        grpc.Handler
	GetUserByIdHandler   grpc.Handler
	GetUserByNameHandler grpc.Handler
	AuthHandler          grpc.Handler
	ModifyHandler        grpc.Handler
}

var UserServer *UserService

func init() {
	UserServer = new(UserService)

	UserServer.CreateHandler = grpc.NewServer(
		Create(),
		decodeRequest,
		encodeResponse,
	)

	UserServer.DeleteHandler = grpc.NewServer(
		Delete(),
		decodeRequest,
		encodeResponse,
	)

	UserServer.GetUserByIdHandler = grpc.NewServer(
		GetUserById(),
		decodeRequest,
		encodeResponse,
	)

	UserServer.GetUserByNameHandler = grpc.NewServer(
		GetUserByName(),
		decodeRequest,
		encodeResponse,
	)

	UserServer.AuthHandler = grpc.NewServer(
		Auth(),
		decodeRequest,
		encodeResponse,
	)

	UserServer.ModifyHandler = grpc.NewServer(
		Modify(),
		decodeRequest,
		encodeResponse,
	)
}

func (this *UserService) Create(ctx context.Context, user *protoRyze.User) (*protoRyze.UserResponse, error) {
	_, rsp, err := this.CreateHandler.ServeGRPC(ctx, user)
	if err != nil {
		return nil, err
	}
	return rsp.(*protoRyze.UserResponse), err
}

func (this *UserService) Delete(ctx context.Context, userId *protoRyze.UserId) (*protoRyze.UserResponse, error) {
	_, rsp, err := this.DeleteHandler.ServeGRPC(ctx, userId)
	if err != nil {
		return nil, err
	}
	return rsp.(*protoRyze.UserResponse), err
}

func (this *UserService) GetUserById(ctx context.Context, userId *protoRyze.UserId) (*protoRyze.UserResponse, error) {
	_, rsp, err := this.GetUserByIdHandler.ServeGRPC(ctx, userId)
	if err != nil {
		return nil, err
	}
	return rsp.(*protoRyze.UserResponse), err
}

func (this *UserService) GetUserByName(ctx context.Context, userName *protoRyze.UserName) (*protoRyze.UserResponse, error) {
	_, rsp, err := this.GetUserByNameHandler.ServeGRPC(ctx, userName)
	if err != nil {
		return nil, err
	}
	return rsp.(*protoRyze.UserResponse), err
}

func (this *UserService) Auth(ctx context.Context, namePwd *protoRyze.NamePwd) (*protoRyze.UserResponse, error) {
	_, rsp, err := this.AuthHandler.ServeGRPC(ctx, namePwd)
	if err != nil {
		return nil, err
	}
	return rsp.(*protoRyze.UserResponse), err
}

func (this *UserService) Modify(ctx context.Context, user *protoRyze.User) (*protoRyze.UserResponse, error) {
	_, rsp, err := this.ModifyHandler.ServeGRPC(ctx, user)
	if err != nil {
		return nil, err
	}
	return rsp.(*protoRyze.UserResponse), err
}
