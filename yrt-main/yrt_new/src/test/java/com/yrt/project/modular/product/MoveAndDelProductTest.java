package com.yrt.project.modular.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.platform.service.IProductService;
import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductMDRF;
import com.yrt.project.modular.product.domain.ProductMDRFImage;
import com.yrt.project.modular.product.domain.ProductRelImage;
import com.yrt.project.modular.product.mapper.ProductMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MoveAndDelProductTest {

	@Autowired private ProductMapper mapper;
	@Autowired private IProductService productService;
	
	//移动指定厂商下的所有产品到另外一个指定厂商下
	@Test
	public void move() throws Exception {
		Long sVid = 14501L;
		Long tVid = 1867L;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", sVid);
		List<Product> pList = mapper.searchProductList(params);
		for (Product product : pList) {
			Long tPid = insertProduct(product, tVid);
			List<ProductRelImage> imageList = productService.selectCertImageList(product.getVendor_inner_sn(), product.getProduct_inner_sn(), 1);
			for (ProductRelImage image : imageList) {
				insertProductRelImage(image, tVid, tPid);
			}
			//复制注册证到新产品，删除旧产品注册证
			handleMDRF(product.getVendor_inner_sn(), product.getProduct_inner_sn(), tVid, tPid);
			
			//复制规格型号到新产品，删除旧产品规格型号
			handleSpecification(product.getVendor_inner_sn(), product.getProduct_inner_sn(), tVid, tPid);
			
			//删除旧产品图片
			delProductImage(sVid, product.getVendor_inner_sn());
		}
		//删除厂商下所有产品信息
		Map<String, Object> params1 = new HashMap<>();
		params1.put("vendor_inner_sn", tVid);
		mapper.trueDeleteProduct(params1);
	}
	
	@Transactional
	private void handleSpecification(Long sVid, Long sPid, Long tVid, Long tPid) {
//		List<ProductSpecification> old_specList = productService.selectProductSpecListById(sVid, sPid);
//		for (ProductSpecification old_spec : old_specList) {
//			
//		}
		//删除原产品下的所有规格型号
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn", tVid);
		params.put("product_inner_sn", tPid);
		mapper.trueDeleteProductSpec(params);
	}
	
	@Transactional
	private void handleMDRF(Long sVid, Long sPid, Long tVid, Long tPid) {
		List<ProductMDRF> mdrfList = productService.searchProductMDRFList(sVid, sPid, null);
		for (ProductMDRF old_mdrf : mdrfList) {
			Map<String, Object> params = new HashMap<>();
			params.put("vendor_inner_sn_i", tVid);
			params.put("product_inner_sn_i", tPid);
			params.put("issue_date_i", old_mdrf.getIssue_date());
			params.put("begin_date_i", old_mdrf.getBegin_date());
			params.put("end_date_i", old_mdrf.getEnd_date());
			params.put("register_sn_i", old_mdrf.getRegister_sn());
			params.put("issue_organization_i", old_mdrf.getIssue_organization());
			params.put("standard_i", old_mdrf.getStandard());
			params.put("production_address_i", old_mdrf.getProduction_address());
			params.put("performance_structure_i", old_mdrf.getPerformance_structure());
			params.put("application_range_i", old_mdrf.getApplication_range());
			params.put("contraindication_i", old_mdrf.getContraindication());
			params.put("description_i", old_mdrf.getDescription());
			params.put("specifications_i", old_mdrf.getSpecifications());
			mapper.p_add_product_mdrf(params);
			if(Integer.valueOf(params.get("result_o").toString()) == 0) {
				Long pmid = Long.valueOf(params.get("mdrf_inner_sn_o").toString());
				//复制旧注册证图片到新产品注册证下
				List<ProductMDRFImage> old_mdrf_images = productService.selectProductMDRFImageList(sVid, sPid, old_mdrf.getMdrf_inner_sn());
				for (ProductMDRFImage old_image : old_mdrf_images) {
					Map<String, Object> params1 = new HashMap<>();
					params1.put("vendor_inner_sn_i", tVid);
					params1.put("product_inner_sn_i", tPid);
					params1.put("mdrf_inner_sn_i", pmid);
					params1.put("url_i", old_image.getUrl());
					mapper.p_add_product_mdrf_image(params1);
				}
				//删除旧注册证图片
				ProductMDRFImage info = new ProductMDRFImage();
				info.setVendor_inner_sn(sVid);
				info.setProduct_inner_sn(sPid);
				info.setMdrf_inner_sn(old_mdrf.getMdrf_inner_sn());
				mapper.deleteMDRFImage(info);
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","产品注册证登记表",params.get("message_string_o")));
			}
		}
		//删除注册证
		Map<String, Object> params1 = new HashMap<>();
		params1.put("vendor_inner_sn", tVid);
		params1.put("product_inner_sn", tPid);
		mapper.deleteProductMDRF(params1);
	}
	
	@Transactional
	public Long insertProduct(Product product, Long vid) throws DBException, DuplicateException {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", vid);
		params.put("release_employee_inner_sn_i", null);
		params.put("type_inner_sn_i", product.getType_inner_sn());
		params.put("code68_sn_i", product.getCode68_sn());
		params.put("data_source_i", 0);
		params.put("storage_condition_i", product.getStorage_condition());
		params.put("health_care_type_i", product.getHealth_care_type());
		params.put("name_i", product.getName().trim());
		params.put("product_vendor_sn_i", null);
		params.put("description_i", product.getDescription());
		params.put("specifications_i", product.getSpecifications());
		params.put("type_i", product.getType());
		params.put("code68_type_i", product.getCode68_type());
		mapper.p_add_product(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			return Long.valueOf(params.get("product_inner_sn_o").toString());
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","产品",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","产品",params.get("message_string_o")));
			}
		}
	}
	
	@Transactional
	public void insertProductRelImage(ProductRelImage form, Long vid, Long pid) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("type_i", form.getType());
		if(StringUtils.isNotNull(form.getBegin_date())) {
			params.put("begin_date_i", form.getBegin_date());
		}
		if(StringUtils.isNotNull(form.getEnd_date())) {
			params.put("end_date_i", form.getEnd_date());
		}
		params.put("url_i", form.getUrl());
		mapper.p_add_product_image(params);
		if(Integer.valueOf(params.get("result_o").toString()) != 0) {
			throw new DBException(MessageUtils.message("db.add.fail","产品",params.get("message_string_o")));
		}
	}
	
	@Transactional
	public int delProductImage(Long vid, Long pid) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		return mapper.deleteProductImage(params);
	}
}
