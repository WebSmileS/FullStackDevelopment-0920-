package com.yrt.project.modular.surgery.plan;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.yrt.project.modular.surgery.OisInstanceStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class OrthopaedicOperationPlanOisInstance {
	
	@ApiModelProperty("手术包实例id(新增时不填)")
    private Long id;

	@ApiModelProperty("计划id(新增时不填)")
    private Long plan_id;

	@NotNull
	@ApiModelProperty("源平台手术包id")
    private Long platform_ois_inner_sn;

	@ApiModelProperty("源定制手术包id(非定制不填)")
    private Long cust_ois_id;

	@ApiModelProperty("经销商id(发布前必填)")
    private Long dealer_inner_sn;
	
	@ApiModelProperty("经销商名称(发布前必填)")
	private String dealer_name;

	@ApiModelProperty("状态(初始=INIT)")
    private OisInstanceStatus status;

	@NotNull
	@ApiModelProperty("手术类型id")
    private Byte type_inner_sn;

	@NotNull
	@ApiModelProperty("品牌id")
    private Short brand_inner_sn;

	@NotNull
	@ApiModelProperty("编号")
    private String ois_sn;

	@NotNull
	@ApiModelProperty("名称")
    private String name;

	@NotNull
	@ApiModelProperty("描述")
    private String description;

	
	@ApiModelProperty("关键字")
    private String keywords;

	@ApiModelProperty("骨科助理id(发布时填)")
    private Long assistant_employee_inner_sn;

	@ApiModelProperty("骨科助理姓名")
    private String assistant_employee_name;

	@ApiModelProperty("出库人id(出库时填)")
    private Long out_employee_inner_sn;

	@ApiModelProperty("出库人姓名")
    private String out_employee_name;
	
	@ApiModelProperty("出库时间")
    private Date out_time;
	
	@ApiModelProperty("出库仓库")
	private Long out_warehouse_inner_sn;
	
	@ApiModelProperty("出库仓库名称")
	private String out_warehouse_name;
	
	@ApiModelProperty("回库人id(回库时填)")
    private Long back_employee_inner_sn;
	
	@ApiModelProperty("回库人姓名")
    private String back_employee_name;

	@ApiModelProperty("回库时间")
    private Date back_time;

	@ApiModelProperty("创建时间")
    private Date created_time;

	@ApiModelProperty("最后更新时间")
    private Date last_modified_time;
	
	@ApiModelProperty("手术类别名")
	@Getter @Setter
	private String type_name;
	
	@ApiModelProperty("品牌名称")
	@Getter @Setter
	private String brand_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(Long plan_id) {
        this.plan_id = plan_id;
    }

    public Long getPlatform_ois_inner_sn() {
        return platform_ois_inner_sn;
    }

    public void setPlatform_ois_inner_sn(Long platform_ois_inner_sn) {
        this.platform_ois_inner_sn = platform_ois_inner_sn;
    }

    public Long getCust_ois_id() {
        return cust_ois_id;
    }

    public void setCust_ois_id(Long cust_ois_id) {
        this.cust_ois_id = cust_ois_id;
    }

    public Long getDealer_inner_sn() {
        return dealer_inner_sn;
    }

    public void setDealer_inner_sn(Long dealer_inner_sn) {
        this.dealer_inner_sn = dealer_inner_sn;
    }

    public OisInstanceStatus getStatus() {
        return status;
    }

    public void setStatus(OisInstanceStatus status) {
        this.status = status;
    }

    public Byte getType_inner_sn() {
        return type_inner_sn;
    }

    public void setType_inner_sn(Byte type_inner_sn) {
        this.type_inner_sn = type_inner_sn;
    }

    public Short getBrand_inner_sn() {
        return brand_inner_sn;
    }

    public void setBrand_inner_sn(Short brand_inner_sn) {
        this.brand_inner_sn = brand_inner_sn;
    }

    public String getOis_sn() {
        return ois_sn;
    }

    public void setOis_sn(String ois_sn) {
        this.ois_sn = ois_sn == null ? null : ois_sn.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords == null ? null : keywords.trim();
    }

    public Long getAssistant_employee_inner_sn() {
        return assistant_employee_inner_sn;
    }

    public void setAssistant_employee_inner_sn(Long assistant_employee_inner_sn) {
        this.assistant_employee_inner_sn = assistant_employee_inner_sn;
    }

    public String getAssistant_employee_name() {
        return assistant_employee_name;
    }

    public void setAssistant_employee_name(String assistant_employee_name) {
        this.assistant_employee_name = assistant_employee_name == null ? null : assistant_employee_name.trim();
    }

    public Long getOut_employee_inner_sn() {
        return out_employee_inner_sn;
    }

    public void setOut_employee_inner_sn(Long out_employee_inner_sn) {
        this.out_employee_inner_sn = out_employee_inner_sn;
    }

    public String getOut_employee_name() {
        return out_employee_name;
    }

    public void setOut_employee_name(String out_employee_name) {
        this.out_employee_name = out_employee_name == null ? null : out_employee_name.trim();
    }

    public Date getOut_time() {
        return out_time;
    }

    public void setOut_time(Date out_time) {
        this.out_time = out_time;
    }

    public Long getBack_employee_inner_sn() {
        return back_employee_inner_sn;
    }

    public void setBack_employee_inner_sn(Long back_employee_inner_sn) {
        this.back_employee_inner_sn = back_employee_inner_sn;
    }

    public String getBack_employee_name() {
        return back_employee_name;
    }

    public void setBack_employee_name(String back_employee_name) {
        this.back_employee_name = back_employee_name == null ? null : back_employee_name.trim();
    }

    public Date getBack_time() {
        return back_time;
    }

    public void setBack_time(Date back_time) {
        this.back_time = back_time;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public Date getLast_modified_time() {
        return last_modified_time;
    }

    public void setLast_modified_time(Date last_modified_time) {
        this.last_modified_time = last_modified_time;
    }

    public String getDealer_name() {
		return dealer_name;
	}

	public void setDealer_name(String dealer_name) {
		this.dealer_name = dealer_name;
	}

	public Long getOut_warehouse_inner_sn() {
		return out_warehouse_inner_sn;
	}

	public void setOut_warehouse_inner_sn(Long out_warehouse_inner_sn) {
		this.out_warehouse_inner_sn = out_warehouse_inner_sn;
	}

	public String getOut_warehouse_name() {
		return out_warehouse_name;
	}

	public void setOut_warehouse_name(String out_warehouse_name) {
		this.out_warehouse_name = out_warehouse_name;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", plan_id=").append(plan_id);
        sb.append(", platform_ois_inner_sn=").append(platform_ois_inner_sn);
        sb.append(", cust_ois_id=").append(cust_ois_id);
        sb.append(", dealer_inner_sn=").append(dealer_inner_sn);
        sb.append(", status=").append(status);
        sb.append(", type_inner_sn=").append(type_inner_sn);
        sb.append(", brand_inner_sn=").append(brand_inner_sn);
        sb.append(", ois_sn=").append(ois_sn);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", keywords=").append(keywords);
        sb.append(", assistant_employee_inner_sn=").append(assistant_employee_inner_sn);
        sb.append(", assistant_employee_name=").append(assistant_employee_name);
        sb.append(", out_employee_inner_sn=").append(out_employee_inner_sn);
        sb.append(", out_employee_name=").append(out_employee_name);
        sb.append(", out_time=").append(out_time);
        sb.append(", back_employee_inner_sn=").append(back_employee_inner_sn);
        sb.append(", back_employee_name=").append(back_employee_name);
        sb.append(", back_time=").append(back_time);
        sb.append(", created_time=").append(created_time);
        sb.append(", last_modified_time=").append(last_modified_time);
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
        OrthopaedicOperationPlanOisInstance other = (OrthopaedicOperationPlanOisInstance) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPlan_id() == null ? other.getPlan_id() == null : this.getPlan_id().equals(other.getPlan_id()))
            && (this.getPlatform_ois_inner_sn() == null ? other.getPlatform_ois_inner_sn() == null : this.getPlatform_ois_inner_sn().equals(other.getPlatform_ois_inner_sn()))
            && (this.getCust_ois_id() == null ? other.getCust_ois_id() == null : this.getCust_ois_id().equals(other.getCust_ois_id()))
            && (this.getDealer_inner_sn() == null ? other.getDealer_inner_sn() == null : this.getDealer_inner_sn().equals(other.getDealer_inner_sn()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getType_inner_sn() == null ? other.getType_inner_sn() == null : this.getType_inner_sn().equals(other.getType_inner_sn()))
            && (this.getBrand_inner_sn() == null ? other.getBrand_inner_sn() == null : this.getBrand_inner_sn().equals(other.getBrand_inner_sn()))
            && (this.getOis_sn() == null ? other.getOis_sn() == null : this.getOis_sn().equals(other.getOis_sn()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getKeywords() == null ? other.getKeywords() == null : this.getKeywords().equals(other.getKeywords()))
            && (this.getAssistant_employee_inner_sn() == null ? other.getAssistant_employee_inner_sn() == null : this.getAssistant_employee_inner_sn().equals(other.getAssistant_employee_inner_sn()))
            && (this.getAssistant_employee_name() == null ? other.getAssistant_employee_name() == null : this.getAssistant_employee_name().equals(other.getAssistant_employee_name()))
            && (this.getOut_employee_inner_sn() == null ? other.getOut_employee_inner_sn() == null : this.getOut_employee_inner_sn().equals(other.getOut_employee_inner_sn()))
            && (this.getOut_employee_name() == null ? other.getOut_employee_name() == null : this.getOut_employee_name().equals(other.getOut_employee_name()))
            && (this.getOut_time() == null ? other.getOut_time() == null : this.getOut_time().equals(other.getOut_time()))
            && (this.getBack_employee_inner_sn() == null ? other.getBack_employee_inner_sn() == null : this.getBack_employee_inner_sn().equals(other.getBack_employee_inner_sn()))
            && (this.getBack_employee_name() == null ? other.getBack_employee_name() == null : this.getBack_employee_name().equals(other.getBack_employee_name()))
            && (this.getBack_time() == null ? other.getBack_time() == null : this.getBack_time().equals(other.getBack_time()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()))
            && (this.getLast_modified_time() == null ? other.getLast_modified_time() == null : this.getLast_modified_time().equals(other.getLast_modified_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPlan_id() == null) ? 0 : getPlan_id().hashCode());
        result = prime * result + ((getPlatform_ois_inner_sn() == null) ? 0 : getPlatform_ois_inner_sn().hashCode());
        result = prime * result + ((getCust_ois_id() == null) ? 0 : getCust_ois_id().hashCode());
        result = prime * result + ((getDealer_inner_sn() == null) ? 0 : getDealer_inner_sn().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getType_inner_sn() == null) ? 0 : getType_inner_sn().hashCode());
        result = prime * result + ((getBrand_inner_sn() == null) ? 0 : getBrand_inner_sn().hashCode());
        result = prime * result + ((getOis_sn() == null) ? 0 : getOis_sn().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getKeywords() == null) ? 0 : getKeywords().hashCode());
        result = prime * result + ((getAssistant_employee_inner_sn() == null) ? 0 : getAssistant_employee_inner_sn().hashCode());
        result = prime * result + ((getAssistant_employee_name() == null) ? 0 : getAssistant_employee_name().hashCode());
        result = prime * result + ((getOut_employee_inner_sn() == null) ? 0 : getOut_employee_inner_sn().hashCode());
        result = prime * result + ((getOut_employee_name() == null) ? 0 : getOut_employee_name().hashCode());
        result = prime * result + ((getOut_time() == null) ? 0 : getOut_time().hashCode());
        result = prime * result + ((getBack_employee_inner_sn() == null) ? 0 : getBack_employee_inner_sn().hashCode());
        result = prime * result + ((getBack_employee_name() == null) ? 0 : getBack_employee_name().hashCode());
        result = prime * result + ((getBack_time() == null) ? 0 : getBack_time().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        result = prime * result + ((getLast_modified_time() == null) ? 0 : getLast_modified_time().hashCode());
        return result;
    }
}