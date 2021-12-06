package com.yrt.project.api.platform.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.yrt.common.constant.Constants;
import com.yrt.common.dict.ProductSnType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.PinyinUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.bean.BeanUtils;
import com.yrt.framework.async.AsyncManager;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.productSn.service.ProductSnService;
import com.yrt.project.api.business.productSn.vo.ProductSnForm;
import com.yrt.project.api.business.warehouse.service.impl.InventoryServiceImpl;
import com.yrt.project.api.platform.service.IProductService;
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
import com.yrt.project.api.platform.vo.product.ProductMDRFImageForm;
import com.yrt.project.api.platform.vo.product.PublicProductIDSForm;
import com.yrt.project.api.platform.vo.product.PublishDealerProductForm;
import com.yrt.project.api.platform.vo.product.SearchPendingAuditForm;
import com.yrt.project.api.platform.vo.product.SearchProductForm;
import com.yrt.project.api.platform.vo.product.SearchProductMDRFForm;
import com.yrt.project.api.platform.vo.product.ViewProductMDRFForm;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.OrganizationMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.product.domain.CompleteProduct;
import com.yrt.project.modular.product.domain.DealerProductRelImage;
import com.yrt.project.modular.product.domain.DealerProductSpecification;
import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductDetail;
import com.yrt.project.modular.product.domain.ProductMDRF;
import com.yrt.project.modular.product.domain.ProductMDRFImage;
import com.yrt.project.modular.product.domain.ProductRelImage;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.project.modular.product.domain.ResponseProductInfo4Dropdown;
import com.yrt.project.modular.product.domain.TmpProduct;
import com.yrt.project.modular.product.domain.TmpProductMDRF;
import com.yrt.project.modular.product.domain.TmpProductMDRFImage;
import com.yrt.project.modular.product.domain.TmpProductRelImage;
import com.yrt.project.modular.product.domain.TmpProductSpecification;
import com.yrt.project.modular.product.mapper.DealerProductMapper;
import com.yrt.project.modular.product.mapper.ProductMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.product.mapper.TmpProductMapper;

