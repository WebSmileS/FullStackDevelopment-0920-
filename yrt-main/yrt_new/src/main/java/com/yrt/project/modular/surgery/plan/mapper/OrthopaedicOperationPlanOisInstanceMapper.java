package com.yrt.project.modular.surgery.plan.mapper;

import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisInstance;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisInstanceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrthopaedicOperationPlanOisInstanceMapper {
    long countByExample(OrthopaedicOperationPlanOisInstanceExample example);

    int deleteByExample(OrthopaedicOperationPlanOisInstanceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrthopaedicOperationPlanOisInstance record);

    int insertSelective(OrthopaedicOperationPlanOisInstance record);

    List<OrthopaedicOperationPlanOisInstance> selectByExample(OrthopaedicOperationPlanOisInstanceExample example);

    OrthopaedicOperationPlanOisInstance selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrthopaedicOperationPlanOisInstance record, @Param("example") OrthopaedicOperationPlanOisInstanceExample example);

    int updateByExample(@Param("record") OrthopaedicOperationPlanOisInstance record, @Param("example") OrthopaedicOperationPlanOisInstanceExample example);

    int updateByPrimaryKeySelective(OrthopaedicOperationPlanOisInstance record);

    int updateByPrimaryKey(OrthopaedicOperationPlanOisInstance record);
}