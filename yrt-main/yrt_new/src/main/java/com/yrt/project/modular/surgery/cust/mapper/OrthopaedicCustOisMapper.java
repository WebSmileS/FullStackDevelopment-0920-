package com.yrt.project.modular.surgery.cust.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yrt.common.lang.KeyValue;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOis;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisExample;

public interface OrthopaedicCustOisMapper {
    long countByExample(OrthopaedicCustOisExample example);

    int deleteByExample(OrthopaedicCustOisExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrthopaedicCustOis record);

    int insertSelective(OrthopaedicCustOis record);

    List<OrthopaedicCustOis> selectByExample(OrthopaedicCustOisExample example);

    OrthopaedicCustOis selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrthopaedicCustOis record, @Param("example") OrthopaedicCustOisExample example);

    int updateByExample(@Param("record") OrthopaedicCustOis record, @Param("example") OrthopaedicCustOisExample example);

    int updateByPrimaryKeySelective(OrthopaedicCustOis record);

    int updateByPrimaryKey(OrthopaedicCustOis record);
    
    /**
     * join type/brand
     * @param example
     * @return
     */
    List<OrthopaedicCustOis> selectByExampleExt(OrthopaedicCustOisExample example);
    
    /**
     * null if name not found
     * @param dealerId
     * @param name
     * @param neqId 如果非null, 则 id <> neqId
     * @return
     */
    @Select({"<script>",
    	"select max(name) as name from orthopaedic_cust_ois",
    	" where owner_dealer_inner_sn = #{owner_dealer_inner_sn}",
    	" and name = #{name}",
    	"<if test='neqId != null'> and id &lt;&gt; #{neqId} </if>", // id <> neqId
    	"</script>"})
    String selectExistsName(@Param("owner_dealer_inner_sn") Long dealerId, @Param("name") String name, @Param("neqId")Long neqId);
    
    /**
     * 实际有的手术包 品牌
     * @param op_inner_sn nullable
     * @return
     */
    List<KeyValue<Long, String>> selectAvaialbeBrand(@Param("op_inner_sn") Long op_inner_sn);
    
    /**
     * 实际有的手术包  手术类型
     * @param op_inner_sn
     * @return
     */
    List<KeyValue<Long, String>> selectAvaialbeType(@Param("op_inner_sn") Long op_inner_sn);
//    /**
//     * example.owner_dealer_inner_sn 必须
//     * @param example
//     * @return
//     */
//    List<OrthopaedicCustOis> selectForDealerByExample(OrthopaedicCustOisExample example);
//    
//    
//    /**
//     * example.owner_dealer_inner_sn/op_inner_sn 必须
//     * @param example
//     * @return
//     */
//    List<OrthopaedicCustOis> selectForDealerByPathAndExample(OrthopaedicCustOisExample example);
//    
//    /**
//     * example.hospital_inner_sn 必须
//     * @param example
//     * @return
//     */
//    List<OrthopaedicCustOis> selectForHospitalByExample(OrthopaedicCustOisExample example);
//    /**
//     * example.hospital_inner_sn/op_inner_sn 必须
//     * @param example
//     * @return
//     */
//    List<OrthopaedicCustOis> selectForHospitalByPathAndExample(OrthopaedicCustOisExample example);
}