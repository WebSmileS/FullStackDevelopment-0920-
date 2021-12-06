package com.yrt.common.constant;

/**
 * 短信公用常量
 * 
 * @author lansy
 *
 */
public interface MessageConstant {

	// 屏蔽字检查请求接口地址
	public static final String CHECK_URL = "http://v.juhe.cn/sms/black";
	// 发送短信请求接口地址
	public static final String SEND_URL = "http://v.juhe.cn/sms/send";
	public static final String DEF_CHATSET = "UTF-8";
	public static final int DEF_CONN_TIMEOUT = 30000;
	public static final int DEF_READ_TIMEOUT = 30000;
	// 配置您申请的KEY
	public static final String APPKEY = "e024ba5badef109ce732494c54278d97";
	public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

	// 模板ID
	
	// 等待审核 【医润通】感谢您的注册，请耐心等待平台审核
	public static final String TEMP_WAITING_AUDIT = "182838";

	// 审核通过 【医润通】尊敬的用户，您的医润通账号已注册成功，用户名为#useraccount#，密码为#password#，请妥善保存。
	public static final String TEMP_ADUIT_PASS = "57379";

	// 审核不通过 【医润通】对不起，您#orgName#的资料审核未通过，原因: #reason#。
	public static final String TEMP_ADUIT_NO_PASS = "57380";
	
	// 帐号开通 【医润通】您的帐号已经开通，用户名为#useraccount#，初始密码为#password#，登录后请尽快修改密码
	public static final String TEMP_ACCOUNT_OPENING = "182871";
		
	// 找回密码 【医润通】您正在找回密码，您的验证码是#code#
	public static final String TEMP_BACK_PASSWORD = "100293";

	// 发送验证码 【医润通】您的验证码是#code#。如非本人操作，请忽略本短信
	public static final String TEMP_SEND_VERIFY_CODE = "100292";

	// 提醒机构认证 【医润通】您的机构尚未认证，为了不影响业务的开展，请尽快认证！如已认证，请忽略本短信
	public static final String TEMP_REMINDER_ORGIN = "57569";

	// 重置密码 【医润通】您的管理员密码已重置为#password#，请妥善保存。
	public static final String TEMP_RESET_PASSWORD = "57574";

	// 您在 【医润通】小程序注册的信息没有通过管理员审核。
	public static final String TEMP_REG_FAIL = "98081";

}
