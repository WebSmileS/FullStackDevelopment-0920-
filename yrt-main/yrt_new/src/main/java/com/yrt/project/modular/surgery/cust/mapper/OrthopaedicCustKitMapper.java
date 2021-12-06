package com.yrt.project.modular.surgery.cust.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yrt.project.modular.surgery.cust.OrthopaedicCustKit;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustKitExample;

public interface OrthopaedicCustKitMapper {
	long countByExample(OrthopaedicCustKitExample example);

	int deleteByExample(OrthopaedicCustKitExample example);

	int deleteByPrimaryKey(Long id);

	int insert(OrthopaedicCustKit record);

	int insertSelective(OrthopaedicCustKit record);

	List<OrthopaedicCustKit> selectByExample(OrthopaedicCustKitExample example);

	OrthopaedicCustKit selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") OrthopaedicCustKit record,
			@Param("example") OrthopaedicCustKitExample example);

	int updateByExample(@Param("record") OrthopaedicCustKit record,
			@Param("example") OrthopaedicCustKitExample example);

	int updateByPrimaryKeySelective(OrthopaedicCustKit record);

	int updateByPrimaryKey(OrthopaedicCustKit record);

	/**
	 * 
	 * join type/brand
	 * 
	 * @param example
	 * @return
	 */
	List<OrthopaedicCustKit> selectByExampleExt(OrthopaedicCustKitExample example);

	/**
	 * example.owner_dealer_inner_sn 必须
	 * 
	 * @param example
	 * @return
	 */
	List<OrthopaedicCustKit> selectForDealerByExample(OrthopaedicCustKitExample example);

	/**
	 * null if name not found
	 * 
	 * @param dealerId
	 * @param name
	 * @param neqId
	 *            如果非null, 则 id <> neqId
	 * @return
	 */
	@Select({ "<script>", 
		"select max(name) as name from orthopaedic_cust_kit",
		" where owner_dealer_inner_sn = #{owner_dealer_inner_sn}", 
		" and name = #{name}", 
		"<if test='neqId != null'> and id &lt;&gt; #{neqId} </if>", // id <> neqId
		"</script>" })
	String selectExistsName(@Param("owner_dealer_inner_sn") Long dealerId, @Param("name") String name,
			@Param("neqId") Long neqId);
}
