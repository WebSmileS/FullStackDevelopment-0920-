package com.yrt.system.quartz.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductMDRF;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.system.quartz.domain.RepetitiveVendor;
import com.yrt.system.quartz.mapper.RepetitiveVendorMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class DeleteRepetitiveVendorTest {
	
	@Autowired private RepetitiveVendorMapper rvMapper;
	
	private List<RepetitiveVendor> data = null;
		
    @Before
    public void loadAllPendingRepetitiveDataTest() {
    	data = rvMapper.selectAllGoods();
    }

    @Test
    public void deleteRepetiviveDataTest() {
//    	data = rvMapper.selectAllGoods();
    	for (RepetitiveVendor rv : data) {
    		try {
	    		Map<String,Object> params = new HashMap<>();
	    		params.put("vendor_inner_sn", rv.getVendor_inner_sn());
	    		List<Product> productList = rvMapper.searchProductList(params);
	    		for (Product product : productList) {
	    			List<ProductSpecification> specList = rvMapper.selectProductSpecListById(params);
	    			for (ProductSpecification spec : specList) {
	    				//删除包装单位
	    				params.put("specification_inner_sn", spec.getSpecification_inner_sn());
	    				rvMapper.trueDeleteProductUnit(params);
	    				//删除规格型号
	    				rvMapper.trueDeleteProductSpec(params);
					}
	    			
					//删除产品图片
	    			params.put("product_inner_sn", product.getProduct_inner_sn());
	    			rvMapper.deleteProductImage(params);
	    			
	    			List<ProductMDRF> mdrfList = rvMapper.searchProductMDRFList(params);
	    			for (ProductMDRF mdrf : mdrfList) {
	    				//删除资证图片
	    				params.put("mdrf_inner_sn", mdrf.getMdrf_inner_sn());
	    				rvMapper.deleteMDRFImage(params);
	    				//删除资证
	    				rvMapper.deleteProductMDRF(params);
					}
	    			//删除产品
	    			rvMapper.trueDeleteProduct(params);
				}
	    		rvMapper.trueDeleteVendor(rv.getVendor_inner_sn());//删除厂商
    		}catch(Exception e) {
    			rv.setMsg(e.getMessage());
    			e.printStackTrace();
//    			continue;
    		}
    		Map<String,Object> params = new HashMap<>();
    		params.put("status", 1);
    		params.put("id", rv.getId());
    		params.put("msg", rv.getMsg());
			rvMapper.updateMsgAndStatus(params);
			System.err.println("-----------------------------------------------------------------------------------------------");
    	}
    }
}
