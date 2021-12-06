package com.yrt.project.modular.premium.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.project.api.business.warehouse.service.impl.InventoryServiceImpl;
import com.yrt.project.api.business.warehouse.vo.WarehouseSearchProductForm;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucherDetail;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.premium.domain.ProductPremiumPercent;
import com.yrt.project.modular.premium.service.ProductPremiumPercentService;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;
import com.yrt.project.modular.warehouse.domain.GrnDetail;
import com.yrt.project.modular.warehouse.domain.InventoryProduct;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yrt.project.modular.premium.domain.PremiumRule;
import com.yrt.project.modular.premium.mapper.PremiumRuleMapper;
import com.yrt.project.modular.premium.service.PremiumRuleService;
@Service
@RequiredArgsConstructor
public class PremiumRuleServiceImpl extends ServiceImpl<PremiumRuleMapper, PremiumRule> implements PremiumRuleService{

    private final ProductPremiumPercentService percentService;
    private final InventoryServiceImpl inventoryService;

    /**
     * 设置溢价比例和售价
     *
     * @param details
     */
    @Override
    public List<TransferVoucherDetail> setPercentAndSalesPrice1(List<TransferVoucherDetail> details) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        QueryWrapper<PremiumRule> queryWrapper = new QueryWrapper();
        queryWrapper.eq(PremiumRule.COL_ORG_ID, curUser.getOrgId());
        List<PremiumRule> premiumRules = this.list(queryWrapper);

