package com.yrt.project.modular.surgery.cust;

import javax.validation.constraints.NotNull;

import com.yrt.project.modular.surgery.OisKitStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class OrthopaedicCustKit {
	@ApiModelProperty(value = "定制工具包id")
    private Long id;

	@NotNull
	@ApiModelProperty(value = "所属经销商inner_sn")
    private Long owner_dealer_inner_sn;
	
	@NotNull
	@ApiModelProperty(value = "所属经销商名称")
    private String owner_dealer_name;

	@ApiModelProperty(value = "源平台工具包inner_sn")
	@NotNull
    private Long platform_kit_inner_sn;

	@ApiModelProperty(value = "手术类型inner_sn")
	@NotNull
    private Long type_inner_sn;

	@ApiModelProperty(value = "品牌inner_sn")
	@NotNull
    private Long brand_inner_sn;

	@ApiModelProperty(value = "有效状态")
    private OisKitStatus status;// = OisKitStatus.VALID;

    @ApiModelProperty(value = "删除标识", hidden=true)
    private Boolean is_delete;

    @ApiModelProperty(value = "编号")
    @NotNull
    private String kit_sn;

    @ApiModelProperty(value = "名称")
    @NotNull
    private String name;

    @ApiModelProperty(value = "描述")
    @NotNull
    private String description;

    @ApiModelProperty(value = "关键字（默认name拼音首字母)")
    private String keywords;
    
    ////////////////////////////////////////////////////
    @ApiModelProperty(value = "类型名称")
    @Getter @Setter
    private String type_name;
    
	@ApiModelProperty(value = "品牌名称")
    @Getter @Setter
    private String brand_name;
    
	@ApiModelProperty(value = "定制标识(false时，id为空)。虚拟字段，保存时无意义。")
    @Getter @Setter
    private Boolean is_cust; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwner_dealer_inner_sn() {
        return owner_dealer_inner_sn;
    }

    public void setOwner_dealer_inner_sn(Long owner_dealer_inner_sn) {
        this.owner_dealer_inner_sn = owner_dealer_inner_sn;
    }

    public Long getPlatform_kit_inner_sn() {
        return platform_kit_inner_sn;
    }

    public void setPlatform_kit_inner_sn(Long platform_kit_inner_sn) {
        this.platform_kit_inner_sn = platform_kit_inner_sn;
    }

    public Long getType_inner_sn() {
        return type_inner_sn;
    }

    public void setType_inner_sn(Long type_inner_sn) {
        this.type_inner_sn = type_inner_sn;
    }

    public Long getBrand_inner_sn() {
        return brand_inner_sn;
    }

    public void setBrand_inner_sn(Long brand_inner_sn) {
        this.brand_inner_sn = brand_inner_sn;
    }

    public OisKitStatus getStatus() {
        return status;
    }

    public void setStatus(OisKitStatus status) {
        this.status = status;
    }

    public Boolean getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Boolean is_delete) {
        this.is_delete = is_delete;
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
        sb.append(", owner_dealer_inner_sn=").append(owner_dealer_inner_sn);
        sb.append(", platform_kit_inner_sn=").append(platform_kit_inner_sn);
        sb.append(", type_inner_sn=").append(type_inner_sn);
        sb.append(", brand_inner_sn=").append(brand_inner_sn);
        sb.append(", status=").append(status);
        sb.append(", is_delete=").append(is_delete);
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
        OrthopaedicCustKit other = (OrthopaedicCustKit) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOwner_dealer_inner_sn() == null ? other.getOwner_dealer_inner_sn() == null : this.getOwner_dealer_inner_sn().equals(other.getOwner_dealer_inner_sn()))
            && (this.getPlatform_kit_inner_sn() == null ? other.getPlatform_kit_inner_sn() == null : this.getPlatform_kit_inner_sn().equals(other.getPlatform_kit_inner_sn()))
            && (this.getType_inner_sn() == null ? other.getType_inner_sn() == null : this.getType_inner_sn().equals(other.getType_inner_sn()))
            && (this.getBrand_inner_sn() == null ? other.getBrand_inner_sn() == null : this.getBrand_inner_sn().equals(other.getBrand_inner_sn()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getIs_delete() == null ? other.getIs_delete() == null : this.getIs_delete().equals(other.getIs_delete()))
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
        result = prime * result + ((getOwner_dealer_inner_sn() == null) ? 0 : getOwner_dealer_inner_sn().hashCode());
        result = prime * result + ((getPlatform_kit_inner_sn() == null) ? 0 : getPlatform_kit_inner_sn().hashCode());
        result = prime * result + ((getType_inner_sn() == null) ? 0 : getType_inner_sn().hashCode());
        result = prime * result + ((getBrand_inner_sn() == null) ? 0 : getBrand_inner_sn().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getIs_delete() == null) ? 0 : getIs_delete().hashCode());
        result = prime * result + ((getKit_sn() == null) ? 0 : getKit_sn().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getKeywords() == null) ? 0 : getKeywords().hashCode());
        return result;
    }

	public String getOwner_dealer_name() {
		return owner_dealer_name;
	}

	public void setOwner_dealer_name(String owner_dealer_name) {
		this.owner_dealer_name = owner_dealer_name;
	}
}