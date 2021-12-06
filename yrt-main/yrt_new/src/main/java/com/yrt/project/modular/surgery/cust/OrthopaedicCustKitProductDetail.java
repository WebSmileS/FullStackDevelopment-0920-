package com.yrt.project.modular.surgery.cust;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModelProperty;

public class OrthopaedicCustKitProductDetail {
	@ApiModelProperty(value = "明细id")
    private Long id;

	@ApiModelProperty(value = "所属工具包id")
    private Long kit_id;

    @ApiModelProperty(value = "厂商inner_sn")
    @NotNull(message="product_vendor_inner_sn不能为空")
    private Long product_vendor_inner_sn;

    @ApiModelProperty(value = "产品inner_sn")
    @NotNull(message="product_inner_sn不能为空")
    private Long product_inner_sn;

    @ApiModelProperty(value = "规格inner_sn")
    @NotNull(message="specification_inner_sn不能为空")
    private Long specification_inner_sn;

    @ApiModelProperty(value = "包装单位inner_sn")
    @NotNull(message="unit_inner_sn不能为空")
    private Long unit_inner_sn;

    @ApiModelProperty(value = "数量")
    @Positive(message="quantity必须大于0")
    private BigDecimal quantity;

    @ApiModelProperty(value = "排序号")
    private Integer sort_number;

    @ApiModelProperty(value = "厂商名称")
    @NotNull
    private String product_vendor_name;

    @ApiModelProperty(value = "产品名称")
    @NotNull
    private String product_name;

    @ApiModelProperty(value = "规格型号名称")
    @NotNull
    private String specification;
    
    @ApiModelProperty(value = "包装单位名称")
    @NotNull
    private String unit;
    
    @ApiModelProperty(value = "规格型号编码(产品编码)")
    private String specification_vendor_sn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKit_id() {
        return kit_id;
    }

    public void setKit_id(Long kit_id) {
        this.kit_id = kit_id;
    }

    public Long getProduct_vendor_inner_sn() {
        return product_vendor_inner_sn;
    }

    public void setProduct_vendor_inner_sn(Long product_vendor_inner_sn) {
        this.product_vendor_inner_sn = product_vendor_inner_sn;
    }

    public Long getProduct_inner_sn() {
        return product_inner_sn;
    }

    public void setProduct_inner_sn(Long product_inner_sn) {
        this.product_inner_sn = product_inner_sn;
    }

    public Long getSpecification_inner_sn() {
        return specification_inner_sn;
    }

    public void setSpecification_inner_sn(Long specification_inner_sn) {
        this.specification_inner_sn = specification_inner_sn;
    }

    public Long getUnit_inner_sn() {
        return unit_inner_sn;
    }

    public void setUnit_inner_sn(Long unit_inner_sn) {
        this.unit_inner_sn = unit_inner_sn;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Integer getSort_number() {
        return sort_number;
    }

    public void setSort_number(Integer sort_number) {
        this.sort_number = sort_number;
    }

    public String getProduct_vendor_name() {
        return product_vendor_name;
    }

    public void setProduct_vendor_name(String product_vendor_name) {
        this.product_vendor_name = product_vendor_name == null ? null : product_vendor_name.trim();
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name == null ? null : product_name.trim();
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification == null ? null : specification.trim();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public String getSpecification_vendor_sn() {
		return specification_vendor_sn;
	}

	public void setSpecification_vendor_sn(String specification_vendor_sn) {
		this.specification_vendor_sn = specification_vendor_sn;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", kit_id=").append(kit_id);
        sb.append(", product_vendor_inner_sn=").append(product_vendor_inner_sn);
        sb.append(", product_inner_sn=").append(product_inner_sn);
        sb.append(", specification_inner_sn=").append(specification_inner_sn);
        sb.append(", unit_inner_sn=").append(unit_inner_sn);
        sb.append(", quantity=").append(quantity);
        sb.append(", sort_number=").append(sort_number);
        sb.append(", product_vendor_name=").append(product_vendor_name);
        sb.append(", product_name=").append(product_name);
        sb.append(", specification=").append(specification);
        sb.append(", unit=").append(unit);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        OrthopaedicCustKitProductDetail other = (OrthopaedicCustKitProductDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getKit_id() == null ? other.getKit_id() == null : this.getKit_id().equals(other.getKit_id()))
            && (this.getProduct_vendor_inner_sn() == null ? other.getProduct_vendor_inner_sn() == null : this.getProduct_vendor_inner_sn().equals(other.getProduct_vendor_inner_sn()))
            && (this.getProduct_inner_sn() == null ? other.getProduct_inner_sn() == null : this.getProduct_inner_sn().equals(other.getProduct_inner_sn()))
            && (this.getSpecification_inner_sn() == null ? other.getSpecification_inner_sn() == null : this.getSpecification_inner_sn().equals(other.getSpecification_inner_sn()))
            && (this.getUnit_inner_sn() == null ? other.getUnit_inner_sn() == null : this.getUnit_inner_sn().equals(other.getUnit_inner_sn()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getSort_number() == null ? other.getSort_number() == null : this.getSort_number().equals(other.getSort_number()))
            && (this.getProduct_vendor_name() == null ? other.getProduct_vendor_name() == null : this.getProduct_vendor_name().equals(other.getProduct_vendor_name()))
            && (this.getProduct_name() == null ? other.getProduct_name() == null : this.getProduct_name().equals(other.getProduct_name()))
            && (this.getSpecification() == null ? other.getSpecification() == null : this.getSpecification().equals(other.getSpecification()))
            && (this.getUnit() == null ? other.getUnit() == null : this.getUnit().equals(other.getUnit()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getKit_id() == null) ? 0 : getKit_id().hashCode());
        result = prime * result + ((getProduct_vendor_inner_sn() == null) ? 0 : getProduct_vendor_inner_sn().hashCode());
        result = prime * result + ((getProduct_inner_sn() == null) ? 0 : getProduct_inner_sn().hashCode());
        result = prime * result + ((getSpecification_inner_sn() == null) ? 0 : getSpecification_inner_sn().hashCode());
        result = prime * result + ((getUnit_inner_sn() == null) ? 0 : getUnit_inner_sn().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getSort_number() == null) ? 0 : getSort_number().hashCode());
        result = prime * result + ((getProduct_vendor_name() == null) ? 0 : getProduct_vendor_name().hashCode());
        result = prime * result + ((getProduct_name() == null) ? 0 : getProduct_name().hashCode());
        result = prime * result + ((getSpecification() == null) ? 0 : getSpecification().hashCode());
        result = prime * result + ((getUnit() == null) ? 0 : getUnit().hashCode());
        return result;
    }
}