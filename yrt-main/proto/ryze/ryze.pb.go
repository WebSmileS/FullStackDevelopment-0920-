// Code generated by protoc-gen-go. DO NOT EDIT.
// source: ryze.proto

package ryze

import proto "github.com/golang/protobuf/proto"
import fmt "fmt"
import math "math"
import timestamp "github.com/golang/protobuf/ptypes/timestamp"

import (
	context "golang.org/x/net/context"
	grpc "google.golang.org/grpc"
)

// Reference imports to suppress errors if they are not otherwise used.
var _ = proto.Marshal
var _ = fmt.Errorf
var _ = math.Inf

// This is a compile-time assertion to ensure that this generated file
// is compatible with the proto package it is being compiled against.
// A compilation error at this line likely means your copy of the
// proto package needs to be updated.
const _ = proto.ProtoPackageIsVersion2 // please upgrade the proto package

type UserId struct {
	Id                   int32    `protobuf:"varint,1,opt,name=id,proto3" json:"id,omitempty"`
	XXX_NoUnkeyedLiteral struct{} `json:"-"`
	XXX_unrecognized     []byte   `json:"-"`
	XXX_sizecache        int32    `json:"-"`
}

func (m *UserId) Reset()         { *m = UserId{} }
func (m *UserId) String() string { return proto.CompactTextString(m) }
func (*UserId) ProtoMessage()    {}
func (*UserId) Descriptor() ([]byte, []int) {
	return fileDescriptor_ryze_1431b95db61ca815, []int{0}
}
func (m *UserId) XXX_Unmarshal(b []byte) error {
	return xxx_messageInfo_UserId.Unmarshal(m, b)
}
func (m *UserId) XXX_Marshal(b []byte, deterministic bool) ([]byte, error) {
	return xxx_messageInfo_UserId.Marshal(b, m, deterministic)
}
func (dst *UserId) XXX_Merge(src proto.Message) {
	xxx_messageInfo_UserId.Merge(dst, src)
}
func (m *UserId) XXX_Size() int {
	return xxx_messageInfo_UserId.Size(m)
}
func (m *UserId) XXX_DiscardUnknown() {
	xxx_messageInfo_UserId.DiscardUnknown(m)
}

var xxx_messageInfo_UserId proto.InternalMessageInfo

func (m *UserId) GetId() int32 {
	if m != nil {
		return m.Id
	}
	return 0
}

type UserName struct {
	Name                 string   `protobuf:"bytes,1,opt,name=name,proto3" json:"name,omitempty"`
	XXX_NoUnkeyedLiteral struct{} `json:"-"`
	XXX_unrecognized     []byte   `json:"-"`
	XXX_sizecache        int32    `json:"-"`
}

func (m *UserName) Reset()         { *m = UserName{} }
func (m *UserName) String() string { return proto.CompactTextString(m) }
func (*UserName) ProtoMessage()    {}
func (*UserName) Descriptor() ([]byte, []int) {
	return fileDescriptor_ryze_1431b95db61ca815, []int{1}
}
func (m *UserName) XXX_Unmarshal(b []byte) error {
	return xxx_messageInfo_UserName.Unmarshal(m, b)
}
func (m *UserName) XXX_Marshal(b []byte, deterministic bool) ([]byte, error) {
	return xxx_messageInfo_UserName.Marshal(b, m, deterministic)
}
func (dst *UserName) XXX_Merge(src proto.Message) {
	xxx_messageInfo_UserName.Merge(dst, src)
}
func (m *UserName) XXX_Size() int {
	return xxx_messageInfo_UserName.Size(m)
}
func (m *UserName) XXX_DiscardUnknown() {
	xxx_messageInfo_UserName.DiscardUnknown(m)
}

var xxx_messageInfo_UserName proto.InternalMessageInfo

func (m *UserName) GetName() string {
	if m != nil {
		return m.Name
	}
	return ""
}

type NamePwd struct {
	Name                 string   `protobuf:"bytes,1,opt,name=name,proto3" json:"name,omitempty"`
	Pwd                  string   `protobuf:"bytes,2,opt,name=pwd,proto3" json:"pwd,omitempty"`
	XXX_NoUnkeyedLiteral struct{} `json:"-"`
	XXX_unrecognized     []byte   `json:"-"`
	XXX_sizecache        int32    `json:"-"`
}

