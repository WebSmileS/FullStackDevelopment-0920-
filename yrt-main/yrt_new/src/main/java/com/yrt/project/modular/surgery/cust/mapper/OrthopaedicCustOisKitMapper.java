package com.yrt.project.modular.surgery.cust.mapper;

import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisKit;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisKitExample;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisKitKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrthopaedicCustOisKitMapper {
    long countByExample(OrthopaedicCustOisKitExample example);

    int deleteByExample(OrthopaedicCustOisKitExample example);

    int deleteByPrimaryKey(OrthopaedicCustOisKitKey key);

    int insert(OrthopaedicCustOisKit record);

    int insertSelective(OrthopaedicCustOisKit record);

    List<OrthopaedicCustOisKit> selectByExample(OrthopaedicCustOisKitExample example);

    OrthopaedicCustOisKit selectByPrimaryKey(OrthopaedicCustOisKitKey key);

    int updateByExampleSelective(@Param("record") OrthopaedicCustOisKit record, @Param("example") OrthopaedicCustOisKitExample example);

    int updateByExample(@Param("record") OrthopaedicCustOisKit record, @Param("example") OrthopaedicCustOisKitExample example);

    int updateByPrimaryKeySelective(OrthopaedicCustOisKit record);

    int updateByPrimaryKey(OrthopaedicCustOisKit record);
}