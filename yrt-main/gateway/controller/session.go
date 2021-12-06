package controller

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/jinzhu/gorm"

	"bjyixiu.com/ent/models"

	"github.com/pkg/errors"

	"bjyixiu.com/gateway/config"

	"github.com/gomodule/redigo/redis"
	"github.com/labstack/echo/v4"
)

var (
	sessionStore      *SessionStore
	ErrCookieNotFound = errors.New("请先登录")        // Cookie Not Found
	ErrSessionExpired = errors.New("登录已过期，请重新登录") // Session Expired
	ErrRoleUnassigned = errors.New("用户无指定角色")
)

type SessionStore struct {
	CookieName  string
	Expire      int
	WxExpire    int
	pool        *redis.Pool
	jsonEncoder *json.Encoder
}

type SessionUser struct {
	SID        string // session id
	UID        int64  // user ID
	OID        int64  // 组织 ID
	RID        int    // 角色 ID
	UserType   int    // 用户类型
	SystemType int    // 老版本的用户系统类型
	GodInnerID int64  // 老版本的用户id
	Belong     int64  // 老版本的组织id
	Version    int    // 针对的版本
	AppRegID   string // APP的消息通知注册ID，为空的是Web端
}

func (u *SessionUser) GroupID() int64 {
	// v3版本的用户，直接返回组织ID
	if u.Version == 3 {
		return u.OID
	}

	// v1版本要计算group id
	if u.OID > 0 {
		return u.OID
	}

	group := models.YrtGroupMap{
		GroupID:    0,
		SystemType: u.SystemType,
		Belong:     u.Belong,
	}
	id, _ := group.GenGroupID()
	u.OID = id

	return id
}

func (u *SessionUser) UserID() int64 {
	// v3 版本直接返回
	if u.Version == 3 {
		return u.UID
	}

	// v1 版本需要计算
	if u.UID > 0 {
		return u.UID
	}

	user := models.YrtUserMap{
		UserID:     0,
		SystemType: u.SystemType,
		GodInnerSN: u.GodInnerID,
	}
	id, _ := user.GenUserID()
	u.UID = id
	return id
}

func InitSession(kvdb config.KVDB, setting config.Session) {
	sessionStore = new(SessionStore)
	// 数据库连接
	sessionStore.pool = &redis.Pool{
		MaxIdle:     50,
		IdleTimeout: 240 * time.Second,
		Dial: func() (redis.Conn, error) {
			c, err := redis.Dial("tcp", kvdb.Host)
			if err != nil {
				return nil, fmt.Errorf("连接KV数据库%v出错: %w", kvdb.Host, err)
			}

			if len(kvdb.Password) > 0 {
				if _, err := c.Do("AUTH", kvdb.Password); err != nil {
					c.Close()
					return nil, fmt.Errorf("KV数据库认证出错: %w", err)
				}
			}

			return c, nil
		},
		TestOnBorrow: func(c redis.Conn, t time.Time) error {
			if time.Since(t) < time.Minute {
				return nil
			}
			_, err := c.Do("PING")
			return fmt.Errorf("KV数据库(%v)健康检查失败: %w", kvdb.Host, err)
		},
	}
	// 设置Session的配置
	sessionStore.CookieName = setting.CookieName
	sessionStore.Expire = setting.Expire
	sessionStore.WxExpire = setting.WXExpire

	// 带EscapeHTML(false)的json Encoder
	bf := bytes.NewBuffer([]byte{})
	jsonEncoder := json.NewEncoder(bf)
	jsonEncoder.SetEscapeHTML(false)
	sessionStore.jsonEncoder = jsonEncoder
}

// 从Cookie中获取session
// Params:
//		context: echo context
// Return:
//		sessionID: session id
//		error: 错误返回
func (ss *SessionStore) GetSessionID(c echo.Context) (string, error) {
	cookie, err := c.Cookie(ss.CookieName)
	if err != nil {
		return "", fmt.Errorf("正常访问需要允许Cookie", err)
	}
	if len(cookie.Value) == 0 {
		return "", ErrCookieNotFound
	}

	return cookie.Value, nil
}

// 从Cookie中获取session key，用于kv数据库中存取
// session在kv中的存储空间为前缀"SS_"
// Params:
//		context: echo context
// Return:
//		sessionKey: session key
//		error: 错误返回
func (ss *SessionStore) getSessionKey(c echo.Context) (string, error) {
	sid, err := ss.GetSessionID(c)
	if err != nil {
		return "", err
	}
	return SessionKey(sid), nil
}

// 生成新的session id
// session在kv中的存储空间为前缀"SS_"，本方法返回的是不带前缀的
// Params:
//		none
// Return:
//		string: session id
func (ss *SessionStore) NewSessionID() string {
	return models.GenUUID()
}

// 生成session ID
// Params:
//		context: echo context
// Return:
//		sessionID: session id
//		error: 错误返回

