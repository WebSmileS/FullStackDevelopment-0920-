/**
 * 
 */
package com.yrt.project.api.external.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.project.api.external.product.vo.ExternalSearchProductForm;
import com.yrt.project.modular.external.domain.ExternalProductInfo;
import com.yrt.project.modular.external.domain.Org4DropDown;
import com.yrt.project.modular.external.mapper.ExternalProductMapper;

/**
 * @author Runner
 *
 */
@Service
public class ExternalProductServiceImpl {
	
	@Autowired ExternalProductMapper mapper;
	
	public List<ExternalProductInfo> getVendorProductList(Long oid, ExternalSearchProductForm form){
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("product_name", form.getProduct_name());
		params.put("vendor_name", form.getVendor_name());
		params.put("specification", form.getSpecification());
		params.put("begin_date", form.getBegin_date());
		if(form.getOnlyContrctProduct()) {
			if(StringUtils.isNull(form.getOrganization_inner_sn())) {
				throw new RuntimeException("查合同内产品时，相关机构ID必填");
			}
			params.put("organization_inner_sn", form.getOrganization_inner_sn());
			return mapper.selectContractVendorProductList(params);
		}else {
			return mapper.selectVendorProductList(params);
		}
	}

	public List<Org4DropDown> selectRelatedOrg4DropDownList(long orgId, String name) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", SqlUtil.escapeLikeValue(name));
		return mapper.selectRelatedOrg4DropDownList(params);
	}

}
