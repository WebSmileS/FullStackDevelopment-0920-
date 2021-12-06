package com.yrt.project.api.platform.surgery;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.exception.BusinessException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.*;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.platform.surgery.service.ISurgeryService;
import com.yrt.project.api.platform.surgery.vo.AddBrand;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicPackageForm;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicPackageToPathForm;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicPathForm;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicKitForm;
import com.yrt.project.api.platform.surgery.vo.EditBrand;
import com.yrt.project.api.platform.surgery.vo.EditOrthopaedicKitForm;
import com.yrt.project.api.platform.surgery.vo.EditOrthopaedicPackageForm;
import com.yrt.project.api.platform.surgery.vo.EditOrthopaedicPathForm;
import com.yrt.project.api.platform.surgery.vo.SearchSurgeryKitForm;
import com.yrt.project.api.platform.surgery.vo.SearchSurgeryPackageForm;
import com.yrt.project.modular.surgery.domain.Brand;
import com.yrt.project.modular.surgery.domain.OrthopaedicPackage;
import com.yrt.project.modular.surgery.domain.OrthopaedicPath;
import com.yrt.project.modular.surgery.domain.OrthopaedicKit;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/platform/surgery")
@Api(tags = "平台-骨科管理接口")
public class SurgeryController extends BaseController {

	@Autowired
	private ISurgeryService service;
	
