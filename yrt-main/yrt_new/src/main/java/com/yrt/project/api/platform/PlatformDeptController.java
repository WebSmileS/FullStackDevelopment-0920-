package com.yrt.project.api.platform;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.*;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.common.vo.SearchForm;
import com.yrt.project.api.platform.service.IPlatformDeptService;
import com.yrt.project.api.platform.vo.organization.AddPlatformDeptForm;
import com.yrt.project.api.platform.vo.organization.AddPlatformEmployeeForm;
import com.yrt.project.api.platform.vo.organization.EditPlatformDeptForm;
import com.yrt.project.api.platform.vo.organization.EditPlatformEmployeeForm;
import com.yrt.project.modular.organization.domain.PlatformDept;
import com.yrt.project.modular.organization.domain.PlatformEmployee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/platform")
@Api(tags = "平台-部门部门、人员管理接口")
public class PlatformDeptController extends BaseController {

	@Autowired
	private IPlatformDeptService platformDeptService;

// 平台部门
	@Log(title = "platform.org.dept", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/dept/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "根据平台部门id查询平台部门信息", notes = "根据平台部门id查询平台部门信息")
	public ResultInfo<PlatformDept> getPlatformDeptInfo(
			@ApiParam(name = "id", value = "部门ID", required = true) @PathVariable Long id) {
		PlatformDept platDept = platformDeptService.selectPlatformDeptInfo(id);
		return ResultInfo.success(platDept);
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/dept/childrenlist/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "根据平台部门id查询平台该部门的子部门列表", notes = "根据平台部门id查询平台该部门的子部门列表")
	public AjaxResult getPlatformDeptListByParentId(
			@ApiParam(name = "id", value = "上级部门ID", required = true) @PathVariable Long id) {
		return success().put("list", platformDeptService.selectPlatformDeptChildrenList(id));
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/dept/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增平台部门信息", notes = "新增平台部门信息")
	public ResultInfo<PlatformDept> addPlatformDept(AddPlatformDeptForm platDept) {
		 long dept_id = platformDeptService.insertPlatformDept(platDept) ;
		 return ResultInfo.success(platformDeptService.selectPlatformDeptInfo(dept_id));
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/dept/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改平台部门信息", notes = "修改平台部门信息")
	public AjaxResult updatePlatformDept(EditPlatformDeptForm platDept) {
		return toAjax(platformDeptService.updatePlatformDept(platDept));
	}
	

	@Log(title = "platform.org.dept", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/dept/del/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除平台部门", notes = "删除平台部门")
	public AjaxResult delPlatformDept(@PathVariable Long id) {
		return toAjax(platformDeptService.deletePlatformDept(id));
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/dept/toplevel", method = RequestMethod.GET)
	@ApiOperation(value = "查询平台所有顶级部门列表", notes = "查询平台所有顶级部门列表")
	public ResultList<PlatformDept> getTopLevelPlatformDept() {
		return ResultList.success(platformDeptService.selectTopLevelPlatformDept());
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/dept/tree", method = RequestMethod.GET)
	@ApiOperation(value = "查询平台所有部门", notes = "查询平台所有部门")
	public ResultList<PlatformDept> getTreelPlatformDeptList() {
		return ResultList.success(platformDeptService.selectTreePlatformDeptList());
	}

	// 平台人员管理
	@Log(title = "platform.org.dept", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "emp/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增平台人员", notes = "新增平台人员")
	public ResultInfo<PlatformEmployee> addPlatformEmp(@Validated AddPlatformEmployeeForm emp) {
		 long emp_id = platformDeptService.insertPlatformEmployee(emp);
		 return ResultInfo.success(platformDeptService.selectPlatformEmployeeInfoById(emp_id));
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/emp/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑平台人员", notes = "编辑平台人员")
	public AjaxResult editPlatformEmp(EditPlatformEmployeeForm emp) {
		return toAjax(platformDeptService.updatePlatformEmployee(emp));
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/emp/del/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除平台人员", notes = "删除平台人员")
	public AjaxResult delPlatformEmp(@PathVariable Long id) {
		return toAjax(platformDeptService.deletePlatformEmployee(id));
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/emp/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "根据平台人员ID查询平台人员详情", notes = "根据平台人员ID查询平台人员详情")
	public ResultInfo<PlatformEmployee> getPlatformEmpInfo(@PathVariable Long id) {
		return ResultInfo.success(platformDeptService.selectPlatformEmployeeInfoById(id));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "platform.org.dept", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/emp/list", method = RequestMethod.GET)
	@ApiOperation(value = "查询平台所有人员(分页)", notes = "查询平台所有人员(分页)")
	public TableDataInfo<PlatformEmployee> getPlatformEmpList(SearchForm form) {
		startPage();
		List<PlatformEmployee> list = platformDeptService.selectPlatformEmployeeList(form);
		return getDataTable(list);
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/emp/dropdownlist", method = RequestMethod.GET)
	@ApiOperation(value = "下列组件使用查询平台所有人员", notes = "查询平台所有人员")
	public ResultList<PlatformEmployee> getPlatformEmp4DropdownList() {
		return ResultList.success(platformDeptService.selectPlatformEmployeeDropdownList());
	}

	@Log(title = "platform.org.dept", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/emp/Tree", method = RequestMethod.GET)
	@ApiOperation(value = "查询平台人员树", notes = "查询平台人员树")
	public ResultList<PlatformEmployee> getPlatformEmpTree() {
		return ResultList.success(platformDeptService.selectTreePlatformEmployeeList());
	}
}
