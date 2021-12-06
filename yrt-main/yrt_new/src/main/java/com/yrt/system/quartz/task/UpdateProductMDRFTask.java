package com.yrt.system.quartz.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yrt.common.utils.StringUtils;
import com.yrt.project.modular.product.domain.ResponseProductInfo4Dropdown;
import com.yrt.project.modular.product.mapper.ProductMapper;

@Component("MDRFTask")
public class UpdateProductMDRFTask {

	@Autowired private ProductMapper mapper;
	
	public void update() {
		List<ResponseProductInfo4Dropdown> list = mapper.selectVendorProductDropdown(new HashMap<String, Object>());
		for (ResponseProductInfo4Dropdown info : list) {
			Map<String, Object> params = new HashMap<>();
    		params.put("vendor_inner_sn", info.getVendor_inner_sn());
    		params.put("product_inner_sn", info.getProduct_inner_sn());
    		List<Map<String, Object>> snList = mapper.selectNewestRegister_sn(params);
    		if(StringUtils.isNotEmpty(snList)) {
    			String sn = snList.get(0).get("register_sn").toString();
    			params.put("cert_no", sn);
    			mapper.updateProduct4Map(params);
    		}else{
    			params.put("cert_no", "");
    			mapper.updateProduct4Map(params);
    		}
		}
	}
}
