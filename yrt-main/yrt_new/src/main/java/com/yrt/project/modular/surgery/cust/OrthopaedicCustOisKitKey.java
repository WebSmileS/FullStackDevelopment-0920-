package com.yrt.project.modular.surgery.cust;

public class OrthopaedicCustOisKitKey {
    private Long ois_id;

    private Long kit_id;

    public Long getOis_id() {
        return ois_id;
    }

    public void setOis_id(Long ois_id) {
        this.ois_id = ois_id;
    }

    public Long getKit_id() {
        return kit_id;
    }

    public void setKit_id(Long kit_id) {
        this.kit_id = kit_id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ois_id=").append(ois_id);
        sb.append(", kit_id=").append(kit_id);
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
        OrthopaedicCustOisKitKey other = (OrthopaedicCustOisKitKey) that;
        return (this.getOis_id() == null ? other.getOis_id() == null : this.getOis_id().equals(other.getOis_id()))
            && (this.getKit_id() == null ? other.getKit_id() == null : this.getKit_id().equals(other.getKit_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOis_id() == null) ? 0 : getOis_id().hashCode());
        result = prime * result + ((getKit_id() == null) ? 0 : getKit_id().hashCode());
        return result;
    }
}