package twisted

import (
	"time"

	"github.com/qiniu/api.v7/auth/qbox"
)

var attService *QiNiu
var imgService *QiNiu

var imageDownloadHost string
var attachmentDownloadHost string

func InitService(conf Object) {
	//附件（对象存储）服务
	attService = new(QiNiu)
	attService.access_key = conf.Attachment.AccessKey
	attService.secret_key = conf.Attachment.SecretKey
	attService.callback_url = conf.Attachment.CallbackUrl
	attService.callback_body = `{"key":"$(key)","hash":"$(etag)","fsize":$(fsize),"bucket":"$(bucket)"}`
	attService.Backet = conf.Attachment.Backet
	attService.mac = qbox.NewMac(attService.access_key, attService.secret_key)
	attService.expireDuration = time.Second * time.Duration(conf.Attachment.Expire)
	attachmentDownloadHost = conf.Attachment.DownloadHost

	//图片存储服务
	imgService = new(QiNiu)
	imgService.access_key = conf.Image.AccessKey
	imgService.secret_key = conf.Image.SecretKey
	imgService.callback_url = conf.Image.CallbackUrl
	imgService.callback_body = `{"key":"$(key)","hash":"$(etag)","fsize":$(fsize),"bucket":"$(bucket)"}`
	imgService.Backet = conf.Image.Backet
	imgService.mac = qbox.NewMac(imgService.access_key, imgService.secret_key)
	imgService.expireDuration = time.Second * time.Duration(conf.Image.Expire)
	imageDownloadHost = conf.Image.DownloadHost
}

func UploadImageToken() (string, int64, int64) {
	now := time.Now()
	return imgService.uploadToken(), now.UnixNano() / 1e6, now.Add(imgService.expireDuration).UnixNano() / 1e6
}

func UploadAttToken() (string, int64, int64) {
	now := time.Now()
	return attService.uploadToken(), now.UnixNano() / 1e6, now.Add(attService.expireDuration).UnixNano() / 1e6
}

func AttDownloadUrl(scheme, key string) string {
	return attService.downloadUrl(scheme+attachmentDownloadHost, key)
}

func ImgDownloadUrl(scheme, key string) string {
	return imgService.downloadUrl(scheme+imageDownloadHost, key)
}

func AttDownloadUrlWithTime(scheme, id string) (string, int64, int64) {
	begin := time.Now()
	url := AttDownloadUrl(scheme, id)
	expire := begin.Add(attService.expireDuration)
	return url, begin.UnixNano() / 1e6, expire.UnixNano() / 1e6
}

func ImgDonwloadUrlWithTime(scheme, id string) (string, int64, int64) {
	begin := time.Now()
	url := ImgDownloadUrl(scheme, id)
	expire := begin.Add(imgService.expireDuration)
	return url, begin.UnixNano() / 1e6, expire.UnixNano() / 1e6
}
