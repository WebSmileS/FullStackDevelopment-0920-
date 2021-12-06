package com.yrt.common.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.constant.MessageConstant;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.spring.SpringUtils;

/**
 * 获取i18n资源文件
 * 
 * @author Runner
 */
public class MessageUtils {
	
	private static final Logger log = LoggerFactory.getLogger(MessageUtils.class);
	
	/**
	 * 根据消息键和参数 获取消息 委托给spring messageSource
	 *
	 * @param code 消息键
	 * @param args 参数
	 * @return
	 */
	public static String message(String code, Object... args) {
		MessageSource messageSource = SpringUtils.getBean(MessageSource.class);
		return messageSource.getMessage(code, args, null);
	}
	
	public static String formatMessage(String template, Object... args) {
		MessageFormat format = new MessageFormat(template);
		return format.format(args);
	}

	/**
	 * 
	 * @param mobilePhone
	 * @param tempID 来自MessageConstant.TEMP_*
	 * @param tempValue 验证码或者空字串
	 */
	public static void sendMessage(String mobilePhone, String tempID, String tempValue) {
		// 判断手机号码的正确性
		try {
			PatternUtils.validatePhone(mobilePhone, "user.mobile.phone.number.not.valid");
			Map<String, Object> params = new HashMap<String, Object>();// 请求参数
			params.put("mobile", mobilePhone);// 接收短信的手机号码
			params.put("tpl_id", tempID);// 短信模板ID，请参考个人中心短信模板设置
			params.put("tpl_value", tempValue);// 变量名和变量值对。如果你的变量名或者变量值中带有#&=中的任意一个特殊符号，请先分别进行urlencode编码后再传递，<a
			// href="http://www.juhe.cn/news/index/id/50" target="_blank">详细说明></a>
			params.put("key", MessageConstant.APPKEY);// 应用APPKEY(应用详细页查询)
			params.put("dtype", "");// 返回数据的格式,xml或json，默认json
			
			try {
				String result = net(MessageConstant.SEND_URL, params, "GET");
				JSONObject object = JSONObject.parseObject(result);
				if (object.getIntValue("error_code") != 0) {
					throw new BusinessException(object.getString("reason"));
				}
			} catch (Exception e) {
				throw new BusinessException(e.getMessage());
			}
		}catch (ValidateException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 *
	 * @param strUrl 请求地址
	 * @param params 请求参数
	 * @param method 请求方法
	 * @return 网络请求字符串
	 * @throws Exception
	 */
	public static String net(String strUrl, Map<String, Object> params, String method) throws Exception {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			if (method == null || method.equals("GET")) {
				strUrl = strUrl + "?" + urlencode(params);
			}
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (method == null || method.equals("GET")) {
				conn.setRequestMethod("GET");
			} else {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
			}
			conn.setRequestProperty("User-agent", MessageConstant.userAgent);
			conn.setUseCaches(false);
			conn.setConnectTimeout(MessageConstant.DEF_CONN_TIMEOUT);
			conn.setReadTimeout(MessageConstant.DEF_READ_TIMEOUT);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			if (params != null && method.equals("POST")) {
				try {
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes(urlencode(params));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			InputStream is = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, MessageConstant.DEF_CHATSET));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sb.append(strRead);
			}
			rs = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rs;
	}

	// 将map型转为请求参数型
	public static String urlencode(Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> i : data.entrySet()) {
			try {
				sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
