package com.yrt.project.api.business.organization;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.organization.service.IOrgService;
import com.yrt.project.api.business.organization.vo.AddOrgDeptForm;
import com.yrt.project.api.business.organization.vo.AddOrgEmployeeForm;
import com.yrt.project.api.business.organization.vo.EditOrgDeptForm;
import com.yrt.project.api.business.organization.vo.EditOrgEmployeeForm;
import com.yrt.project.api.common.vo.SearchForm;
import com.yrt.project.modular.organization.domain.OrgDept;
import com.yrt.project.modular.organization.domain.OrgEmployee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/org")
@Api(tags = "业务-机构部门、人员管理接口")
public class OrgController extends BaseController {

	@Autowired
	private IOrgService orgService;
	
	//机构部门管理
	@Log(title = "business.org", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/dept/add/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "新增机构部门", notes = "新增机构部门")
	public ResultInfo<OrgDept> addDealerDept(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@Validated AddOrgDeptForm form) {
		OrgDept dept = orgService.insertOrgDept(convertSystemType(systemType), id, form);
		if(dept != null) {
			return ResultInfo.success(dept);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "business.org", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/dept/edit/{id}/{did}", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑机构部门", notes = "编辑机构部门")
	public AjaxResult editDealerDept(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@ApiParam(name="did",value="部门ID",required=true) @PathVariable Long did,
			EditOrgDeptForm form) {
		return toAjax(orgService.updateOrgDept(convertSystemType(systemType), id, did, form));
	}
	
	@Log(title = "business.org", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/dept/del/{id}/{did}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除机构部门", notes = "删除机构部门")
	public AjaxResult delDealerDept(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@ApiParam(name="did",value="部门ID",required=true) @PathVariable Long did) {
		return toAjax(orgService.deleteOrgDept(convertSystemType(systemType), id, did));
	}
	
	@Log(title = "business.org", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/dept/info/{id}/{did}", method = RequestMethod.GET)
	@ApiOperation(value = "根据机构ID和部门ID查询机构部门详情", notes = "根据机构ID和部门ID查询机构部门详情")
	public ResultInfo<OrgDept> getDealerDeptInfo(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@ApiParam(name="did",value="部门ID",required=true) @PathVariable Long did) {
		OrgDept dept = orgService.selectOrgDeptInfoById(convertSystemType(systemType), id, did);
		orgService.selectOrgDeptParentsById(dept, convertSystemType(systemType), id, dept.getParent_department_inner_sn());
		return ResultInfo.success(dept);
	}
	
	@Log(title = "business.org", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/dept/toplist/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "根据机构ID查询机构所有一级部门", notes = "根据机构ID和部门ID查询机构部门详情")
	public ResultInfo<List<OrgDept>> getTopLevelDealerDeptList(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		return ResultInfo.success(orgService.selectTopLevelOrgDeptListByOrgId(convertSystemType(systemType), id));
	}
	
	@Log(title = "business.org", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/dept/toplist/{id}/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据机构ID和上级部门ID查询机构所有下级部门", notes = "根据机构ID和上级部门ID查询机构所有下级部门")
	public ResultInfo<List<OrgDept>> getTopLevelDealerDeptList(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@ApiParam(name="pid",value="上级部门ID",required=true) @PathVariable Long pid) {
		return ResultInfo.success(orgService.selectOrgDeptListByPid(convertSystemType(systemType), id, pid));
	}
	
	@Log(title = "business.org", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/dept/topTree/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "根据机构ID查询机构所有部门", notes = "根据机构ID查询机构所有部门")
	public AjaxResult getTreeDealerDeptList(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		return success().put("list", orgService.selectTreeOrgDeptListByOrgId(convertSystemType(systemType), id));
	}
	
	@Log(title = "business.org", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/dept/rel/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取当前人员相关部门", notes = "获取当前人员相关部门")
	public AjaxResult getRelDeptList(
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Long curUserId = ServletUtils.getCurUserID();
		return success().put("list", orgService.selectRelDeptList(systemType, oid, curUserId));
	}
	
	//机构人员管理
	@Log(title = "business.org", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/emp/add/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "新增机构人员", notes = "新增机构人员")
	public ResultInfo<OrgEmployee> addOrgEmployee(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@Validated AddOrgEmployeeForm form) {
		OrgEmployee emp = orgService.insertOrgEmployee(convertSystemType(systemType), id, form);
		if(emp != null) {
			return ResultInfo.success(emp);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "business.org", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/emp/edit/{id}/{eid}", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑机构人员", notes = "编辑机构人员")
	public AjaxResult editOrgEmployee(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@ApiParam(name="eid",value="人员ID",required=true) @PathVariable Long eid,
			EditOrgEmployeeForm form) {
		return toAjax(orgService.updateOrgEmployee(convertSystemType(systemType), id, eid, form));
	}
	
	@Log(title = "business.org", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/emp/del/{id}/{eid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除机构人员", notes = "删除机构人员")
	public AjaxResult delOrgEmployee(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@ApiParam(name="eid",value="人员ID",required=true) @PathVariable Long eid) {
		return toAjax(orgService.deleteOrgEmployee(convertSystemType(systemType), id, eid));
	}
	
	@Log(title = "business.org", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/emp/info/{id}/{eid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据机构ID和人员ID查询机构人员详情", notes = "根据机构ID和人员ID查询机构人员详情")
	public ResultInfo<OrgEmployee> getOrgEmployeeInfo(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@ApiParam(name="eid",value="人员ID",required=true) @PathVariable Long eid) {
		return ResultInfo.success(orgService.selectOrgEmployeeInfoById(convertSystemType(systemType), id, eid));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.org", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/emp/list/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "根据机构ID查询机构所有人员(分页)", notes = "根据机构ID查询机构所有人员(分页)")
	public TableDataInfo<OrgEmployee> getOrgEmployeeList(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			SearchForm form) {
		startPage();
		List<OrgEmployee> list = orgService.selectOrgEmployeeListByOrgId(convertSystemType(systemType), id, form);
		return getDataTable(list);
	}
	
	@Log(title = "business.org", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/emp/dropdownlist/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "查询机构所有人员用于下列列表", notes = "查询机构所有人员用于下列列表")
	public AjaxResult getOrgEmployeeList4DropdownList(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		return success().put("list",orgService.selectOrgEmployeeListByOrgId(convertSystemType(systemType), id));
	}
	
	@Log(title = "business.org", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/emp/topTree/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "所有员工规划图接口", notes = "所有员工规划图接口")
	public AjaxResult getTreeDealerEmpList(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		return success().put("list", orgService.selectTreeOrgEmpListByOrgId(convertSystemType(systemType), id));
	}
}
