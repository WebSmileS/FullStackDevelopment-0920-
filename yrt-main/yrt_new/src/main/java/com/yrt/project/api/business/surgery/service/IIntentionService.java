package com.yrt.project.api.business.surgery.service;

import java.util.List;

import com.yrt.common.lang.KeyValue;
import com.yrt.project.api.business.surgery.vo.SearchIntentionForm;
import com.yrt.project.modular.surgery.domain.OrthopaedicIntention;

public interface IIntentionService {

	//骨科合作意向
	public OrthopaedicIntention addOrthopaedicIntention(OrthopaedicIntention form);

	public OrthopaedicIntention editOrthopaedicIntention(OrthopaedicIntention form);

	public int delOrthopaedicIntention(Long hid, Long did);

	public List<OrthopaedicIntention> searchOrthopaedicIntentionList(SearchIntentionForm form);

	public OrthopaedicIntention selectOrthopaedicIntentionInfo(Long hid, Long did);
	
	/**
	 * list Dealer(id/name) (依据 意向)：<p>
	 * 医院合作意向范围内的经销商(当选中一个平台手术包时, 通过此方法获取经销商列表, 如果为多个，需用户选择)
	 * @return {key:dealerId, value:dealerName}
	 */
	public List<KeyValue<Long, String>> getAvailableDealer(long hospitalId, long operationTypeId, long brandId);
	
	/**
	 * list brand(id/name) ( 意向品牌)
	 * @return {key:brandId, value:brandName}
	 */
	public List<KeyValue<Long, String>> getAvailableBrand(long hospitalId);
	
	/**
	 * list type(id/name) ( 意向 手术类型)
	 * @return {key:typeId, value:typeName}
	 */
	public List<KeyValue<Long, String>> getAvailableType(long hospitalId);
	
	/**
	 * list Hospital(id/name) (依据 意向)：<p>
	 * 经销商合作意向范围内的医院。
	 * @return {key:hospitalId, value:hospitalName}
	 */
	public List<KeyValue<Long, String>> getAvailableHospital(long dealerId);
}
