package twisted

// 对象存储，七牛云
type Object struct {
	Image      QiNiuBacket
	Attachment QiNiuBacket
}

// 七牛云配置
type QiNiuBacket struct {
	Service      string
	AccessKey    string
	SecretKey    string
	CallbackUrl  string
	Backet       string
	DownloadHost string
	Expire       int
}
