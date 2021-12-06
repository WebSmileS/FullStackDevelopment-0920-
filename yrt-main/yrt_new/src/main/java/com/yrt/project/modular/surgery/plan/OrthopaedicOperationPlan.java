package com.yrt.project.modular.surgery.plan;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.yrt.project.modular.surgery.OperationPlanStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class OrthopaedicOperationPlan {
	
	//@NotNull(message="计划类型为空")
	@ApiModelProperty("计划手术类型(逗号分隔数字, 至少一个)")
	@Getter @Setter
	private String types;
	
	///////////////////////////////////////////////////
	@ApiModelProperty("计划id(新增时不填)")
    private Long id;

	@NotNull(message="创建人user_inner_sn为空")
	@ApiModelProperty("创建人user_inner_sn")
    private Long creator_user_inner_sn;
	
	@NotNull(message="创建人 姓名为空")
	@ApiModelProperty("创建人 姓名")
    private String creator_user_name;

	@NotNull(message="创建人 所属机构inner_sn")
	@ApiModelProperty("创建人 所属机构inner_sn")
    private Long creator_org_inner_sn;

	@NotNull(message="创建人 所属机构类型为空")
	@ApiModelProperty("创建人 所属机构类型")
    private Byte creator_org_type;

	@NotNull(message="医生员工inner_sn为空")
	@ApiModelProperty("医生员工inner_sn")
    private Long doctor_employee_inner_sn;

	@NotNull(message="医生姓名为空")
	@ApiModelProperty("医生姓名")
    private String doctor_name;

	@NotNull(message="医院inner_sn为空")
	@ApiModelProperty("医院inner_sn")
    private Long hospital_inner_sn;

	@NotNull(message="医院名称为空")
	@ApiModelProperty("医院名称")
    private String hospital_name;

	@NotNull(message="医院科室inner_sn为空")
	@ApiModelProperty("医院科室inner_sn")
    private Long hospital_department_inner_sn;

	@NotNull(message="医院科室名称为空")
	@ApiModelProperty("医院科室名称")
    private String hospital_department_name;

	@ApiModelProperty("被手术人")
    private String patient_name;

	@ApiModelProperty("计划名称")
    private String name;

	@ApiModelProperty("说明")
    private String description;

	@ApiModelProperty("关键字(默认 name/doctor/patient_name 拼音首字母)")
    private String keywords;

	@ApiModelProperty("手术时间")
    private Date operation_time;

	@ApiModelProperty("计划状态")
    private OperationPlanStatus status;

    private Boolean is_delete;

    @ApiModelProperty("创建时间")
    private Date created_time;

    @ApiModelProperty("最后更新时间")
    private Date last_modified_time;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreator_user_inner_sn() {
        return creator_user_inner_sn;
    }

    public void setCreator_user_inner_sn(Long creator_user_inner_sn) {
        this.creator_user_inner_sn = creator_user_inner_sn;
    }

    public String getCreator_user_name() {
        return creator_user_name;
    }

    public void setCreator_user_name(String creator_user_name) {
        this.creator_user_name = creator_user_name == null ? null : creator_user_name.trim();
    }

    public Long getCreator_org_inner_sn() {
        return creator_org_inner_sn;
    }

    public void setCreator_org_inner_sn(Long creator_org_inner_sn) {
        this.creator_org_inner_sn = creator_org_inner_sn;
    }

    public Byte getCreator_org_type() {
        return creator_org_type;
    }

    public void setCreator_org_type(Byte creator_org_type) {
        this.creator_org_type = creator_org_type;
    }

    public Long getDoctor_employee_inner_sn() {
        return doctor_employee_inner_sn;
    }

    public void setDoctor_employee_inner_sn(Long doctor_employee_inner_sn) {
        this.doctor_employee_inner_sn = doctor_employee_inner_sn;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name == null ? null : doctor_name.trim();
    }

    public Long getHospital_inner_sn() {
        return hospital_inner_sn;
    }

    public void setHospital_inner_sn(Long hospital_inner_sn) {
        this.hospital_inner_sn = hospital_inner_sn;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name == null ? null : hospital_name.trim();
    }

    public Long getHospital_department_inner_sn() {
        return hospital_department_inner_sn;
    }

    public void setHospital_department_inner_sn(Long hospital_department_inner_sn) {
        this.hospital_department_inner_sn = hospital_department_inner_sn;
    }

    public String getHospital_department_name() {
        return hospital_department_name;
    }

    public void setHospital_department_name(String hospital_department_name) {
        this.hospital_department_name = hospital_department_name == null ? null : hospital_department_name.trim();
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name == null ? null : patient_name.trim();
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

    public Date getOperation_time() {
        return operation_time;
    }

    public void setOperation_time(Date operation_time) {
        this.operation_time = operation_time;
    }

    public OperationPlanStatus getStatus() {
        return status;
    }

    public void setStatus(OperationPlanStatus status) {
        this.status = status;
    }

    public Boolean getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Boolean is_delete) {
        this.is_delete = is_delete;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", creator_user_inner_sn=").append(creator_user_inner_sn);
        sb.append(", creator_user_name=").append(creator_user_name);
        sb.append(", creator_org_inner_sn=").append(creator_org_inner_sn);
        sb.append(", creator_org_type=").append(creator_org_type);
        sb.append(", doctor_employee_inner_sn=").append(doctor_employee_inner_sn);
        sb.append(", doctor_name=").append(doctor_name);
        sb.append(", hospital_inner_sn=").append(hospital_inner_sn);
        sb.append(", hospital_name=").append(hospital_name);
        sb.append(", hospital_department_inner_sn=").append(hospital_department_inner_sn);
        sb.append(", hospital_department_name=").append(hospital_department_name);
        sb.append(", patient_name=").append(patient_name);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", keywords=").append(keywords);
        sb.append(", operation_time=").append(operation_time);
        sb.append(", status=").append(status);
        sb.append(", is_delete=").append(is_delete);
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
        OrthopaedicOperationPlan other = (OrthopaedicOperationPlan) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCreator_user_inner_sn() == null ? other.getCreator_user_inner_sn() == null : this.getCreator_user_inner_sn().equals(other.getCreator_user_inner_sn()))
            && (this.getCreator_user_name() == null ? other.getCreator_user_name() == null : this.getCreator_user_name().equals(other.getCreator_user_name()))
            && (this.getCreator_org_inner_sn() == null ? other.getCreator_org_inner_sn() == null : this.getCreator_org_inner_sn().equals(other.getCreator_org_inner_sn()))
            && (this.getCreator_org_type() == null ? other.getCreator_org_type() == null : this.getCreator_org_type().equals(other.getCreator_org_type()))
            && (this.getDoctor_employee_inner_sn() == null ? other.getDoctor_employee_inner_sn() == null : this.getDoctor_employee_inner_sn().equals(other.getDoctor_employee_inner_sn()))
            && (this.getDoctor_name() == null ? other.getDoctor_name() == null : this.getDoctor_name().equals(other.getDoctor_name()))
            && (this.getHospital_inner_sn() == null ? other.getHospital_inner_sn() == null : this.getHospital_inner_sn().equals(other.getHospital_inner_sn()))
            && (this.getHospital_name() == null ? other.getHospital_name() == null : this.getHospital_name().equals(other.getHospital_name()))
            && (this.getHospital_department_inner_sn() == null ? other.getHospital_department_inner_sn() == null : this.getHospital_department_inner_sn().equals(other.getHospital_department_inner_sn()))
            && (this.getHospital_department_name() == null ? other.getHospital_department_name() == null : this.getHospital_department_name().equals(other.getHospital_department_name()))
            && (this.getPatient_name() == null ? other.getPatient_name() == null : this.getPatient_name().equals(other.getPatient_name()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getKeywords() == null ? other.getKeywords() == null : this.getKeywords().equals(other.getKeywords()))
            && (this.getOperation_time() == null ? other.getOperation_time() == null : this.getOperation_time().equals(other.getOperation_time()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getIs_delete() == null ? other.getIs_delete() == null : this.getIs_delete().equals(other.getIs_delete()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()))
            && (this.getLast_modified_time() == null ? other.getLast_modified_time() == null : this.getLast_modified_time().equals(other.getLast_modified_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCreator_user_inner_sn() == null) ? 0 : getCreator_user_inner_sn().hashCode());
        result = prime * result + ((getCreator_user_name() == null) ? 0 : getCreator_user_name().hashCode());
        result = prime * result + ((getCreator_org_inner_sn() == null) ? 0 : getCreator_org_inner_sn().hashCode());
        result = prime * result + ((getCreator_org_type() == null) ? 0 : getCreator_org_type().hashCode());
        result = prime * result + ((getDoctor_employee_inner_sn() == null) ? 0 : getDoctor_employee_inner_sn().hashCode());
        result = prime * result + ((getDoctor_name() == null) ? 0 : getDoctor_name().hashCode());
        result = prime * result + ((getHospital_inner_sn() == null) ? 0 : getHospital_inner_sn().hashCode());
        result = prime * result + ((getHospital_name() == null) ? 0 : getHospital_name().hashCode());
        result = prime * result + ((getHospital_department_inner_sn() == null) ? 0 : getHospital_department_inner_sn().hashCode());
        result = prime * result + ((getHospital_department_name() == null) ? 0 : getHospital_department_name().hashCode());
        result = prime * result + ((getPatient_name() == null) ? 0 : getPatient_name().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getKeywords() == null) ? 0 : getKeywords().hashCode());
        result = prime * result + ((getOperation_time() == null) ? 0 : getOperation_time().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getIs_delete() == null) ? 0 : getIs_delete().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        result = prime * result + ((getLast_modified_time() == null) ? 0 : getLast_modified_time().hashCode());
        return result;
    }
}