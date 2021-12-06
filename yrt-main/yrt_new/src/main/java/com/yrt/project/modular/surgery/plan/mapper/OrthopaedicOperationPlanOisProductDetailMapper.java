package com.yrt.project.modular.surgery.plan.mapper;

import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisProductDetail;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisProductDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrthopaedicOperationPlanOisProductDetailMapper {
    long countByExample(OrthopaedicOperationPlanOisProductDetailExample example);

    int deleteByExample(OrthopaedicOperationPlanOisProductDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrthopaedicOperationPlanOisProductDetail record);

    int insertSelective(OrthopaedicOperationPlanOisProductDetail record);

    List<OrthopaedicOperationPlanOisProductDetail> selectByExample(OrthopaedicOperationPlanOisProductDetailExample example);

    OrthopaedicOperationPlanOisProductDetail selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrthopaedicOperationPlanOisProductDetail record, @Param("example") OrthopaedicOperationPlanOisProductDetailExample example);

    int updateByExample(@Param("record") OrthopaedicOperationPlanOisProductDetail record, @Param("example") OrthopaedicOperationPlanOisProductDetailExample example);

    int updateByPrimaryKeySelective(OrthopaedicOperationPlanOisProductDetail record);

    int updateByPrimaryKey(OrthopaedicOperationPlanOisProductDetail record);
}