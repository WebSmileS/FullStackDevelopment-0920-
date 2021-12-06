package com.yrt.system.quartz.task;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.common.service.WarningService;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.warning.domain.AuthOrganization;
import com.yrt.project.modular.warning.domain.CertWarning;
import com.yrt.project.modular.warning.domain.ProductMdrfWarning;
import com.yrt.project.modular.warning.domain.ProductValidityWarning;
import com.yrt.project.modular.warning.domain.ValidContractOrg;
import com.yrt.project.modular.warning.domain.ValidContractProduct;
import com.yrt.project.modular.warning.domain.WarningSetupValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class WarningTaskTest {
	
	private static final Logger log = LoggerFactory.getLogger(WarningTaskTest.class);
	
	@Autowired WarningService service;
	@Autowired SysParameterService paramService;
	
	@Test
	public void scanWarning() {
		log.info("清除全部预警信息，准备重新扫描");
		log.info("清除完成，预警计划扫描开始");
		String value = paramService.getDefaultWarningSetup();
		WarningSetupValue setupValue = JSONObject.parseObject(value, WarningSetupValue.class);
		strat(setupValue);
		log.info("预警计划扫描结束");
	}
	
	public void strat(WarningSetupValue defaultSetup) {
		//搜索满足平台预警设置的产品注册证列表
//		List<ProductMdrfWarning> defaultMdrfList = service.getcheckExpireProductMdrfList(null, null, defaultSetup.getCertWarningSetup().getCew_day());
//		for (ProductMdrfWarning mdrf : defaultMdrfList) {
//			mdrf.setCreated_org_id(0L);
//			mdrf.setCreated_system_type(SystemType.PLATFORM);
//			service.insertProductMdrfWarning(mdrf);
//		}
		List<AuthOrganization> orgList = service.getAllAuthOrg();
		for (AuthOrganization org : orgList) {
			String value = paramService.getWarningSetup(org.getSystemType(), org.getOrgId());
			WarningSetupValue setupValue = JSONObject.parseObject(value, WarningSetupValue.class);
			log.info(String.format("启动 %s 资证预警扫描",org.getOrgName()));
			scanCertWarning(org, setupValue.getCertWarningSetup().getCew_day(), defaultSetup.getCertWarningSetup().getCew_day());
			log.info(String.format("启动 %s 产品注册证预警扫描",org.getOrgName()));
			scanProductMdrfWarning(org, setupValue.getProductMdrfWaringSetup().getCew_day());
			log.info(String.format("启动 %s 产品效期预警扫描",org.getOrgName()));
			scanProductValidityWarning(org, setupValue.getProductValidityWarningSetup().getCew_day());
		}
	}

	private void scanProductValidityWarning(AuthOrganization org, Integer cew_day) {
		List<ProductValidityWarning> validityList = service.getExpireInventoryProductValidityList(org.getOrgId(), org.getSystemType(), cew_day);
		for (ProductValidityWarning product : validityList) {
			product.setCreated_org_id(org.getOrgId());
			product.setCreated_system_type(org.getSystemType());
			service.insertProductValidityWarning(product);
		}
	}

	private void scanProductMdrfWarning(AuthOrganization org, Integer cew_day) {
		//搜索满足指定机构预警设置的有效合同内产品注册证列表
		List<ValidContractProduct> productList = service.getValidContractProductList(org.getOrgId(),
				org.getSystemType());//获取有效合同内产品列表
		for (ValidContractProduct product : productList) {
			List<ProductMdrfWarning> orgMdrfList = service.getcheckExpireProductMdrfList(product.getVendor_inner_sn(),
					product.getProduct_inner_sn(), cew_day);
			for (ProductMdrfWarning mdrf : orgMdrfList) {
				mdrf.setCreated_org_id(org.getOrgId());
				mdrf.setCreated_system_type(org.getSystemType());
				service.insertProductMdrfWarning(mdrf);
			}
		}
	}

	private void scanCertWarning(AuthOrganization org, Integer cew_day, Integer default_cew_day) {
		//搜索满足平台预警设置的机构资证列表
		List<CertWarning> certList = service.getCheckExpireCertList(org.getOrgId(), org.getSystemType(), default_cew_day);
		for (CertWarning cert : certList) {
			cert.setCreated_org_id(0L);
			cert.setCreated_system_type(SystemType.PLATFORM);
			service.insertCertWarning(cert);//保存平台查看部分
			CertWarning cert1 = Utils.clone(cert, CertWarning.class);
			cert1.setId(null);
			cert1.setCreated_org_id(org.getOrgId());
			cert1.setCreated_system_type(org.getSystemType());
			service.insertCertWarning(cert1);//保存当前机构查看部分
		}
		//搜索满足指定机构预警设置的有效合同内合作机构资证列表
		List<ValidContractOrg> orgList = service.getValidContractOrgList(org.getOrgId(), org.getSystemType());
		for (ValidContractOrg validOrg : orgList) {
			List<CertWarning> validOrgCertList = service.getCheckExpireCertList(validOrg.getOrgId(), validOrg.getSystemType(), cew_day);
			for (CertWarning cert : validOrgCertList) {
				cert.setCreated_org_id(org.getOrgId());
				cert.setCreated_system_type(org.getSystemType());
				service.insertCertWarning(cert);
			}
		}
	}
}
