package com.yrt.project.api.business.surgery.plan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.lang.KeyValue;
import com.yrt.project.api.business.surgery.service.IIntentionService;
import com.yrt.project.modular.surgery.cust.mapper.OrthopaedicCustOisMapper;

@Service
public class OperationPlanSupportService {
	@Autowired
	private OrthopaedicCustOisMapper custOisMapper;
	

	@Autowired
	private IIntentionService intentionService;
	
	/**
	 * 实有手术包 且 在意向内 的 品牌
	 * @param hospitalId
	 * @param op_inner_sn
	 * @return
	 */
	public List<KeyValue<Long, String>> getAvailableBrandForPlan(long hospitalId, Long op_inner_sn) {
		List<KeyValue<Long, String>> available = custOisMapper.selectAvaialbeBrand(op_inner_sn);
		List<KeyValue<Long, String>> intentional = intentionService.getAvailableBrand(hospitalId);
		available.retainAll(intentional);
		return available;
	}
	
	/**
	 * 实有手术包 且 在意向内 的 手术类型
	 * @param hospitalId
	 * @param op_inner_sn
	 * @return
	 */
	public List<KeyValue<Long, String>> getAvailableTypeForPlan(long hospitalId, Long op_inner_sn) {
		List<KeyValue<Long, String>> available = custOisMapper.selectAvaialbeType(op_inner_sn);
		List<KeyValue<Long, String>> intentional = intentionService.getAvailableType(hospitalId);
		available.retainAll(intentional);
		return available;
	}
}
