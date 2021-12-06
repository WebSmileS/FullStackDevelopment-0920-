package com.yrt.project.modular.surgery.cust;

public class OrthopaedicCustOisOrthopaedicOperationPathwayKey {
    private Long op_inner_sn;

    private Long cust_ois_id;

    public Long getOp_inner_sn() {
        return op_inner_sn;
    }

    public void setOp_inner_sn(Long op_inner_sn) {
        this.op_inner_sn = op_inner_sn;
    }

    public Long getCust_ois_id() {
        return cust_ois_id;
    }

    public void setCust_ois_id(Long cust_ois_id) {
        this.cust_ois_id = cust_ois_id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", op_inner_sn=").append(op_inner_sn);
        sb.append(", cust_ois_id=").append(cust_ois_id);
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
        OrthopaedicCustOisOrthopaedicOperationPathwayKey other = (OrthopaedicCustOisOrthopaedicOperationPathwayKey) that;
        return (this.getOp_inner_sn() == null ? other.getOp_inner_sn() == null : this.getOp_inner_sn().equals(other.getOp_inner_sn()))
            && (this.getCust_ois_id() == null ? other.getCust_ois_id() == null : this.getCust_ois_id().equals(other.getCust_ois_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOp_inner_sn() == null) ? 0 : getOp_inner_sn().hashCode());
        result = prime * result + ((getCust_ois_id() == null) ? 0 : getCust_ois_id().hashCode());
        return result;
    }
}