	//骨科路径
	@Log(title = "platform.surgery", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/path/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增骨科路径", notes = "新增骨科路径")
	public ResultInfo<OrthopaedicPath> addOrthopaedicPath(@Validated AddOrthopaedicPathForm form) {
		OrthopaedicPath path = service.addOrthopaedicPath(form);
		if(path != null) {
			return ResultInfo.success(path);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/path/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改骨科路径信息", notes = "修改骨科路径信息")
	public AjaxResult editOrthopaedicPath(
			@Validated EditOrthopaedicPathForm form) {
		if (!form.hasUpdate()) {
			throw new BusinessException(MessageUtils.message("update.param.not.null"));
		}
		return toAjax(service.editOrthopaedicPath(form));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/path/del/{pid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除骨科路径", notes = "删除骨科路径")
	public AjaxResult delOrthopaedicPath(
			@ApiParam(name="pid",value="骨科路径ID",required=true) @PathVariable Long pid) {
		return toAjax(service.delOrthopaedicPath(pid));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/path/top", method = RequestMethod.GET)
	@ApiOperation(value = "获取顶级骨科路径", notes = "获取顶级骨科路径")
	public AjaxResult selectTopLevelOrthopaedicPathList() {
		return success().put("list" ,service.selectTopLevelOrthopaedicPathList());
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/path/child/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取下级骨科路径", notes = "获取下级骨科路径")
	public AjaxResult selectOrthopaedicPathChildrenById(
			@ApiParam(name="pid",value="骨科路径ID",required=true) @PathVariable Long pid) {
		return success().put("list" ,service.selectOrthopaedicPathChildrenById(pid));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/path/info/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取骨科路径信息", notes = "获取顶级骨科路径信息")
	public ResultInfo<OrthopaedicPath> selectOrthopaedicPathById(
			@ApiParam(name="pid",value="骨科路径ID",required=true) @PathVariable Long pid,
			@ApiParam(name="type_inner_sn",value="手术类型ID",required=false) Integer type_inner_sn,
			@ApiParam(name="brand_inner_sn",value="品牌类型ID",required=false) Long brand_inner_sn) {
		return ResultInfo.success(service.selectOrthopaedicPathById(pid, type_inner_sn, brand_inner_sn));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/path/relpackage", method = RequestMethod.POST)
	@ApiOperation(value = "关联手术包到骨科路径", notes = "关联手术包到骨科路径")
	public ResultInfo<OrthopaedicPath> addOrthopaedicPackageToPath(@Validated AddOrthopaedicPackageToPathForm form) {
		OrthopaedicPath path = service.addOrthopaedicPackageToPath(form);
		if(path != null) {
			return ResultInfo.success(path);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/path/unrelpackage", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除骨科路径上关联的手术包", notes = "删除骨科路径上关联的手术包")
	public AjaxResult delOrthopaedicPackageToPath(@Validated AddOrthopaedicPackageToPathForm form) {
		return toAjax(service.delOrthopaedicPackageToPath(form));
	}
	
	//骨科手术包
	@Log(title = "platform.surgery", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/package/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增骨科手术包", notes = "新增骨科手术包")
	public ResultInfo<OrthopaedicPackage> addOrthopaedicPackage(@RequestBody @Valid AddOrthopaedicPackageForm form) {
		OrthopaedicPackage packages = service.addOrthopaedicPackage(form);
		if(packages != null) {
			return ResultInfo.success(packages);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/package/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改骨科手术包信息", notes = "修改骨科手术包信息")
	public AjaxResult editOrthopaedicPackage(
			@RequestBody @Valid EditOrthopaedicPackageForm form) {
		return toAjax(service.editOrthopaedicPackage(form));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/package/del/{pid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除骨科手术包", notes = "删除骨科手术包")
	public AjaxResult delOrthopaedicPackage(
			@ApiParam(name="pid",value="骨科手术包ID",required=true) @PathVariable Long pid) {
		return toAjax(service.delOrthopaedicPackage(pid));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/package/del/{pid}/{did}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除骨科手术包明细", notes = "删除骨科手术包明细")
	public AjaxResult delOrthopaedicPackageDetail(
			@ApiParam(name="pid",value="骨科手术包ID",required=true) @PathVariable Long pid,
			@ApiParam(name="did",value="骨科手术包明细ID",required=true) @PathVariable Long did) {
		return toAjax(service.delOrthopaedicPackageDetail(pid, did));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/package/kit/add/{kid}", method = RequestMethod.POST)
	@ApiOperation(value = "添加骨科工具包", notes = "添加骨科工具包")
	public ResultInfo<OrthopaedicKit> addOrthopaedicPackageKit(
			@ApiParam(name="kid",value="骨科工具包ID",required=true) @PathVariable Long kid) {
		return ResultInfo.success(service.addOrthopaedicPackageKit(kid));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/package/kit/del/{pid}/{kid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除骨科工具包", notes = "删除骨科工具包")
	public AjaxResult delOrthopaedicPackageKit(
			@ApiParam(name="pid",value="骨科手术包ID",required=true) @PathVariable Long pid,
			@ApiParam(name="kid",value="骨科工具包ID",required=true) @PathVariable Long kid) {
		return toAjax(service.delOrthopaedicPackageKit(pid, kid));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/package/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取骨科手术包列表(分页)", notes = "获取骨科手术包列表(分页)")
	public TableDataInfo<OrthopaedicPackage> searchOrthopaedicPackageList(
			@Validated SearchSurgeryPackageForm form) {
		startPage();
		return getDataTable(service.searchOrthopaedicPackageList(form));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/package/info/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取骨科手术包信息", notes = "获取骨科手术包信息")
	public ResultInfo<OrthopaedicPackage> selectOrthopaedicPackageInfoById(
			@ApiParam(name="pid",value="骨科手术包ID",required=true) @PathVariable Long pid) {
		return ResultInfo.success(service.selectOrthopaedicPackageInfoById(pid));
	}
	
	//骨科工具包
	@Log(title = "platform.surgery", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/kit/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增骨科工具包", notes = "新增骨科工具包")
	public ResultInfo<OrthopaedicKit> addOrthopaedicTools(@RequestBody @Valid AddOrthopaedicKitForm form) {
		OrthopaedicKit kit = service.addOrthopaedicKit(form);
		if(kit != null) {
			return ResultInfo.success(kit);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/kit/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改骨科工具包信息", notes = "修改骨科工具包信息")
	public AjaxResult editOrthopaedicKit(
			@RequestBody @Valid EditOrthopaedicKitForm form) {
		return toAjax(service.editOrthopaedicKit(form));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/kit/del/{kid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除骨科工具包", notes = "删除骨科工具包")
	public AjaxResult delOrthopaedicKit(
			@ApiParam(name="pid",value="骨科手术包ID",required=true) @PathVariable Long kid) {
		return toAjax(service.delOrthopaedicKit(kid));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/kit/del/{kid}/{did}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除骨科工具包明细", notes = "删除骨科工具包明细")
	public AjaxResult delOrthopaedicKitDetail(
			@ApiParam(name="pid",value="骨科手术包ID",required=true) @PathVariable Long kid,
			@ApiParam(name="did",value="骨科手术包明细ID",required=true) @PathVariable Long did) {
		return toAjax(service.delOrthopaedicKitDetail(kid, did));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/kit/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取骨科工具包列表(分页)", notes = "获取骨科工具包列表(分页)")
	public TableDataInfo<OrthopaedicKit> searchOrthopaedicKitList(
			@Validated SearchSurgeryKitForm form) {
		startPage();
		return getDataTable(service.searchOrthopaedicKitList(form));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/kit/info/{kid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取骨科工具包信息", notes = "获取骨科工具包信息")
	public ResultInfo<OrthopaedicKit> selectOrthopaedicKitInfoById(
			@ApiParam(name="kid",value="骨科工具包ID",required=true) @PathVariable Long kid) {
		return ResultInfo.success(service.selectOrthopaedicKitInfoById(kid));
	}
	
	//品牌
	@Log(title = "platform.surgery", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/brand/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增骨科品牌", notes = "新增骨科品牌")
	public ResultInfo<Brand> addBrand( @Validated AddBrand form) {
		Brand brand = service.addBrand(form);
		if(brand != null) {
			return ResultInfo.success(brand);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/brand/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改骨科品牌", notes = "修改骨科品牌")
	public AjaxResult editBrand(
			 @Validated EditBrand form) {
	
		return toAjax(service.editBrand(form));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/brand/del/{brand_inner_sn}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除骨科品牌", notes = "删除骨科品牌")
	public AjaxResult delBrand(
			@ApiParam(name="brand_inner_sn",value="骨科品牌ID",required=true) @PathVariable Long brand_inner_sn) {
		return toAjax(service.delBrand(brand_inner_sn));
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/brand/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取骨科品牌列表", notes = "获取骨科品牌列表")
	public AjaxResult searchBrandList() {
		return success().put("list", service.searchBrandList());
	}
	
	@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/brand/info/{brand_inner_sn}", method = RequestMethod.GET)
	@ApiOperation(value = "获取骨科品牌", notes = "获取骨科品牌")
	public AjaxResult selectBrandById(
			@ApiParam(name="brand_inner_sn",value="骨科品牌ID",required=true) @PathVariable Long brand_inner_sn) {
		return success().put("list" ,service.searchBrandById(brand_inner_sn));
	}
}
