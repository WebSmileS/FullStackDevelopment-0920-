package twisted

import (
	"time"

	"github.com/qiniu/api.v7/auth/qbox"
	"github.com/qiniu/api.v7/storage"
)

type QiNiu struct {
	mac            *qbox.Mac
	Backet         string
	access_key     string
	secret_key     string
	callback_url   string
	callback_body  string
	expireDuration time.Duration
}

type CallBack struct {
	Key    string `json:"key"`
	Hash   string `json:"hash"`
	Fsize  int    `json:"fsize"`
	Bucket string `json:"bucket"`
}

//func NewQiNiu() *QiNiu {
//	qiniu := new(QiNiu)
//
//	qiniu.access_key = "CSCkkxGgnGcDva7W_x5x20dZUulOQdchyWBtmNPi"
//	qiniu.secret_key = "tQcw2D7w166B2iGn0GOgdfEh--BAbLJtHwPQP3-l"
//	qiniu.callback_url = "http://yrtdev.bjyixiu.com/image/qiniu/upcallback"
//	qiniu.callback_body = `{"key":"$(key)","hash":"$(etag)","fsize":$(fsize),"bucket":"$(bucket)"}`
//	qiniu.Backet = "yrtdev"
//	qiniu.mac = qbox.NewMac(qiniu.access_key, qiniu.secret_key)
//	return qiniu
//}

func (qiniu *QiNiu) uploadToken() string {
	putPolicy := storage.PutPolicy{
		Scope:            qiniu.Backet,
		CallbackURL:      qiniu.callback_url,
		CallbackBody:     qiniu.callback_body,
		CallbackBodyType: "application/json",
	}
	upToken := putPolicy.UploadToken(qiniu.mac)
	return upToken
}

func (qiniu *QiNiu) downloadUrl(host, key string) string {
	return storage.MakePrivateURL(qiniu.mac, host, key, time.Now().Add(time.Second*qiniu.expireDuration).Unix())
}