func (m *NamePwd) Reset()         { *m = NamePwd{} }
func (m *NamePwd) String() string { return proto.CompactTextString(m) }
func (*NamePwd) ProtoMessage()    {}
func (*NamePwd) Descriptor() ([]byte, []int) {
	return fileDescriptor_ryze_1431b95db61ca815, []int{2}
}
func (m *NamePwd) XXX_Unmarshal(b []byte) error {
	return xxx_messageInfo_NamePwd.Unmarshal(m, b)
}
func (m *NamePwd) XXX_Marshal(b []byte, deterministic bool) ([]byte, error) {
	return xxx_messageInfo_NamePwd.Marshal(b, m, deterministic)
}
func (dst *NamePwd) XXX_Merge(src proto.Message) {
	xxx_messageInfo_NamePwd.Merge(dst, src)
}
func (m *NamePwd) XXX_Size() int {
	return xxx_messageInfo_NamePwd.Size(m)
}
func (m *NamePwd) XXX_DiscardUnknown() {
	xxx_messageInfo_NamePwd.DiscardUnknown(m)
}

var xxx_messageInfo_NamePwd proto.InternalMessageInfo

func (m *NamePwd) GetName() string {
	if m != nil {
		return m.Name
	}
	return ""
}

func (m *NamePwd) GetPwd() string {
	if m != nil {
		return m.Pwd
	}
	return ""
}

type User struct {
	Id                   int32                `protobuf:"varint,1,opt,name=id,proto3" json:"id,omitempty"`
	Name                 string               `protobuf:"bytes,2,opt,name=name,proto3" json:"name,omitempty"`
	Pwd                  string               `protobuf:"bytes,3,opt,name=pwd,proto3" json:"pwd,omitempty"`
	CreateAt             *timestamp.Timestamp `protobuf:"bytes,4,opt,name=CreateAt,proto3" json:"CreateAt,omitempty"`
	XXX_NoUnkeyedLiteral struct{}             `json:"-"`
	XXX_unrecognized     []byte               `json:"-"`
	XXX_sizecache        int32                `json:"-"`
}

func (m *User) Reset()         { *m = User{} }
func (m *User) String() string { return proto.CompactTextString(m) }
func (*User) ProtoMessage()    {}
func (*User) Descriptor() ([]byte, []int) {
	return fileDescriptor_ryze_1431b95db61ca815, []int{3}
}
func (m *User) XXX_Unmarshal(b []byte) error {
	return xxx_messageInfo_User.Unmarshal(m, b)
}
func (m *User) XXX_Marshal(b []byte, deterministic bool) ([]byte, error) {
	return xxx_messageInfo_User.Marshal(b, m, deterministic)
}
func (dst *User) XXX_Merge(src proto.Message) {
	xxx_messageInfo_User.Merge(dst, src)
}
func (m *User) XXX_Size() int {
	return xxx_messageInfo_User.Size(m)
}
func (m *User) XXX_DiscardUnknown() {
	xxx_messageInfo_User.DiscardUnknown(m)
}

var xxx_messageInfo_User proto.InternalMessageInfo

func (m *User) GetId() int32 {
	if m != nil {
		return m.Id
	}
	return 0
}

func (m *User) GetName() string {
	if m != nil {
		return m.Name
	}
	return ""
}

func (m *User) GetPwd() string {
	if m != nil {
		return m.Pwd
	}
	return ""
}

func (m *User) GetCreateAt() *timestamp.Timestamp {
	if m != nil {
		return m.CreateAt
	}
	return nil
}

type UserResponse struct {
	Code                 string   `protobuf:"bytes,1,opt,name=code,proto3" json:"code,omitempty"`
	Msg                  string   `protobuf:"bytes,2,opt,name=msg,proto3" json:"msg,omitempty"`
	User                 *User    `protobuf:"bytes,3,opt,name=user,proto3" json:"user,omitempty"`
	XXX_NoUnkeyedLiteral struct{} `json:"-"`
	XXX_unrecognized     []byte   `json:"-"`
	XXX_sizecache        int32    `json:"-"`
}

