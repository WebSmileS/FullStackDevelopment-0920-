package com.yrt.project.modular.inventory.dto;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/30 16:26
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class ProductUniqueProperties {
    private Long orgId;
    private Long productId;
    private Long productVendorId;
    private Long specificationId;
    private Long warehouseId;
    private String batchNumber;

    private String productName;
    private String productVendorName;
    private String specificationName;
    private String warehouseName;

    /**
     *
     * @param column
     * @param value
     * @return
     */
    public QueryWrapper getProductUniqueQw(String column, Long value){
        QueryWrapper qw = new QueryWrapper();
        qw.eq("warehouse_inner_sn", this.getWarehouseId());
        qw.eq("product_inner_sn", this.getProductId());
        qw.eq("product_vendor_inner_sn", this.getProductVendorId());
        qw.eq("specification_inner_sn", this.getSpecificationId());
        qw.eq("batch_number",this.getBatchNumber()==null?"":this.getBatchNumber());
        qw.eq(column,value);
        return qw;
    }
}
