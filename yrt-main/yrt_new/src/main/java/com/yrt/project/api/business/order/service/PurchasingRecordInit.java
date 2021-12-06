package com.yrt.project.api.business.order.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.ValidateException;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.order.domain.OrderDetail;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.order.mapper.PurchasingRecordInitMapper;

@Component
public class PurchasingRecordInit {

	private static final Logger log = LoggerFactory.getLogger(PurchasingRecordInit.class);
	
	@Autowired PurchasingRecordInitMapper mapper;
	@Autowired PurchasingRecordService service;
	@Autowired SysParameterService paramService;
	
	private SystemType getOrgSytem_type(BillsType billsType) {
		switch (billsType) {
		case DD:
			return SystemType.DEALER;
		case DV:
			return SystemType.DEALER;
		case DH:
			return SystemType.DEALER;
		case HH:
			return SystemType.HOSPITAL;
		case HD:
			return SystemType.HOSPITAL;
		case HV:
			return SystemType.HOSPITAL;
		case VD:
			return SystemType.VENDOR;
		case VH:
			return SystemType.VENDOR;
		case VV:
			return SystemType.VENDOR;
		}
		throw new ValidateException("错误的业务类型!");
	}
	
	@PostConstruct
	public void init() {
		log.info("判断是否需要初始化采购记录");
		if(paramService.isInitPurchasingRecord()) return;
		log.info("开始初始化采购记录");
		List<PurchaseOrder> orderList = mapper.searchPurchaseOrderList();
		for (PurchaseOrder order : orderList) {
			List<OrderDetail> details = new ArrayList<>();
			Map<String,Object> params = new HashMap<>();
			params.put("partA", order.getPartA());
			params.put("partB", order.getPartB());
			params.put("po_so_inner_sn", order.getPo_so_inner_sn());
			if(service.isExistPurchasingRecord(order.getPartA(), order.getPartB(), order.getBillsType(), RefVoucherType.ORDER_PO, order.getPo_so_inner_sn(), null)) {
				continue;
			}
			switch (order.getBillsType()) {
			case DD:
				details = mapper.selectDDOrderDetailList(params);
				break;
			case DV:
				details = mapper.selectDVOrderDetailList(params);
				break;
			case HD:
				details = mapper.selectHDOrderDetailList(params);
				break;
			case HV:
				details = mapper.selectHVOrderDetailList(params);
				break;
				
			default:
				break;
			}
			order.setDetails(details);
			service.insertPurchasingRecord(order, getOrgSytem_type(order.getBillsType()), order.getCreator_name());
		}
		log.info("完成初始化采购记录");
		paramService.saveParameter(SystemType.PLATFORM, 0L, SysParameterService.INIT_PURCHASING_RECORD, "从已有采购订单初始化采购记录", "1");
		log.info("修改初始化采购记录的系统参数为已完成");
	}
}
