package com.yrt.project.api.business.warehouse;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.project.api.business.warehouse.service.IWarehouseService;
import com.yrt.project.api.business.warehouse.vo.AddWareHouseForm;
import com.yrt.project.api.business.warehouse.vo.AddWarehouseTypeForm;
import com.yrt.project.api.business.warehouse.vo.EditWareHouseForm;
import com.yrt.project.api.business.warehouse.vo.EditWarehouseTypeForm;
import com.yrt.project.api.business.warehouse.vo.WarehouseCheckForm;
import com.yrt.project.api.business.warehouse.vo.WarehouseScanForm;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import com.yrt.project.modular.warehouse.domain.WarehouseType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/warehouse")
@Api(tags = "业务-仓库管理接口")
public class WarehouseController extends BaseController {

	@Autowired IWarehouseService service;
	
	//机构仓库管理
	@Log(title = "business.warehouse", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/add/{oid}", method = RequestMethod.POST)
	@ApiOperation(value = "新增机构仓库", notes = "新增机构仓库")
	public ResultInfo<WarehouseModel> addWarehouse(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
			@RequestBody @Valid AddWareHouseForm form) {
		WarehouseModel model = null;
		model = service.insertWarehouseMode(oid, form);
		return ResultInfo.success(model);
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/edit/{oid}/{wid}", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑机构仓库", notes = "编辑机构仓库")
	public AjaxResult editWarehouse(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
			@ApiParam(name="wid",value="仓库ID",required=true) @PathVariable Long wid,
			@RequestBody @Valid EditWareHouseForm form) {
		return toAjax(service.updateWarehouseMode(oid, wid, form));
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/del/{oid}/{wid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除机构仓库", notes = "删除机构仓库")
	public AjaxResult deleteWarehouse(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
			@ApiParam(name="wid",value="仓库ID",required=true) @PathVariable Long wid) {
		return toAjax(service.deleteWarehouseMode(oid, wid));
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/toplist/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "查询所有机构一级仓库", notes = "查询所有机构一级仓库")
	public ResultList<WarehouseModel> getTopLevelWarehouseList(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid) {
		List<WarehouseModel> models = null;
		models = service.selectTopLevelWarehouseList(oid, null);
		
		return ResultList.success(models);
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/usable/toplist/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "查询所有可用的机构一级仓库", notes = "查询所有可用的机构一级仓库")
	public ResultList<WarehouseModel> getUsableTopLevelWarehouseList(
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid) {
		List<WarehouseModel> models = null;
		models = service.selectTopLevelWarehouseList(oid, 1L);
		
		return ResultList.success(models);
	}

	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "查询登录人所在部门关联的仓库chenbing", notes = "可能同时属于多个部门，存在多个仓库")
	public ResultList<WarehouseModel> getWarehouseList() {
		ContextUserInfo userInfo = ServletUtils.getCurUserInfo();
		return ResultList.success(userInfo.getWarehouseModelList());
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/list/{oid}/{wid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据机构ID和仓库ID查询所有下级仓库", notes = "根据机构ID和仓库ID查询所有下级仓库")
	public ResultList<WarehouseModel> getWarehouseListByPid(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
			@ApiParam(name="wid",value="仓库ID",required=true) @PathVariable Long wid) {
		List<WarehouseModel> models = null;
		models = service.selectWarehouseListByPid(oid, wid);
		
		return ResultList.success(models);
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/info/{oid}/{wid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据机构ID和仓库ID查询仓库信息", notes = "根据机构ID和仓库ID查询仓库信息")
	public ResultInfo<WarehouseModel> getWarehouseInfoById(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
			@ApiParam(name="wid",value="仓库ID",required=true) @PathVariable Long wid) {
		WarehouseModel model = null;
		model = service.selectWarehouseInfoById(oid, wid);
		return ResultInfo.success(model);
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/tree/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "树状显示所有仓库", notes = "树状显示所有仓库")
	public ResultList<WarehouseModel> getWarehouseListWithTree(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid) {
		List<WarehouseModel> models = null;
		models = service.selectAllWarehouseTree(oid, null);
		
		return ResultList.success(models);
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/usable/tree/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "树状显示所有可用仓库", notes = "树状显示所有可用仓库")
	public ResultList<WarehouseModel> getUsableWarehouseListWithTree(
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid) {
		List<WarehouseModel> models = null;
		models = service.selectAllWarehouseTree(oid, 1L);
		
		return ResultList.success(models);
	}
	
	//机构仓库类型
	@Log(title = "business.warehouse", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/type/{systemType}/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增机构仓库类型", notes = "新增机构仓库类型")
	public ResultInfo<WarehouseType> addWarehouseType(
			@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@Validated AddWarehouseTypeForm form) {
		WarehouseType type = null;
		type = service.insertWarehouseType(form);
		return ResultInfo.success(type);
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/type/{systemType}/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑机构仓库类型", notes = "编辑机构仓库类型")
	public AjaxResult editWarehouseType(
			@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@Validated EditWarehouseTypeForm form) {
		return toAjax(service.updateWarehouseType(form));
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/type/{systemType}/del/{oid}/{wtid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除机构仓库类型", notes = "删除机构仓库类型")
	public AjaxResult deleteWarehouseType(
			@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
			@ApiParam(name="wtid",value="仓库类型ID",required=true) @PathVariable Long wtid) {
		return toAjax(service.deleteWarehouseType(oid, wtid));
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/type/{systemType}/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "查询机构仓库类型", notes = "查询机构仓库类型")
	public ResultList<WarehouseType> getWarehouseTypeList(@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid) {
		List<WarehouseType> types = null;
		types = service.selectWarehouseTypeList(oid, null);
		return ResultList.success(types);
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/type/usable/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "查询可用机构仓库类型", notes = "查询可用机构仓库类型")
	public ResultList<WarehouseType> getUsableWarehouseTypeList(
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid) {
		List<WarehouseType> types = null;
		types = service.selectWarehouseTypeList(oid, 1L);
		return ResultList.success(types);
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{systemType}/check/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "检测仓库编码", notes = "检测仓库编码")
	public AjaxResult checkWarehouseSn(
			@ApiParam(name="systemType",value=" 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
			@Validated WarehouseCheckForm form) {
		return toAjax(service.checkWarehouseSn(oid, form));
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/scan/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "扫码查询仓库信息", notes = "扫码查询仓库信息")
	public ResultInfo<WarehouseModel> scan(
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
			@Validated WarehouseScanForm form) {
		return ResultInfo.success(service.scan(oid, form));
	}
}
