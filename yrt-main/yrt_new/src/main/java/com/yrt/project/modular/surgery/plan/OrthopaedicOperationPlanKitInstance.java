package com.yrt.project.modular.surgery.plan;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class OrthopaedicOperationPlanKitInstance {
	
	@ApiModelProperty("工具包实例id(新增时不填)")
    private Long id;

	@ApiModelProperty("所属手术包实例id(新增时不填)")
    private Long ois_ins_id;

	@ApiModelProperty("所属计划id(新增时不填)")
    private Long plan_id;

	
	@ApiModelProperty("源 定制工具包id(选非定制包时为空)")
    private Long cust_kit_id;

	@NotNull
	@ApiModelProperty("源 平台工具包id")
    private Long platform_kit_inner_sn;

	@NotNull
	@ApiModelProperty("类型inner_sn")
    private Byte type_inner_sn;

	@NotNull
	@ApiModelProperty("品牌inner_sn")
    private Short brand_inner_sn;

	@NotNull
	@ApiModelProperty("工具包编号")
    private String kit_sn;

	@NotNull
	@ApiModelProperty("工具包名称")
    private String name;

	@NotNull
	@ApiModelProperty("说明")
    private String description;

	@ApiModelProperty("关键字")
    private String keywords;
	
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

    public Long getCust_kit_id() {
        return cust_kit_id;
    }

    public void setCust_kit_id(Long cust_kit_id) {
        this.cust_kit_id = cust_kit_id;
    }

    public Long getPlatform_kit_inner_sn() {
        return platform_kit_inner_sn;
    }

    public void setPlatform_kit_inner_sn(Long platform_kit_inner_sn) {
        this.platform_kit_inner_sn = platform_kit_inner_sn;
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

    public String getKit_sn() {
        return kit_sn;
    }

    public void setKit_sn(String kit_sn) {
        this.kit_sn = kit_sn == null ? null : kit_sn.trim();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ois_ins_id=").append(ois_ins_id);
        sb.append(", plan_id=").append(plan_id);
        sb.append(", cust_kit_id=").append(cust_kit_id);
        sb.append(", platform_kit_inner_sn=").append(platform_kit_inner_sn);
        sb.append(", type_inner_sn=").append(type_inner_sn);
        sb.append(", brand_inner_sn=").append(brand_inner_sn);
        sb.append(", kit_sn=").append(kit_sn);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", keywords=").append(keywords);
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
        OrthopaedicOperationPlanKitInstance other = (OrthopaedicOperationPlanKitInstance) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOis_ins_id() == null ? other.getOis_ins_id() == null : this.getOis_ins_id().equals(other.getOis_ins_id()))
            && (this.getPlan_id() == null ? other.getPlan_id() == null : this.getPlan_id().equals(other.getPlan_id()))
            && (this.getCust_kit_id() == null ? other.getCust_kit_id() == null : this.getCust_kit_id().equals(other.getCust_kit_id()))
            && (this.getPlatform_kit_inner_sn() == null ? other.getPlatform_kit_inner_sn() == null : this.getPlatform_kit_inner_sn().equals(other.getPlatform_kit_inner_sn()))
            && (this.getType_inner_sn() == null ? other.getType_inner_sn() == null : this.getType_inner_sn().equals(other.getType_inner_sn()))
            && (this.getBrand_inner_sn() == null ? other.getBrand_inner_sn() == null : this.getBrand_inner_sn().equals(other.getBrand_inner_sn()))
            && (this.getKit_sn() == null ? other.getKit_sn() == null : this.getKit_sn().equals(other.getKit_sn()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getKeywords() == null ? other.getKeywords() == null : this.getKeywords().equals(other.getKeywords()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOis_ins_id() == null) ? 0 : getOis_ins_id().hashCode());
        result = prime * result + ((getPlan_id() == null) ? 0 : getPlan_id().hashCode());
        result = prime * result + ((getCust_kit_id() == null) ? 0 : getCust_kit_id().hashCode());
        result = prime * result + ((getPlatform_kit_inner_sn() == null) ? 0 : getPlatform_kit_inner_sn().hashCode());
        result = prime * result + ((getType_inner_sn() == null) ? 0 : getType_inner_sn().hashCode());
        result = prime * result + ((getBrand_inner_sn() == null) ? 0 : getBrand_inner_sn().hashCode());
        result = prime * result + ((getKit_sn() == null) ? 0 : getKit_sn().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getKeywords() == null) ? 0 : getKeywords().hashCode());
        return result;
    }
}