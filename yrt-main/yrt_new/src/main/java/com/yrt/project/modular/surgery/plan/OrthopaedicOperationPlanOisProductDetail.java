package com.yrt.project.modular.surgery.plan;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class OrthopaedicOperationPlanOisProductDetail {

	@ApiModelProperty("耗材明细id(新增时不填)")
	private Long id;

	@ApiModelProperty("所属手术包实例id(新增时不填)")
	private Long ois_ins_id;

	@ApiModelProperty("所属计划id(新增时不填)")
	private Long plan_id;

	@NotNull(message = "厂商inner_sn为空")
	@ApiModelProperty("厂商inner_sn")
	private Integer product_vendor_inner_sn;

	@NotNull(message = "产品inner_sn为空")
	@ApiModelProperty("产品inner_sn")
	private Integer product_inner_sn;

	@NotNull(message = "规格型号inner_sn为空")
	@ApiModelProperty("规格型号inner_sn")
	private Short specification_inner_sn;

	@NotNull(message = "包装单位inner_sn为空")
	@ApiModelProperty("包装单位inner_sn")
	private Short unit_inner_sn;

	@NotNull(message = "数量为空")
	@ApiModelProperty("数量")
	private BigDecimal quantity;

	@ApiModelProperty("单价(待结算时填)")
	private BigDecimal unit_price = BigDecimal.ZERO;

	@NotNull(message = "序号为空")
	@ApiModelProperty("序号")
	private Short sort_number;

	@NotNull(message = "厂商名称为空")
	@ApiModelProperty("厂商名称")
	private String product_vendor_name;

	@NotNull(message = "产品名称为空")
	@ApiModelProperty("产品名称")
	private String product_name;

	@NotNull(message = "规格型号为空")
	@ApiModelProperty("规格型号名称")
	private String specification;

	@NotNull(message = "包装单位为空")
	@ApiModelProperty("包装单位名称")
	private String unit;

	@ApiModelProperty("回库数量(回库时填)")
	private BigDecimal back_quantity;

	@ApiModelProperty("销售数量(确认销售时填)")
	private BigDecimal sale_quantity;

	@ApiModelProperty("报损数量(确认销售时填): quantity = back_quantity + sale_quantity + loss_quantity")
	private BigDecimal loss_quantity;

	@ApiModelProperty("报损原因(确认销售时填)")
	private String loss_reason;

	@ApiModelProperty("批次号(回库时填)")
	private String batch_number;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty("生产日期(回库时填)")
	@Getter
	@Setter
	private Date produce_date;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty("过期日期(回库时填)")
	@Getter
	@Setter
	private Date overdue_date;

	@ApiModelProperty("编号")
	@Getter
	@Setter
	private String specification_vendor_sn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOis_ins_id() {
		return ois_ins_id;
	}

	public void setOis_ins_id(Long ois_ins_id) {
		this.ois_ins_id = ois_ins_id;
	}

	public Long getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(Long plan_id) {
		this.plan_id = plan_id;
	}

	public Integer getProduct_vendor_inner_sn() {
		return product_vendor_inner_sn;
	}

	public void setProduct_vendor_inner_sn(Integer product_vendor_inner_sn) {
		this.product_vendor_inner_sn = product_vendor_inner_sn;
	}

	public Integer getProduct_inner_sn() {
		return product_inner_sn;
	}

	public void setProduct_inner_sn(Integer product_inner_sn) {
		this.product_inner_sn = product_inner_sn;
	}

	public Short getSpecification_inner_sn() {
		return specification_inner_sn;
	}

	public void setSpecification_inner_sn(Short specification_inner_sn) {
		this.specification_inner_sn = specification_inner_sn;
	}

	public Short getUnit_inner_sn() {
		return unit_inner_sn;
	}

	public void setUnit_inner_sn(Short unit_inner_sn) {
		this.unit_inner_sn = unit_inner_sn;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(BigDecimal unit_price) {
		this.unit_price = unit_price;
	}

	public Short getSort_number() {
		return sort_number;
	}

	public void setSort_number(Short sort_number) {
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

	public BigDecimal getBack_quantity() {
		return back_quantity;
	}

	public void setBack_quantity(BigDecimal back_quantity) {
		this.back_quantity = back_quantity;
	}

	public BigDecimal getSale_quantity() {
		return sale_quantity;
	}

	public void setSale_quantity(BigDecimal sale_quantity) {
		this.sale_quantity = sale_quantity;
	}

	public BigDecimal getLoss_quantity() {
		return loss_quantity;
	}

	public void setLoss_quantity(BigDecimal loss_quantity) {
		this.loss_quantity = loss_quantity;
	}

	public String getLoss_reason() {
		return loss_reason;
	}

	public void setLoss_reason(String loss_reason) {
		this.loss_reason = loss_reason == null ? null : loss_reason.trim();
	}

	public String getBatch_number() {
		return batch_number;
	}

	public void setBatch_number(String batch_number) {
		this.batch_number = batch_number;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", id=").append(id);
		sb.append(", ois_ins_id=").append(ois_ins_id);
		sb.append(", plan_id=").append(plan_id);
		sb.append(", product_vendor_inner_sn=").append(product_vendor_inner_sn);
		sb.append(", product_inner_sn=").append(product_inner_sn);
		sb.append(", specification_inner_sn=").append(specification_inner_sn);
		sb.append(", unit_inner_sn=").append(unit_inner_sn);
		sb.append(", quantity=").append(quantity);
		sb.append(", unit_price=").append(unit_price);
		sb.append(", sort_number=").append(sort_number);
		sb.append(", product_vendor_name=").append(product_vendor_name);
		sb.append(", product_name=").append(product_name);
		sb.append(", specification=").append(specification);
		sb.append(", unit=").append(unit);
		sb.append(", back_quantity=").append(back_quantity);
		sb.append(", sale_quantity=").append(sale_quantity);
		sb.append(", loss_quantity=").append(loss_quantity);
		sb.append(", loss_reason=").append(loss_reason);
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
		OrthopaedicOperationPlanOisProductDetail other = (OrthopaedicOperationPlanOisProductDetail) that;
		return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
				&& (this.getOis_ins_id() == null ? other.getOis_ins_id() == null
						: this.getOis_ins_id().equals(other.getOis_ins_id()))
				&& (this.getPlan_id() == null ? other.getPlan_id() == null
						: this.getPlan_id().equals(other.getPlan_id()))
				&& (this.getProduct_vendor_inner_sn() == null ? other.getProduct_vendor_inner_sn() == null
						: this.getProduct_vendor_inner_sn().equals(other.getProduct_vendor_inner_sn()))
				&& (this.getProduct_inner_sn() == null ? other.getProduct_inner_sn() == null
						: this.getProduct_inner_sn().equals(other.getProduct_inner_sn()))
				&& (this.getSpecification_inner_sn() == null ? other.getSpecification_inner_sn() == null
						: this.getSpecification_inner_sn().equals(other.getSpecification_inner_sn()))
				&& (this.getUnit_inner_sn() == null ? other.getUnit_inner_sn() == null
						: this.getUnit_inner_sn().equals(other.getUnit_inner_sn()))
				&& (this.getQuantity() == null ? other.getQuantity() == null
						: this.getQuantity().equals(other.getQuantity()))
				&& (this.getUnit_price() == null ? other.getUnit_price() == null
						: this.getUnit_price().equals(other.getUnit_price()))
				&& (this.getSort_number() == null ? other.getSort_number() == null
						: this.getSort_number().equals(other.getSort_number()))
				&& (this.getProduct_vendor_name() == null ? other.getProduct_vendor_name() == null
						: this.getProduct_vendor_name().equals(other.getProduct_vendor_name()))
				&& (this.getProduct_name() == null ? other.getProduct_name() == null
						: this.getProduct_name().equals(other.getProduct_name()))
				&& (this.getSpecification() == null ? other.getSpecification() == null
						: this.getSpecification().equals(other.getSpecification()))
				&& (this.getUnit() == null ? other.getUnit() == null : this.getUnit().equals(other.getUnit()))
				&& (this.getBack_quantity() == null ? other.getBack_quantity() == null
						: this.getBack_quantity().equals(other.getBack_quantity()))
				&& (this.getSale_quantity() == null ? other.getSale_quantity() == null
						: this.getSale_quantity().equals(other.getSale_quantity()))
				&& (this.getLoss_quantity() == null ? other.getLoss_quantity() == null
						: this.getLoss_quantity().equals(other.getLoss_quantity()))
				&& (this.getLoss_reason() == null ? other.getLoss_reason() == null
						: this.getLoss_reason().equals(other.getLoss_reason()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result + ((getOis_ins_id() == null) ? 0 : getOis_ins_id().hashCode());
		result = prime * result + ((getPlan_id() == null) ? 0 : getPlan_id().hashCode());
		result = prime * result
				+ ((getProduct_vendor_inner_sn() == null) ? 0 : getProduct_vendor_inner_sn().hashCode());
		result = prime * result + ((getProduct_inner_sn() == null) ? 0 : getProduct_inner_sn().hashCode());
		result = prime * result + ((getSpecification_inner_sn() == null) ? 0 : getSpecification_inner_sn().hashCode());
		result = prime * result + ((getUnit_inner_sn() == null) ? 0 : getUnit_inner_sn().hashCode());
		result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
		result = prime * result + ((getUnit_price() == null) ? 0 : getUnit_price().hashCode());
		result = prime * result + ((getSort_number() == null) ? 0 : getSort_number().hashCode());
		result = prime * result + ((getProduct_vendor_name() == null) ? 0 : getProduct_vendor_name().hashCode());
		result = prime * result + ((getProduct_name() == null) ? 0 : getProduct_name().hashCode());
		result = prime * result + ((getSpecification() == null) ? 0 : getSpecification().hashCode());
		result = prime * result + ((getUnit() == null) ? 0 : getUnit().hashCode());
		result = prime * result + ((getBack_quantity() == null) ? 0 : getBack_quantity().hashCode());
		result = prime * result + ((getSale_quantity() == null) ? 0 : getSale_quantity().hashCode());
		result = prime * result + ((getLoss_quantity() == null) ? 0 : getLoss_quantity().hashCode());
		result = prime * result + ((getLoss_reason() == null) ? 0 : getLoss_reason().hashCode());
		return result;
	}
}
