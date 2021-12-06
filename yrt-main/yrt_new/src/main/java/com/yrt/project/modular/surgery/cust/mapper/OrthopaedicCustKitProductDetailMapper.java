package com.yrt.project.modular.surgery.cust.mapper;

import com.yrt.project.modular.surgery.cust.OrthopaedicCustKitProductDetail;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustKitProductDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrthopaedicCustKitProductDetailMapper {
    long countByExample(OrthopaedicCustKitProductDetailExample example);

    int deleteByExample(OrthopaedicCustKitProductDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrthopaedicCustKitProductDetail record);

    int insertSelective(OrthopaedicCustKitProductDetail record);

    List<OrthopaedicCustKitProductDetail> selectByExample(OrthopaedicCustKitProductDetailExample example);

    OrthopaedicCustKitProductDetail selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrthopaedicCustKitProductDetail record, @Param("example") OrthopaedicCustKitProductDetailExample example);

    int updateByExample(@Param("record") OrthopaedicCustKitProductDetail record, @Param("example") OrthopaedicCustKitProductDetailExample example);

    int updateByPrimaryKeySelective(OrthopaedicCustKitProductDetail record);

    int updateByPrimaryKey(OrthopaedicCustKitProductDetail record);
}