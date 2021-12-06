package com.yrt.common.utils;

import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.sun.mail.util.MailSSLSocketFactory;

public class EMailUtils {
	
	private static final String FromTo="mail.sys@bjyixiu.com";// 发件人电子邮箱
	private static final String Host ="smtp.mxhichina.com"; //  邮件服务器
	private static final String PassWord ="Aa1234567"; //第三方发件人账号授权码，跟邮箱密码不一样的

	/**
	 * 
	 * @param To    收件人电子邮箱
	 * @param Title 标题
	 * @param Text  文本内容
	 * @throws GeneralSecurityException
	 */
	public static void sendEmail(String To, String Title, String Text) throws GeneralSecurityException {
		PatternUtils.validateEmail(To, "user.email.not.valid");
		// 获取系统属性
		Properties properties = System.getProperties();
		// 设置邮件服务器
		properties.setProperty("mail.smtp.host", Host);
		properties.put("mail.smtp.auth", "true");
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.ssl.socketFactory", sf);
		// 获取默认session对象
		//Session session = Session.getDefaultInstance(properties, new Authenticator() {
		Session session = Session.getInstance(properties, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(FromTo, PassWord); // 发件人邮件用户名、第三方密码
			}
		});
		try {
			// 创建默认的 MimeMessage 对象
			MimeMessage message = new MimeMessage(session);
			// Set From: 头部头字段
			message.setFrom(new InternetAddress(FromTo));
			// Set To: 头部头字段
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(To));
			// Set Subject: 头部头字段
			message.setSubject(Title);
			// 设置消息体
			message.setText(Text);
			// 发送消息
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
