package com.yrt.project.api.business.dealer.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.dict.ProductSnType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.dealer.service.ITmpProductService;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductImageForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductMDRFForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductSpecForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AuditForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductMDRFForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductSpecForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.ImageIdsForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpIDSForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpProductComposeForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpProductSpecComposeForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpSearchProductForm;
import com.yrt.project.api.business.productSn.service.ProductSnService;
import com.yrt.project.api.business.productSn.vo.ProductSnForm;
import com.yrt.project.modular.product.domain.TmpProduct;
import com.yrt.project.modular.product.domain.TmpProductMDRF;
import com.yrt.project.modular.product.domain.TmpProductRelImage;
import com.yrt.project.modular.product.domain.TmpProductSpecification;
import com.yrt.project.modular.product.mapper.TmpProductMapper;

@Service
public class TmpProductServiceImpl implements ITmpProductService {

	@Autowired private TmpProductMapper tpMapper;
	@Autowired private ProductSnService service;
	
	//待审核产品
	@Override
	public TableDataInfo<TmpProduct> searchtTmpProduct(TmpSearchProductForm form, Integer pageNum, Integer pageSize) {
		Map<String, Object> params = new HashMap<>();
		params.put("page_i", pageNum==null?1:pageNum);
		params.put("row_i", pageSize==null?10:pageSize);
		params.put("data_source_i", form.getSource());
		params.put("operation_organization_i", form.getOrg_id());
		params.put("type_inner_sn_i", form.getType_inner_sn());
		params.put("code68_sn_i", form.getCode68_sn());
		params.put("product_name_i", form.getName());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		List<TmpProduct> list = tpMapper.searchTmpProduct(params);
		for (TmpProduct tmp : list) {
			Map<String, Object> params1 = new HashMap<>();
			params1.put("data_source", form.getSource());
			params1.put("operation_organization", form.getOrg_id());
			params1.put("vendor_inner_sn", tmp.getVendor_inner_sn());
			params1.put("product_inner_sn", tmp.getProduct_inner_sn());
			List<TmpProductSpecification> specList = tpMapper.selectTmpProductSpecList(params1);
			//添加医保编号和阳光流水号
			SystemType type = ServletUtils.getCurUserSystemType();
			if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
				Long oid = ServletUtils.getCurUserOrgID();
				for (TmpProductSpecification spec : specList) {
					String default_health_care_sn = service.selectDefaultProductSn(oid, spec.getVendor_inner_sn(),
							spec.getProduct_inner_sn(), spec.getSpecification_inner_sn(), ProductSnType.HEALTH_CARE_SN);
					String default_sun_sn = service.selectDefaultProductSn(oid, spec.getVendor_inner_sn(),
							spec.getProduct_inner_sn(), spec.getSpecification_inner_sn(), ProductSnType.SUN_SN);
					spec.setDefault_health_care_sn(default_health_care_sn);
					spec.setDefault_sun_sn(default_sun_sn);
				}
			}
			tmp.setTmpSpecList(specList);
		}
		long total = Long.valueOf(params.get("total_row_o").toString());
		TableDataInfo<TmpProduct> rspData = new TableDataInfo<TmpProduct>();
        rspData.setCode(0);
        rspData.setRows(list);
        rspData.setTotal(total);
        return rspData;
	}
	
	@Override
	public TmpProduct getTmpProductInfo(TmpIDSForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		return tpMapper.selectTmpProductInfo(params);
	}

	@Override
	@Transactional
	public TmpProduct addTmpProduct(AddTmpProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source_i", form.getSource());
		params.put("operation_organization_i", form.getOrg_id());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", null);
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
		params.put("product_vendor_sn_i", null);
		params.put("storage_condition_i", form.getStorage_condition());
		params.put("health_care_type_i", form.getHealth_care_type());
		params.put("type_i", form.getType());
		params.put("operation_i", form.getOperation());
		params.put("audit_level_i", form.getAudit_level());
		params.put("status_i", form.getStatus());
		params.put("name_i", form.getName().trim());
		params.put("description_i", form.getDescription());
		params.put("specifications_i", form.getSpecifications());
		params.put("code68_type_i", form.getCode68_type());
		tpMapper.addOrEditTmpProduct(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Map<String, Object> params1 = new HashMap<>();
			params1.put("data_source", form.getSource());
			params1.put("operation_organization", form.getOrg_id());
			params1.put("vendor_inner_sn", form.getVendor_inner_sn());
			params1.put("product_inner_sn", Long.valueOf(params.get("product_inner_sn_o").toString()));
			return tpMapper.selectTmpProductInfo(params1);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","产品",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","产品",params.get("message_string_o")));
			}
		}
	}
	
	@Override
	@Transactional
	public TmpProduct addOrEditTmpProductCompose(TmpProductComposeForm form, boolean isUpdate) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source_i", form.getSource());
		params.put("operation_organization_i", form.getOrg_id());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", isUpdate?form.getProduct_inner_sn():null);
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
		params.put("product_vendor_sn_i", null);
		params.put("storage_condition_i", form.getStorage_condition());
		params.put("health_care_type_i", form.getHealth_care_type());
		params.put("type_i", form.getType());
		params.put("operation_i", form.getOperation());
		params.put("audit_level_i", form.getAudit_level());
		params.put("status_i", form.getStatus());
		params.put("name_i", form.getName().trim());
		params.put("description_i", form.getDescription());
		params.put("specifications_i", form.getSpecifications());
		params.put("code68_type_i", form.getCode68_type());
		tpMapper.addOrEditTmpProduct(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long pid = null;
			if(isUpdate) {
				pid = form.getProduct_inner_sn();
			}else {
				if(StringUtils.isNull(params.get("product_inner_sn_o"))) {
					throw new DBException(MessageUtils.message("db.add.fail","产品规格型号","未返回产品ID"));
				}
				pid = Long.valueOf(params.get("product_inner_sn_o").toString());
			}
			Map<String, Object> params1 = new HashMap<>();
			params1.put("data_source", form.getSource());
			params1.put("operation_organization", form.getOrg_id());
			params1.put("vendor_inner_sn", form.getVendor_inner_sn());
			params1.put("product_inner_sn", pid);
			TmpProduct info = tpMapper.selectTmpProductInfo(params1);
			Map<String, Object> tmp = new HashMap<>();
			tmp.put("data_source", form.getSource());
			tmp.put("operation_organization", form.getOrg_id());
			tmp.put("vendor_inner_sn", form.getVendor_inner_sn());
			tmp.put("product_inner_sn", form.getProduct_inner_sn());
			List<TmpProductSpecification> specList = tpMapper.selectTmpProductSpecList1(tmp);
			for (TmpProductSpecification ss : specList) {
				Map<String, Object> params2 = new HashMap<>();
				params2.put("data_source", form.getSource());
				params2.put("operation_organization", form.getOrg_id());
				params2.put("vendor_inner_sn", ss.getVendor_inner_sn());
				params2.put("product_inner_sn", ss.getProduct_inner_sn());
				params2.put("specification_inner_sn", ss.getSpecification_inner_sn());
				//删除医保编号和阳光流水号
				SystemType type = ServletUtils.getCurUserSystemType();
				if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
					Long oid = ServletUtils.getCurUserOrgID();
					service.deleteBatchProductSn(oid, ss.getVendor_inner_sn(), ss.getProduct_inner_sn(),
							ss.getSpecification_inner_sn(), ProductSnType.HEALTH_CARE_SN);
					service.deleteBatchProductSn(oid, form.getVendor_inner_sn(), ss.getProduct_inner_sn(),
							ss.getSpecification_inner_sn(),ProductSnType.SUN_SN);
				}
				tpMapper.deleteTmpProductSpec(params2);
			}
			List<TmpProductSpecComposeForm> list = form.getSpecAndMinUnitList();
			for (TmpProductSpecComposeForm form1 : list) {
				params1.put("data_source_i", form.getSource());
				params1.put("operation_organization_i", form.getOrg_id());
				params1.put("vendor_inner_sn_i", form.getVendor_inner_sn());
				params1.put("product_inner_sn_i", isUpdate?form.getProduct_inner_sn():pid);
				params1.put("specification_inner_sn_i", null);
				params1.put("operation_i", form.getOperation());
				params1.put("audit_level_i", form.getAudit_level());
				params1.put("status_i", 2);
				params1.put("specification_i", form1.getSpecifications());
				params1.put("specification_vendor_sn_i", StringUtils.isEmpty(form1.getSpecification_vendor_sn())?null:form1.getSpecification_vendor_sn());
				params1.put("unit_name_i", StringUtils.isNotEmpty(form1.getMinUnit())?form1.getMinUnit():"");
				tpMapper.addOrEditTmpProductSpec(params1);
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
						throw new DuplicateException(MessageUtils.message("db.add.fail","产品规格型号",params.get("message_string_o")));
					}else {
						throw new DBException(MessageUtils.message("db.add.fail","产品规格型号",params.get("message_string_o")));
					}
				}else {
//					Long psid = Long.valueOf(params.get("specification_inner_sn_o").toString());
					//添加医保编号和阳光流水号
//					SystemType type = ServletUtils.getCurUserSystemType();
//					if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
//						Long oid = ServletUtils.getCurUserOrgID();
//						ProductSnForm form2 = new ProductSnForm();
//						form2.setVendor_inner_sn(form.getVendor_inner_sn());
//						form2.setProduct_inner_sn(isUpdate?form.getProduct_inner_sn():pid);
//						form2.setSpecification_inner_sn(psid);
//						form2.setSn(form1.getDefault_health_care_sn());
//						form2.setType(ProductSnType.HEALTH_CARE_SN);
//						service.addOrEditProductSn(oid, form2);
//						form2.setSn(form1.getDefault_sun_sn());
//						form2.setType(ProductSnType.SUN_SN);
//						service.addOrEditProductSn(oid, form2);
//					}
				}
			}
			Map<String, Object> params2 = new HashMap<>();
			params2.put("data_source", form.getSource());
			params2.put("operation_organization", form.getOrg_id());
			params2.put("vendor_inner_sn", form.getVendor_inner_sn());
			params2.put("product_inner_sn", isUpdate?form.getProduct_inner_sn():pid);
			info.setTmpSpecList(tpMapper.selectTmpProductSpecList(params2));
			return info;
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","产品",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","产品",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public TmpProduct editTmpProduct(EditTmpProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source_i", form.getSource());
		params.put("operation_organization_i", form.getOrg_id());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
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
		params.put("type_i", form.getType());
		params.put("operation_i", form.getOperation());
		params.put("audit_level_i", form.getAudit_level());
		params.put("status_i", 2);
		params.put("name_i", form.getName().trim());
		params.put("product_vendor_sn_i", null);
		params.put("description_i", form.getDescription());
		params.put("specifications_i", form.getSpecifications());
		params.put("code68_type_i", form.getCode68_type());
		tpMapper.addOrEditTmpProduct(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Map<String, Object> params1 = new HashMap<>();
			params1.put("data_source", form.getSource());
			params1.put("operation_organization", form.getOrg_id());
			params1.put("vendor_inner_sn", form.getVendor_inner_sn());
			params1.put("product_inner_sn", form.getProduct_inner_sn());
			TmpProduct info = tpMapper.selectTmpProductInfo(params1);
			if(info != null) {
				info.setTmpSpecList(tpMapper.selectTmpProductSpecList(params1));
			}
			return info;
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","产品",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","产品",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int deleteTmpProudct(TmpIDSForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		TmpProduct tmp = tpMapper.selectTmpProductInfo(params);
		if(tmp.getOperation() == 1) {//新增的草稿产品
			tpMapper.deleteTmpProductSpec(params);
			tpMapper.deleteTmpProductMDRF(params);
			tpMapper.deleteTmpMDRFImage(params);
			tpMapper.deleteTmpProductImage(params);
		}
		return tpMapper.deleteTmpProduct(params);
	}

	//待审核规格型号
	@Override
	@Transactional
	public TmpProductSpecification addTmpProductSpec(AddTmpProductSpecForm form) throws DBException, DuplicateException {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source_i", form.getSource());
		params.put("operation_organization_i", form.getOrg_id());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("specification_inner_sn_i", null);
		params.put("operation_i", form.getOperation());
		params.put("audit_level_i", form.getAudit_level());
		params.put("status_i", 2);
		params.put("specification_i", form.getSpecifications());
		params.put("specification_vendor_sn_i", StringUtils.isEmpty(form.getSpecification_vendor_sn())?null:form.getSpecification_vendor_sn());
		params.put("unit_name_i", StringUtils.isNotEmpty(form.getUnit_name())?form.getUnit_name():"");
		tpMapper.addOrEditTmpProductSpec(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long psid = Long.valueOf(params.get("specification_inner_sn_o").toString());
			Map<String, Object> params1 = new HashMap<>();
			params1.put("data_source", form.getSource());
			params1.put("operation_organization", form.getOrg_id());
			params1.put("vendor_inner_sn", form.getVendor_inner_sn());
			params1.put("product_inner_sn", form.getProduct_inner_sn());
			params1.put("specification_inner_sn", psid);
			//添加医保编号和阳光流水号
			SystemType type = ServletUtils.getCurUserSystemType();
			if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
				Long oid = ServletUtils.getCurUserOrgID();
				ProductSnForm form1 = new ProductSnForm();
				form1.setVendor_inner_sn(form.getVendor_inner_sn());
				form1.setProduct_inner_sn(form.getProduct_inner_sn());
				form1.setSpecification_inner_sn(psid);
				form1.setSn(form.getDefault_health_care_sn());
				form1.setType(ProductSnType.HEALTH_CARE_SN);
				service.addOrEditProductSn(oid, form1);
				form1.setSn(form.getDefault_sun_sn());
				form1.setType(ProductSnType.SUN_SN);
				service.addOrEditProductSn(oid, form1);
			}
			return tpMapper.selectTmpProductSpecInfo(params1);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","产品规格型号",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","产品规格型号",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int editTmpProductSpec(EditTmpProductSpecForm form) throws DBException, DuplicateException {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source_i", form.getSource());
		params.put("operation_organization_i", form.getOrg_id());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("specification_inner_sn_i", form.getSpecification_inner_sn());
		params.put("operation_i", form.getOperation());
		params.put("audit_level_i", form.getAudit_level());
		params.put("status_i", 2);
		params.put("specification_i", form.getSpecification());
		params.put("specification_vendor_sn_i", form.getSpecification_vendor_sn());
		params.put("unit_name_i", StringUtils.isNotEmpty(form.getUnit_name())?form.getUnit_name():"");
		TmpIDSForm ids = new TmpIDSForm();
		ids.setOrg_id(form.getOrg_id());
		ids.setSource(form.getSource());
		ids.setVendor_inner_sn(form.getVendor_inner_sn());
		ids.setProduct_inner_sn(form.getProduct_inner_sn());
		ids.setSpecification_inner_sn(form.getSpecification_inner_sn());
		TmpProductSpecification oldSpec = selectTmpProductSpecInfo(ids);
		if(oldSpec.isUpdate(form)) {
			tpMapper.addOrEditTmpProductSpec(params);
			if(Integer.valueOf(params.get("result_o").toString()) != 0) {
				if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
					throw new DuplicateException(MessageUtils.message("db.update.fail","产品规格型号",params.get("message_string_o")));
				}else {
					throw new DBException(MessageUtils.message("db.update.fail","产品规格型号",params.get("message_string_o")));
				}
			}
		}
		//添加医保编号和阳光流水号
		SystemType type = ServletUtils.getCurUserSystemType();
		if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
			Long oid = ServletUtils.getCurUserOrgID();
			ProductSnForm form1 = new ProductSnForm();
			form1.setVendor_inner_sn(form.getVendor_inner_sn());
			form1.setProduct_inner_sn(form.getProduct_inner_sn());
			form1.setSpecification_inner_sn(form.getSpecification_inner_sn());
			form1.setSn(form.getDefault_health_care_sn());
			form1.setType(ProductSnType.HEALTH_CARE_SN);
			service.addOrEditProductSn(oid, form1);
			form1.setSn(form.getDefault_sun_sn());
			form1.setType(ProductSnType.SUN_SN);
			service.addOrEditProductSn(oid, form1);
		}
		
		return 1;
	}

	@Override
	@Transactional
	public int deleteTmpProductSpec(TmpIDSForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		TmpProductSpecification info = tpMapper.selectTmpProductSpecInfo(params);

		//这里主要因为修改时编码和规格型号一样则不会往临时表插入记录，导致查不出来为null，可以这儿判空或者修改时即使编码和规格型号一样也向临时表插入记录
		if (Objects.isNull(info)) return 1;

		if(info.getStatus().equals(0) || info.getStatus().equals(1)) {
			throw new ValidateException("当前状态下的规格型号不能删除!");
		}
		//删除医保编号和阳光流水号
		SystemType type = ServletUtils.getCurUserSystemType();
		if (info.getOperation().intValue() == 1 && (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR))) {
			Long oid = ServletUtils.getCurUserOrgID();
			service.deleteProductSn(oid, form.getVendor_inner_sn(), form.getProduct_inner_sn(),
					form.getSpecification_inner_sn(), null, ProductSnType.HEALTH_CARE_SN);
			service.deleteProductSn(oid, form.getVendor_inner_sn(), form.getProduct_inner_sn(),
					form.getSpecification_inner_sn(), null, ProductSnType.SUN_SN);
		}
		return tpMapper.deleteTmpProductSpec(params);
	}

	@Override
	public List<TmpProductSpecification> selectTmpProductSpecList(TmpIDSForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());

		// 得到所有的规格型号
		List<TmpProductSpecification> result = tpMapper.selectTmpProductSpecList(params);
		//添加医保编号和阳光流水号
		SystemType type = ServletUtils.getCurUserSystemType();
		if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
			Long oid = ServletUtils.getCurUserOrgID();
			for (TmpProductSpecification spec : result) {
				String default_health_care_sn = service.selectDefaultProductSn(oid, spec.getVendor_inner_sn(),
						spec.getProduct_inner_sn(), spec.getSpecification_inner_sn(), ProductSnType.HEALTH_CARE_SN);
				String default_sun_sn = service.selectDefaultProductSn(oid, spec.getVendor_inner_sn(),
						spec.getProduct_inner_sn(), spec.getSpecification_inner_sn(), ProductSnType.SUN_SN);
				spec.setDefault_health_care_sn(default_health_care_sn);
				spec.setDefault_sun_sn(default_sun_sn);
			}
		}
		return result;
	}
	
	@Override
	public TmpProductSpecification selectTmpProductSpecInfo(TmpIDSForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		TmpProductSpecification info = tpMapper.selectTmpProductSpecInfo(params);
		if(info == null) {
			info = tpMapper.selectProductSpecInfo(params);
		}else {
			//添加医保编号和阳光流水号
			SystemType type = ServletUtils.getCurUserSystemType();
			if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
				Long oid = ServletUtils.getCurUserOrgID();
				String default_health_care_sn = service.selectDefaultProductSn(oid, info.getVendor_inner_sn(),
						info.getProduct_inner_sn(), info.getSpecification_inner_sn(), ProductSnType.HEALTH_CARE_SN);
				String default_sun_sn = service.selectDefaultProductSn(oid, info.getVendor_inner_sn(),
						info.getProduct_inner_sn(), info.getSpecification_inner_sn(), ProductSnType.SUN_SN);
				info.setDefault_health_care_sn(default_health_care_sn);
				info.setDefault_sun_sn(default_sun_sn);
			}
		}
		return info;
	}

	//图片
	@Override
	@Transactional
	public TmpProductRelImage addTmpProductRelImage(AddTmpProductImageForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source_i", form.getSource());
		params.put("operation_organization_i", form.getOrg_id());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("image_inner_sn_i", null);
		params.put("operation_i", form.getOperation());
		params.put("audit_level_i", form.getAudit_level());
		params.put("status_i", 2);
		params.put("type_i", 1);
		params.put("url_i", form.getUrl());
		tpMapper.addOrEditTmpProductImage(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Map<String, Object> params1 = new HashMap<>();
			params1.put("data_source", form.getSource());
			params1.put("operation_organization", form.getOrg_id());
			params1.put("vendor_inner_sn", form.getVendor_inner_sn());
			params1.put("product_inner_sn", form.getProduct_inner_sn());
			params1.put("image_inner_sn", Long.valueOf(params.get("image_inner_sn_o").toString()));
			return tpMapper.selectCertImageInfo(params1);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","产品图片",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","产品图片",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int delTmpProductImage(ImageIdsForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("image_inner_sn", form.getImage_inner_sn());
		return tpMapper.deleteTmpProductImage(params);
	}

	@Override
	public List<TmpProductRelImage> selectCertImageList(ImageIdsForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		return tpMapper.selectCertImageList(params);
	}

	//注册证
	@Override
	@Transactional
	public TmpProductMDRF addTmpProductMDRF(AddTmpProductMDRFForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source_i", form.getSource());
		params.put("operation_organization_i", form.getOrg_id());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("mdrf_inner_sn_i", null);
		params.put("operation_i", form.getOperation());
		params.put("audit_level_i", form.getAudit_level());
		params.put("status_i", 2);
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
		params.put("data_i", JSONObject.toJSONString(form.getImages()));
		tpMapper.addOrEditTmpProductMDRF(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Map<String, Object> params1 = new HashMap<>();
			params1.put("data_source", form.getSource());
			params1.put("operation_organization", form.getOrg_id());
			params1.put("vendor_inner_sn", form.getVendor_inner_sn());
			params1.put("product_inner_sn", form.getProduct_inner_sn());
			params1.put("mdrf_inner_sn", Long.valueOf(params.get("mdrf_inner_sn_o").toString()));
			return tpMapper.selectTmpProductMDRFInfo(params1);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","注册证",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","注册证",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int editTmpProductMDRF(EditTmpProductMDRFForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source_i", form.getSource());
		params.put("operation_organization_i", form.getOrg_id());
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("mdrf_inner_sn_i", form.getMdrf_inner_sn());
		params.put("operation_i", form.getOperation());
		params.put("audit_level_i", form.getAudit_level());
		params.put("status_i", 2);
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
		params.put("data_i", JSONObject.toJSONString(form.getImages()));
		tpMapper.addOrEditTmpProductMDRF(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			return 1;
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","注册证",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","注册证",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int delTmpProductMDRF(TmpIDSForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("mdrf_inner_sn", form.getMdrf_inner_sn());
		tpMapper.deleteTmpMDRFImage(params);
		return tpMapper.deleteTmpProductMDRF(params);
	}

	@Override
	public TmpProductMDRF getTmpProductMDRF(TmpIDSForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("mdrf_inner_sn", form.getMdrf_inner_sn());
		TmpProductMDRF info = tpMapper.selectTmpProductMDRFInfo(params);
		if(info == null) {
			info = tpMapper.selectProductMDRFInfo(params);
			if(info != null) {
				info.setImages(tpMapper.selectMDRFImageList(params));
			}
		}else {
			info.setImages(tpMapper.selectTmpMDRFImageList(params));
		}
		return info;
	}

	@Override
	public List<TmpProductMDRF> getTmpProductMDRFList(TmpIDSForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		List<TmpProductMDRF> list = tpMapper.searchTmpProductMDRFList(params);
		for (TmpProductMDRF info : list) {
			params.put("mdrf_inner_sn", info.getMdrf_inner_sn());
			info.setImages(tpMapper.selectTmpMDRFImageList(params));
		}
		return list;
	}

	//审批
	@Override
	public int sendAudit(AuditForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("data_source", form.getSource());
		params.put("operation_organization", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("status", 3);
		if(form.getIs_tmp() == 1) {
			tpMapper.updateTmpProductStatus(params);
		}
		tpMapper.updateTmpProductImageStatus(params);
		tpMapper.updateTmpProductSpecStatus(params);
		tpMapper.updateTmpProductMDRFStatus(params);
		return 1;
	}
}
