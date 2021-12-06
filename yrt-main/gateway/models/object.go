package models

// swagger:model
type DownloadUrlAndTime struct {
	URL    string `json:"url"`
	Begin  int64  `json:"begin"`
	Expire int64  `json:"expire"`
}

// swagger:model
type UploadUrlAndTime struct {
	Token  string `json:"token"`
	Begin  int64  `json:"begin"`
	Expire int64  `json:"expire"`
}
