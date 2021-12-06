package com.yrt.common.utils.sql;

import com.yrt.common.utils.StringUtils;

/**
 * sql操作工具类
 * 
 * @author Runner
 */
public class SqlUtil
{
    /**
     * 仅支持字母、数字、下划线、空格、逗号（支持多个字段排序）
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,]+";
    
    /**
     * like 值里 需要转义的特殊字符：_%'"\
     */
    private static final String LIKE_VALUE_ESC_PATTERN = "[_%'\"\\\\]";

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value)
    {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value))
        {
            return StringUtils.EMPTY;
        }
        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySql(String value)
    {
        return value.matches(SQL_PATTERN);
    }
    
    /**
     * escape user input text for like operation, replace sql-widcard chars to make sure represents itself(escape <code>_%'"\</code>).
     * <p>
     * <strong>Notes: </strong> 这里用默认的mysql转义符(\), sql 中不要重新定义escape符。
     * @param value original user input text
     * @return escaped string without like-wildcard semantics.
     */
    public static String escapeLikeValue(String value) {
    	if (StringUtils.isEmpty(value)) {
    		return value;
    	}
    	return value.replaceAll(LIKE_VALUE_ESC_PATTERN, "\\\\$0");
    }
    
    /**
     * 
     * @param value
     * @return
     */
    public static String containsLikeValue(String value) {
    	if (StringUtils.isEmpty(value)) {
    		return null;
    	}
    	return "%" + escapeLikeValue(value) + "%";
    }

}
