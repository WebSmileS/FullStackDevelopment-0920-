package com.yrt.common.constant;

import java.math.BigDecimal;

/**
 * <p>通用常量信息</p>
 * 
 * @author Runner
 */
public class Constants
{
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";
    
    public static final String GBK = "GBK";

    /**
     * <p>通用成功标识</p>
     */
    public static final String SUCCESS = "0";

    /**
     * <p>通用失败标识</p>
     */
    public static final String FAIL = "1";

    /**
     * <p>登录成功</p>
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * <p>注销</p>
     */
    public static final String LOGOUT = "Logout";

    /**
     * <p>登录失败</p>
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * <p>自动去除表前缀</p>
     */
    public static String AUTO_REOMVE_PRE = "true";

    /**
     * <p>当前记录起始索引</p>
     */
    public static String PAGE_NUM = "pageNum";

    /**
     * <p>每页显示记录数</p>
     */
    public static String PAGE_SIZE = "pageSize";

    /**
     * <p>排序列</p>
     */
    public static String ORDER_BY_COLUMN = "orderByColumn";
    
    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * <p>排序的方向 "desc" 或者 "asc".</p>
     */
    public static String IS_ASC = "isAsc";
    
    //基本状态
    public static final int ORG_STATUS_NORMAL = 1;
    public static final int ORG_STATUS_BLOCKED = 0;
    
    //删除状态
    public static final int ORG_DELETE = 1;
    public static final int ORG_NOT_DELETE = 0;
    
    //平台审核注册机构状态
    public static final int ORG_REG_AUDIT_STATUS_PEDDING = 0;
    public static final int ORG_REG_AUDIT_STATUS_PASS = 1;
    public static final int ORG_REG_AUDIT_STATUS_NOPASS = 2;
    
    //机构类型
    public static final int SYSTEM_TYPE_VENDOR = 3;
    public static final int SYSTEM_TYPE_DEALER = 2;
    public static final int SYSTEM_TYPE_HOSPITAL = 1;
    public static final int SYSTEM_TYPE_PLATFORM = 0;
    
    //默认管理员账号
    public static final String DEFAULT_USER_NAME_PREFIX = "871";
    public static final String DEFAULT_USER_PASSWORD = "a1234567";
    
    //产品发布状态
    public static final int PRODUCT_RELEASE_STATUS_PANDING = 0;
    public static final int PRODUCT_RELEASE_STATUS_PUBLIC = 1;
    
    //用户账号类型
    public static final int USER_TYPE_NORMAL = 0;
    public static final int USER_TYPE_ADMIN = 1;
    
    //预锁定库存标识
    public static final String PRE_LOCK_BATCH_NUMBER = "PRE_LOCK";

    // 倍率
    public static final BigDecimal QUANTITY_INT_SCALE = BigDecimal.valueOf(1000);
    
}
