package com.yrt.framework.aspectj.lang.enums;

/**
 * 操作人类别
 * 
 * @author Runner
 *
 */
public enum OperatorType
{
    /**
     * 其它
     */
    OTHER,

    /**
     * 后台用户
     */
    MANAGE,
    
    /**
     * 业务用户
     */
    BUSINESS,
    
    /**
     * 平台用户
     */
    PLATFORM,

    /**
     * 手机端用户
     */
    MOBILE,
    
    /**
     * 外部系统调用
     */
    EXTERNAL_CALL
}
