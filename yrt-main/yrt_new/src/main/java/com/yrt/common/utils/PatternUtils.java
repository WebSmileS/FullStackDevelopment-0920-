package com.yrt.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yrt.common.exception.ValidateException;

public class PatternUtils {

	/**
	 * 常用正则表达式：匹配非负整数（正整数 + 0）
	 */
	public final static Pattern pattern_integer_1 = Pattern.compile("^\\d+$");
	
	/**
	 * 常用正则表达式：匹配正整数
	 */
	public final static Pattern pattern_integer_2 = Pattern.compile("^[0-9]*[1-9][0-9]*$");
	
	/**
	 * 常用正则表达式：匹配非正整数（负整数  + 0）
	 */
	public final static Pattern pattern_integer_3 = Pattern.compile("^((-\\d+) ?(0+))$");
	
	/**
	 * 常用正则表达式：匹配负整数
	 */
	public final static Pattern pattern_integer_4 = Pattern.compile("^-[0-9]*[1-9][0-9]*$");
	
	/**
	 * 常用正则表达式：匹配整数
	 */
	public final static Pattern pattern_integer_5 = Pattern.compile("^-?\\d+$");

	/**
	 * 常用正则表达式：匹配非负浮点数（正浮点数 + 0）
	 */
	public final static Pattern pattern_float_1 = Pattern.compile("^\\d+(\\.\\d+)?$");
	
	/**
	 * 常用正则表达式：匹配正浮点数
	 */
	public final static Pattern pattern_float_2 = Pattern.compile("^(([0-9]+\\.[0-9]*[1-9][0-9]*) ?([0-9]*[1-9][0-9]*\\.[0-9]+) ?([0-9]*[1-9][0-9]*))$");
	
	/**
	 * 常用正则表达式：匹配非正浮点数（负浮点数 + 0）
	 */
	public final static Pattern pattern_float_3 = Pattern.compile("^((-\\d+(\\.\\d+)?) ?(0+(\\.0+)?))$");
	
	/**
	 * 常用正则表达式：匹配负浮点数
	 */
	public final static Pattern pattern_float_4 = Pattern.compile("^(-(([0-9]+\\.[0-9]*[1-9][0-9]*) ?([0-9]*[1-9][0-9]*\\.[0-9]+) ?([0-9]*[1-9][0-9]*)))$");
	
	/**
	 * 常用正则表达式：匹配浮点数
	 */
	public final static Pattern pattern_float_5 = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");

	/**
	 * 常用正则表达式：匹配由26个英文字母组成的字符串
	 */
	public final static Pattern pattern_letter_1 = Pattern.compile("^[A-Za-z]+$");
	
	/**
	 * 常用正则表达式：匹配由26个英文字母的大写组成的字符串
	 */
	public final static Pattern pattern_letter_2 = Pattern.compile("^[A-Z]+$");
	
	/**
	 * 常用正则表达式：匹配由26个英文字母的小写组成的字符串
	 */
	public final static Pattern pattern_letter_3 = Pattern.compile("^[a-z]+$");
	
	/**
	 * 常用正则表达式：匹配由数字、26个英文字母组成的字符串
	 */
	public final static Pattern pattern_letter_4 = Pattern.compile("^[A-Za-z0-9]+$");
	
	/**
	 * 常用正则表达式：匹配由数字、26个英文字母、下划线组成的字符串
	 */
	public final static Pattern pattern_letter_5 = Pattern.compile("^\\w+$");

	/**
	 * 常用正则表达式：匹配由数字、26个英文字母、下划线、中划线、点组成的字符串
	 */
	public final static Pattern pattern_letter_6 = Pattern.compile("^([a-z_A-Z-.+0-9]+)$");

	/**
	 * 常用正则表达式：匹配email地址
	 */
	public final static Pattern pattern_email = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");

	/**
	 * 常用正则表达式：匹配url
	 */
	public final static Pattern pattern_url_1 = Pattern.compile("^[a-zA-z]+://(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\S*)?$");
	
	/**
	 * 常用正则表达式：匹配url
	 */
	public final static Pattern pattern_url_2 = Pattern.compile("[a-zA-z]+://[^\\s]*");

	/**
	 * 常用正则表达式：匹配中文字符
	 */
	public final static Pattern pattern_chinese_1 = Pattern.compile("[\\u4e00-\\u9fa5]");
	
	/**
	 * 常用正则表达式：匹配双字节字符(包括汉字在内)
	 */
	public final static Pattern pattern_chinese_2 = Pattern.compile("[^\\x00-\\xff]");

	/**
	 * 常用正则表达式：匹配空行
	 */
	public final static Pattern pattern_line = Pattern.compile("\\n[\\s ? ]*\\r");

	/**
	 * 常用正则表达式：匹配HTML标记
	 */
	public final static Pattern pattern_html_1 = Pattern.compile("/ <(.*)>.* <\\/\\1> ? <(.*) \\/>/");
	
	/**
	 * 常用正则表达式：匹配首尾空格
	 */
	public final static Pattern pattern_startEndEmpty = Pattern.compile("(^\\s*) ?(\\s*$)");

	/**
	 * 常用正则表达式：匹配帐号是否合法(字母开头，允许5-16字节，允许字母数字下划线)
	 */
	public final static Pattern pattern_accountNumber = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{4,15}$");

	/**
	 * 常用正则表达式：匹配国内电话号码，匹配形式如 0511-4405222 或 021-87888822
	 */
	public final static Pattern pattern_telephone = Pattern.compile("\\d{3}-\\d{8} ?\\d{4}-\\d{7}");

	/**
	 * 常用正则表达式：腾讯QQ号, 腾讯QQ号从10000开始
	 */
	public final static Pattern pattern_qq = Pattern.compile("[1-9][0-9]{4,}");

	/**
	 * 常用正则表达式：匹配中国邮政编码
	 */
	public final static Pattern pattern_postbody = Pattern.compile("[1-9]\\d{5}(?!\\d)");

	/**
	 * 常用正则表达式：匹配身份证, 中国的身份证为15位或18位
	 */
	public final static Pattern pattern_idCard = Pattern.compile("\\d{15} ?\\d{18}");

	/**
	 * 常用正则表达式：IP
	 */
	public final static Pattern pattern_ip = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

	/**
	 * 常用正则表达式：手机号
	 */
	public final static Pattern pattern_mobile = Pattern.compile("^0?(13[0-9]|15[012356789]|17[0123456789]|18[0123456789]|19[0123456789]|14[57])[0-9]{8}$");
	
	public static void validateRegex(String field, String regExpression, String errorKey) {
		validateRegex(field, regExpression, false, errorKey);
	}
	
	public static void validateRegex(String field, String regExpression, boolean isCaseSensitive, String errorKey) {
		if(StringUtils.isNull(field)) {
			throw new ValidateException(MessageUtils.message(errorKey));
		}
		Pattern pattern = isCaseSensitive ? Pattern.compile(regExpression) : Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(field);
        if (!matcher.matches()) {
        	throw new ValidateException(MessageUtils.message(errorKey));
        }
	}
	
	public static void validateEmail(String mail, String errorKey) {
		validateRegex(mail,pattern_email.pattern(), true, errorKey);
	}
	
	public static void validatePhone(String phone, String errorKey) {
		validateRegex(phone,pattern_mobile.pattern(),errorKey);
	}
	
	public static void main(String[] args) {
		Matcher matcher = pattern_mobile.matcher("19988704135");
		System.out.println(matcher.matches());
	}
}