func (m *UserResponse) Reset()         { *m = UserResponse{} }
func (m *UserResponse) String() string { return proto.CompactTextString(m) }
func (*UserResponse) ProtoMessage()    {}
func (*UserResponse) Descriptor() ([]byte, []int) {
	return fileDescriptor_ryze_1431b95db61ca815, []int{4}
}
func (m *UserResponse) XXX_Unmarshal(b []byte) error {
	return xxx_messageInfo_UserResponse.Unmarshal(m, b)
}
func (m *UserResponse) XXX_Marshal(b []byte, deterministic bool) ([]byte, error) {
	return xxx_messageInfo_UserResponse.Marshal(b, m, deterministic)
}
func (dst *UserResponse) XXX_Merge(src proto.Message) {
	xxx_messageInfo_UserResponse.Merge(dst, src)
}
func (m *UserResponse) XXX_Size() int {
	return xxx_messageInfo_UserResponse.Size(m)
}
func (m *UserResponse) XXX_DiscardUnknown() {
	xxx_messageInfo_UserResponse.DiscardUnknown(m)
}

var xxx_messageInfo_UserResponse proto.InternalMessageInfo

func (m *UserResponse) GetCode() string {
	if m != nil {
		return m.Code
	}
	return ""
}

func (m *UserResponse) GetMsg() string {
	if m != nil {
		return m.Msg
	}
	return ""
}

func (m *UserResponse) GetUser() *User {
	if m != nil {
		return m.User
	}
	return nil
}

func init() {
	proto.RegisterType((*UserId)(nil), "UserId")
	proto.RegisterType((*UserName)(nil), "UserName")
	proto.RegisterType((*NamePwd)(nil), "NamePwd")
	proto.RegisterType((*User)(nil), "User")
	proto.RegisterType((*UserResponse)(nil), "UserResponse")
}

// Reference imports to suppress errors if they are not otherwise used.
var _ context.Context
var _ grpc.ClientConn

// This is a compile-time assertion to ensure that this generated file
// is compatible with the grpc package it is being compiled against.
const _ = grpc.SupportPackageIsVersion4

// UserServiceClient is the client API for UserService service.
//
// For semantics around ctx use and closing/ending streaming RPCs, please refer to https://godoc.org/google.golang.org/grpc#ClientConn.NewStream.
type UserServiceClient interface {
	// create user
	Create(ctx context.Context, in *User, opts ...grpc.CallOption) (*UserResponse, error)
	// delete user
	Delete(ctx context.Context, in *UserId, opts ...grpc.CallOption) (*UserResponse, error)
	// get User by userID
	GetUserById(ctx context.Context, in *UserId, opts ...grpc.CallOption) (*UserResponse, error)
	// get User by userName
	GetUserByName(ctx context.Context, in *UserName, opts ...grpc.CallOption) (*UserResponse, error)
	// Auth user
	Auth(ctx context.Context, in *NamePwd, opts ...grpc.CallOption) (*UserResponse, error)
	// modify user
	Modify(ctx context.Context, in *User, opts ...grpc.CallOption) (*UserResponse, error)
}

type userServiceClient struct {
	cc *grpc.ClientConn
}

func NewUserServiceClient(cc *grpc.ClientConn) UserServiceClient {
	return &userServiceClient{cc}
}

