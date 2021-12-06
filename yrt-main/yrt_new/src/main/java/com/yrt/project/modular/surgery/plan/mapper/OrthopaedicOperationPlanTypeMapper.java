package com.yrt.project.modular.surgery.plan.mapper;

import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanTypeExample;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanTypeKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrthopaedicOperationPlanTypeMapper {
    long countByExample(OrthopaedicOperationPlanTypeExample example);

    int deleteByExample(OrthopaedicOperationPlanTypeExample example);

    int deleteByPrimaryKey(OrthopaedicOperationPlanTypeKey key);

    int insert(OrthopaedicOperationPlanTypeKey record);

    int insertSelective(OrthopaedicOperationPlanTypeKey record);

    List<OrthopaedicOperationPlanTypeKey> selectByExample(OrthopaedicOperationPlanTypeExample example);

    int updateByExampleSelective(@Param("record") OrthopaedicOperationPlanTypeKey record, @Param("example") OrthopaedicOperationPlanTypeExample example);

    int updateByExample(@Param("record") OrthopaedicOperationPlanTypeKey record, @Param("example") OrthopaedicOperationPlanTypeExample example);
}