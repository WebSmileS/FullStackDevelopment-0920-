package com.yrt.project.modular.surgery.cust.mapper;

import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisOrthopaedicOperationPathwayExample;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisOrthopaedicOperationPathwayKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrthopaedicCustOisOrthopaedicOperationPathwayMapper {
    long countByExample(OrthopaedicCustOisOrthopaedicOperationPathwayExample example);

    int deleteByExample(OrthopaedicCustOisOrthopaedicOperationPathwayExample example);

    int deleteByPrimaryKey(OrthopaedicCustOisOrthopaedicOperationPathwayKey key);

    int insert(OrthopaedicCustOisOrthopaedicOperationPathwayKey record);

    int insertSelective(OrthopaedicCustOisOrthopaedicOperationPathwayKey record);

    List<OrthopaedicCustOisOrthopaedicOperationPathwayKey> selectByExample(OrthopaedicCustOisOrthopaedicOperationPathwayExample example);

    int updateByExampleSelective(@Param("record") OrthopaedicCustOisOrthopaedicOperationPathwayKey record, @Param("example") OrthopaedicCustOisOrthopaedicOperationPathwayExample example);

    int updateByExample(@Param("record") OrthopaedicCustOisOrthopaedicOperationPathwayKey record, @Param("example") OrthopaedicCustOisOrthopaedicOperationPathwayExample example);
}