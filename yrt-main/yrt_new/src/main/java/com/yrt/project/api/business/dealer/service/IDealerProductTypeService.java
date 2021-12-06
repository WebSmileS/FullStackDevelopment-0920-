package com.yrt.project.api.business.dealer.service;

import java.util.List;

import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductTypeForm;
import com.yrt.project.api.common.vo.EditProductType;
import com.yrt.project.modular.product.domain.DealerProductType;

public interface IDealerProductTypeService {

	public List<DealerProductType> selectTopLevelProductType(Long did);
	
	public DealerProductType selectProductType(Long oid, Long tid);
	
	public List<DealerProductType> selectProductTypeChildrenList(Long oid, Long tid);
	
	public DealerProductType insertDealerProductType(Long oid, AddDealerProductTypeForm form);
	
	public int updateProductType(Long oid, Long tid, EditProductType pType);
	
	public int deleteProductType(Long oid, Long tid);
	
	public void selectParent(DealerProductType type, Long oid, Long ptid);
	
	public List<DealerProductType> selectAllProductType(Long oid);
}
