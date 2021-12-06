package com.yrt.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class PinyinUtils {

	private static final Logger logger = LoggerFactory.getLogger(PinyinUtils.class);

	/**
	 * 拼音首字母(源串为null 或 转换异常 返回 "")
	 * 
	 * @param s
	 * @return
	 */
	public static String getPy(String s) {
		if(StringUtils.isEmpty(s)) {
			return "";
		}
		try {
			return PinyinHelper.getShortPinyin(s);
		} catch (PinyinException e) {
			logger.info(s + "转换拼音错误!", e);
			return "";
		}
	}

	/**
	 * 全拼(源串为null 或 转换异常 返回 "")
	 * @param s
	 * @return 
	 */
	public static String getPinyin(String s) {
		if(StringUtils.isEmpty(s)) {
			return "";
		}
		try {
			return PinyinHelper.convertToPinyinString(s, "", PinyinFormat.WITHOUT_TONE);
		} catch (PinyinException e) {
			logger.info(s + "转换拼音错误!", e);
			return "";
		}
	}
}
