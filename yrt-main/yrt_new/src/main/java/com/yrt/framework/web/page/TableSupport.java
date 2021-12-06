package com.yrt.framework.web.page;

import com.yrt.common.constant.Constants;
import com.yrt.common.utils.ServletUtils;

/**
 * 表格数据处理
 * 
 * @author Runner
 */
public class TableSupport
{
    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain()
    {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(ServletUtils.getParameterToInt(Constants.PAGE_NUM));
        pageDomain.setPageSize(ServletUtils.getParameterToInt(Constants.PAGE_SIZE));
        pageDomain.setOrderByColumn(ServletUtils.getParameter(Constants.ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(Constants.IS_ASC));
        return pageDomain;
    }

    public static PageDomain buildPageRequest()
    {
        return getPageDomain();
    }
    
    /**
     * 
     * @return null if without valid page info.
     */
    public static PageDomain buildValidPageRequest() {
    	PageDomain page = getPageDomain();
    	return page.getPageNum() != null && page.getPageSize() != null?page : null;
    }

}
