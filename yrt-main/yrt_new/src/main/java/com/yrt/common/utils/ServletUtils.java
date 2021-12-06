package com.yrt.common.utils;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.user.UserException;
import com.yrt.common.utils.spring.SpringUtils;
import com.yrt.common.utils.text.Convert;
import com.yrt.project.modular.organization.domain.OrgDept;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import com.yrt.project.modular.warehouse.mapper.DealerWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.VendorWarehouseMapper;

/**
 * 客户端工具类
 * 
 * @author Runner
 */
public class ServletUtils
{
    /**
     * 获取String参数
     */
    public static String getParameter(String name)
    {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue)
    {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name)
    {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue)
    {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest()
    {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse()
    {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession()
    {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     * 
     * @param response 渲染对象
     * @param string 待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string)
    {
        try
        {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否是Ajax异步请求
     * 
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request)
    {

        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("application/json") != -1)
        {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1)
        {
            return true;
        }

        String uri = request.getRequestURI();
        if (StringUtils.inStringIgnoreCase(uri, ".json", ".xml"))
        {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        if (StringUtils.inStringIgnoreCase(ajax, "json", "xml"))
        {
            return true;
        }

        return false;
    }
    
    //从URL中获取当前用户ID
    public static Long getCurUserID() {
    	if(StringUtils.isNotEmpty(getRequest().getHeader("uid"))) {
    		return Long.valueOf(getRequest().getHeader("uid").toString());
    	}else {
    		throw new UserException("user.unknown.error",null);
    	}
    }
    
  //从URL中获取当前用户所在机构类型 (0-平台、 1-医院 、 2-经销商、 3-厂商)
    public static SystemType getCurUserSystemType() {
    	if(StringUtils.isNotEmpty(getRequest().getHeader("systemType"))) {
    		switch (Integer.valueOf(getRequest().getHeader("systemType"))) {
    		case 0:
    			return SystemType.PLATFORM;
			case 1:
				return SystemType.HOSPITAL;
			case 2:
				return SystemType.DEALER;
			case 3:
				return SystemType.VENDOR;
			}
    		throw new DBException("当前机构不支持该功能!");
    	}else {
    		throw new UserException("user.unknown.error",null);
    	}
    }
    
    /**
     * 当前登录用户 org id(platform 为0)
     * @return
     */
    public static Long getCurUserOrgID() {
    	if(StringUtils.isNotEmpty(getRequest().getHeader("oid"))) {
    		return Long.valueOf(getRequest().getHeader("oid").toString());
    	} else {
    		throw new UserException("user.unknown.error",null);
    	}
    }
    
    public static ContextUserInfo getCurUserInfo() {
        Long curUserId = ServletUtils.getCurUserID();
        Long orgID = getCurUserOrgID();
        SystemType systemType = getCurUserSystemType();

        OrgEmployee employee=null;
        List<OrgDept> deptList = null;
        List<WarehouseModel> warehouseModelList=null;

        Map<String, Object> params = new HashMap<>();
        params.put("oid", orgID);
        params.put("uid", curUserId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id",orgID);
        map.put("user_inner_sn",curUserId);

        switch (systemType) {
            case HOSPITAL:
                HospitalMapper hospitalMapper = SpringUtils.getBean(HospitalMapper.class);
                employee = hospitalMapper.selectOrgEmployeeInfoByUid(map);
                deptList=hospitalMapper.deptList(params);

                HospitalWarehouseMapper hospitalWarehouseMapper = SpringUtils.getBean(HospitalWarehouseMapper.class);
                params.put("deptIds", deptList.stream().map(OrgDept::getDepartment_inner_sn).collect(toList()));
                warehouseModelList=hospitalWarehouseMapper.warehouses(params);
                break;
            case DEALER:
                DealerMapper dealerMapper = SpringUtils.getBean(DealerMapper.class);
                employee = dealerMapper.selectOrgEmployeeInfoByUid(map);
                deptList=dealerMapper.deptList(params);

                DealerWarehouseMapper dealerWarehouseMapper = SpringUtils.getBean(DealerWarehouseMapper.class);
                params.put("deptIds", deptList.stream().map(OrgDept::getDepartment_inner_sn).collect(toList()));
                warehouseModelList=dealerWarehouseMapper.warehouses(params);
                break;
            case VENDOR:
                VendorMapper vendorMapper = SpringUtils.getBean(VendorMapper.class);
                employee = vendorMapper.selectOrgEmployeeInfoByUid(map);
                deptList=vendorMapper.deptList(params);

                VendorWarehouseMapper vendorWarehouseMapper = SpringUtils.getBean(VendorWarehouseMapper.class);
                params.put("deptIds", deptList.stream().map(OrgDept::getDepartment_inner_sn).collect(toList()));
                warehouseModelList=vendorWarehouseMapper.warehouses(params);
                break;
//            case PLATFORM:
//            	PlatformDeptMapper platformMapper = SpringUtils.getBean(PlatformDeptMapper.class);
//            	PlatformEmployee platformEmployee = platformMapper.selectPlatformEmployeeInfoById(curUserId);
//            	employee = Utils.clone(platformEmployee, OrgEmployee.class);
//            	employee.setOrg_name("平台");
//            	employee.setId(orgID);
//                break;
            default:
            	break;
        }
        return ContextUserInfo.of(curUserId,orgID, systemType,employee,deptList,warehouseModelList);
    }
}
