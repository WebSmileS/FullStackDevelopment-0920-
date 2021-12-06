package com.yrt.project.modular.surgery.plan;

public class OrthopaedicOperationPlanTypeKey {
    private Long plan_id;

    private Byte type_inner_sn;

    public Long getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(Long plan_id) {
        this.plan_id = plan_id;
    }

    public Byte getType_inner_sn() {
        return type_inner_sn;
    }

    public void setType_inner_sn(Byte type_inner_sn) {
        this.type_inner_sn = type_inner_sn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", plan_id=").append(plan_id);
        sb.append(", type_inner_sn=").append(type_inner_sn);
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
        OrthopaedicOperationPlanTypeKey other = (OrthopaedicOperationPlanTypeKey) that;
        return (this.getPlan_id() == null ? other.getPlan_id() == null : this.getPlan_id().equals(other.getPlan_id()))
            && (this.getType_inner_sn() == null ? other.getType_inner_sn() == null : this.getType_inner_sn().equals(other.getType_inner_sn()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPlan_id() == null) ? 0 : getPlan_id().hashCode());
        result = prime * result + ((getType_inner_sn() == null) ? 0 : getType_inner_sn().hashCode());
        return result;
    }
}