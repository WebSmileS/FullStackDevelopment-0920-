package com.yrt.project.api.platform;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.file.FileException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.poi.ExcelUtil;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.dealer.service.IDealerProductService;
import com.yrt.project.api.platform.service.IImportProductService;
import com.yrt.project.api.platform.service.IProductService;
import com.yrt.project.api.platform.service.IVendorOrgService;
import com.yrt.project.api.platform.vo.product.AddMDRFImageForm;
import com.yrt.project.api.platform.vo.product.AddProductForm;
import com.yrt.project.api.platform.vo.product.AddProductImageForm;
import com.yrt.project.api.platform.vo.product.AddProductMDRFForm;
import com.yrt.project.api.platform.vo.product.AddProductSpecForm;
import com.yrt.project.api.platform.vo.product.AuditProductForm;
import com.yrt.project.api.platform.vo.product.CheckForm;
import com.yrt.project.api.platform.vo.product.EditProductForm;
import com.yrt.project.api.platform.vo.product.EditProductMDRFForm;
import com.yrt.project.api.platform.vo.product.EditProductSpecForm;
import com.yrt.project.api.platform.vo.product.IdsForm;
import com.yrt.project.api.platform.vo.product.MatchBean;
import com.yrt.project.api.platform.vo.product.PendingAuditProductForm;
import com.yrt.project.api.platform.vo.product.PublicProductIDSForm;
import com.yrt.project.api.platform.vo.product.PublishDealerProductForm;
import com.yrt.project.api.platform.vo.product.SearchPendingAuditForm;
import com.yrt.project.api.platform.vo.product.SearchProductForm;
import com.yrt.project.api.platform.vo.product.SearchProductMDRFForm;
import com.yrt.project.api.platform.vo.product.ViewProductMDRFForm;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.product.domain.ImportProduct;
import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductDetail;
import com.yrt.project.modular.product.domain.ProductMDRF;
import com.yrt.project.modular.product.domain.ProductMDRFImage;
import com.yrt.project.modular.product.domain.ProductRelImage;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.project.modular.product.domain.TmpProduct;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/platform/product")
@Api(tags = "平台-产品管理相关接口")
public class ProductManagerController extends BaseController {

	@Autowired
	private IVendorOrgService vendorService;
	
	@Autowired
	private IProductService productService;
	
	@Autowired IImportProductService importProductService;
	
	@Autowired
	private IDealerProductService dealerProductService;

