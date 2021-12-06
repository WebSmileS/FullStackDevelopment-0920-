package com.yrt.framework.web.controller;

import java.beans.PropertyEditorSupport;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.DateUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.domain.*;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.OrgMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;

/**
 * web层通用数据处理
 * 
 * @author Runner
 */
public class BaseController
{
	
    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }else {
        	clearPage();
        }
    }
    
    protected void clearPage() {
    	PageHelper.clearPage();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        PageInfo pageInfo = new PageInfo(list);
        if(pageInfo.getPageNum() > pageInfo.getPages()) {
        	rspData.setRows(new ArrayList<>());
        }else {
        	rspData.setRows(list);
        }
        rspData.setTotal(pageInfo.getTotal());
        
        return rspData;
    }
    
    /**
     * 响应返回结果
     * 
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? success() : error();
    }
    
    /**
     * 响应返回结果
     * 
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }
    
    /**
     * 返回失败消息
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }
    
    /**
     * 返回成功消息
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    /**
     * 返回错误码消息
     */
    public AjaxResult error(int code, String message)
    {
        return AjaxResult.error(code, message);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

    /**
     * 转换收货单类型成为入库单类型
     * @param receiptType
     * @return
     */
    protected Integer convertReceiptTypeToGrnType(int receiptType) {
    	//收货单类型  0-DealerDealer 1-DealerVendor 2-hospitalDealer 3-hospitalVendor
    	//入库单类型  0-DealerDealer 1-DealerVendor 2-DealerHospital 3-HospitalHospital 4-HospitalDealer 5-HospitalVendor
    	switch (receiptType) {
		case 0:
			return 0;
		case 1:
			return 1;
		case 2:
			return 4;
		case 3:
			return 5;
		}
    	throw new DBException(String.format("错误的收货单类型【%d】,请检查!", receiptType));
    }
    
    @Autowired DealerMapper dMapper;
    @Autowired VendorMapper vMapper;
	@Autowired HospitalMapper hMapper;
	
	public OrgMapper getOrgMapper(SystemType systemType) {
		switch (systemType) {
		case HOSPITAL:
			return hMapper;
		case DEALER:
			return dMapper;
		case VENDOR:
			return vMapper;
		default:
		}
		throw new DBException("错误的机构类型!");
	}
	
    public OrgEmployee getCurEmployee() {
    	SystemType systemType = ServletUtils.getCurUserSystemType();
    	OrgMapper mapper = getOrgMapper(systemType);
    	OrgEmployee curEmp = mapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
    	if(curEmp == null) {
    		throw new DBException("获取登录人员信息出错!");
    	}
    	return curEmp;
    }
    
    public SystemType convertSystemType(int systemType) {
		switch(systemType) {
		case 1:
			return SystemType.HOSPITAL;
		case 2:
			return SystemType.DEALER;
		case 3:
			return SystemType.VENDOR;
		}
		throw new DBException("当前机构不支持该功能!");
	}
    
    public String setFileDownloadHeader(HttpServletRequest request, String fileName)
			throws UnsupportedEncodingException {
		final String agent = request.getHeader("USER-AGENT");
		String filename = fileName;
		if (agent.contains("MSIE")) {
			// IE浏览器
			filename = URLEncoder.encode(filename, "utf-8");
			filename = filename.replace("+", " ");
		} else if (agent.contains("Firefox")) {
			// 火狐浏览器
			filename = new String(fileName.getBytes(), "ISO8859-1");
		} else if (agent.contains("Chrome")) {
			// google浏览器
			filename = URLEncoder.encode(filename, "utf-8");
		} else {
			// 其它浏览器
			filename = URLEncoder.encode(filename, "utf-8");
		}
		return filename;
	}
    
    //获取当年登录人仓库权限
    public List<Long> getCurUserWarehouseIds() {
  		return ServletUtils.getCurUserInfo().getWarehouseModelList()
  				.stream()
  				.map(WarehouseModel::getWarehouse_model_inner_sn)
  				.collect(Collectors.toList());
  	}
}