func (ss *SessionStore) SaveSession(c echo.Context, key string, value interface{}) {

}

func (ss *SessionStore) SetCookie(c echo.Context, sid string) {
	cookie := http.Cookie{
		Path:    "/",
		Name:    ss.CookieName,
		Value:   sid,
		Expires: time.Now().Add(time.Second * time.Duration(ss.Expire)),
	}
	c.SetCookie(&cookie)
}

func (ss *SessionStore) FlushSession() {

}

// DeleteSession
// 删除用户Session
//
// @Params
//		userSession: 用户session
//		error: 传入错误
// @Return
//		error: 错误返回
func (ss *SessionStore) DeleteSession(user *SessionUser) error {
	//user, err := ss.GetSessionUser(c)
	//if err != nil || user == nil {
	//	log.Println("删除用户Session时未找到用户Session:%w", err)
	//	return nil
	//}

	conn := ss.pool.Get()
	defer conn.Close()

	if _, err := conn.Do("DEL", SessionKey(user.SID)); err != nil {
		return fmt.Errorf("删除用户Session出错：%w", err)
	}
	if err := ss.RemoveOnlineUser(user.UID, user.SID); err != nil {
		return fmt.Errorf("移除Online user列表出错:%w", err)
	}

	return nil
}

// 展期Session的过期时间
// Params:
//		context: echo context
// Return:
//		error: 错误返回
func (ss *SessionStore) ExtendSession(c echo.Context) error {
	sid, err := ss.GetSessionID(c)
	if err != nil {
		return err
	}

	conn := ss.pool.Get()
	defer conn.Close()

	if _, err := conn.Do("EXPIRE", redis.Args{}.Add(sid).Add(ss.Expire)...); err != nil {
		return errors.Wrap(err, "设置过期时间出错")
	}

	return nil
}

// 从Session中读取缓存的用户信息
// Params:
//		context: echo context
// Return:
//		sessionUser: 缓存的用户信息
//		error: 错误返回
func (ss *SessionStore) GetSessionUser(c echo.Context) (*SessionUser, error) {
	sid, err := ss.GetSessionID(c)
	if err != nil {
		return nil, err
	}

	return ss.GetSessionUserByID(sid)

	//conn := ss.pool.Get()
	//defer conn.Close()
	//
	//key := SessionKey(sid)
	//
	//values, err := redis.Values(conn.Do("HGETALL", key))
	//if err != nil || len(values) == 0 {
	//	// TODO 应该做log处理
	//	return nil, ErrSessionExpired
	//}
	//var user SessionUser
	//if err := redis.ScanStruct(values, &user); err != nil {
	//	// TODO 应该做log处理
	//	return nil, ErrSessionExpired
	//}
	//return &user, nil
}

// 保存Session信息
// 同时会生成新的SessionID，更新Cookie
// Params:
//		context: echo context
//		sessionUser: 要缓存的用户信息
// Return:
//		error: 错误返回
func (ss *SessionStore) SaveSessionUser(c echo.Context, user *SessionUser) (string, error) {
	conn := ss.pool.Get()
	defer conn.Close()

	// 检查
	if user == nil {
		return "", errors.New("用户信息为空")
	}

	uid := user.UserID()
	if uid == 0 {
		return "", errors.New("用户id为空")
	}

	gid := user.GroupID()
	if gid == 0 {
		return "", errors.New("用户组织id为空")
	}

	// 设置用户角色id
	rid := 0
	var err error
	// 获取用户role
	if user.UserType == 1 {
		//	admin
		role, err := ent.GetInternalRoleByName(ADMIN_ROLE_NAME, user.GroupID())
		if err != nil {
			if gorm.IsRecordNotFoundError(err); err != nil || role == nil {
				// 没有组织模板和角色配置
				//return "", fmt.Errorf("用户未分配角色")
				return "", ErrRoleUnassigned
			} else {
				return "", fmt.Errorf("读取用户角色出错:%w", err)
			}
		}
		if role != nil {
			rid = role.ID
		}
	} else {
		// 普通用户
		rid, err = ent.GetRoleIDByUser(uid)
		if err != nil {
			if gorm.IsRecordNotFoundError(err); err != nil {
				// 没有组织模板和角色配置
				return "", ErrRoleUnassigned
			} else {
				return "", fmt.Errorf("读取用户角色出错:%w", err)
			}
		}
	}

	if rid <= 0 {
		// 没有组织模板和角色配置
		return "", ErrRoleUnassigned
	}

	user.RID = rid

	// sid, err := ss.GetSessionID(c)
	// 直接生成新的session id，旧的session自动过期，TODO 应该删除原值
	sid := ss.NewSessionID()

	key := SessionKey(sid)
	if _, err := conn.Do("HMSET", redis.Args{}.Add(key).AddFlat(user)...); err != nil {
		return "", fmt.Errorf("用户信息缓存失败:%w", err)
	}

	// 设置过期时间
	if _, err := conn.Do("EXPIRE", key, sessionStore.Expire); err != nil {
		return "", fmt.Errorf("设置缓存过期时间失败: %w", err)
	}

	// 设置为在线用户
	if err := ss.InsertOnlineUser(user.UID, sid); err != nil {
		return "", fmt.Errorf("注册在线用户出错: %w", err)
	}

	// 更新/设置 Cookie
	ss.SetCookie(c, sid)
	return sid, nil
}

