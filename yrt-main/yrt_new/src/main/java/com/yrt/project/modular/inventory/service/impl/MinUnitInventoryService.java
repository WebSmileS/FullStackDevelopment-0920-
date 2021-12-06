package com.yrt.project.modular.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.bean.BeanUtils;
import com.yrt.project.modular.inventory.domain.DealerProductMinUnitInventory;
import com.yrt.project.modular.inventory.domain.HospitalProductMinUnitInventory;
import com.yrt.project.modular.inventory.domain.MinUnitInventory;
import com.yrt.project.modular.inventory.domain.VendorProductMinUnitInventory;
import com.yrt.project.modular.inventory.dto.ProductUniqueProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/30 14:57
 * @description：最小单位库存
 * @modified By：
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class MinUnitInventoryService {

    private final HospitalProductMinUnitInventoryService hospitalService;
    private final DealerProductMinUnitInventoryService dealerService;
    private final VendorProductMinUnitInventoryService vendorService;

    /**
     * 查询单个商品
     *
     * @param systemType
     * @param condition
     * @return
     */
    public MinUnitInventory getMinUnitInventory(SystemType systemType, ProductUniqueProperties condition) {

        if (systemType.equals(SystemType.HOSPITAL)) {
            QueryWrapper qw = condition.getProductUniqueQw(HospitalProductMinUnitInventory.COL_HOSPITAL_INNER_SN, condition.getOrgId());
            HospitalProductMinUnitInventory hospitalProductMinUnitInventory = hospitalService.getOne(qw);
            if (hospitalProductMinUnitInventory == null){
                throw new RuntimeException(String.format("未找到产品[%s]，生产厂商[%s]，规格型号[%s]，仓库[%s]", condition.getProductName(),
                        condition.getProductVendorName(),condition.getSpecificationName(),condition.getWarehouseName()));
            }
            MinUnitInventory minUnitInventory = BeanUtils.clone(hospitalProductMinUnitInventory, MinUnitInventory.class);
            return minUnitInventory;
        } else if (systemType.equals(SystemType.VENDOR)) {
            QueryWrapper qw = condition.getProductUniqueQw(VendorProductMinUnitInventory.COL_VENDOR_INNER_SN, condition.getOrgId());
            VendorProductMinUnitInventory vendorProductMinUnitInventory = vendorService.getOne(qw);
            if (vendorProductMinUnitInventory == null){
                throw new RuntimeException(String.format("未找到产品[%s]，生产厂商[%s]，规格型号[%s]，仓库[%s]", condition.getProductName(),
                        condition.getProductVendorName(),condition.getSpecificationName(),condition.getWarehouseName()));
            }
            MinUnitInventory minUnitInventory = BeanUtils.clone(vendorProductMinUnitInventory, MinUnitInventory.class);
            return minUnitInventory;
        } else if (systemType.equals(SystemType.DEALER)) {
            QueryWrapper qw = condition.getProductUniqueQw(DealerProductMinUnitInventory.COL_DEALER_INNER_SN, condition.getOrgId());
            DealerProductMinUnitInventory dealerProductMinUnitInventory = dealerService.getOne(qw);
            if (dealerProductMinUnitInventory == null){
                throw new RuntimeException(String.format("未找到产品[%s]，生产厂商[%s]，规格型号[%s]，仓库[%s]", condition.getProductName(),
                        condition.getProductVendorName(),condition.getSpecificationName(),condition.getWarehouseName()));
            }
            MinUnitInventory minUnitInventory = BeanUtils.clone(dealerProductMinUnitInventory, MinUnitInventory.class);
            return minUnitInventory;
        } else {
            return null;
        }
    }

    /**
     * 库存数量变化方法
     *
     * @param systemType
     * @param condition
     * @param quantity   值有可能会正的有可能为负的，为负的就是减库存, 值为最小单位且需要用前端传输值*1000
     */
    public void changeQuantity(SystemType systemType, ProductUniqueProperties condition, Long quantity) {
        MinUnitInventory minUnitInventory = getMinUnitInventory(systemType, condition);
        if (minUnitInventory == null) {
            throw new RuntimeException("未找到该产品，产品可能已经被出库");
        }

        Long newQuantity = minUnitInventory.getQuantity() + quantity;

        // 判断库存数量是否够
        if (newQuantity < 0L) {
            // 数量不足
            String message = String.format("库存不足: %s>>%s>>%s, 批号：%s, 请求最小单位扣减量：%s，库存余量（最小单位）%s",
                    minUnitInventory.getProductName(), minUnitInventory.getSpecification(), minUnitInventory.getUnit(), minUnitInventory.getBatchNumber(),
                    quantity / 1000, minUnitInventory.getQuantity() / 1000);
            throw new RuntimeException(message);
        }

        if (systemType.equals(SystemType.HOSPITAL)) {
            QueryWrapper qw = condition.getProductUniqueQw(HospitalProductMinUnitInventory.COL_HOSPITAL_INNER_SN, condition.getOrgId());
            hospitalService.update(new HospitalProductMinUnitInventory().setQuantity(newQuantity), qw);
        } else if (systemType.equals(SystemType.VENDOR)) {
            QueryWrapper qw = condition.getProductUniqueQw(VendorProductMinUnitInventory.COL_VENDOR_INNER_SN, condition.getOrgId());
            vendorService.update(new VendorProductMinUnitInventory().setQuantity(newQuantity), qw);
        } else if (systemType.equals(SystemType.DEALER)) {
            QueryWrapper qw = condition.getProductUniqueQw(DealerProductMinUnitInventory.COL_DEALER_INNER_SN, condition.getOrgId());
            dealerService.update(new DealerProductMinUnitInventory().setQuantity(newQuantity), qw);
        }
    }

    /**
     * 库存数量和平均价变化方法
     *
     * @param systemType
     * @param condition
     * @param quantity  值有可能会正的有可能为负的，为负的就是减库存, 值为最小单位且需要用前端传输值*1000
     * @param unitPrice 值固定为正数，代表本次操作的产品的单价,值为前端传值*10000
     */
    public void changeQuantityAndAvgPrice(SystemType systemType, ProductUniqueProperties condition, Long quantity, Long unitPrice){
        MinUnitInventory minUnitInventory = getMinUnitInventory(systemType, condition);
        if (minUnitInventory == null) {
            throw new RuntimeException("未找到该产品，产品可能已经被出库");
        }

        Long newQuantity = minUnitInventory.getQuantity() + quantity;

        // 结果可能为正数或者负数，负数代表减
        Long inventorySumPrice = minUnitInventory.getAveragePrice() * minUnitInventory.getQuantity();
        Long reqSumPrice = unitPrice * quantity;
        Long newSumPrice = inventorySumPrice + reqSumPrice;

        // 判断库存数量是否够
        if (newQuantity < 0L) {
            // 数量不足
            String message = String.format("库存不足: %s>>%s>>%s, 批号：%s, 请求最小单位扣减量：%s，库存余量（最小单位）%s",
                    minUnitInventory.getProductName(), minUnitInventory.getSpecification(), minUnitInventory.getUnit(), minUnitInventory.getBatchNumber(),
                    quantity / 1000, minUnitInventory.getQuantity() / 1000);
            throw new RuntimeException(message);
        }

        // 判断总价是否大于现有库存总金额
        if (newSumPrice < 0L){
            String message = String.format("本次操作总金额大于库存总金额，本次操作总金额为：%s,库存总金额为：%s",inventorySumPrice/10000, reqSumPrice);
            throw new RuntimeException(message);
        }

        // 算平均价
        Long newAvgPrice = new BigDecimal(newSumPrice).divide(new BigDecimal(newQuantity)).setScale(0,BigDecimal.ROUND_HALF_UP).longValue();
        if (systemType.equals(SystemType.HOSPITAL)) {
            QueryWrapper qw = condition.getProductUniqueQw(HospitalProductMinUnitInventory.COL_HOSPITAL_INNER_SN, condition.getOrgId());
            hospitalService.update(new HospitalProductMinUnitInventory().setQuantity(newQuantity).setAveragePrice(newAvgPrice), qw);
        } else if (systemType.equals(SystemType.VENDOR)) {
            QueryWrapper qw = condition.getProductUniqueQw(VendorProductMinUnitInventory.COL_VENDOR_INNER_SN, condition.getOrgId());
            vendorService.update(new VendorProductMinUnitInventory().setQuantity(newQuantity).setAveragePrice(newAvgPrice), qw);
        } else if (systemType.equals(SystemType.DEALER)) {
            QueryWrapper qw = condition.getProductUniqueQw(DealerProductMinUnitInventory.COL_DEALER_INNER_SN, condition.getOrgId());
            dealerService.update(new DealerProductMinUnitInventory().setQuantity(newQuantity).setAveragePrice(newAvgPrice), qw);
        }

    }
}
