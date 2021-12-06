package com.yrt.project.api.external.order;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.BaseIntEnum;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.common.service.VoucherNumberService.OrderNumberPair;
import com.yrt.project.api.external.order.vo.ExternalSaleOrderDetailForm;
import com.yrt.project.api.external.order.vo.ExternalSaleOrderForm;
import com.yrt.project.modular.external.mapper.DealerDealerExternalOrderMapper;
import com.yrt.project.modular.external.mapper.DealerVendorExternalOrderMapper;
import com.yrt.project.modular.external.mapper.ExternalOrderMapper;
import com.yrt.project.modular.external.mapper.HospitalDealerExternalOrderMapper;
import com.yrt.project.modular.external.mapper.HospitalVendorExternalOrderMapper;

@Service
public class ExternalOrderServiceImpl {
	
	@Autowired DealerDealerExternalOrderMapper ddMapper;
	@Autowired DealerVendorExternalOrderMapper dvMapper;
	@Autowired HospitalDealerExternalOrderMapper hdMapper;
	@Autowired HospitalVendorExternalOrderMapper hvMapper;
	
	@Autowired private VoucherNumberService numberUtils;
	
	private ExternalOrderMapper getOrderMapper(SystemType curSystemType, int organization_type) {
		if(curSystemType.equals(SystemType.VENDOR)) {
			throw new DBException("当前机构不支持该功能!");
		}
		switch (curSystemType) {
		case HOSPITAL:
			switch (organization_type) {
			case 1:
				throw new DBException("当前机构不支持该功能!");
			case 2:
				return hdMapper;
			case 3:
				return hvMapper;
			}
		case DEALER:
			switch (organization_type) {
			case 1:
				throw new DBException("当前机构不支持该功能!");
			case 2:
				return ddMapper;
			case 3:
				throw new DBException("当前机构不支持该功能!");
			}
		default:
		}
		throw new DBException("当前机构不支持该功能!");
	}

	@Transactional
	public int buildPurchaseOrder(@Valid ExternalSaleOrderForm form, Long curId, String curName) {
		List<ExternalSaleOrderDetailForm> details = form.getDetails();
		if(StringUtils.isEmpty(details)) {
			throw new DBException("明细列表不能为空!");
		}
		
		Long org_id = ServletUtils.getCurUserOrgID();
		SystemType curSystemType = ServletUtils.getCurUserSystemType();
		int organization_type = form.getOrg_type();
		ExternalOrderMapper mapper = getOrderMapper(curSystemType, organization_type);
		Map<String,Object> params = new HashMap<>();
		params.put("org_id_i", org_id);
		params.put("organization_inner_sn_i", form.getOrganization_inner_sn());
		params.put("buyer_inner_sn_i", curId);
		params.put("buyer_name_i", curName);
		params.put("purchase_department_inner_sn_i", null);
		params.put("purchase_department_i", "");
		params.put("purchase_creator_inner_sn_i", curId);
		params.put("purchase_creator_name_i", curName);
		params.put("purchase_auditor_inner_sn_i", curId);
		params.put("purchase_auditor_name_i", curName);
		params.put("purchase_warehouse_inner_sn_i", null);//TODO:暂时是个前端没有输入的内容
		params.put("purchase_warehouse_name_i", "");
		OrderNumberPair pair = numberUtils.getOrderNumberPair(curSystemType, org_id, BaseIntEnum.valueOf(SystemType.class, organization_type), form.getOrganization_inner_sn());
		params.put("po_sn_i", pair.getPo().get());
		params.put("so_sn_i", pair.getSo().get());
		params.put("expect_aog_date_i", null);
		params.put("status_i", 2);//2-已审批
		params.put("create_organization_type_i", 0);
		params.put("sale_creator_inner_sn_i", null);
		params.put("sale_creator_name_i", "");
		params.put("sale_auditor_inner_sn_i", null);
		params.put("sale_auditor_name_i", "");
		params.put("create_time_i", new Date());
		params.put("audit_time_i", new Date());
		params.put("confirm_time_i", null);
		params.put("description_i", form.getDescription());
		params.put("purchase_affirmant_inner_sn_i", null);
		params.put("purchase_affirmant_name_i", "");
		params.put("sale_affirmant_inner_sn_i", null);
		params.put("sale_affirmant_name_i", "");
		mapper.insertPurchaseOrder(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long poid = Long.valueOf(params.get("po_so_inner_sn_o").toString());
			int index = 0;
			for (ExternalSaleOrderDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("org_id_i", org_id);
				params1.put("organization_inner_sn_i", form.getOrganization_inner_sn());
				params1.put("po_so_inner_sn_i", poid);
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("unit_price_i", detail.getUnit_price());
				params1.put("discount_i", detail.getDiscount());
				params1.put("rate_i", detail.getRate());
				params1.put("sort_number_i", index);
				params1.put("status_i", 0);
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				params1.put("batch_number_i", detail.getBatch_number());
				params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
				params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
				mapper.insertPurchaseOrderVendorDetail(params1);
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","采购订单明细",params1.get("message_string_o")));
				}
				index++;
			}
			return 1;
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","采购订单",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","采购订单",params.get("message_string_o")));
			}
		}
	}
}