func (ss *SessionStore) GetSession(c echo.Context) {

}

func (ss *SessionStore) GetSessionUserByID(sid string) (*SessionUser, error) {
	conn := ss.pool.Get()
	defer conn.Close()

	key := SessionKey(sid)

	//values, err := redis.Values(conn.Do("HGETALL", key))
	values, err := redis.Values(conn.Do("HGETALL", key))
	if err != nil || len(values) == 0 {
		// TODO 应该做log处理
		return nil, ErrSessionExpired
	}

	var user SessionUser
	//if err := json.Unmarshal(values, &user); err != nil {
	//	return nil, ErrSessionExpired
	//}

	if err := redis.ScanStruct(values, &user); err != nil {
		// TODO 应该做log处理
		return nil, ErrSessionExpired
	}

	if &user == nil {
		return nil, errors.New("用户信息未找到")
	}

	user.SID = sid
	return &user, nil
}

func (ss *SessionStore) UpdateSessionUser(c echo.Context, user *SessionUser) error {
	conn := ss.pool.Get()
	defer conn.Close()
	if _, err := conn.Do("HMSET", redis.Args{}.Add(SessionKey(user.SID)).AddFlat(user)...); err != nil {
		return fmt.Errorf("更新用户信息缓存失败:%w", err)
	}
	return nil
}

func SessionKey(sid string) string {
	return SESSION_KEY_PREFIX + sid
}

func (ss *SessionStore) InsertOnlineUser(uid int64, sid string) error {

	sids, err := ss.GetOnlineUserSid(uid)
	if err != nil {
		return fmt.Errorf("更新在线用户列表出错:%w", err)
	}

	duplicate := false
	for _, id := range sids {
		if id == sid {
			duplicate = true
			break
		}
	}

	if duplicate {
		return nil
	}
	sids = append(sids, sid)

	return ss.SaveOnlineUser(uid, sids)
}

func (ss *SessionStore) SaveOnlineUser(uid int64, sids []string) error {
	value, err := json.Marshal(sids)
	if err != nil {
		return fmt.Errorf("更新在线用户，序列化出错:%w", err)
	}

	conn := ss.pool.Get()
	defer conn.Close()

	if _, err := conn.Do("HSET", ONLINE_USERS, strconv.FormatInt(uid, 10), string(value)); err != nil {
		return fmt.Errorf("更新在线用户，存储出错:%w", err)
	}
	return nil
}

func (ss *SessionStore) GetOnlineUserByUid(uid int64) ([]*SessionUser, error) {

	sids, err := ss.GetOnlineUserSid(uid)
	if err != nil {
		return nil, fmt.Errorf("获取在线用户出错: %w", err)
	}
	var users []*SessionUser
	var survivor []string
	for _, sid := range sids {
		user, err := ss.GetSessionUserByID(sid)
		if err == nil && user != nil {
			survivor = append(survivor, sid)
			users = append(users, user)
		}
	}
	if len(sids) > len(survivor) {
		if err := ss.SaveOnlineUser(uid, survivor); err != nil {
			//	TODO log
		}
	}

	return users, nil
}

func (ss *SessionStore) GetOnlineUserSid(uid int64) ([]string, error) {
	conn := ss.pool.Get()
	defer conn.Close()

	var sids []string

	value, err := redis.Bytes(conn.Do("HGET", ONLINE_USERS, uid))

	if err != nil {
		if err == redis.ErrNil {
			return sids, nil
		}
		return nil, fmt.Errorf("获取在线用户%v SID出错:%w", uid, err)
	}

	if err := json.Unmarshal(value, &sids); err != nil {
		return nil, fmt.Errorf("获取在线用户%v SID,解析出错:%w", uid, err)
	}

	return sids, nil
}

// RemoveOnlineUser
// 从在线用户列表中删除对应的Session
func (ss *SessionStore) RemoveOnlineUser(uid int64, sid string) error {
	conn := ss.pool.Get()
	defer conn.Close()

	sids, err := ss.GetOnlineUserSid(uid)
	if err != nil {
		return fmt.Errorf("移除在线用户session列表出错:%w", err)
	}
	for i, id := range sids {
		if sid == id {
			sids = append(sids[:i], sids[i+1:]...)
			break
		}
	}

	if err := ss.SaveOnlineUser(uid, sids); err != nil {
		return fmt.Errorf("移除在线用户session列表，保存列表出错：%w", err)
	}
	return nil
}
