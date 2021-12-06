package com.yrt.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 * 
 * @author Runner
 */
@Component
@ConfigurationProperties(prefix = "yrt")
public class YrtConfig {
	/** 项目名称 */
	private String name;
	/** 版本 */
	private String version;
	/** 版权年份 */
	private String copyrightYear;
	/** 上传路径 */
	private static String profile;
	/** 获取地址开关 */
	private static boolean addressEnabled;
	/** 获取jwt过期时间 */
	private static String jwtValidityPeriod;
	
	private static String pushAddress;
	
	/**
	 * 数据节点id（多节点id 生成需要）. max=31
	 */
	private int dataNodeId = 1;

	public static String getJwtValidityPeriod() {
		return jwtValidityPeriod;
	}

	public static void setJwtValidityPeriod(String jwtValidityPeriod) {
		YrtConfig.jwtValidityPeriod = jwtValidityPeriod;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCopyrightYear() {
		return copyrightYear;
	}

	public void setCopyrightYear(String copyrightYear) {
		this.copyrightYear = copyrightYear;
	}

	public static String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		YrtConfig.profile = profile;
	}

	public static boolean isAddressEnabled() {
		return addressEnabled;
	}

	public static void setAddressEnabled(boolean addressEnabled) {
		YrtConfig.addressEnabled = addressEnabled;
	}

	public static String getAvatarPath() {
		return profile + "avatar/";
	}

	public static String getDownloadPath() {
		return profile + "download/";
	}

	public static String getUploadPath() {
		return profile + "upload/";
	}

	public int getDataNodeId() {
		return dataNodeId;
	}

	public void setDataNodeId(int dataNodeId) {
		this.dataNodeId = dataNodeId;
	}

	public static String getPushAddress() {
		return pushAddress;
	}

	public static void setPushAddress(String pushAddress) {
		YrtConfig.pushAddress = pushAddress;
	}
}
