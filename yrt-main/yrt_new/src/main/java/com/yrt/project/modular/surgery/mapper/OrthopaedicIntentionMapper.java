package com.yrt.project.modular.surgery.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yrt.common.lang.KeyValue;
import com.yrt.project.modular.surgery.domain.OrthopaedicIntention;
import com.yrt.project.modular.surgery.domain.OrthopaedicIntentionKey;

@Repository
public interface OrthopaedicIntentionMapper {

	/**
	 * @param condition
	 *            effect-fields: hid/hname/h-alias & did/dealer-name/status
	 * @return
	 */
	public List<OrthopaedicIntention> searchIntention(OrthopaedicIntention condition);

	public OrthopaedicIntention selectIntentionInfo(OrthopaedicIntentionKey key);

	public int insertOrthopaedicIntention(OrthopaedicIntention intention);

	public int updateIntention(OrthopaedicIntention intention);

	public int deleteIntention(OrthopaedicIntentionKey key);

	// 关联 手术类型
	public int insertIntentionRefType(//
			@Param("hospital_inner_sn") Long hid, //
			@Param("dealer_inner_sn") Long did, //
			@Param("type_inner_sn") Long type_inner_sn, //
			@Param("status") Integer status);

	public int deleteIntentionRefType(OrthopaedicIntentionKey key);

	// 关联品牌
	int insertIntentionRefBrand(//
			@Param("hospital_inner_sn") Long hid, //
			@Param("dealer_inner_sn") Long did, //
			@Param("brand_inner_sn") Long brand_inner_sn, //
			@Param("status") Integer status);

	/**
	 * hospital_inner_sn/dealer_inner_sn
	 * 
	 * @param params
	 * @return
	 */
	int deleteIntentionRefBrand(OrthopaedicIntentionKey key);
	
	/**
	 * 手术包 可选 dealers
	 * @param hospital
	 * @param type
	 * @param brand
	 * @param date 当前日期
	 * @return key(number): dealerId, value(string): dealerName
	 */
	List<KeyValue<Long, String>> selectAvailableDealer(
			@Param("hospital_inner_sn") Long hospital, 
			@Param("type_inner_sn") Long type, 
			@Param("brand_inner_sn") Long brand
			//, @Param("date") Date date
			);
	
	/**
	 * 有意向的 手术类型
	 * @param hospital
	 * @return key(number): brandId, value(string): brandName
	 */
	List<KeyValue<Long, String>> selectAvailableBrand(
			@Param("hospital_inner_sn") Long hospital
			//, @Param("date") Date date
			);
	/**
	 * 有意向的品牌
	 * @param hospital
	 * @return key(number): brandId, value(string): brandName
	 */
	List<KeyValue<Long, String>> selectAvailableType(
			@Param("hospital_inner_sn") Long hospital
			//, @Param("date") Date date
			);
	
	/**
	 * 经销商制定计划 可选医院
	 * @param dealer
	 * @return
	 */
	List<KeyValue<Long, String>> selectAvailableHospital(
			@Param("dealer_inner_sn") Long dealer 
			//, @Param("date") Date date
			);
}