@Service
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ProductSnService service;

	// 异步执行更新最新的注册证编号到产品表
	public TimerTask updateProductMDRFSN(Long vid, Long pid, ProductMapper mapper) {
		return new TimerTask() {
			@Override
			@Transactional
			public void run() {
				Map<String, Object> params = new HashMap<>();
				params.put("vendor_inner_sn", vid);
				params.put("product_inner_sn", pid);
				List<Map<String, Object>> list = mapper.selectNewestRegister_sn(params);
				if (StringUtils.isNotEmpty(list)) {
					String sn = list.get(0).get("register_sn").toString();
					params.put("cert_no", sn);
					mapper.updateProduct4Map(params);
				} else {
					params.put("cert_no", "");
					mapper.updateProduct4Map(params);
				}
			}
		};
	}

	@Autowired
	private OrganizationMapper orgMapper;
	@Autowired
	private ProductMapper mapper;
	@Autowired
	private TmpProductMapper tpMapper;
	@Autowired
	private DealerProductMapper dMapper;
	@Autowired
	private ProductUnitMapper uMapper;

	@Autowired
	private VendorMapper vMapper;

	private Long getCurUserEmployeeId() {
		OrgEmployee curEmp = orgMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		Long id = null;
		if (curEmp != null) {
			id = curEmp.getEmployee_inner_sn();
		}
		return id;
	}

	@Override
	@Transactional
	public Product insertProduct(AddProductForm product) throws DBException, DuplicateException {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", product.getVendor_inner_sn());
		params.put("release_employee_inner_sn_i", getCurUserEmployeeId());
		if (StringUtils.isNull(product.getType_inner_sn())) {
			params.put("type_inner_sn_i", null);
		} else {
			params.put("type_inner_sn_i", product.getType_inner_sn());
		}
		if (StringUtils.isNull(product.getCode68_sn())) {
			params.put("code68_sn_i", null);
		} else {
			params.put("code68_sn_i", product.getCode68_sn());
		}
		if (systemType.equals(SystemType.VENDOR)) {
			params.put("data_source_i", 3);
		} else {
			params.put("data_source_i", 0);
		}
		params.put("storage_condition_i", product.getStorage_condition());
		params.put("health_care_type_i", product.getHealth_care_type());
		params.put("name_i", product.getName().trim());
		params.put("product_vendor_sn_i", null);
		params.put("description_i", product.getDescription());
		params.put("specifications_i", product.getSpecifications());
		params.put("type_i", product.getType());
		params.put("code68_type_i", product.getCode68_type());
		params.put("pinyin_i", PinyinUtils.getPy(product.getName().trim()));

		mapper.p_add_product(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {
			if (StringUtils.isNull(params.get("product_inner_sn_o"))) {
				throw new DBException(MessageUtils.message("db.add.fail", "产品", "未返回产品ID"));
			}
			Product p = new Product();
			p.setVendor_inner_sn(product.getVendor_inner_sn());
			p.setProduct_inner_sn(Long.valueOf(params.get("product_inner_sn_o").toString()));
			return mapper.selectProductInfo(p);
		} else {
			if ("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail", "产品", params.get("message_string_o")));
			} else {
				throw new DBException(MessageUtils.message("db.add.fail", "产品", params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int updateProduct(Long id, Long vid, EditProductForm form) {
		try {
			Product p = new Product();
			BeanUtils.copyBeanProp(p, form);
			p.setVendor_inner_sn(vid);
			p.setProduct_inner_sn(id);
			return mapper.updateProduct(p);
		} catch (Exception e) {
			if (e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail", "产品", "同一个厂商下的产品名称重复"));
			} else {
				throw new DBException(MessageUtils.message("db.update.fail", "产品", e.getMessage()));
			}
		}
	}

	@Override
	@Transactional
	public int deleteProduct(Long id, Long vid) {
		Product p = new Product();
		p.setVendor_inner_sn(vid);
		p.setProduct_inner_sn(id);
		return mapper.deleteProduct(p);
	}

	@Override
	@Transactional
	public int importProduct() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional
	public ProductRelImage insertProductRelImage(AddProductImageForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("type_i", form.getType());
		if (StringUtils.isNotNull(form.getBegin_date())) {
			params.put("begin_date_i", form.getBegin_date());
		}
		if (StringUtils.isNotNull(form.getEnd_date())) {
			params.put("end_date_i", form.getEnd_date());
		}
		params.put("url_i", form.getUrl());
		mapper.p_add_product_image(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {
			ProductRelImage info = new ProductRelImage();
			info.setVendor_inner_sn(form.getVendor_inner_sn());
			info.setProduct_inner_sn(form.getProduct_inner_sn());
			info.setType(form.getType());
			info.setImage_inner_sn(Long.valueOf(params.get("image_inner_sn_o").toString()));
			return mapper.selectCertImageInfoById(info);
		} else {
			throw new DBException(MessageUtils.message("db.add.fail", getCertTypeName(form.getType()),
					params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public int delProductImage(Long vid, Long pid, Integer certType, Integer id) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("type", certType);
		params.put("image_inner_sn", id);
		return mapper.deleteProductImage(params);
	}

	@Override
	public List<ProductRelImage> selectCertImageList(Long vid, Long pid, Integer certType) {
		ProductRelImage info = new ProductRelImage();
		info.setVendor_inner_sn(vid);
		info.setProduct_inner_sn(pid);
		info.setType(certType);
		return mapper.selectCertImageList(info);
	}

	@Override
	public Product selectProductInfo(Long id, Long vid) {
		Product p = new Product();
		p.setVendor_inner_sn(vid);
		p.setProduct_inner_sn(id);
		return mapper.selectProductInfo(p);
	}

	@Override
	public ProductDetail getProductDetailInfo(Long pid, Long vid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		ProductDetail detail = mapper.selectProductDetailInfo(params);
		if (detail != null) {
			// 添加产品图片
			detail.setProductImageList(selectCertImageList(vid, pid, 1));
			// 添加规格型号列表
			detail.setSpecificationList(selectProductSpecListById(vid, pid, 1));
			// 添加注册证列表
			List<ViewProductMDRFForm> MDRFList = new ArrayList<>();
			List<ProductMDRF> mList = mapper.searchProductMDRFList(params);
			for (ProductMDRF tmp : mList) {
				ViewProductMDRFForm form = new ViewProductMDRFForm();
				BeanUtils.copyBeanProp(form, tmp);
				ProductMDRFImage info = new ProductMDRFImage();
				info.setVendor_inner_sn(vid);
				info.setProduct_inner_sn(pid);
				info.setMdrf_inner_sn(tmp.getMdrf_inner_sn());
				List<ProductMDRFImage> list = mapper.selectMDRFImageList(info);
				form.setImages(list);
				MDRFList.add(form);
			}
			detail.setMDRFList(MDRFList);
			return detail;
		}
		return null;
	}

	@Override
	public List<Product> searchUnPublicProductList(SearchProductForm form) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("name", form.getName());
		params.put("dealer_inner_sn", form.getDealer_inner_sn());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("data_source", Constants.SYSTEM_TYPE_PLATFORM);// 平台录入
//		params.put("status", Constants.ORG_STATUS_NORMAL);//可用状态
		params.put("release_status", Constants.PRODUCT_RELEASE_STATUS_PANDING);// 可发布
		List<Product> tmpList = mapper.searchProductList(params);
		for (Product product : tmpList) {
			if (product.getType() == 0) {
				List<ProductMDRF> mdrfList = searchProductMDRFList(product.getVendor_inner_sn(),
						product.getProduct_inner_sn(), null);
				if (StringUtils.isNotEmpty(mdrfList) && mdrfList.get(0) != null) {
					product.setSpecifications(mdrfList.get(0).getSpecifications());
				}
			}
		}
		return tmpList;
	}

	@Override
	public List<Product> searchPublicedProductList(SearchProductForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("name", form.getName());
		params.put("dealer_inner_sn", form.getDealer_inner_sn());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("register_sn", StringUtils.isEmpty(form.getRegister_sn())?null:form.getRegister_sn());
//		params.put("status", Constants.ORG_STATUS_NORMAL);//可用状态
		params.put("release_status", Constants.PRODUCT_RELEASE_STATUS_PUBLIC);// 已发布
		if (systemType.equals(SystemType.VENDOR)) {
			params.put("vendor_inner_sn", form.getOrg_id());
			params.put("vendor", 1);
		}
		return mapper.searchProductList(params);
	}

	@Override
	public List<Product> searchProductListBySource(SearchProductForm form, int source) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("name", form.getName());
		params.put("dealer_inner_sn", form.getDealer_inner_sn());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
//		params.put("status", Constants.ORG_STATUS_NORMAL);//可用状态
		params.put("release_status", Constants.PRODUCT_RELEASE_STATUS_PANDING);// 可发布
		params.put("data_source", source);
		if (systemType.equals(SystemType.VENDOR)) {
			params.put("vendor_inner_sn", form.getOrg_id());
			params.put("vendor", 1);
		}
		List<Product> tmpList = mapper.searchProductList(params);
		if (source == 0 || source == 3) {
			for (Product product : tmpList) {
				if (product.getType() == 0) {
					List<ProductMDRF> mdrfList = searchProductMDRFList(product.getVendor_inner_sn(),
							product.getProduct_inner_sn(), null);
					if (StringUtils.isNotEmpty(mdrfList) && mdrfList.get(0) != null) {
						product.setSpecifications(mdrfList.get(0).getSpecifications());
					}
				}
			}
		}
		return tmpList;
	}

	@Override
	public TableDataInfo<ResponseProductInfo> searchProductList4Contract(SearchProductForm form, int systemType,
			Long oid, Integer pageNum, Integer pageSize) {
		Map<String, Object> params = new HashMap<>();
		if (StringUtils.isEmpty(form.getKey())) {
			params.put("type_inner_sn_i", form.getType_inner_sn());
			params.put("code68_sn_i", form.getCode68_sn());
			params.put("product_name_i", form.getName());
			params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
			params.put("specification_i", form.getSpecification());
			if (StringUtils.isNotEmpty(form.getSpecification_vendor_sn())) {
				params.put("specification_vendor_sn_i", form.getSpecification_vendor_sn());
			} else {
				params.put("specification_vendor_sn_i", null);
			}
		} else {
			params.put("key_i", form.getKey());
		}
		params.put("page_i", pageNum == null ? 1 : pageNum);
		params.put("row_i", pageSize == null ? 10 : pageSize);
		params.put("include_null_unit_i", false);
		switch (systemType) {
		case Constants.SYSTEM_TYPE_VENDOR:
//			params.put("vendor_inner_sn_i", oid);放开对厂商只能销售自己产品的限制
			break;
		}
		List<ResponseProductInfo> list = mapper.selectVendorProductList(params);
		long total = Long.valueOf(params.get("total_row_o").toString());
		TableDataInfo<ResponseProductInfo> rspData = new TableDataInfo<ResponseProductInfo>();
		rspData.setCode(0);
		rspData.setRows(list);
		rspData.setTotal(total);
		return rspData;
	}

	@Override
	@Transactional
	public Product publishDealerProduct(PublishDealerProductForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("release_employee_inner_sn_i", getCurUserEmployeeId());
		if (StringUtils.isNull(form.getType_inner_sn())) {
			params.put("type_inner_sn_i", null);
		} else {
			params.put("type_inner_sn_i", form.getType_inner_sn());
		}
		if (StringUtils.isNull(form.getCode68_sn())) {
			params.put("code68_sn_i", null);
		} else {
			params.put("code68_sn_i", form.getCode68_sn());
		}
		if (systemType.equals(SystemType.VENDOR)) {
			params.put("data_source_i", 3);
		} else {
			params.put("data_source_i", 0);
		}
		params.put("storage_condition_i", form.getStorage_condition());
		params.put("health_care_type_i", form.getHealth_care_type());
		params.put("name_i", form.getName().trim());
		params.put("product_vendor_sn_i", null);
		params.put("description_i", form.getDescription());
		params.put("specifications_i", form.getSpecifications());
		params.put("type_i", form.getType());
		params.put("code68_type_i", form.getCode68_type());
		params.put("pinyin_i", PinyinUtils.getPy(form.getName().trim()));

		mapper.p_add_product(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long product_inner_sn_o = Long.valueOf(params.get("product_inner_sn_o").toString());
			DealerProductSpecification spec = new DealerProductSpecification();
			spec.setDealer_inner_sn(form.getDealer_inner_sn());
			spec.setProduct_inner_sn(form.getProduct_inner_sn());
			List<DealerProductSpecification> dpsList = dMapper.selectDealerProductSpecListById(spec);
			for (DealerProductSpecification dps : dpsList) {
				Map<String, Object> params1 = new HashMap<>();
				params1.put("vendor_inner_sn_i", form.getVendor_inner_sn());
				params1.put("product_inner_sn_i", product_inner_sn_o);
				params1.put("specification_i", dps.getSpecification());
				params1.put("specification_vendor_sn_i", null);
				params1.put("status_i", dps.getStatus());
				mapper.p_add_product_specification(params1);
			}
			DealerProductRelImage info = new DealerProductRelImage();
			info.setDealer_inner_sn(form.getDealer_inner_sn());
			info.setProduct_inner_sn(form.getProduct_inner_sn());
			List<DealerProductRelImage> images = dMapper.selectCertImageList(info);
			for (DealerProductRelImage image : images) {
				Map<String, Object> params2 = new HashMap<>();
				params2.put("vendor_inner_sn_i", form.getVendor_inner_sn());
				params2.put("product_inner_sn_i", product_inner_sn_o);
				params.put("begin_date_i", image.getBegin_date());
				params.put("end_date_i", image.getEnd_date());
				params2.put("type_i", image.getType());
				params2.put("url_i", image.getUrl());
				mapper.p_add_product_image(params2);
			}

			Product p = new Product();
			p.setVendor_inner_sn(form.getVendor_inner_sn());
			p.setProduct_inner_sn(product_inner_sn_o);
			return mapper.selectProductInfo(p);
		} else {
			if ("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail", "产品", params.get("message_string_o")));
			} else {
				throw new DBException(MessageUtils.message("db.add.fail", "产品", params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int publishProduct(Long pid, Long vid) {
		Product p = new Product();
		p.setVendor_inner_sn(vid);
		p.setProduct_inner_sn(pid);
		p.setRelease_status(Constants.PRODUCT_RELEASE_STATUS_PUBLIC);// 发布产品
		return mapper.updateProduct(p);
	}

	@Override
	@Transactional
	public int publishProduct(@Validated List<PublicProductIDSForm> list) {
		for (PublicProductIDSForm form : list) {
			publishProduct(form.getProduct_inner_sn(), form.getVendor_inner_sn());
		}
		return 1;
	}

	@Override
	public List<ProductSpecification> selectProductSpecListById(Long vid, Long pid) {
		return selectProductSpecListById(vid, pid, null);
	}

	public List<ProductSpecification> selectProductSpecListById(Long vid, Long pid, Integer status) {
		ProductSpecification spec = new ProductSpecification();
		spec.setVendor_inner_sn(vid);
		spec.setProduct_inner_sn(pid);
		if (status != null) {
			spec.setStatus(status);
		}
		List<ProductSpecification> result = mapper.selectProductSpecListById(spec);
		//添加医保编号和阳光流水号
		SystemType type = ServletUtils.getCurUserSystemType();
		if (type.equals(SystemType.VENDOR)) {
			Long oid = ServletUtils.getCurUserOrgID();
			for (ProductSpecification spec1 : result) {
				String default_health_care_sn = service.selectDefaultProductSn(oid, vid, pid, spec1.getSpecification_inner_sn(), ProductSnType.HEALTH_CARE_SN);
				String default_sun_sn = service.selectDefaultProductSn(oid, vid, pid, spec1.getSpecification_inner_sn(), ProductSnType.SUN_SN);
				spec1.setDefault_health_care_sn(default_health_care_sn);
				spec1.setDefault_sun_sn(default_sun_sn);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public ProductSpecification insertProductSpec(AddProductSpecForm form) throws DBException, DuplicateException {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", form.getOid());
		params.put("product_inner_sn_i", form.getPid());
		params.put("specification_i", form.getSpec());
		params.put("specification_vendor_sn_i", StringUtils.isEmpty(form.getPvd()) ? null : form.getPvd());
		params.put("status_i", form.getStatus());
		mapper.p_add_product_specification(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long psid = Long.valueOf(params.get("specification_inner_sn_o").toString());
			ProductSpecification sp = new ProductSpecification();
			sp.setVendor_inner_sn(form.getOid());
			sp.setProduct_inner_sn(form.getPid());
			sp.setSpecification_inner_sn(psid);
			ProductSpecification info = mapper.selectProductSpecInfoById(sp);
			// 添加医保编号和阳光流水号
			SystemType type = ServletUtils.getCurUserSystemType();
			if (type.equals(SystemType.VENDOR)) {
				Long oid = ServletUtils.getCurUserOrgID();
				ProductSnForm form2 = new ProductSnForm();
				form2.setVendor_inner_sn(form.getOid());
				form2.setProduct_inner_sn(form.getPid());
				form2.setSpecification_inner_sn(psid);
				form2.setSn(form.getDefault_health_care_sn());
				form2.setType(ProductSnType.HEALTH_CARE_SN);
				service.addOrEditProductSn(oid, form2);
				info.setDefault_health_care_sn(form.getDefault_health_care_sn());
				form2.setSn(form.getDefault_sun_sn());
				form2.setType(ProductSnType.SUN_SN);
				service.addOrEditProductSn(oid, form2);
				info.setDefault_sun_sn(form.getDefault_sun_sn());
			}
			return info;
		} else {
			if ("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(
						MessageUtils.message("db.add.fail", "产品规格型号", params.get("message_string_o")));
			} else {
				throw new DBException(MessageUtils.message("db.add.fail", "产品规格型号", params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int updateProductSpec(EditProductSpecForm form) {
		Map<String, Object> params1 = new HashMap<>();
		params1.put("vendor_inner_sn", form.getOid());
		params1.put("product_inner_sn", form.getPid());
		params1.put("specification_inner_sn", form.getPsid());
		params1.put("specification", form.getSpec());
		params1.put("specification_vendor_sn", StringUtils.isEmpty(form.getPvd()) ? null : form.getPvd());
		params1.put("status", form.getStatus());
		try {
			// 添加医保编号和阳光流水号
			SystemType type = ServletUtils.getCurUserSystemType();
			if (type.equals(SystemType.VENDOR)) {
				Long oid = ServletUtils.getCurUserOrgID();
				ProductSnForm form2 = new ProductSnForm();
				form2.setVendor_inner_sn(form.getOid());
				form2.setProduct_inner_sn(form.getPid());
				form2.setSpecification_inner_sn(form.getPsid());
				form2.setSn(form.getDefault_health_care_sn());
				form2.setType(ProductSnType.HEALTH_CARE_SN);
				service.addOrEditProductSn(oid, form2);
				form2.setSn(form.getDefault_sun_sn());
				form2.setType(ProductSnType.SUN_SN);
				service.addOrEditProductSn(oid, form2);
			}
			return mapper.updateProductSpec(params1);
		} catch (Exception e) {
			if (e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail", "产品规格型号", "产品规格型号或者规格型号编码重复"));
			} else {
				throw new DBException(MessageUtils.message("db.update.fail", "产品规格型号", e.getMessage()));
			}
		}
	}

	@Override
	@Transactional
	public int deleteProductSpec(Long vid, Long pid, Long id) {
		ProductSpecification productSpec = new ProductSpecification();
		productSpec.setVendor_inner_sn(vid);
		productSpec.setProduct_inner_sn(pid);
		productSpec.setSpecification_inner_sn(id);
		//删除医保编号和阳光流水号
		SystemType type = ServletUtils.getCurUserSystemType();
		if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
			Long oid = ServletUtils.getCurUserOrgID();
			service.deleteProductSn(oid, vid, pid, id, null, ProductSnType.HEALTH_CARE_SN);
			service.deleteProductSn(oid, vid, pid, id, null, ProductSnType.SUN_SN);
		}
		return mapper.deleteProductSpec(productSpec);
	}

	@Override
	@Transactional
	public ProductMDRF insertProductMDRF(Long vid, Long pid, AddProductMDRFForm form) {
		if (form.hasProductInfo()) {
			Map<String, Object> params = form.getProductInfo();
			params.put("vendor_inner_sn", vid);
			params.put("product_inner_sn", pid);
			if (mapper.updateProduct4Map(params) <= 0) {
				throw new DBException(MessageUtils.message("db.add.fail", "产品注册证登记表", "产品信息更新失败"));
			}
		}
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("issue_date_i", form.getIssue_date());
		params.put("begin_date_i", form.getBegin_date());
		params.put("end_date_i", form.getEnd_date());
		params.put("register_sn_i", form.getRegister_sn());
		params.put("issue_organization_i", form.getIssue_organization());
		params.put("standard_i", form.getStandard());
		params.put("production_address_i", form.getProduction_address());
		params.put("performance_structure_i", form.getPerformance_structure());
		params.put("application_range_i", form.getApplication_range());
		params.put("contraindication_i", form.getContraindication());
		params.put("description_i", form.getDescription());
		params.put("specifications_i", form.getSpecifications());
		mapper.p_add_product_mdrf(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long pmid = Long.valueOf(params.get("mdrf_inner_sn_o").toString());
			// 图片
			ProductMDRFImage info = new ProductMDRFImage();
			info.setVendor_inner_sn(vid);
			info.setProduct_inner_sn(pid);
			info.setMdrf_inner_sn(pmid);
			mapper.deleteMDRFImage(info);
			List<ProductMDRFImageForm> images = form.getImages();
			for (ProductMDRFImageForm image : images) {
				Map<String, Object> params1 = new HashMap<>();
				params1.put("vendor_inner_sn_i", vid);
				params1.put("product_inner_sn_i", pid);
				params1.put("mdrf_inner_sn_i", pmid);
				params1.put("url_i", image.getUrl());
				params1.put("file_name_i", image.getFile_name());
				mapper.p_add_product_mdrf_image(params1);
			}
			AsyncManager.me().execute(updateProductMDRFSN(vid, pid, mapper));
			ProductMDRF tmp = new ProductMDRF();
			tmp.setVendor_inner_sn(vid);
			tmp.setProduct_inner_sn(pid);
			tmp.setMdrf_inner_sn(pmid);
			return mapper.selectProductMDRF(tmp);
		} else {
			throw new DBException(MessageUtils.message("db.add.fail", "产品注册证登记表", params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public ProductMDRF updateProductMDRF(Long vid, Long pid, Long mid, EditProductMDRFForm form) {
		boolean isUpdate = false;
		if (form.hasProductInfo()) {
			Map<String, Object> params = form.getProductInfo();
			params.put("vendor_inner_sn", vid);
			params.put("product_inner_sn", pid);
			if (mapper.updateProduct4Map(params) > 0) {
				isUpdate = true;
			}
		}
		if (form.hasProductMDRF()) {
			Map<String, Object> params = form.getProductMDRF();
			params.put("vendor_inner_sn", vid);
			params.put("product_inner_sn", pid);
			params.put("mdrf_inner_sn", mid);
			if (mapper.updateProductMDRF4Map(params) > 0) {
				isUpdate = true;
				AsyncManager.me().execute(updateProductMDRFSN(vid, pid, mapper));
			}
		}
		// 图片
		List<ProductMDRFImageForm> images = form.getImages();
		ProductMDRFImage info = new ProductMDRFImage();
		info.setVendor_inner_sn(vid);
		info.setProduct_inner_sn(pid);
		info.setMdrf_inner_sn(mid);
		if (StringUtils.isNotEmpty(images) && images.get(0) != null) {
			mapper.deleteMDRFImage(info);
			for (ProductMDRFImageForm image : images) {
				Map<String, Object> params = new HashMap<>();
				params.put("vendor_inner_sn_i", vid);
				params.put("product_inner_sn_i", pid);
				params.put("mdrf_inner_sn_i", mid);
				params.put("url_i", image.getUrl());
				params.put("file_name_i", image.getFile_name());
				mapper.p_add_product_mdrf_image(params);
			}
			isUpdate = true;
		} else {
			mapper.deleteMDRFImage(info);
			isUpdate = true;
		}
		if (isUpdate) {
			ProductMDRF tmp = new ProductMDRF();
			tmp.setVendor_inner_sn(vid);
			tmp.setProduct_inner_sn(pid);
			tmp.setMdrf_inner_sn(mid);
			return mapper.selectProductMDRF(tmp);
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public int delProductMDRF(Long vid, Long pid, Long mid) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("mdrf_inner_sn", mid);
		params.put("is_delete", 1);
		if (mapper.updateProductMDRF4Map(params) > 0) {
			AsyncManager.me().execute(updateProductMDRFSN(vid, pid, mapper));
			return 1;
		}
		return 0;
	}

	@Override
	public ViewProductMDRFForm selectProductMDRF(Long vid, Long pid, Long mid) {
		ViewProductMDRFForm form = new ViewProductMDRFForm();
		Company v = vMapper.selectOrgInfo(vid);
		if (v != null) {
			form.setVendor_name(v.getName());
			form.setAddress(v.getAddress());
		}
		Product p = new Product();
		p.setVendor_inner_sn(vid);
		p.setProduct_inner_sn(pid);
		Product product = mapper.selectProductInfo(p);
		if (product != null) {
			form.setProduct_name(product.getName());
			form.setType(product.getType());
		}
		ProductMDRF tmp = new ProductMDRF();
		tmp.setVendor_inner_sn(vid);
		tmp.setProduct_inner_sn(pid);
		tmp.setMdrf_inner_sn(mid);
		ProductMDRF mdrf = mapper.selectProductMDRF(tmp);
		if (mdrf != null) {
			BeanUtils.copyBeanProp(form, mdrf);
		}
		if (product.getType() == 1) {// 如果产品类型是非医疗器械，规格型号应该来自产品表内部，而不是注册证登记表
			form.setSpecifications(product.getSpecifications());
		}
		ProductMDRFImage info = new ProductMDRFImage();
		info.setVendor_inner_sn(vid);
		info.setProduct_inner_sn(pid);
		info.setMdrf_inner_sn(mid);
		List<ProductMDRFImage> list = mapper.selectMDRFImageList(info);
		form.setImages(list);
		return form;
	}

	@Override
	public ViewProductMDRFForm getNewestProductMDRF(Long vid, Long pid) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		List<Map<String, Object>> newestList = mapper.selectNewestRegister_sn(params);
		if (StringUtils.isNotEmpty(newestList)) {
			ViewProductMDRFForm form = new ViewProductMDRFForm();
			Company v = vMapper.selectOrgInfo(vid);
			if (v != null) {
				form.setVendor_name(v.getName());
				form.setAddress(v.getAddress());
			}
			Product p = new Product();
			p.setVendor_inner_sn(vid);
			p.setProduct_inner_sn(pid);
			Product product = mapper.selectProductInfo(p);
			if (product != null) {
				form.setProduct_name(product.getName());
				form.setType(product.getType());
			}
			Long mid = Long.valueOf(newestList.get(0).get("mdrf_inner_sn").toString());
			ProductMDRF tmp = new ProductMDRF();
			tmp.setVendor_inner_sn(vid);
			tmp.setProduct_inner_sn(pid);
			tmp.setMdrf_inner_sn(mid);
			ProductMDRF mdrf = mapper.selectProductMDRF(tmp);
			if (mdrf != null) {
				BeanUtils.copyBeanProp(form, mdrf);
			}
			if (product.getType() == 1) {// 如果产品类型是非医疗器械，规格型号应该来自产品表内部，而不是注册证登记表
				form.setSpecifications(product.getSpecifications());
			}
			ProductMDRFImage info = new ProductMDRFImage();
			info.setVendor_inner_sn(vid);
			info.setProduct_inner_sn(pid);
			info.setMdrf_inner_sn(mid);
			List<ProductMDRFImage> list = mapper.selectMDRFImageList(info);
			form.setImages(list);
			return form;
		}
		return null;
	}

	@Override
	public List<ProductMDRF> searchProductMDRFList(Long vid, Long pid, Long mid) {
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("vendor_inner_sn", vid);
		info.put("product_inner_sn", pid);
		info.put("mdrf_inner_sn", mid);
		return mapper.searchProductMDRFList(info);
	}

	@Override
	public List<ProductMDRF> searchProductMDRFList(SearchProductMDRFForm form) {
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("vendor_name", form.getVendor_name());
		info.put("product_name", form.getProduct_name());
		info.put("register_sn", form.getRegister_sn());
		info.put("hasCertImage", form.getHasCertImage());
		return mapper.searchProductMDRFList1(info);
	}

	@Override
	public List<ProductMDRFImage> selectProductMDRFImageList(Long vid, Long pid, Long mid) {
		ProductMDRFImage info = new ProductMDRFImage();
		info.setVendor_inner_sn(vid);
		info.setProduct_inner_sn(pid);
		info.setMdrf_inner_sn(mid);
		return mapper.selectMDRFImageList(info);
	}

	@Override
	@Transactional
	public Long addProductMDRFImage(AddMDRFImageForm form) {
		if (StringUtils.isNotEmpty(form.getUrl())) {
			Map<String, Object> params = new HashMap<>();
			params.put("vendor_inner_sn_i", form.getVid());
			params.put("product_inner_sn_i", form.getPid());
			params.put("mdrf_inner_sn_i", form.getMid());
			params.put("url_i", form.getUrl());
			params.put("file_name_i", form.getFileName());
			mapper.p_add_product_mdrf_image(params);
			if (Integer.valueOf(params.get("result_o").toString()) == 0) {
				Long miid = Long.valueOf(params.get("image_inner_sn_o").toString());
				return miid;
			} else {
				throw new DBException(
						MessageUtils.message("db.add.fail", "产品注册证登记表图片", params.get("message_string_o")));
			}
		} else {
			throw new DBException("图片地址不能为空!");
		}
	}

	@Override
	@Transactional
	public int delProductMDRFImage(Long vid, Long pid, Long mid, Long miid) {
		ProductMDRFImage info = new ProductMDRFImage();
		info.setVendor_inner_sn(vid);
		info.setProduct_inner_sn(pid);
		info.setMdrf_inner_sn(mid);
		info.setImage_inner_sn(miid);
		return mapper.deleteMDRFImage(info);
	}

	private String getCertTypeName(int certType) {
		switch (certType) {
		case CERT_TYPE_OTHER:
			return "其他";
		case CERT_TYPE_PRODUCT:
			return "产品";
		case CERT_TYPE_LICENSE:
			return "注册证";
		case CERT_TYPE_LICENSE_TABLE:
			return "注册证登记表";
		case CERT_TYPE_QMS:
			return "质量检测报告";
		}
		return "";
	}

	/*
	 * @Override public boolean syncProductSpec(Long vid, Long pid) { Map<String,
	 * Object> params = new HashMap<>(); params.put("vendor_inner_sn", vid);
	 * params.put("product_inner_sn", pid); List<String> specList =
	 * mapper.selectAllProductSpecifications(params);
	 * 
	 * Product p1 = selectProductInfo(pid, vid); if(p1.getRelease_status() != 1) {
	 * throw new DBException(MessageUtils.message("product.spec.sync.nopublish")); }
	 * if(p1.getType() == 1) {//如果产品类型是非医疗器械，规格型号应该来自产品表内部，而不是注册证登记表 String old_spec
	 * = p1.getSpecifications(); if(StringUtils.isNotEmpty(old_spec)) { String[]
	 * specs = old_spec.split(","); for (String spec : specs) {
	 * if(specList.contains(spec.trim())) continue; insertProductSpec(vid, pid,
	 * spec.trim(), ""); } } }else { List<ProductMDRF> list =
	 * searchProductMDRFList(vid, pid, null); StringBuffer sbf = new StringBuffer();
	 * if(list != null && list.size() > 0) { for (int i = 0; i < list.size(); i++) {
	 * ProductMDRF productMDRF = list.get(i);
	 * if(StringUtils.isNotEmpty(productMDRF.getSpecifications())) { if(i > 0)
	 * sbf.append(","); sbf.append(productMDRF.getSpecifications()); } } String
	 * old_spec = sbf.toString(); if(StringUtils.isNotEmpty(old_spec)) { String[]
	 * specs = old_spec.split(","); for (String spec : specs) {
	 * if(specList.contains(spec.trim())) continue; insertProductSpec(vid, pid,
	 * spec.trim(), ""); } } } } return true; }
	 */

	@Override
	public TableDataInfo<ResponseProductInfo> selectVendorProductList(SearchProductForm form, Integer pageNum,
			Integer pageSize) {
		Map<String, Object> params = new HashMap<>();
		params.put("type_inner_sn_i", form.getType_inner_sn());
		params.put("code68_sn_i", form.getCode68_sn());
		params.put("product_name_i", form.getName());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		if (StringUtils.isNotEmpty(form.getSpecification_vendor_sn())) {
			params.put("specification_vendor_sn_i", form.getSpecification_vendor_sn());
		} else {
			params.put("specification_vendor_sn_i", null);
		}

		if (StringUtils.isNotEmpty(form.getSpecification())) {
			params.put("specification_i", form.getSpecification());
		}
		params.put("page_i", pageNum == null ? 1 : pageNum);
		params.put("row_i", pageSize == null ? 10 : pageSize);
		params.put("include_null_unit_i", false);
		List<ResponseProductInfo> list = mapper.selectVendorProductList(params);
		long total = Long.valueOf(params.get("total_row_o").toString());
		TableDataInfo<ResponseProductInfo> rspData = new TableDataInfo<ResponseProductInfo>();
		rspData.setCode(0);
		rspData.setRows(list);
		rspData.setTotal(total);
		return rspData;
	}

	@Override
	public List<ResponseProductInfo4Dropdown> getVendorProductDropdown(Long vid, String name) {
		Map<String, Object> params = new HashMap<>();
		params.put("product_name", StringUtils.isEmpty(name) ? null : name);
		params.put("vendor_inner_sn", vid);
		return mapper.selectVendorProductDropdown(params);
	}

	// 经销商编辑产品
	@Override
	public List<TmpProduct> searchPendingAuditProductList(SearchPendingAuditForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("name", form.getName());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		return tpMapper.searchPendingAuditProductList(params);
	}

	@Override
	public TmpProduct selectPendingAuditProductOtherInfo(IdsForm form) {
		Map<String, Object> params = new HashMap<>();
		if (form.getSource() == 2 || form.getSource() == 1) {
			params.put("data_source", form.getSource());
			params.put("operation_organization", form.getOrg_id());
		}
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		TmpProduct p = tpMapper.selectTmpProductInfo(params);
		if (p == null) {
			p = tpMapper.selectProductInfo(params);
		}

		if (p != null) {
			p.setTmpSpecList(tpMapper.selectPendingAuditProductSpecList(params));
			p.setTmpImageList(tpMapper.selectPendingAuditCertImageList(params));
			List<TmpProductMDRF> list = tpMapper.selectPendingAuditMDRFList(params);
			for (TmpProductMDRF tmpProductMDRF : list) {
				Map<String, Object> params1 = new HashMap<>();
				params1.put("data_source", tmpProductMDRF.getSource());
				params1.put("operation_organization", tmpProductMDRF.getOperation_organization());
				params1.put("vendor_inner_sn", tmpProductMDRF.getVendor_inner_sn());
				params1.put("product_inner_sn", tmpProductMDRF.getProduct_inner_sn());
				params1.put("mdrf_inner_sn", tmpProductMDRF.getMdrf_inner_sn());
				tmpProductMDRF.setImages(tpMapper.selectTmpMDRFImageList(params1));
			}
			p.setTmpMDRFeList(list);
		}
		return p;
	}

	@Override
	public List<MatchBean> selectPendingAuditProductDetailInfo(PendingAuditProductForm form) {
		if (form.getAudit_type() == 0) {// 审批产品
			return getTmpProductDetail(form);
		} else if (form.getAudit_type() == 1) {// 审批规格型号
			return getTmpProductSpecDetail(form);
		} else if (form.getAudit_type() == 2) {// 审批注册证登记表
			return getTmpProductMDRFDetail(form);
		} else {// 审批产品图片
			return getTmpProductImageDetail(form);
		}
	}

	private List<MatchBean> getTmpProductImageDetail(PendingAuditProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("image_inner_sn", form.getImage_inner_sn());
		TmpProductRelImage newImage = tpMapper.selectCertImageInfo(params);
		if (newImage == null) {
			throw new DBException("没有待审批内容，可能已经被其他人员审批通过了！");
		}
		ProductRelImage info = new ProductRelImage();
		info.setVendor_inner_sn(form.getVendor_inner_sn());
		info.setProduct_inner_sn(form.getProduct_inner_sn());
		info.setImage_inner_sn(form.getImage_inner_sn());
		ProductRelImage oldImage = mapper.selectCertImageInfoById(info);

		return newImage.getMatchProperty(oldImage);
	}

	private List<MatchBean> getTmpProductMDRFDetail(PendingAuditProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("mdrf_inner_sn", form.getMdrf_inner_sn());
		TmpProductMDRF newMDRF = tpMapper.selectTmpProductMDRFInfo(params);
		if (newMDRF == null) {
			throw new DBException("没有待审批内容，可能已经被其他人员审批通过了！");
		}
		ProductMDRF mdrf = new ProductMDRF();
		mdrf.setVendor_inner_sn(form.getVendor_inner_sn());
		mdrf.setProduct_inner_sn(form.getProduct_inner_sn());
		mdrf.setMdrf_inner_sn(form.getMdrf_inner_sn());
		ProductMDRF oldMDRF = mapper.selectProductMDRF(mdrf);
		return newMDRF.getMatchProperty(oldMDRF);
	}

	private List<MatchBean> getTmpProductSpecDetail(PendingAuditProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		TmpProductSpecification newSpec = tpMapper.selectTmpProductSpecInfo(params);
		if (newSpec == null) {
			throw new DBException("没有待审批内容，可能已经被其他人员审批通过了！");
		}
		ProductSpecification spec = new ProductSpecification();
		spec.setVendor_inner_sn(form.getVendor_inner_sn());
		spec.setProduct_inner_sn(form.getProduct_inner_sn());
		spec.setSpecification_inner_sn(form.getSpecification_inner_sn());
		ProductSpecification oldSpec = mapper.selectProductSpecInfoById(spec);
		return newSpec.getMatchProperty(oldSpec);
	}

	private List<MatchBean> getTmpProductDetail(PendingAuditProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		TmpProduct newProduct = tpMapper.selectTmpProductInfo(params);
		if (newProduct == null) {
			throw new DBException("没有待审批内容，可能已经被其他人员审批通过了！");
		}
		Product product = new Product();
		product.setVendor_inner_sn(form.getVendor_inner_sn());
		product.setProduct_inner_sn(form.getProduct_inner_sn());
		Product oldProduct = mapper.selectProductInfo(product);
		return newProduct.getMatchProperty(oldProduct);
	}

	@Override
	@Transactional
	public int audit(AuditProductForm form) {
		if (form.getAudit_type() == 0) {// 审批产品
			return auditTmpProduct(form);
		} else if (form.getAudit_type() == 1) {// 审批规格型号
			return auditTmpProductSpec(form);
		} else if (form.getAudit_type() == 2) {// 审批注册证登记表
			return auditTmpProductMDRF(form);
		} else {// 审批产品图片
			return auditTmpProductImage(form);
		}
	}

	@Transactional
	private int auditTmpProduct(AuditProductForm form) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("data_source", form.getSource());
			params.put("operation_organization", form.getOrg_id());
			params.put("vendor_inner_sn", form.getVendor_inner_sn());
			params.put("product_inner_sn", form.getProduct_inner_sn());
			TmpProduct tp = tpMapper.selectTmpProductInfo(params);
			Product p = new Product();
			BeanUtils.copyBeanProp(p, tp);
			switch (form.getAuditStatus()) {
			case 0:// 0-通过
				if (form.getOperation() == 1) {// 1-新增
					p.setStatus(1);
					p.setRelease_status(1);
					p.setCert_no("");
					p.setData_source(0);
					if (mapper.insertProduct(p) > 0) {
						tpMapper.deleteTmpProduct(params);
					}
				} else {// 2-修改
					Product tmp = mapper.selectProductInfo(p);
					p.setData_source(tmp.getData_source());
					p.setStatus(tmp.getStatus());
					p.setCert_no(tmp.getCert_no());
					p.setRelease_status(tmp.getRelease_status());
					p.setRelease_employee_inner_sn(tmp.getRelease_employee_inner_sn());
					if (mapper.updateProduct(p) > 0) {
						tpMapper.deleteTmpProduct(params);
					}
				}
				break;
			case 1:// 1-不通过
				params.put("status", 4);
				params.put("audit_description", form.getDescription());
				params.put("operation_time", new Date());
				if (form.getOperation() == 1) {
					tpMapper.updateTmpProductImageStatus(params);
					tpMapper.updateTmpProductSpecStatus(params);
					tpMapper.updateTmpProductMDRFStatus(params);
				}
				tpMapper.updateTmpProductStatus(params);
				break;
			case 2:// 2-已有相同审核
				TmpProduct tmp = tpMapper.selectTmpProductInfo(params);
				if (tmp.getOperation() == 1) {// 新增的草稿产品
					tpMapper.deleteTmpProductSpec(params);
					tpMapper.deleteTmpProductMDRF(params);
					tpMapper.deleteTmpMDRFImage(params);
					tpMapper.deleteTmpProductImage(params);
				}
				tpMapper.deleteTmpProduct(params);
				break;
			}
			return 1;
		} catch (Exception e) {
			if (e instanceof DuplicateKeyException) {
				throw new DuplicateException("已有相同产品存在！");
			} else {
				throw new DBException(MessageUtils.message("db.update.fail", "审批产品", e.getMessage()));
			}

		}
	}

	@Transactional
	private int auditTmpProductSpec(AuditProductForm form) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("data_source", form.getSource());
			params.put("operation_organization", form.getOrg_id());
			params.put("vendor_inner_sn", form.getVendor_inner_sn());
			params.put("product_inner_sn", form.getProduct_inner_sn());
			params.put("specification_inner_sn", form.getSpecification_inner_sn());
			TmpProductSpecification tp = tpMapper.selectTmpProductSpecInfo(params);
			ProductSpecification p = new ProductSpecification();
			BeanUtils.copyBeanProp(p, tp);
			switch (form.getAuditStatus()) {
				case 0:// 0-通过
					if (form.getOperation() == 1) {// 1-新增
						p.setStatus(1);
						p.setCommon_use_unit("");
						if (mapper.insertProductSpec(p) > 0) {
							if (StringUtils.isNotEmpty(tp.getUnit_name())) {
								Map<String, Object> params1 = new HashMap<>();
								params1.put("vendor_inner_sn_i", form.getVendor_inner_sn());
								params1.put("product_inner_sn_i", form.getProduct_inner_sn());
								params1.put("specification_inner_sn_i", form.getSpecification_inner_sn());
								params1.put("is_common_use_i", 1);// 常用包装单位
								params1.put("parent_unit_inner_sn_i", null);
								params1.put("name_i", tp.getUnit_name());
								params1.put("measure_i", 1);// 最小包装单位
								uMapper.p_add_product_unit(params1);
								if (Integer.valueOf(params1.get("result_o").toString()) == 0) {
									Long puid = Long.valueOf(params1.get("unit_inner_sn_o").toString());
									Map<String, Object> params2 = new HashMap<>();
									params2.put("vendor_inner_sn", form.getVendor_inner_sn());
									params2.put("product_inner_sn", form.getProduct_inner_sn());
									params2.put("specification_inner_sn", form.getSpecification_inner_sn());
									params2.put("common_use_unit_inner_sn", puid);

									params2.put("specification_vendor_sn", p.getSpecification_vendor_sn());

									params2.put("common_use_unit", tp.getUnit_name());
									mapper.updateProductSpec(params2);
								}
							}
							tpMapper.deleteTmpProductSpec(params);
						}
					} else {// 2-修改
						p.setStatus(null);
						ProductSpecification tmp = mapper.selectProductSpecInfoById(p);
						Map<String, Object> params2 = new HashMap<>();
						params2.put("vendor_inner_sn", tmp.getVendor_inner_sn());
						params2.put("product_inner_sn", tmp.getProduct_inner_sn());
						params2.put("specification_inner_sn", tp.getSpecification_inner_sn());
						params2.put("specification", tp.getSpecification());
						params2.put("specification_vendor_sn", StringUtils.isEmpty(tp.getSpecification_vendor_sn())?null:tp.getSpecification_vendor_sn());
						params2.put("status", 1);//送审成功后修改原规格型号状态为可用
						if (mapper.updateProductSpec(params2) > 0) {
							tpMapper.deleteTmpProductSpec(params);
						}
					}
					break;
				case 1:// 1-不通过
					params.put("status", 4);
					params.put("description", form.getDescription());
					params.put("operation_time", new Date());
					tpMapper.updateTmpProductSpecStatus(params);
					break;
				case 2:// 2-已有相同审核
					tpMapper.deleteTmpProductSpec(params);
					break;
				}
				return 1;
			} catch (Exception e) {
				if (e instanceof DuplicateKeyException) {
					throw new DuplicateException("该产品已存在相同的规格型号或者产品编码！");
				} else {
					throw new DBException(MessageUtils.message("db.update.fail", "审批产品规格型号", e.getMessage()));
				}

		}
	}

	@Transactional
	private int auditTmpProductMDRF(AuditProductForm form) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("data_source", form.getSource());
			params.put("operation_organization", form.getOrg_id());
			params.put("vendor_inner_sn", form.getVendor_inner_sn());
			params.put("product_inner_sn", form.getProduct_inner_sn());
			params.put("mdrf_inner_sn", form.getMdrf_inner_sn());
			TmpProductMDRF tp = tpMapper.selectTmpProductMDRFInfo(params);
			ProductMDRF p = new ProductMDRF();
			BeanUtils.copyBeanProp(p, tp);
			switch (form.getAuditStatus()) {
			case 0:// 0-通过
				if (form.getOperation() == 1) {// 1-新增
					if (mapper.insertProductMDRF(p) > 0) {
						ProductMDRFImage tmp = new ProductMDRFImage();
						tmp.setVendor_inner_sn(form.getVendor_inner_sn());
						tmp.setProduct_inner_sn(form.getProduct_inner_sn());
						tmp.setMdrf_inner_sn(form.getMdrf_inner_sn());
						mapper.deleteMDRFImage(tmp);
						List<TmpProductMDRFImage> images = tpMapper.selectTmpMDRFImageList(params);
						for (TmpProductMDRFImage tmpProductMDRFImage : images) {
							ProductMDRFImage info = new ProductMDRFImage();
							BeanUtils.copyBeanProp(info, tmpProductMDRFImage);
							mapper.insertMDRFImage(info);
						}
						tpMapper.deleteTmpMDRFImage(params);
						tpMapper.deleteTmpProductMDRF(params);
					}
				} else {// 2-修改
					if (mapper.updateProductMDRF(p) > 0) {
						ProductMDRFImage tmp = new ProductMDRFImage();
						tmp.setVendor_inner_sn(form.getVendor_inner_sn());
						tmp.setProduct_inner_sn(form.getProduct_inner_sn());
						tmp.setMdrf_inner_sn(form.getMdrf_inner_sn());
						mapper.deleteMDRFImage(tmp);
						List<TmpProductMDRFImage> images = tpMapper.selectTmpMDRFImageList(params);
						for (TmpProductMDRFImage tmpProductMDRFImage : images) {
							ProductMDRFImage info = new ProductMDRFImage();
							BeanUtils.copyBeanProp(info, tmpProductMDRFImage);
							mapper.insertMDRFImage(info);
						}
						tpMapper.deleteTmpMDRFImage(params);
						tpMapper.deleteTmpProductMDRF(params);
					}
				}
				break;
			case 1:// 1-不通过
				params.put("status", 4);
				params.put("description", form.getDescription());
				params.put("operation_time", new Date());
				tpMapper.updateTmpProductMDRFStatus(params);
				break;
			case 2:// 2-已有相同审核
				tpMapper.deleteTmpMDRFImage(params);
				tpMapper.deleteTmpProductMDRF(params);

				break;
			}
			AsyncManager.me().execute(updateProductMDRFSN(p.getVendor_inner_sn(), p.getProduct_inner_sn(), mapper));// 同步最新注册证号到产品信息
			return 1;
		} catch (Exception e) {
			if (e instanceof DuplicateKeyException) {
				throw new DuplicateException("该产品已存在相同的注册证编号！");
			} else {
				throw new DBException(MessageUtils.message("db.update.fail", "审批产品医疗器械注册证", e.getMessage()));
			}

		}
	}

	@Transactional
	private int auditTmpProductImage(AuditProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("image_inner_sn", form.getImage_inner_sn());
		params.put("type", 1);
		TmpProductRelImage tp = tpMapper.selectCertImageInfo(params);
		ProductRelImage p = new ProductRelImage();
		BeanUtils.copyBeanProp(p, tp);
		switch (form.getAuditStatus()) {
		case 0:// 0-通过
			if (form.getOperation() == 1) {// 1-新增
				if (mapper.insertProductImage(p) > 0) {
					tpMapper.deleteTmpProductImage(params);
				}
			} else {// 2-修改
				if (mapper.updateProductImage(p) > 0) {
					tpMapper.deleteTmpProductImage(params);
				}
			}
			break;
		case 1:// 1-不通过
			params.put("status", 4);
			params.put("description", form.getDescription());
			params.put("operation_time", new Date());
			tpMapper.updateTmpProductImageStatus(params);
			break;
		case 2:// 2-已有相同审核
			tpMapper.deleteTmpProductImage(params);
			break;
		}
		return 1;
	}

	// 产品内容检测
	@Override
	public int checkProudctExist(CheckForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("name", form.getCheck_name());
		return mapper.checkProudctExist(params) > 0 ? 0 : 1;
	}

	@Override
	public int checkProudctSpecExist(CheckForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification", form.getCheck_name());
		params.put("specification_vendor_sn", form.getSpecification_vendor_sn());
		return mapper.checkProudctSpecExist(params) > 0 ? 0 : 1;
	}

	@Override
	public int checkProudctMDRFExist(CheckForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("register_sn", form.getCheck_name());
		return mapper.checkProudctMDRFExist(params) > 0 ? 0 : 1;
	}

	@Override
	@Transactional
	public int copyMDRF(Long vid, Long pid, Long mid, List<Long> list) {
		for (Long produt_inner_sn : list) {
			ProductMDRF tmp = new ProductMDRF();
			tmp.setVendor_inner_sn(vid);
			tmp.setProduct_inner_sn(pid);
			tmp.setMdrf_inner_sn(mid);
			ProductMDRF form = mapper.selectProductMDRF(tmp);
			Map<String, Object> params = new HashMap<>();
			params.put("vendor_inner_sn_i", vid);
			params.put("product_inner_sn_i", produt_inner_sn);
			params.put("issue_date_i", form.getIssue_date());
			params.put("begin_date_i", form.getBegin_date());
			params.put("end_date_i", form.getEnd_date());
			params.put("register_sn_i", form.getRegister_sn());
			params.put("issue_organization_i", form.getIssue_organization());
			params.put("standard_i", form.getStandard());
			params.put("production_address_i", form.getProduction_address());
			params.put("performance_structure_i", form.getPerformance_structure());
			params.put("application_range_i", form.getApplication_range());
			params.put("contraindication_i", form.getContraindication());
			params.put("description_i", form.getDescription());
			params.put("specifications_i", form.getSpecifications());
			mapper.p_add_product_mdrf(params);
			if (Integer.valueOf(params.get("result_o").toString()) == 0) {
				Long pmid = Long.valueOf(params.get("mdrf_inner_sn_o").toString());
				// 图片
				ProductMDRFImage info = new ProductMDRFImage();
				info.setVendor_inner_sn(vid);
				info.setProduct_inner_sn(pid);
				info.setMdrf_inner_sn(mid);
				List<ProductMDRFImage> images = mapper.selectMDRFImageList(info);
				for (ProductMDRFImage image : images) {
					Map<String, Object> params1 = new HashMap<>();
					params1.put("vendor_inner_sn_i", vid);
					params1.put("product_inner_sn_i", produt_inner_sn);
					params1.put("mdrf_inner_sn_i", pmid);
					params1.put("url_i", image.getUrl());
					params1.put("file_name_i", image.getFile_name());
					mapper.p_add_product_mdrf_image(params1);
				}
				AsyncManager.me().execute(updateProductMDRFSN(vid, produt_inner_sn, mapper));
			} else {
				throw new DBException(MessageUtils.message("db.add.fail", "产品注册证登记表", params.get("message_string_o")));
			}
		}
		return 1;
	}

	@Autowired InventoryServiceImpl inventoryService;
	
	@Override
	public void sync(Long vid, Long pid) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		List<CompleteProduct> list = mapper.selectCompleteProductInfo(params);
		for (CompleteProduct product : list) {
			inventoryService.updateInventoryProductInfo(product);
		}
	}
}