func (c *userServiceClient) Create(ctx context.Context, in *User, opts ...grpc.CallOption) (*UserResponse, error) {
	out := new(UserResponse)
	err := c.cc.Invoke(ctx, "/UserService/Create", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *userServiceClient) Delete(ctx context.Context, in *UserId, opts ...grpc.CallOption) (*UserResponse, error) {
	out := new(UserResponse)
	err := c.cc.Invoke(ctx, "/UserService/Delete", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *userServiceClient) GetUserById(ctx context.Context, in *UserId, opts ...grpc.CallOption) (*UserResponse, error) {
	out := new(UserResponse)
	err := c.cc.Invoke(ctx, "/UserService/GetUserById", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *userServiceClient) GetUserByName(ctx context.Context, in *UserName, opts ...grpc.CallOption) (*UserResponse, error) {
	out := new(UserResponse)
	err := c.cc.Invoke(ctx, "/UserService/GetUserByName", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *userServiceClient) Auth(ctx context.Context, in *NamePwd, opts ...grpc.CallOption) (*UserResponse, error) {
	out := new(UserResponse)
	err := c.cc.Invoke(ctx, "/UserService/Auth", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *userServiceClient) Modify(ctx context.Context, in *User, opts ...grpc.CallOption) (*UserResponse, error) {
	out := new(UserResponse)
	err := c.cc.Invoke(ctx, "/UserService/Modify", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

// UserServiceServer is the server API for UserService service.
type UserServiceServer interface {
	// create user
	Create(context.Context, *User) (*UserResponse, error)
	// delete user
	Delete(context.Context, *UserId) (*UserResponse, error)
	// get User by userID
	GetUserById(context.Context, *UserId) (*UserResponse, error)
	// get User by userName
	GetUserByName(context.Context, *UserName) (*UserResponse, error)
	// Auth user
	Auth(context.Context, *NamePwd) (*UserResponse, error)
	// modify user
	Modify(context.Context, *User) (*UserResponse, error)
}

func RegisterUserServiceServer(s *grpc.Server, srv UserServiceServer) {
	s.RegisterService(&_UserService_serviceDesc, srv)
}

func _UserService_Create_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(User)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(UserServiceServer).Create(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/UserService/Create",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(UserServiceServer).Create(ctx, req.(*User))
	}
	return interceptor(ctx, in, info, handler)
}

func _UserService_Delete_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(UserId)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(UserServiceServer).Delete(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/UserService/Delete",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(UserServiceServer).Delete(ctx, req.(*UserId))
	}
	return interceptor(ctx, in, info, handler)
}

func _UserService_GetUserById_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(UserId)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(UserServiceServer).GetUserById(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/UserService/GetUserById",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(UserServiceServer).GetUserById(ctx, req.(*UserId))
	}
	return interceptor(ctx, in, info, handler)
}

func _UserService_GetUserByName_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(UserName)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(UserServiceServer).GetUserByName(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/UserService/GetUserByName",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(UserServiceServer).GetUserByName(ctx, req.(*UserName))
	}
	return interceptor(ctx, in, info, handler)
}

func _UserService_Auth_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(NamePwd)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(UserServiceServer).Auth(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/UserService/Auth",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(UserServiceServer).Auth(ctx, req.(*NamePwd))
	}
	return interceptor(ctx, in, info, handler)
}

func _UserService_Modify_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(User)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(UserServiceServer).Modify(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/UserService/Modify",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(UserServiceServer).Modify(ctx, req.(*User))
	}
	return interceptor(ctx, in, info, handler)
}

var _UserService_serviceDesc = grpc.ServiceDesc{
	ServiceName: "UserService",
	HandlerType: (*UserServiceServer)(nil),
	Methods: []grpc.MethodDesc{
		{
			MethodName: "Create",
			Handler:    _UserService_Create_Handler,
		},
		{
			MethodName: "Delete",
			Handler:    _UserService_Delete_Handler,
		},
		{
			MethodName: "GetUserById",
			Handler:    _UserService_GetUserById_Handler,
		},
		{
			MethodName: "GetUserByName",
			Handler:    _UserService_GetUserByName_Handler,
		},
		{
			MethodName: "Auth",
			Handler:    _UserService_Auth_Handler,
		},
		{
			MethodName: "Modify",
			Handler:    _UserService_Modify_Handler,
		},
	},
	Streams:  []grpc.StreamDesc{},
	Metadata: "ryze.proto",
}

func init() { proto.RegisterFile("ryze.proto", fileDescriptor_ryze_1431b95db61ca815) }

var fileDescriptor_ryze_1431b95db61ca815 = []byte{
	// 326 bytes of a gzipped FileDescriptorProto
	0x1f, 0x8b, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0xff, 0x7c, 0x91, 0xcf, 0x4a, 0xc3, 0x40,
	0x10, 0xc6, 0x4d, 0x9a, 0xa6, 0xed, 0xc4, 0x8a, 0xec, 0x29, 0xf6, 0xa0, 0x71, 0x2f, 0x16, 0x84,
	0x2d, 0x54, 0xf0, 0x5e, 0x15, 0xa4, 0x07, 0xff, 0x10, 0xf5, 0x01, 0xd2, 0xee, 0x34, 0x06, 0x9a,
	0x6e, 0xd8, 0xdd, 0x5a, 0xe2, 0x43, 0xfb, 0x0c, 0xb2, 0x1b, 0x13, 0xc4, 0xa8, 0xb7, 0xd9, 0x99,
	0xdf, 0xec, 0x37, 0xdf, 0x0c, 0x80, 0x2c, 0xdf, 0x91, 0x15, 0x52, 0x68, 0x31, 0x3a, 0x49, 0x85,
	0x48, 0xd7, 0x38, 0xb1, 0xaf, 0xc5, 0x76, 0x35, 0xd1, 0x59, 0x8e, 0x4a, 0x27, 0x79, 0x51, 0x01,
	0x34, 0x04, 0xff, 0x45, 0xa1, 0x9c, 0x73, 0x72, 0x00, 0x6e, 0xc6, 0x43, 0x27, 0x72, 0xc6, 0xdd,
	0xd8, 0xcd, 0x38, 0x3d, 0x86, 0xbe, 0xa9, 0xdc, 0x27, 0x39, 0x12, 0x02, 0xde, 0x26, 0xc9, 0xd1,
	0x56, 0x07, 0xb1, 0x8d, 0xe9, 0x04, 0x7a, 0xa6, 0xf6, 0xb8, 0xe3, 0xbf, 0x95, 0xc9, 0x21, 0x74,
	0x8a, 0x1d, 0x0f, 0x5d, 0x9b, 0x32, 0x21, 0xd5, 0xe0, 0x99, 0x0f, 0x7f, 0x0a, 0x35, 0xdd, 0x6e,
	0xbb, 0xbb, 0xd3, 0x74, 0x93, 0x4b, 0xe8, 0x5f, 0x4b, 0x4c, 0x34, 0xce, 0x74, 0xe8, 0x45, 0xce,
	0x38, 0x98, 0x8e, 0x58, 0x65, 0x8e, 0xd5, 0xe6, 0xd8, 0x73, 0x6d, 0x2e, 0x6e, 0x58, 0xfa, 0x00,
	0xfb, 0x46, 0x35, 0x46, 0x55, 0x88, 0x8d, 0xb2, 0x56, 0x96, 0x82, 0x37, 0xb3, 0x9a, 0xd8, 0xa8,
	0xe5, 0x2a, 0xad, 0x67, 0xcd, 0x55, 0x4a, 0x8e, 0xc0, 0xdb, 0x2a, 0x94, 0x76, 0x80, 0x60, 0xda,
	0x65, 0xf6, 0x0b, 0x9b, 0x9a, 0x7e, 0x38, 0x10, 0x98, 0xe7, 0x13, 0xca, 0xb7, 0x6c, 0x89, 0x24,
	0x02, 0xbf, 0x12, 0x23, 0x15, 0x36, 0x1a, 0xb2, 0xef, 0x82, 0x74, 0x8f, 0x50, 0xf0, 0x6f, 0x70,
	0x8d, 0x1a, 0x49, 0x8f, 0x55, 0xcb, 0x6e, 0x33, 0x67, 0x10, 0xdc, 0xa2, 0x36, 0xc9, 0xab, 0x72,
	0xce, 0xff, 0x01, 0xcf, 0x61, 0xd8, 0x80, 0xf6, 0x36, 0x03, 0x56, 0x9f, 0xa9, 0x0d, 0x9f, 0x82,
	0x37, 0xdb, 0xea, 0x57, 0xd2, 0x67, 0x5f, 0xa7, 0x6a, 0x23, 0x11, 0xf8, 0x77, 0x82, 0x67, 0xab,
	0xf2, 0xaf, 0xf1, 0x17, 0xbe, 0xdd, 0xef, 0xc5, 0x67, 0x00, 0x00, 0x00, 0xff, 0xff, 0xc4, 0x2e,
	0x08, 0x86, 0x58, 0x02, 0x00, 0x00,
}
