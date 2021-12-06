package com.yrt.project.modular.surgery.plan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlan;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanExample;
import com.yrt.project.modular.surgery.plan.PlanSearchParam;
import com.yrt.project.modular.surgery.plan.PlanOisView;

public interface OrthopaedicOperationPlanMapper {
    long countByExample(OrthopaedicOperationPlanExample example);

    int deleteByExample(OrthopaedicOperationPlanExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrthopaedicOperationPlan record);

    int insertSelective(OrthopaedicOperationPlan record);

    List<OrthopaedicOperationPlan> selectByExample(OrthopaedicOperationPlanExample example);

    OrthopaedicOperationPlan selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrthopaedicOperationPlan record, @Param("example") OrthopaedicOperationPlanExample example);

    int updateByExample(@Param("record") OrthopaedicOperationPlan record, @Param("example") OrthopaedicOperationPlanExample example);

    int updateByPrimaryKeySelective(OrthopaedicOperationPlan record);

    int updateByPrimaryKey(OrthopaedicOperationPlan record);
    
    List<OrthopaedicOperationPlan> selectForHospital(PlanSearchParam param);
    List<OrthopaedicOperationPlan> selectForDealer(PlanSearchParam param);
    
    List<PlanOisView> selectOisView(PlanSearchParam param);
}