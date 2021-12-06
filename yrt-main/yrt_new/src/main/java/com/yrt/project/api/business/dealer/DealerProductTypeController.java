package com.yrt.project.api.business.dealer;

import java.util.List;
import java.util.Objects;

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
import com.yrt.project.api.business.dealer.service.IDealerProductTypeService;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductTypeForm;
import com.yrt.project.api.common.vo.EditProductType;
import com.yrt.project.modular.product.domain.DealerProductType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/dealer/type")
@Api(tags = "业务-经销商自建产品类型管理接口")
public class DealerProductTypeController extends BaseController {
	
	@Autowired
	private IDealerProductTypeService service;
	
	@Log(title = "business.dealer.type", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add/{oid}", method = RequestMethod.POST)
	@ApiOperation(value = "新增产品类型", notes = "新增产品类型")
	public ResultInfo<DealerProductType> addDealerProductType(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@Validated AddDealerProductTypeForm form) {
		
		return ResultInfo.success(service.insertDealerProductType(oid, form));
	}

	@Log(title = "business.dealer.type", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit/{oid}/{tid}", method = RequestMethod.PUT)
	@ApiOperation(value = "修改产品类型", notes = "修改产品类型")
	public AjaxResult editDealerProductType(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="tid",value="产品类型ID",required=true) @PathVariable Long tid,
			EditProductType type) {
		return toAjax(service.updateProductType(oid, tid, type));
	}

	@Log(title = "business.dealer.type", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del/{oid}/{tid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除产品类型", notes = "删除产品类型")
	public AjaxResult delDealerProductType(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="tid",value="产品类型ID",required=true) @PathVariable Long tid) {
		return toAjax(service.deleteProductType(oid, tid));
	}
	
	@Log(title = "business.dealer.type", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value="获取顶级产品分类", notes="获取顶级产品分类")
	public ResultList<DealerProductType> getTopLevelDealerProductTypeList(@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid) {
		List<DealerProductType> list = service.selectTopLevelProductType(oid);
		return ResultList.success(list);
	}
	
	@Log(title = "business.dealer.type", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{oid}/{tid}", method = RequestMethod.GET)
	@ApiOperation(value="查询分类信息", notes="查询分类信息")
	public ResultInfo<DealerProductType> getProductTypeInfo(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="tid",value="产品类型ID",required=true) @PathVariable Long tid) {
		DealerProductType type = service.selectProductType(oid, tid);
		return ResultInfo.success(type);
	}

	@Log(title = "business.dealer.type", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list/{oid}/{tid}", method = RequestMethod.GET)
	@ApiOperation(value="查询下级分类信息列表", notes="查询下级分类信息列表")
	public ResultList<DealerProductType> getProductTypeChildrenList(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="tid",value="产品类型ID",required=true) @PathVariable Long tid) {
		List<DealerProductType> list = service.selectProductTypeChildrenList(oid, tid);
		return ResultList.success(list);
	}
	
	@Log(title = "business.dealer.type", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/parents/{oid}/{tid}", method = RequestMethod.GET)
	@ApiOperation(value="查询所有上级信息",notes="查询所有上级信息")
	public ResultInfo<DealerProductType> getParentsProductTypeListById(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="tid",value="产品类型ID",required=true) @PathVariable Long tid) {
		DealerProductType type = service.selectProductType(oid, tid);
		if (Objects.isNull(type)) return ResultInfo.success(null);
		service.selectParent(type, oid, type.getParent_type_inner_sn());
		
		return ResultInfo.success(type);
	}
	
	@Log(title = "business.dealer.type", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/all/tree/{oid}", method = RequestMethod.GET)
	@ApiOperation(value="获取全部产品自定义分类",notes="获取全部产品自定义分类")
	public ResultList<DealerProductType> getAllProductTypeList(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid) {
		return ResultList.success(service.selectAllProductType(oid));
	}
}
