package com.yrt.project.modular.premium.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.project.api.business.warehouse.vo.grn.GrnDetailForm;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yrt.project.modular.premium.domain.ProductPremiumPercent;
import com.yrt.project.modular.premium.mapper.ProductPremiumPercentMapper;
import com.yrt.project.modular.premium.service.ProductPremiumPercentService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class ProductPremiumPercentServiceImpl extends ServiceImpl<ProductPremiumPercentMapper, ProductPremiumPercent> implements ProductPremiumPercentService {

    @Override
    public void saveOrUpdateProductPremiumPercent(List<GrnDetailForm> forms) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();

        // 去重
        forms = forms.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(o ->
                o.getProduct_inner_sn() + ";" + o.getProduct_vendor_inner_sn()+ ";" +o.getSpecification_inner_sn()+ ";" +o.getUnit_price()+";" +o.getUnit_inner_sn()
        ))), ArrayList::new));

        // 插入溢价比例记录表
        List<ProductPremiumPercent> percents = forms.stream().map(e -> {
            QueryWrapper<ProductPremiumPercent> pqw = new QueryWrapper<>();
            pqw.eq(ProductPremiumPercent.COL_ORG_ID, curUser.getOrgId())
                    .eq(ProductPremiumPercent.COL_PRODUCT_ID, e.getProduct_inner_sn())
                    .eq(ProductPremiumPercent.COL_PRODUCT_VENDOR_ID, e.getProduct_vendor_inner_sn())
                    .eq(ProductPremiumPercent.COL_SPECIFICATION_ID, e.getSpecification_inner_sn())
                    .eq(ProductPremiumPercent.COL_PRICE, e.getUnit_price())
                    .eq(ProductPremiumPercent.COL_UNIT_INNER_SN, e.getUnit_inner_sn());
            ProductPremiumPercent percent = this.getOne(pqw);
            if (percent == null) {
                percent = new ProductPremiumPercent();
                percent.setProductId(e.getProduct_inner_sn());
                percent.setProductName(e.getProduct_name());
                percent.setSpecificationName(e.getSpecification());
                percent.setSpecificationId(e.getSpecification_inner_sn());
                percent.setProductVendorId(e.getProduct_vendor_inner_sn());
                percent.setProductVendorName(e.getProduct_vendor_name());
                percent.setBatchNumber(e.getBatch_number());
                percent.setPrice(e.getUnit_price());
                percent.setOrgId(curUser.getOrgId());
                percent.setUpdateUserId(curUser.getUid());
                percent.setUnitInnerSn(e.getUnit_inner_sn());
            }
            if (e.getUnit_price().compareTo(BigDecimal.ZERO)==0){
                percent.setPercent(BigDecimal.ZERO);
                percent.setSalesPrice(e.getSalesPrice());
            }else {
                percent.setPercent(e.getPercent());
                percent.setSalesPrice(e.getUnit_price().add(e.getUnit_price().multiply(e.getPercent()
                        .divide(new BigDecimal(100)))).setScale(4, BigDecimal.ROUND_HALF_UP));
            }
            return percent;
        }).collect(Collectors.toList());
        this.saveOrUpdateBatch(percents);
    }
}
