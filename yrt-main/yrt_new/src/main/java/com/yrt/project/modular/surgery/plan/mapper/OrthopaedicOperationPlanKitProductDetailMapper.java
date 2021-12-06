package com.yrt.project.modular.surgery.plan.mapper;

import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanKitProductDetail;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanKitProductDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrthopaedicOperationPlanKitProductDetailMapper {
    long countByExample(OrthopaedicOperationPlanKitProductDetailExample example);

    int deleteByExample(OrthopaedicOperationPlanKitProductDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrthopaedicOperationPlanKitProductDetail record);

    int insertSelective(OrthopaedicOperationPlanKitProductDetail record);

    List<OrthopaedicOperationPlanKitProductDetail> selectByExample(OrthopaedicOperationPlanKitProductDetailExample example);

    OrthopaedicOperationPlanKitProductDetail selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrthopaedicOperationPlanKitProductDetail record, @Param("example") OrthopaedicOperationPlanKitProductDetailExample example);

    int updateByExample(@Param("record") OrthopaedicOperationPlanKitProductDetail record, @Param("example") OrthopaedicOperationPlanKitProductDetailExample example);

    int updateByPrimaryKeySelective(OrthopaedicOperationPlanKitProductDetail record);

    int updateByPrimaryKey(OrthopaedicOperationPlanKitProductDetail record);
}