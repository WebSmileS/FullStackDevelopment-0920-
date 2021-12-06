package com.yrt.project.modular.product.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.product.domain.DealerProductType;

@Repository
public interface DealerProductTypeMapper {
	
	public List<DealerProductType> selectTopLevelProductType(Long did);
	
	public DealerProductType selectProductType(DealerProductType type);
	
	public List<DealerProductType> selectProductTypeChildrenList(DealerProductType type);
	
	public void d_add_dealer_product_type(Map<String,Object> params);
	
	public int updateProductType(DealerProductType type);
	
	public int deleteProductType(DealerProductType type);
}