        for (TransferVoucherDetail detail : details) {
            // 查询历史记录，优先返回历史记录
            BigDecimal unitPrice = detail.getUnit_price();
            try {
                QueryWrapper<ProductPremiumPercent> pqw = new QueryWrapper<>();
                pqw.eq(ProductPremiumPercent.COL_ORG_ID, curUser.getOrgId())
                        .eq(ProductPremiumPercent.COL_PRODUCT_ID, detail.getProduct_inner_sn())
                        .eq(ProductPremiumPercent.COL_PRODUCT_VENDOR_ID, detail.getProduct_vendor_inner_sn())
                        .eq(ProductPremiumPercent.COL_SPECIFICATION_ID, detail.getSpecification_inner_sn())
                        .eq(ProductPremiumPercent.COL_PRICE, unitPrice.setScale(4, BigDecimal.ROUND_HALF_UP))
                        .eq(ProductPremiumPercent.COL_UNIT_INNER_SN, detail.getUnit_inner_sn());
                ProductPremiumPercent percent = percentService.getOne(pqw);

                if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO)==0) {
                    // 单价为0，所以设置溢价比例为0，售价设置为历史售价
                    detail.setPercent(BigDecimal.ZERO);
                    detail.setSalesPrice(percent == null?BigDecimal.ZERO:percent.getSalesPrice());
                }else {
                    if (percent != null) {
                        // 如果有历史记录就设置溢价比例为历史设置
                        detail.setPercent(percent.getPercent());
                        detail.setSalesPrice(unitPrice.add(unitPrice.multiply(percent.getPercent().divide(new BigDecimal(100)))).setScale(4, BigDecimal.ROUND_HALF_UP));
                    } else {
                        boolean b = false;
                        // 如果没有历史记录就去查询溢价规则设置表
                        for (PremiumRule premiumRule : premiumRules) {
                            if (premiumRule.getMinPrice().compareTo(unitPrice) < 1 && premiumRule.getMaxPrice().compareTo(unitPrice) > -1) {
                                detail.setPercent(premiumRule.getPercent());
                                detail.setSalesPrice(unitPrice.add(unitPrice.multiply(premiumRule.getPercent().divide(new BigDecimal(100)))).setScale(4, BigDecimal.ROUND_HALF_UP));
                                b = true;
                                break;
                            }
                        }
                        // 如果溢价规则设置表也没有找到记录就设置售价为单价
                        if (!b) {
                            detail.setPercent(BigDecimal.ZERO);
                            detail.setSalesPrice(unitPrice);
                        }
                    }

                }
            }catch (Exception e){
                log.error("设置溢价比例和售价异常", e);
                detail.setPercent(BigDecimal.ZERO);
                detail.setSalesPrice(unitPrice);
            }
        }
        return details;
    }

    /**
     * 设置溢价比例和售价
     *
     * @param details
     */
    @Override
    public List<GrnDetail> setPercentAndSalesPrice2(List<GrnDetail> details) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        QueryWrapper<PremiumRule> queryWrapper = new QueryWrapper();
        queryWrapper.eq(PremiumRule.COL_ORG_ID, curUser.getOrgId());
        List<PremiumRule> premiumRules = this.list(queryWrapper);

        for (GrnDetail detail : details) {
            // 查询历史记录，优先返回历史记录
            BigDecimal unitPrice = detail.getUnit_price();
            try {
                QueryWrapper<ProductPremiumPercent> pqw = new QueryWrapper<>();
                pqw.eq(ProductPremiumPercent.COL_ORG_ID, curUser.getOrgId())
                        .eq(ProductPremiumPercent.COL_PRODUCT_ID, detail.getProduct_inner_sn())
                        .eq(ProductPremiumPercent.COL_PRODUCT_VENDOR_ID, detail.getVendor_inner_sn())
                        .eq(ProductPremiumPercent.COL_SPECIFICATION_ID, detail.getSpecification_inner_sn())
                        .eq(ProductPremiumPercent.COL_PRICE, unitPrice.setScale(4, BigDecimal.ROUND_HALF_UP))
                        .eq(ProductPremiumPercent.COL_UNIT_INNER_SN, detail.getUnit_inner_sn());
                ProductPremiumPercent percent = percentService.getOne(pqw);

                if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO)==0) {
                    // 单价为0，所以设置溢价比例为0，售价设置为历史售价
                    detail.setPercent(BigDecimal.ZERO);
                    detail.setSalesPrice(percent == null?BigDecimal.ZERO:percent.getSalesPrice());
                }else {
                    if (percent != null) {
                        // 如果有历史记录就设置溢价比例为历史设置
                        detail.setPercent(percent.getPercent());
                        detail.setSalesPrice(unitPrice.add(unitPrice.multiply(percent.getPercent().divide(new BigDecimal(100)))).setScale(4, BigDecimal.ROUND_HALF_UP));
                    } else {
                        boolean b = false;
                        // 如果没有历史记录就去查询溢价规则设置表
                        for (PremiumRule premiumRule : premiumRules) {
                            if (premiumRule.getMinPrice().compareTo(unitPrice) < 1 && premiumRule.getMaxPrice().compareTo(unitPrice) > -1) {
                                detail.setPercent(premiumRule.getPercent());
                                detail.setSalesPrice(unitPrice.add(unitPrice.multiply(premiumRule.getPercent().divide(new BigDecimal(100)))).setScale(4, BigDecimal.ROUND_HALF_UP));
                                b = true;
                                break;
                            }
                        }
                        // 如果溢价规则设置表也没有找到记录就设置售价为单价
                        if (!b) {
                            detail.setPercent(BigDecimal.ZERO);
                            detail.setSalesPrice(unitPrice);
                        }
                    }

                }
            }catch (Exception e){
                log.error("设置溢价比例和售价异常", e);
                detail.setPercent(BigDecimal.ZERO);
                detail.setSalesPrice(unitPrice);
            }
        }
        return details;
    }

    @Override
    public List<HospitalGrantVoucherDetail> setPercentAndSalesPrice4(List<HospitalGrantVoucherDetail> details) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        QueryWrapper<PremiumRule> queryWrapper = new QueryWrapper();
        queryWrapper.eq(PremiumRule.COL_ORG_ID, curUser.getOrgId());
        List<PremiumRule> premiumRules = this.list(queryWrapper);

        for (HospitalGrantVoucherDetail detail : details) {
            // 该表不存在单价，所以先查询单价
            WarehouseSearchProductForm form = new WarehouseSearchProductForm();
            form.setOrg_id(detail.getHospital_inner_sn());
            form.setProduct_name(detail.getProduct_name());
            form.setWarehouse_inner_sn(detail.getWarehouse_inner_sn());
            form.setSpecification(detail.getSpecification());
            form.setBatch_number(detail.getBatch_number());
            form.setProduct_vendor_inner_sn(detail.getVendor_inner_sn());
            form.setProduct_dealer_inner_sn(detail.getDetail_inner_sn());
            form.setProduct_inner_sn(detail.getProduct_inner_sn());
            form.setSpecification_inner_sn(detail.getSpecification_inner_sn());
            List<InventoryProduct> products = inventoryService.selectInventoryByLevel(3, form, curUser.getSystemType(), Arrays.asList(detail.getWarehouse_inner_sn()));
            BigDecimal unitPrice;
            if (products!=null && !products.isEmpty()){
                unitPrice = products.get(0).getUnit_price();
            }else{
                unitPrice = BigDecimal.ZERO;
            }

            // 查询历史记录，优先返回历史记录
            try {
                QueryWrapper<ProductPremiumPercent> pqw = new QueryWrapper<>();
                pqw.eq(ProductPremiumPercent.COL_ORG_ID, curUser.getOrgId())
                        .eq(ProductPremiumPercent.COL_PRODUCT_ID, detail.getProduct_inner_sn())
                        .eq(ProductPremiumPercent.COL_PRODUCT_VENDOR_ID, detail.getVendor_inner_sn())
                        .eq(ProductPremiumPercent.COL_SPECIFICATION_ID, detail.getSpecification_inner_sn())
                        .eq(ProductPremiumPercent.COL_PRICE, unitPrice.setScale(4, BigDecimal.ROUND_HALF_UP))
                        .eq(ProductPremiumPercent.COL_UNIT_INNER_SN, detail.getUnit_inner_sn());
                ProductPremiumPercent percent = percentService.getOne(pqw);

                if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO)==0) {
                    // 单价为0，所以设置溢价比例为0，售价设置为历史售价
                    detail.setPercent(BigDecimal.ZERO);
                    detail.setSalesPrice(percent == null?BigDecimal.ZERO:percent.getSalesPrice());
                }else {
                    if (percent != null) {
                        // 如果有历史记录就设置溢价比例为历史设置
                        detail.setPercent(percent.getPercent());
                        detail.setSalesPrice(unitPrice.add(unitPrice.multiply(percent.getPercent().divide(new BigDecimal(100)))).setScale(4, BigDecimal.ROUND_HALF_UP));
                    } else {
                        // 如果没有历史记录就去查询溢价规则设置表
                        boolean b = false;
                        for (PremiumRule premiumRule : premiumRules) {
                            if (premiumRule.getMinPrice().compareTo(unitPrice) < 1 && premiumRule.getMaxPrice().compareTo(unitPrice) > -1) {
                                detail.setPercent(premiumRule.getPercent());
                                detail.setSalesPrice(unitPrice.add(unitPrice.multiply(premiumRule.getPercent().divide(new BigDecimal(100)))).setScale(4, BigDecimal.ROUND_HALF_UP));
                                b = true;
                                break;
                            }
                        }
                        // 如果溢价规则设置表也没有找到记录就设置售价为单价
                        if (!b) {
                            detail.setPercent(BigDecimal.ZERO);
                            detail.setSalesPrice(unitPrice);
                        }
                    }

                }
            }catch (Exception e){
                log.error("设置溢价比例和售价异常", e);
                detail.setPercent(BigDecimal.ZERO);
                detail.setSalesPrice(unitPrice);
            }
        }
        return details;
    }

}
