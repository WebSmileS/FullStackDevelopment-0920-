package controller

// 发送消息通知
// swagger:parameters reqNotification
type reqNotification struct {
	// Required: true
	// in: body
	notificationReq NotificationReq
}
