package com.yrt.project.api.business.dealer.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yrt.common.utils.PinyinUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.yrt.common.constant.Constants;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.dealer.service.IDealerProductService;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductImageForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductSpecForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.EditDealerProductForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.EditDealerProductSpecForm;
import com.yrt.project.api.platform.vo.product.SearchProductForm;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.product.domain.DealerProduct;
import com.yrt.project.modular.product.domain.DealerProductRelImage;
import com.yrt.project.modular.product.domain.DealerProductSpecification;
import com.yrt.project.modular.product.domain.ProductDetail;
import com.yrt.project.modular.product.mapper.DealerProductMapper;

@Service
public class DealerProductServiceImpl implements IDealerProductService {
	
	@Autowired
	private DealerProductMapper mapper;
	@Autowired DealerMapper dMapper;
	
	protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }
	
	private Long getCurUserEmployeeId() {
		OrgEmployee curEmp =  dMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		Long id = null;
		if(curEmp != null) {
			id = curEmp.getEmployee_inner_sn();
		}
		return id;
	}
	
	@Override
	@Transactional
	public DealerProduct insertDealerProduct(AddDealerProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("dealer_inner_sn_i", form.getDealer_inner_sn());
		params.put("create_employee_inner_sn_i", getCurUserEmployeeId());
		if(StringUtils.isNull(form.getType_inner_sn())) {
			params.put("type_inner_sn_i", null);
		}else {
			params.put("type_inner_sn_i", form.getType_inner_sn());
		}
		if(StringUtils.isNull(form.getCode68_sn())) {
			params.put("code68_sn_i", null);
		}else {
			params.put("code68_sn_i", form.getCode68_sn());
		}
		
		params.put("storage_condition_i", form.getStorage_condition());
		params.put("health_care_type_i", form.getHealth_care_type());
		params.put("name_i", form.getName().trim());
		params.put("product_dealer_sn_i", null);
		params.put("description_i", form.getDescription());
		params.put("specifications_i", form.getSpecifications());
		params.put("pinyin_i", PinyinUtils.getPy(form.getName().trim()));

		mapper.d_add_dealer_product(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long pid = Long.valueOf(params.get("product_inner_sn_o").toString());
			return selectDealerProductInfo(pid, form.getDealer_inner_sn());
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","自建产品",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","自建产品",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int updateDealerProduct(Long id, Long did, EditDealerProductForm form) {
		try {
			Map<String, Object> params = form.getInfo();
			params.put("dealer_inner_sn", did);
			params.put("product_inner_sn", id);
			return mapper.updateDealerProduct(params);
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","自建产品","同一个厂商下的产品名称重复"));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","自建产品",e.getMessage()));
			}
		}
	}

	@Override
	@Transactional
	public int deleteDealerProduct(Long id, Long did) {
		DealerProduct p = new DealerProduct();
		p.setDealer_inner_sn(did);
		p.setProduct_inner_sn(id);
		return mapper.deleteDealerProduct(p);
	}

	@Override
	@Transactional
	public int importDealerProduct() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional
	public DealerProductRelImage insertDealerProductRelImage(AddDealerProductImageForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("dealer_inner_sn_i", form.getDealer_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("type_i", form.getType());
		if(StringUtils.isNotNull(form.getBegin_date())) {
			params.put("begin_date_i", form.getBegin_date());
		}
		if(StringUtils.isNotNull(form.getEnd_date())) {
			params.put("end_date_i", form.getEnd_date());
		}
		params.put("url_i", form.getUrl());
		mapper.d_add_dealer_product_image(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long piid = Long.valueOf(params.get("image_inner_sn_o").toString());
			return selectCertImageInfo(form.getDealer_inner_sn(), form.getProduct_inner_sn(), piid, form.getType().intValue());
		}else {
			throw new DBException(MessageUtils.message("db.procedures.error"));
		}
	}
	
	@Override
	@Transactional
	public int delProductImage(Long did, Long pid, int certType, int id) {
		Map<String, Object> params = new HashMap<>();
		params.put("dealer_inner_sn", did);
		params.put("product_inner_sn", pid);
		params.put("type", certType);
		params.put("image_inner_sn", id);
		return mapper.deleteProductImage(params);
	}

	@Override
	public DealerProductRelImage selectCertImageInfo(Long did, Long pid, Long piid, int certType) {
		DealerProductRelImage info = new DealerProductRelImage();
		info.setDealer_inner_sn(did);
		info.setProduct_inner_sn(pid);
		info.setImage_inner_sn(piid);
		info.setType(certType);
		return mapper.selectCertImageInfo(info);
	}
	
	@Override
	public List<DealerProductRelImage> selectCertImageList(Long did, Long pid, int certType) {
		DealerProductRelImage info = new DealerProductRelImage();
		info.setDealer_inner_sn(did);
		info.setProduct_inner_sn(pid);
		info.setType(certType);
		return mapper.selectCertImageList(info);
	}

	@Override
	public DealerProduct selectDealerProductInfo(Long pid, Long did) {
		DealerProduct p = new DealerProduct();
		p.setDealer_inner_sn(did);
		p.setProduct_inner_sn(pid);
		return mapper.selectDealerProductInfo(p);
	}
	
	@Override
	public ProductDetail getProductDetailInfo(Long pid, Long vid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		ProductDetail detail = mapper.selectProductDetailInfo(params);
		if(detail != null) {
			//添加产品图片
			params.put("type", 1);
			detail.setProductImageList(mapper.selectCertImageList1(params));
			//添加规格型号列表
			params.put("status", 1);
			detail.setSpecificationList(mapper.selectProductSpecListById(params));
			return detail;
		}
		return null;
	}

	@Override
	public List<DealerProduct> searchAllDealerProductList(SearchProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", form.getName());
		params.put("code68_sn", form.getCode68_sn());
		params.put("dealer_inner_sn", form.getDealer_inner_sn());
		return mapper.searchAllDealerProductList(params);
	}
	
	@Override
	public List<DealerProduct> searchDealerProductList(SearchProductForm form) {
		DealerProduct p = new DealerProduct();
		BeanUtils.copyProperties(form, p);
		p.setDealer_inner_sn(form.getOrg_id());
//		p.setStatus(1);//可用状态
		return mapper.searchDealerProductList(p);
	}
	
	@Override
	public List<ResponseProductInfo> searchDealerProductList4Contract(SearchProductForm form, int systemType, Long oid) {
		startPage();
		Map<String, Object> params = new HashMap<>();
		if(StringUtils.isEmpty(form.getKey())) {
			params.put("name", form.getName());
			params.put("code68_sn", form.getCode68_sn());
			params.put("dealer_inner_sn", form.getDealer_inner_sn());
			params.put("type_inner_sn", form.getType_inner_sn());
			params.put("specification", form.getSpecification());
		}else {
			params.put("key", form.getKey());
		}
		switch (systemType) {
		case Constants.SYSTEM_TYPE_DEALER:
			params.put("dealer_inner_sn", oid);
			return mapper.searchDealerProductList4Contract(params);
		default:
			return new ArrayList<ResponseProductInfo>();
		}
	}

	@Override
	public List<DealerProductSpecification> selectDealerProductSpecListById(Long did, Long pid) {
		DealerProductSpecification spec = new DealerProductSpecification();
		spec.setDealer_inner_sn(did);
		spec.setProduct_inner_sn(pid);
		return mapper.selectDealerProductSpecListById(spec);
	}

	@Override
	@Transactional
	public DealerProductSpecification insertDealerProductSpec(AddDealerProductSpecForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("dealer_inner_sn_i", form.getOid());
		params.put("product_inner_sn_i", form.getPid());
		params.put("specification_i", form.getSpec());
		params.put("status_i", form.getStatus());
		mapper.d_add_dealer_product_specification(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long psid = Long.valueOf(params.get("specification_inner_sn_o").toString());
			return selectDealerProductSpecInfoById(form.getOid(), form.getPid(), psid);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","自建产品规格型号",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","自建产品规格型号",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int updateDealerProductSpec(EditDealerProductSpecForm form) {
		DealerProductSpecification productSpec = new DealerProductSpecification();
		productSpec.setDealer_inner_sn(form.getOid());
		productSpec.setProduct_inner_sn(form.getPid());
		productSpec.setSpecification_inner_sn(form.getPsid());
		productSpec.setSpecification(form.getSpec());
		productSpec.setStatus(form.getStatus());
		return mapper.updateDealerProductSpec(productSpec);
	}

	@Override
	@Transactional
	public int deleteDealerProductSpec(Long did, Long pid, Long psid) {
		DealerProductSpecification productSpec = new DealerProductSpecification();
		productSpec.setDealer_inner_sn(did);
		productSpec.setProduct_inner_sn(pid);
		productSpec.setSpecification_inner_sn(psid);
		return mapper.deleteDealerProductSpec(productSpec);
	}

	@Override
	public DealerProductSpecification selectDealerProductSpecInfoById(Long oid, Long pid, Long psid) {
		DealerProductSpecification productSpec = new DealerProductSpecification();
		productSpec.setDealer_inner_sn(oid);
		productSpec.setProduct_inner_sn(pid);
		productSpec.setSpecification_inner_sn(psid);
		return mapper.selectDealerProductSpecInfoById(productSpec);
	}

}
