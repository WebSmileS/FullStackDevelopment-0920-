package com.yrt.project.modular.product.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.product.domain.DealerProductUnit;

@Repository
public interface DealerProductUnitMapper {
	
	public void p_add_dealer_product_unit(Map<String, Object> params);
	
	public int updateDealerProductUnit(DealerProductUnit unit);
	
	public int cleanCommonUse(DealerProductUnit unit);
	
	public int deleteProductUnit(DealerProductUnit unit);
	
	public DealerProductUnit selectDealerProductUnitInfo(DealerProductUnit unit);
	
	public List<DealerProductUnit> selectDealerProductUnitList(DealerProductUnit unit);

	public List<DealerProductUnit> selectDealerProductUnitListByPid(DealerProductUnit unit);
	
	public BigDecimal calculate_product_min_unit(Map<String, Object> params);
	public Long c_get_dealer_product_min_unit_inner_sn(Map<String, Object> params);
	public String c_get_dealer_product_min_unit(Map<String, Object> params);
}