	//手机专用
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/app/info/{vid}/{pid}/{product_type}", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品详细信息(手机专用)", notes = "获取产品详细信息(手机专用)")
	public ResultInfo<ProductDetail> getProductDetailInfo(
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="product_type",value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true) @PathVariable Integer product_type) {
		if(product_type == 1) {
			return ResultInfo.success(productService.getProductDetailInfo(pid, vid));
		}else {
			return ResultInfo.success(dealerProductService.getProductDetailInfo(pid, vid));
		}
	}
	
	//手机和PC共用
	@Log(title = "platform.product", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增产品", notes = "新增产品")
	public ResultInfo<Product> addGoods(@Validated AddProductForm product) {
		Product p = productService.insertProduct(product);
		if(p != null) {
			return ResultInfo.success(p);
		}
		return ResultInfo.error();
	}

	@Log(title = "platform.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/edit/{vid}/{pid}", method = RequestMethod.PUT)
	@ApiOperation(value = "修改产品信息", notes = "修改产品信息")
	public AjaxResult editProduct(@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@Validated EditProductForm product) {
		if (!product.hasUpdate()) {
			throw new BusinessException(MessageUtils.message("update.param.not.null"));
		}
		return toAjax(productService.updateProduct(pid, vid, product));
	}

	@Log(title = "platform.product", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/del/{vid}/{pid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除产品信息", notes = "删除产品信息")
	public AjaxResult delProduct(@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid) {
		return toAjax(productService.deleteProduct(pid, vid));
	}

	@Log(title = "platform.product", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/image/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增产品相关图片", notes = "新增产品相关图片")
	public ResultInfo<ProductRelImage> addGoods(@Validated AddProductImageForm form) {
		ProductRelImage image = productService.insertProductRelImage(form);
		if(image != null) {
			return ResultInfo.success(image);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "platform.product", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/image/del/{vid}/{pid}/{certType}/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除产品相关图片", notes = "删除产品相关图片")
	public AjaxResult delProductImage(@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="certType",value="图片类型",required=true) @PathVariable int certType,
			@ApiParam(name="id",value="图片ID",required=true) @PathVariable int id) {
		return toAjax(productService.delProductImage(vid, pid, certType, id));
	}

	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/image/{vid}/{pid}/{certType}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品ID和图片类型获取相关图片", notes = "根据产品ID和图片类型获取相关图片")
	public AjaxResult getCertImage(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="certType",value="图片类型",required=true) @PathVariable int certType) {
		List<ProductRelImage> list = productService.selectCertImageList(vid, pid, certType);
		return success().put("images", list);
	}

	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/info/{vid}/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "按照产品编号查询未删除的正常商品信息", notes = "按照产品编号查询未删除的正常商品信息")
	public ResultInfo<Product> getProductInfo(@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid) {
		return ResultInfo.success(productService.selectProductInfo(pid, vid));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/unpublic/list", method = RequestMethod.GET)
	@ApiOperation(value = "按条件搜索未发布的产品列表(分页)", notes = "按条件搜索未发布的产品列表(分页)")
	public TableDataInfo<Product> searchUnPublicProductList(SearchProductForm form) {
		startPage();
		form.escapeLikeValue();
		return getDataTable(productService.searchUnPublicProductList(form));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/public/list", method = RequestMethod.GET)
	@ApiOperation(value = "按条件搜索已发布的产品列表(分页)", notes = "按条件搜索已发布的产品列表(分页)")
	public TableDataInfo<Product> searchPublicedProductList(
			@Validated SearchProductForm form) {
		startPage();
		form.escapeLikeValue();
		return getDataTable(productService.searchPublicedProductList(form));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/list/{source}", method = RequestMethod.GET)
	@ApiOperation(value = "根据搜索条件和产品数据来源获取产品(分页,厂商添加的可发布产品和平台添加的可发布产品)列表", notes = "根据搜索条件和产品数据来源获取产品(分页,厂商添加的可发布产品和平台添加的可发布产品)列表")
	public TableDataInfo<Product> searchProductListBySource(@ApiParam(name="source",value="信息来源",required=true) @PathVariable int source,
			@Validated SearchProductForm form) {
		startPage();
		form.escapeLikeValue();
		if(source == 2) {
			return getDataTable(dealerProductService.searchDealerProductList(form));
		}
		return getDataTable(productService.searchProductListBySource(form, source));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/dealer/list/contract/{systemType}/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "搜索所有可以添加到合同的自建产品(分页)", notes = "搜索所有可以添加到合同的自建产品(分页)")
	public TableDataInfo<ResponseProductInfo> searchDealerProductList4Contract(
			@ApiParam(name="systemType",value="合同乙方机构类型 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name = "oid", value = "合同乙方机构ID", required = true) @PathVariable Long oid,
			@Validated SearchProductForm form) {
		form.escapeLikeValue();
		return getDataTable(dealerProductService.searchDealerProductList4Contract(form, systemType, oid));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/vendor/list/contract/{systemType}/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "搜索所有可以添加到合同的厂商产品", notes = "搜索所有可以添加到合同的厂商产品")
	public TableDataInfo<ResponseProductInfo> searchVendorProductList4Contract(
			@ApiParam(name="systemType",value="合同乙方机构类型 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name = "oid", value = "合同乙方机构ID", required = true) @PathVariable Long oid,
			@Validated SearchProductForm form) {
		PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        form.escapeLikeValue();
		return productService.searchProductList4Contract(form, systemType, oid, pageNum, pageSize);
	}

	@Log(title = "platform.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/public/{vid}/{pid}", method = RequestMethod.PUT)
	@ApiOperation(value = "发布产品", notes = "发布产品")
	public AjaxResult publishProduct(@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid, 
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid) {
		return toAjax(productService.publishProduct(pid, vid));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/public", method = RequestMethod.POST)
	@ApiOperation(value = "批量发布产品", notes = "批量发布产品")
	public AjaxResult publishProduct(
			@Validated @RequestBody List<PublicProductIDSForm> list) {
		return toAjax(productService.publishProduct(list));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/public/dealer", method = RequestMethod.PUT)
	@ApiOperation(value = "发布经销商自建产品", notes = "发布经销商自建产品")
	public ResultInfo<Product> publishDealerProduct(@Validated PublishDealerProductForm form) {
		return ResultInfo.success(productService.publishDealerProduct(form));
	}

	@Log(title = "platform.product", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/spec/add", method = RequestMethod.POST)
	@ApiOperation(value = "根据厂商ID和已发布产品ID新增规格型号", notes = "根据厂商ID和已发布产品ID新增规格型号")
	public ResultInfo<ProductSpecification> addProductSpec(@Validated AddProductSpecForm form) {
		ProductSpecification sp = productService.insertProductSpec(form);
		if(sp != null) {
			return ResultInfo.success(sp);
		}
		return ResultInfo.error();
	}

	@Log(title = "platform.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/spec/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "根据厂商ID、已发布产品ID和规格型号ID修改规格型号", notes = "根据厂商ID、已发布产品ID和规格型号ID修改规格型号")
	public AjaxResult editProductSpec(@Validated EditProductSpecForm form) {
		return toAjax(productService.updateProductSpec(form));
	}

	@Log(title = "platform.product", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/spec/del/{vid}/{pid}/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "根据厂商ID和已发布产品ID删除规格型号", notes = "根据厂商ID和已发布产品ID删除规格型号")
	public AjaxResult delProductSpec(@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="id",value="规格型号ID",required=true) @PathVariable Long id) {
		return toAjax(productService.deleteProductSpec(vid, pid, id));
	}

	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/spec/list/{vid}/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据厂商ID和已发布产品ID获取产品规格型号列表", notes = "根据厂商ID和已发布产品ID获取产品规格型号列表")
	public AjaxResult searchProductListBySource(@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid) {
		return success().put("list", productService.selectProductSpecListById(vid, pid));
	}
	
	//医疗器材注册登记表
	@Log(title = "platform.product", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/add/{vid}/{pid}", method = RequestMethod.POST)
	@ApiOperation(value = "新增医疗器材注册登记表", notes = "新增医疗器材注册登记表")
	public ResultInfo<ProductMDRF> addProductMDRF(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@RequestBody @Valid AddProductMDRFForm form) {
		ProductMDRF info = productService.insertProductMDRF(vid, pid, form);
		return ResultInfo.success(info);
	}

	@Log(title = "platform.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/edit/{vid}/{pid}/{mid}", method = RequestMethod.PUT)
	@ApiOperation(value = "修改医疗器材注册登记表", notes = "修改医疗器材注册登记表")
	public ResultInfo<ProductMDRF> editProductMDRF(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid, 
			@ApiParam(name="mid",value="注册证ID",required=true) @PathVariable Long mid,
			@RequestBody @Valid EditProductMDRFForm form) {
		return ResultInfo.success(productService.updateProductMDRF(vid, pid, mid, form));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/del/{vid}/{pid}/{mid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除医疗器材注册登记表", notes = "删除医疗器材注册登记表")
	public AjaxResult delProductMDRF(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid, 
			@ApiParam(name="mid",value="注册证ID",required=true) @PathVariable Long mid) {
		return toAjax(productService.delProductMDRF(vid, pid, mid));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/list/{vid}/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品医疗器材注册登记表列表", notes = "获取产品医疗器材注册登记表列表")
	public AjaxResult getProductMDRFList(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid) {
		Company v = vendorService.selectNoAuditOrgInfo(vid);
		Product p = productService.selectProductInfo(pid, vid);
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("vendor_inner_sn", v.getId());
		info.put("vendor_name", v.getName());
		info.put("vendor_address", v.getAddress());
		info.put("product_inner_sn", p.getProduct_inner_sn());
		info.put("product_name", p.getName());
		return success().put("info", info).put("list", productService.searchProductMDRFList(vid, pid, null));
	}
	
	//注册证管理
	@SuppressWarnings("unchecked")
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/search", method = RequestMethod.GET)
	@ApiOperation(value = "搜索产品医疗器材注册登记表列表", notes = "搜索产品医疗器材注册登记表列表")
	public TableDataInfo<ProductMDRF> searchProductMDRFList(
			@Validated SearchProductMDRFForm form) {
		startPage();
		form.escapeLikeValue();
		return getDataTable(productService.searchProductMDRFList(form));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/copy/{vid}/{pid}/{mid}", method = RequestMethod.POST)
	@ApiOperation(value = "复制产品医疗器材注册登记表到其他产品", notes = "复制产品医疗器材注册登记表到其他产品")
	public AjaxResult searchProductMDRFList(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="mid",value="注册证ID",required=true) @PathVariable Long mid,
			@RequestBody List<Long> list) {
		return toAjax(productService.copyMDRF(vid, pid, mid, list));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/image/list/{vid}/{pid}/{mid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品医疗器材注册登记表图片列表", notes = "搜索产品医疗器材注册登记表图片列表")
	public ResultList<ProductMDRFImage> selectProductMDRFImageList(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="mid",value="注册证ID",required=true) @PathVariable Long mid) {
		return ResultList.success(productService.selectProductMDRFImageList(vid, pid, mid));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/image/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增医疗器材注册登记表图片", notes = "新增医疗器材注册登记表图片")
	public ResultInfo<Long> addProductMDRF(
			@RequestBody @Validated AddMDRFImageForm form) {
		return ResultInfo.success(productService.addProductMDRFImage(form));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/image/del/{vid}/{pid}/{mid}/{miid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除医疗器材注册登记表图片", notes = "删除医疗器材注册登记表图片")
	public AjaxResult addProductMDRF(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="mid",value="注册证ID",required=true) @PathVariable Long mid,
			@ApiParam(name="miid",value="注册证图片ID",required=true) @PathVariable Long miid) {
		return toAjax(productService.delProductMDRFImage(vid, pid, mid, miid));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/info/{vid}/{pid}/{mid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据厂商ID和产品ID获取产品医疗器材注册登记表", notes = "根据厂商ID和产品ID获取产品医疗器材注册登记表")
	public ResultInfo<ViewProductMDRFForm> getProductMDRF(
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="mid",value="注册证ID",required=true) @PathVariable Long mid) {
		return ResultInfo.success(productService.selectProductMDRF(vid, pid, mid));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/newest/info/{vid}/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品医疗器材最新注册登记表", notes = "获取产品医疗器材最新注册登记表")
	public ResultInfo<ViewProductMDRFForm> getNewestProductMDRF(
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid) {
		return ResultInfo.success(productService.getNewestProductMDRF(vid, pid));
	}
	
//	@Log(title = "platform.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
//	@RequestMapping(value = "/spec/sync/{vid}/{pid}", method = RequestMethod.PUT)
//	@ApiOperation(value = "同步已发布产品规格型号", notes = "同步已发布产品规格型号")
//	public AjaxResult syncPuoductSpec(
//			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
//			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid) {
//		return toAjax(productService.syncProductSpec(vid, pid));
//	}
	
	//待审批产品
	@SuppressWarnings("unchecked")
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/tmp/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取待审核产品列表(分页)", notes = "获取待审核产品列表(分页)")
	public TableDataInfo<TmpProduct> selectPendingAuditProduct(
			@Validated SearchPendingAuditForm form) {
		startPage();
		return getDataTable(productService.searchPendingAuditProductList(form));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/tmp/otherInfo", method = RequestMethod.GET)
	@ApiOperation(value = "获取待审核产品关联信息", notes = "获取待审核产品关联信息")
	public ResultInfo<TmpProduct> selectPendingAuditProductOtherInfo(
			@Validated IdsForm form) {
		return ResultInfo.success(productService.selectPendingAuditProductOtherInfo(form));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/tmp/detail", method = RequestMethod.GET)
	@ApiOperation(value = "获取待审核产品详细信息(包含规格型号、图片和登记表信息)", notes = "获取待审核产品详细信息(包含规格型号、图片和登记表信息)")
	public ResultInfo<List<MatchBean>> selectPendingAuditProductDetailInfo(
			@Validated PendingAuditProductForm form) {
		return ResultInfo.success(productService.selectPendingAuditProductDetailInfo(form));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	@ApiOperation(value = "审核产品", notes = "审核产品")
	public AjaxResult addPendingAuditProduct(
			@Validated AuditProductForm form) {
		return toAjax(productService.audit(form));
	}
	
	//产品内容检测
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	@ApiOperation(value = "检查产品名称是否存在", notes = "检查产品名称是否存在")
	public AjaxResult checkProudctExist(
			@Validated(CheckForm.CheckProduct.class) CheckForm form) {
		return toAjax(productService.checkProudctExist(form));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/spec/check", method = RequestMethod.GET)
	@ApiOperation(value = "检查产品规格型号是否存在", notes = "检查产品名称是否存在")
	public AjaxResult checkProudctSpecExist(
			@Validated(CheckForm.CheckProductSpec.class) CheckForm form) {
		return toAjax(productService.checkProudctSpecExist(form));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/mdrf/check", method = RequestMethod.GET)
	@ApiOperation(value = "检查产品注册证号是否存在", notes = "检查产品注册证号是否存在")
	public AjaxResult checkProudctMDRFExist(
			@Validated(CheckForm.CheckProductMDRF.class) CheckForm form) {
		return toAjax(productService.checkProudctMDRFExist(form));
	}
	
	@Log(title = "platform.product", businessType = BusinessType.IMPORT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/importData", method = RequestMethod.POST)
	@ApiOperation(value = "提交导入产品任务", notes = "导入产品(根据模板导入产品,导入失败的数据保存为新的Excel文件)：后续调importDataResult/taskId查结果")
	@ResponseBody
	public AjaxResult importData(MultipartFile file) throws Exception{
		ExcelUtil<ImportProduct> util = new ExcelUtil<ImportProduct>(ImportProduct.class);
		List<ImportProduct> productList = util.importExcel("导入数据", file.getInputStream());
		String taskId = importProductService.asyncImportData(productList);
		AjaxResult result = AjaxResult.success(taskId);
		result.put("taskId", taskId);
		return result;
	}
	
	//@Log(title = "platform.product", businessType = BusinessType.IMPORT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/importDataResult/{taskId}", method = RequestMethod.GET)
	@ApiOperation(value = "导入产品结果查询", notes = "导入产品结果查询(根据模板导入产品,导入失败的数据保存为新的Excel文件)")
	@ResponseBody
	public AjaxResult importDataResult(@PathVariable("taskId")String taskId) throws Exception{
		Future<List<ImportProduct>> future = importProductService.fetchImportTask(taskId);
		if (future == null) {
			return AjaxResult.error(1, "任务不存在!");
		}
		
		if(future.isDone()) {
			List<ImportProduct> errorProductList = future.get();
			if(StringUtils.isNotEmpty(errorProductList)) {
				ExcelUtil<ImportProduct> util = new ExcelUtil<ImportProduct>(ImportProduct.class);
				return util.exportExcel(errorProductList, "错误数据");
			}
			return success("数据导入成功!");
		}
		return AjaxResult.error(2, "还在在处理中...");
		
	}
	
	@Log(title = "platform.product", businessType = BusinessType.EXPORT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ApiOperation(value = "下载产品导入模板", notes = "下载产品导入模板")
	public Resource export(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
		try {
			ClassPathResource cpr = new ClassPathResource("static/导入数据.xlsx");
            Resource resource = new UrlResource(cpr.getURI());
            if(resource.exists()) {
            	response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("导入数据.xlsx", "UTF-8"));
                return resource;
            } else {
                throw new FileException("没有找到文件'导入数据.xlsx'", null);
            }
        } catch (MalformedURLException ex) {
            throw new FileException("没有找到文件'导入数据.xlsx'", null);
        }
	}
	
	@Log(title = "platform.product", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/sync/{vid}/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "同步平台产品信息与所有仓库的库存产品信息", notes = "同步平台产品信息(包括厂商名称、产品名称、规格型号和包装单位)与所有仓库的库存产品信息")
	public AjaxResult syncProductInfo(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid) {
		productService.sync(vid, pid);
		return success();
	}
}
