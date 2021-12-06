package com.yrt.project.api.common;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.exception.BusinessException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.*;
import com.yrt.project.api.common.vo.EditProductType;
import com.yrt.project.modular.category.domain.ProductType;
import com.yrt.project.modular.category.service.IProductTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/comm/category")
@Api(tags = "公用-商品自定义分类相关接口")
public class ProductTypeController extends BaseController {

	@Autowired
	private IProductTypeService productTypeService;

//	@Log(title = "common.category", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value="获取顶级自定义分类", notes="获取顶级自定义分类")
	public AjaxResult getTopLevelProductTypeList(
			@ApiParam(name = "all", value = "是否包含禁用分类 false-不包含 true-包含, 默认true", required = false) @RequestParam(required = false,defaultValue = "true") boolean all) {
		return success().put("list", productTypeService.selectTopLevelProductType(all));
	}
	
	@Log(title = "common.category", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据id查询分类信息", notes="根据id查询分类信息")
	public ResultInfo<ProductType> getProductTypeInfo(@PathVariable Long id) {
		ProductType type = productTypeService.selectProductType(id);
		return ResultInfo.success(type);
	}

//	@Log(title = "common.category", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据id查询下级分类信息列表", notes="根据id查询下级分类信息列表")
	public ResultList<ProductType> getProductTypeChildrenList(@PathVariable Long id) {
		return ResultList.success(productTypeService.selectProductTypeChildrenList(id));
	}

	@Log(title = "common.category", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value="增加商品自定义分类", notes="增加商品自定义分类")
	public ResultInfo<ProductType> updateCert(ProductType type) {
		return ResultInfo.success(productTypeService.insertProductType(type));
	}

	@Log(title = "common.category", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="编辑商品自定义分类", notes="编辑商品自定义分类")
	public AjaxResult updateCert(@PathVariable Long id, EditProductType type) {
		if(!type.hasUpdate()) {
			throw new BusinessException(MessageUtils.message("update.param.not.null"));
		}
		ProductType t = new ProductType();
		BeanUtils.copyProperties(type, t);
		return toAjax(productTypeService.updateProductType(id, t));
	}
	
	@Log(title = "common.category", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/parents/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据id查询所有上级信息",notes="根据id查询所有上级信息")
	public ResultInfo<ProductType> getParentsProductTypeListById(@PathVariable Long id) {
		ProductType type = productTypeService.selectProductType(id);
		productTypeService.selectParent(type, type.getParent_type_inner_sn());
		
		return ResultInfo.success(type);
	}
	
	@Log(title = "common.category", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/all/list", method = RequestMethod.GET)
	@ApiOperation(value="获取全部产品自定义分类",notes="获取全部产品自定义分类")
	public ResultInfo<List<ProductType>> getAllProductTypeList() {
		return ResultInfo.success(productTypeService.selectAllProductType());
	}
}
