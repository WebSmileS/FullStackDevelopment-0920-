package com.yrt.project.modular.surgery.cust;

public class OrthopaedicCustOisKit extends OrthopaedicCustOisKitKey {
    private Integer sort_number;

    public Integer getSort_number() {
        return sort_number;
    }

    public void setSort_number(Integer sort_number) {
        this.sort_number = sort_number;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", sort_number=").append(sort_number);
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
        OrthopaedicCustOisKit other = (OrthopaedicCustOisKit) that;
        return (this.getOis_id() == null ? other.getOis_id() == null : this.getOis_id().equals(other.getOis_id()))
            && (this.getKit_id() == null ? other.getKit_id() == null : this.getKit_id().equals(other.getKit_id()))
            && (this.getSort_number() == null ? other.getSort_number() == null : this.getSort_number().equals(other.getSort_number()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOis_id() == null) ? 0 : getOis_id().hashCode());
        result = prime * result + ((getKit_id() == null) ? 0 : getKit_id().hashCode());
        result = prime * result + ((getSort_number() == null) ? 0 : getSort_number().hashCode());
        return result;
    }
}