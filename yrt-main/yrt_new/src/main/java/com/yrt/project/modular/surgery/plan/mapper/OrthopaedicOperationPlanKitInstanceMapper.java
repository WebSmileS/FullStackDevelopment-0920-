package com.yrt.project.modular.surgery.plan.mapper;

import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanKitInstance;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanKitInstanceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrthopaedicOperationPlanKitInstanceMapper {
    long countByExample(OrthopaedicOperationPlanKitInstanceExample example);

    int deleteByExample(OrthopaedicOperationPlanKitInstanceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrthopaedicOperationPlanKitInstance record);

    int insertSelective(OrthopaedicOperationPlanKitInstance record);

    List<OrthopaedicOperationPlanKitInstance> selectByExample(OrthopaedicOperationPlanKitInstanceExample example);

    OrthopaedicOperationPlanKitInstance selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrthopaedicOperationPlanKitInstance record, @Param("example") OrthopaedicOperationPlanKitInstanceExample example);

    int updateByExample(@Param("record") OrthopaedicOperationPlanKitInstance record, @Param("example") OrthopaedicOperationPlanKitInstanceExample example);

    int updateByPrimaryKeySelective(OrthopaedicOperationPlanKitInstance record);

    int updateByPrimaryKey(OrthopaedicOperationPlanKitInstance record);
}