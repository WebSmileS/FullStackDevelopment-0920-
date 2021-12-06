package com.yrt.project.modular.surgery.cust.mapper;

import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisProductDetail;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisProductDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrthopaedicCustOisProductDetailMapper {
    long countByExample(OrthopaedicCustOisProductDetailExample example);

    int deleteByExample(OrthopaedicCustOisProductDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrthopaedicCustOisProductDetail record);

    int insertSelective(OrthopaedicCustOisProductDetail record);

    List<OrthopaedicCustOisProductDetail> selectByExample(OrthopaedicCustOisProductDetailExample example);

    OrthopaedicCustOisProductDetail selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrthopaedicCustOisProductDetail record, @Param("example") OrthopaedicCustOisProductDetailExample example);

    int updateByExample(@Param("record") OrthopaedicCustOisProductDetail record, @Param("example") OrthopaedicCustOisProductDetailExample example);

    int updateByPrimaryKeySelective(OrthopaedicCustOisProductDetail record);

    int updateByPrimaryKey(OrthopaedicCustOisProductDetail record);
}