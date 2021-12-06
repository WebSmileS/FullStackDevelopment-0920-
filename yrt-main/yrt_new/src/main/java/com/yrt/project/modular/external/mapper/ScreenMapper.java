package com.yrt.project.modular.external.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.api.external.screen.dto.DeptInfo;
import com.yrt.project.api.external.screen.dto.OrgInfo;
import com.yrt.project.api.external.screen.dto.OrgTypeCount;
import com.yrt.project.api.external.screen.dto.OrgWarn;
import com.yrt.project.api.external.screen.dto.OrgWarnInfo;
import com.yrt.project.api.external.screen.dto.ProductTotal;
import com.yrt.project.api.external.screen.dto.ProductTrade;
import com.yrt.project.api.external.screen.dto.ProductTradeInfo;
import com.yrt.project.api.external.screen.dto.ProductTypeQuantity;
import com.yrt.project.api.external.screen.dto.ProductWarn;
import com.yrt.project.api.external.screen.dto.ProductWarnInfo;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductRelImage;

@Repository
public interface ScreenMapper {

    List<OrgTypeCount> searchOrgs();

    List<OrgWarn> searchOrgWarn();

    List<OrgWarnInfo> searchOrgWarnInfos();

    List<ProductWarn> searchProductWarn();

    List<ProductWarnInfo> searchProductWarnInfos();

    List<OrgWarnInfo> searchOrgWarnInfoPage(Map<String, Object> params);

    List<ProductWarnInfo> searchProductWarnInfoPage(Map<String, Object> params);

    List<OrgTypeCount> searchOrgsByAdministrative_division_sn(Long administrative_division_sn);

    List<Company> searchOrgsByTypeAndAdministrative_division_sn(int type, Long administrative_division_sn_city);

    BigDecimal searchGospitalDeptTotal();

    Product searchProduct(Map<String, Object> params);

    List<ProductRelImage> searchProductImages(Map<String, Object> params);

    List<OrgInfo> searchHospitalUpstream(Long oid);

    List<OrgInfo> searchVendorDownstream(Long oid);

    List<OrgInfo> searchDealerUpstream(Long oid);

    List<OrgInfo> searchDealerDownstream(Long oid);

    List<DeptInfo> searchDeptsLevel1(Long oid);

    List<ProductTypeQuantity> searchProductType();

    List<ProductTrade> searchTrade(Map<String,Object> times);

    List<ProductTradeInfo> searchTradeItems();

    List<ProductTradeInfo> searchVendorTradeItems(long orgId);

    List<ProductTradeInfo> searchDealerTradeItems(long orgId);

    List<ProductTradeInfo> searchHospitalTradeItems(long orgId);

    List<ProductTradeInfo> searchTradeItemsByAdSn(Long adSn);

    ProductTotal getProductTotal(long vd_inner_sn, long product_inner_sn);

    long getTradeDealerCount(long vd_inner_sn, long product_inner_sn);

    long getDeptCount(long vd_inner_sn, long product_inner_sn);

    String getImgUrl(long vd_inner_sn, long product_inner_sn);
}
