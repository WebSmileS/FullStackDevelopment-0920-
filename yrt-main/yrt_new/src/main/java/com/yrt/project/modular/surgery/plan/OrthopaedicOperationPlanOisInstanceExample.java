package com.yrt.project.modular.surgery.plan;

import com.yrt.project.modular.surgery.OisInstanceStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrthopaedicOperationPlanOisInstanceExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OrthopaedicOperationPlanOisInstanceExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andPlan_idIsNull() {
            addCriterion("plan_id is null");
            return (Criteria) this;
        }

        public Criteria andPlan_idIsNotNull() {
            addCriterion("plan_id is not null");
            return (Criteria) this;
        }

        public Criteria andPlan_idEqualTo(Long value) {
            addCriterion("plan_id =", value, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlan_idNotEqualTo(Long value) {
            addCriterion("plan_id <>", value, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlan_idGreaterThan(Long value) {
            addCriterion("plan_id >", value, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlan_idGreaterThanOrEqualTo(Long value) {
            addCriterion("plan_id >=", value, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlan_idLessThan(Long value) {
            addCriterion("plan_id <", value, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlan_idLessThanOrEqualTo(Long value) {
            addCriterion("plan_id <=", value, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlan_idIn(List<Long> values) {
            addCriterion("plan_id in", values, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlan_idNotIn(List<Long> values) {
            addCriterion("plan_id not in", values, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlan_idBetween(Long value1, Long value2) {
            addCriterion("plan_id between", value1, value2, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlan_idNotBetween(Long value1, Long value2) {
            addCriterion("plan_id not between", value1, value2, "plan_id");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snIsNull() {
            addCriterion("platform_ois_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snIsNotNull() {
            addCriterion("platform_ois_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snEqualTo(Long value) {
            addCriterion("platform_ois_inner_sn =", value, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snNotEqualTo(Long value) {
            addCriterion("platform_ois_inner_sn <>", value, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snGreaterThan(Long value) {
            addCriterion("platform_ois_inner_sn >", value, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("platform_ois_inner_sn >=", value, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snLessThan(Long value) {
            addCriterion("platform_ois_inner_sn <", value, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("platform_ois_inner_sn <=", value, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snIn(List<Long> values) {
            addCriterion("platform_ois_inner_sn in", values, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snNotIn(List<Long> values) {
            addCriterion("platform_ois_inner_sn not in", values, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snBetween(Long value1, Long value2) {
            addCriterion("platform_ois_inner_sn between", value1, value2, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andPlatform_ois_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("platform_ois_inner_sn not between", value1, value2, "platform_ois_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idIsNull() {
            addCriterion("cust_ois_id is null");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idIsNotNull() {
            addCriterion("cust_ois_id is not null");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idEqualTo(Long value) {
            addCriterion("cust_ois_id =", value, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idNotEqualTo(Long value) {
            addCriterion("cust_ois_id <>", value, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idGreaterThan(Long value) {
            addCriterion("cust_ois_id >", value, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idGreaterThanOrEqualTo(Long value) {
            addCriterion("cust_ois_id >=", value, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idLessThan(Long value) {
            addCriterion("cust_ois_id <", value, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idLessThanOrEqualTo(Long value) {
            addCriterion("cust_ois_id <=", value, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idIn(List<Long> values) {
            addCriterion("cust_ois_id in", values, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idNotIn(List<Long> values) {
            addCriterion("cust_ois_id not in", values, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idBetween(Long value1, Long value2) {
            addCriterion("cust_ois_id between", value1, value2, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andCust_ois_idNotBetween(Long value1, Long value2) {
            addCriterion("cust_ois_id not between", value1, value2, "cust_ois_id");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snIsNull() {
            addCriterion("dealer_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snIsNotNull() {
            addCriterion("dealer_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snEqualTo(Long value) {
            addCriterion("dealer_inner_sn =", value, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snNotEqualTo(Long value) {
            addCriterion("dealer_inner_sn <>", value, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snGreaterThan(Long value) {
            addCriterion("dealer_inner_sn >", value, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("dealer_inner_sn >=", value, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snLessThan(Long value) {
            addCriterion("dealer_inner_sn <", value, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("dealer_inner_sn <=", value, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snIn(List<Long> values) {
            addCriterion("dealer_inner_sn in", values, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snNotIn(List<Long> values) {
            addCriterion("dealer_inner_sn not in", values, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snBetween(Long value1, Long value2) {
            addCriterion("dealer_inner_sn between", value1, value2, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDealer_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("dealer_inner_sn not between", value1, value2, "dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(OisInstanceStatus value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(OisInstanceStatus value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(OisInstanceStatus value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(OisInstanceStatus value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(OisInstanceStatus value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(OisInstanceStatus value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<OisInstanceStatus> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<OisInstanceStatus> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(OisInstanceStatus value1, OisInstanceStatus value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(OisInstanceStatus value1, OisInstanceStatus value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andType_inner_snIsNull() {
            addCriterion("type_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andType_inner_snIsNotNull() {
            addCriterion("type_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andType_inner_snEqualTo(Byte value) {
            addCriterion("type_inner_sn =", value, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andType_inner_snNotEqualTo(Byte value) {
            addCriterion("type_inner_sn <>", value, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andType_inner_snGreaterThan(Byte value) {
            addCriterion("type_inner_sn >", value, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andType_inner_snGreaterThanOrEqualTo(Byte value) {
            addCriterion("type_inner_sn >=", value, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andType_inner_snLessThan(Byte value) {
            addCriterion("type_inner_sn <", value, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andType_inner_snLessThanOrEqualTo(Byte value) {
            addCriterion("type_inner_sn <=", value, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andType_inner_snIn(List<Byte> values) {
            addCriterion("type_inner_sn in", values, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andType_inner_snNotIn(List<Byte> values) {
            addCriterion("type_inner_sn not in", values, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andType_inner_snBetween(Byte value1, Byte value2) {
            addCriterion("type_inner_sn between", value1, value2, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andType_inner_snNotBetween(Byte value1, Byte value2) {
            addCriterion("type_inner_sn not between", value1, value2, "type_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snIsNull() {
            addCriterion("brand_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snIsNotNull() {
            addCriterion("brand_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snEqualTo(Short value) {
            addCriterion("brand_inner_sn =", value, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snNotEqualTo(Short value) {
            addCriterion("brand_inner_sn <>", value, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snGreaterThan(Short value) {
            addCriterion("brand_inner_sn >", value, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snGreaterThanOrEqualTo(Short value) {
            addCriterion("brand_inner_sn >=", value, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snLessThan(Short value) {
            addCriterion("brand_inner_sn <", value, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snLessThanOrEqualTo(Short value) {
            addCriterion("brand_inner_sn <=", value, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snIn(List<Short> values) {
            addCriterion("brand_inner_sn in", values, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snNotIn(List<Short> values) {
            addCriterion("brand_inner_sn not in", values, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snBetween(Short value1, Short value2) {
            addCriterion("brand_inner_sn between", value1, value2, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBrand_inner_snNotBetween(Short value1, Short value2) {
            addCriterion("brand_inner_sn not between", value1, value2, "brand_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snIsNull() {
            addCriterion("ois_sn is null");
            return (Criteria) this;
        }

        public Criteria andOis_snIsNotNull() {
            addCriterion("ois_sn is not null");
            return (Criteria) this;
        }

        public Criteria andOis_snEqualTo(String value) {
            addCriterion("ois_sn =", value, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snNotEqualTo(String value) {
            addCriterion("ois_sn <>", value, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snGreaterThan(String value) {
            addCriterion("ois_sn >", value, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snGreaterThanOrEqualTo(String value) {
            addCriterion("ois_sn >=", value, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snLessThan(String value) {
            addCriterion("ois_sn <", value, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snLessThanOrEqualTo(String value) {
            addCriterion("ois_sn <=", value, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snLike(String value) {
            addCriterion("ois_sn like", value, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snNotLike(String value) {
            addCriterion("ois_sn not like", value, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snIn(List<String> values) {
            addCriterion("ois_sn in", values, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snNotIn(List<String> values) {
            addCriterion("ois_sn not in", values, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snBetween(String value1, String value2) {
            addCriterion("ois_sn between", value1, value2, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andOis_snNotBetween(String value1, String value2) {
            addCriterion("ois_sn not between", value1, value2, "ois_sn");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andKeywordsIsNull() {
            addCriterion("keywords is null");
            return (Criteria) this;
        }

        public Criteria andKeywordsIsNotNull() {
            addCriterion("keywords is not null");
            return (Criteria) this;
        }

        public Criteria andKeywordsEqualTo(String value) {
            addCriterion("keywords =", value, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsNotEqualTo(String value) {
            addCriterion("keywords <>", value, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsGreaterThan(String value) {
            addCriterion("keywords >", value, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsGreaterThanOrEqualTo(String value) {
            addCriterion("keywords >=", value, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsLessThan(String value) {
            addCriterion("keywords <", value, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsLessThanOrEqualTo(String value) {
            addCriterion("keywords <=", value, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsLike(String value) {
            addCriterion("keywords like", value, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsNotLike(String value) {
            addCriterion("keywords not like", value, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsIn(List<String> values) {
            addCriterion("keywords in", values, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsNotIn(List<String> values) {
            addCriterion("keywords not in", values, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsBetween(String value1, String value2) {
            addCriterion("keywords between", value1, value2, "keywords");
            return (Criteria) this;
        }

        public Criteria andKeywordsNotBetween(String value1, String value2) {
            addCriterion("keywords not between", value1, value2, "keywords");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snIsNull() {
            addCriterion("assistant_employee_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snIsNotNull() {
            addCriterion("assistant_employee_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snEqualTo(Long value) {
            addCriterion("assistant_employee_inner_sn =", value, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snNotEqualTo(Long value) {
            addCriterion("assistant_employee_inner_sn <>", value, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snGreaterThan(Long value) {
            addCriterion("assistant_employee_inner_sn >", value, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("assistant_employee_inner_sn >=", value, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snLessThan(Long value) {
            addCriterion("assistant_employee_inner_sn <", value, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("assistant_employee_inner_sn <=", value, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snIn(List<Long> values) {
            addCriterion("assistant_employee_inner_sn in", values, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snNotIn(List<Long> values) {
            addCriterion("assistant_employee_inner_sn not in", values, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snBetween(Long value1, Long value2) {
            addCriterion("assistant_employee_inner_sn between", value1, value2, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("assistant_employee_inner_sn not between", value1, value2, "assistant_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameIsNull() {
            addCriterion("assistant_employee_name is null");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameIsNotNull() {
            addCriterion("assistant_employee_name is not null");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameEqualTo(String value) {
            addCriterion("assistant_employee_name =", value, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameNotEqualTo(String value) {
            addCriterion("assistant_employee_name <>", value, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameGreaterThan(String value) {
            addCriterion("assistant_employee_name >", value, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameGreaterThanOrEqualTo(String value) {
            addCriterion("assistant_employee_name >=", value, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameLessThan(String value) {
            addCriterion("assistant_employee_name <", value, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameLessThanOrEqualTo(String value) {
            addCriterion("assistant_employee_name <=", value, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameLike(String value) {
            addCriterion("assistant_employee_name like", value, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameNotLike(String value) {
            addCriterion("assistant_employee_name not like", value, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameIn(List<String> values) {
            addCriterion("assistant_employee_name in", values, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameNotIn(List<String> values) {
            addCriterion("assistant_employee_name not in", values, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameBetween(String value1, String value2) {
            addCriterion("assistant_employee_name between", value1, value2, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andAssistant_employee_nameNotBetween(String value1, String value2) {
            addCriterion("assistant_employee_name not between", value1, value2, "assistant_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snIsNull() {
            addCriterion("out_employee_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snIsNotNull() {
            addCriterion("out_employee_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snEqualTo(Long value) {
            addCriterion("out_employee_inner_sn =", value, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snNotEqualTo(Long value) {
            addCriterion("out_employee_inner_sn <>", value, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snGreaterThan(Long value) {
            addCriterion("out_employee_inner_sn >", value, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("out_employee_inner_sn >=", value, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snLessThan(Long value) {
            addCriterion("out_employee_inner_sn <", value, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("out_employee_inner_sn <=", value, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snIn(List<Long> values) {
            addCriterion("out_employee_inner_sn in", values, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snNotIn(List<Long> values) {
            addCriterion("out_employee_inner_sn not in", values, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snBetween(Long value1, Long value2) {
            addCriterion("out_employee_inner_sn between", value1, value2, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("out_employee_inner_sn not between", value1, value2, "out_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameIsNull() {
            addCriterion("out_employee_name is null");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameIsNotNull() {
            addCriterion("out_employee_name is not null");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameEqualTo(String value) {
            addCriterion("out_employee_name =", value, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameNotEqualTo(String value) {
            addCriterion("out_employee_name <>", value, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameGreaterThan(String value) {
            addCriterion("out_employee_name >", value, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameGreaterThanOrEqualTo(String value) {
            addCriterion("out_employee_name >=", value, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameLessThan(String value) {
            addCriterion("out_employee_name <", value, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameLessThanOrEqualTo(String value) {
            addCriterion("out_employee_name <=", value, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameLike(String value) {
            addCriterion("out_employee_name like", value, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameNotLike(String value) {
            addCriterion("out_employee_name not like", value, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameIn(List<String> values) {
            addCriterion("out_employee_name in", values, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameNotIn(List<String> values) {
            addCriterion("out_employee_name not in", values, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameBetween(String value1, String value2) {
            addCriterion("out_employee_name between", value1, value2, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_employee_nameNotBetween(String value1, String value2) {
            addCriterion("out_employee_name not between", value1, value2, "out_employee_name");
            return (Criteria) this;
        }

        public Criteria andOut_timeIsNull() {
            addCriterion("out_time is null");
            return (Criteria) this;
        }

        public Criteria andOut_timeIsNotNull() {
            addCriterion("out_time is not null");
            return (Criteria) this;
        }

        public Criteria andOut_timeEqualTo(Date value) {
            addCriterion("out_time =", value, "out_time");
            return (Criteria) this;
        }

        public Criteria andOut_timeNotEqualTo(Date value) {
            addCriterion("out_time <>", value, "out_time");
            return (Criteria) this;
        }

        public Criteria andOut_timeGreaterThan(Date value) {
            addCriterion("out_time >", value, "out_time");
            return (Criteria) this;
        }

        public Criteria andOut_timeGreaterThanOrEqualTo(Date value) {
            addCriterion("out_time >=", value, "out_time");
            return (Criteria) this;
        }

        public Criteria andOut_timeLessThan(Date value) {
            addCriterion("out_time <", value, "out_time");
            return (Criteria) this;
        }

        public Criteria andOut_timeLessThanOrEqualTo(Date value) {
            addCriterion("out_time <=", value, "out_time");
            return (Criteria) this;
        }

        public Criteria andOut_timeIn(List<Date> values) {
            addCriterion("out_time in", values, "out_time");
            return (Criteria) this;
        }

        public Criteria andOut_timeNotIn(List<Date> values) {
            addCriterion("out_time not in", values, "out_time");
            return (Criteria) this;
        }

        public Criteria andOut_timeBetween(Date value1, Date value2) {
            addCriterion("out_time between", value1, value2, "out_time");
            return (Criteria) this;
        }

        public Criteria andOut_timeNotBetween(Date value1, Date value2) {
            addCriterion("out_time not between", value1, value2, "out_time");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snIsNull() {
            addCriterion("back_employee_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snIsNotNull() {
            addCriterion("back_employee_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snEqualTo(Long value) {
            addCriterion("back_employee_inner_sn =", value, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snNotEqualTo(Long value) {
            addCriterion("back_employee_inner_sn <>", value, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snGreaterThan(Long value) {
            addCriterion("back_employee_inner_sn >", value, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("back_employee_inner_sn >=", value, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snLessThan(Long value) {
            addCriterion("back_employee_inner_sn <", value, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("back_employee_inner_sn <=", value, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snIn(List<Long> values) {
            addCriterion("back_employee_inner_sn in", values, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snNotIn(List<Long> values) {
            addCriterion("back_employee_inner_sn not in", values, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snBetween(Long value1, Long value2) {
            addCriterion("back_employee_inner_sn between", value1, value2, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("back_employee_inner_sn not between", value1, value2, "back_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameIsNull() {
            addCriterion("back_employee_name is null");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameIsNotNull() {
            addCriterion("back_employee_name is not null");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameEqualTo(String value) {
            addCriterion("back_employee_name =", value, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameNotEqualTo(String value) {
            addCriterion("back_employee_name <>", value, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameGreaterThan(String value) {
            addCriterion("back_employee_name >", value, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameGreaterThanOrEqualTo(String value) {
            addCriterion("back_employee_name >=", value, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameLessThan(String value) {
            addCriterion("back_employee_name <", value, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameLessThanOrEqualTo(String value) {
            addCriterion("back_employee_name <=", value, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameLike(String value) {
            addCriterion("back_employee_name like", value, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameNotLike(String value) {
            addCriterion("back_employee_name not like", value, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameIn(List<String> values) {
            addCriterion("back_employee_name in", values, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameNotIn(List<String> values) {
            addCriterion("back_employee_name not in", values, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameBetween(String value1, String value2) {
            addCriterion("back_employee_name between", value1, value2, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_employee_nameNotBetween(String value1, String value2) {
            addCriterion("back_employee_name not between", value1, value2, "back_employee_name");
            return (Criteria) this;
        }

        public Criteria andBack_timeIsNull() {
            addCriterion("back_time is null");
            return (Criteria) this;
        }

        public Criteria andBack_timeIsNotNull() {
            addCriterion("back_time is not null");
            return (Criteria) this;
        }

        public Criteria andBack_timeEqualTo(Date value) {
            addCriterion("back_time =", value, "back_time");
            return (Criteria) this;
        }

        public Criteria andBack_timeNotEqualTo(Date value) {
            addCriterion("back_time <>", value, "back_time");
            return (Criteria) this;
        }

        public Criteria andBack_timeGreaterThan(Date value) {
            addCriterion("back_time >", value, "back_time");
            return (Criteria) this;
        }

        public Criteria andBack_timeGreaterThanOrEqualTo(Date value) {
            addCriterion("back_time >=", value, "back_time");
            return (Criteria) this;
        }

        public Criteria andBack_timeLessThan(Date value) {
            addCriterion("back_time <", value, "back_time");
            return (Criteria) this;
        }

        public Criteria andBack_timeLessThanOrEqualTo(Date value) {
            addCriterion("back_time <=", value, "back_time");
            return (Criteria) this;
        }

        public Criteria andBack_timeIn(List<Date> values) {
            addCriterion("back_time in", values, "back_time");
            return (Criteria) this;
        }

        public Criteria andBack_timeNotIn(List<Date> values) {
            addCriterion("back_time not in", values, "back_time");
            return (Criteria) this;
        }

        public Criteria andBack_timeBetween(Date value1, Date value2) {
            addCriterion("back_time between", value1, value2, "back_time");
            return (Criteria) this;
        }

        public Criteria andBack_timeNotBetween(Date value1, Date value2) {
            addCriterion("back_time not between", value1, value2, "back_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeIsNull() {
            addCriterion("created_time is null");
            return (Criteria) this;
        }

        public Criteria andCreated_timeIsNotNull() {
            addCriterion("created_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreated_timeEqualTo(Date value) {
            addCriterion("created_time =", value, "created_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeNotEqualTo(Date value) {
            addCriterion("created_time <>", value, "created_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeGreaterThan(Date value) {
            addCriterion("created_time >", value, "created_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeGreaterThanOrEqualTo(Date value) {
            addCriterion("created_time >=", value, "created_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeLessThan(Date value) {
            addCriterion("created_time <", value, "created_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeLessThanOrEqualTo(Date value) {
            addCriterion("created_time <=", value, "created_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeIn(List<Date> values) {
            addCriterion("created_time in", values, "created_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeNotIn(List<Date> values) {
            addCriterion("created_time not in", values, "created_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeBetween(Date value1, Date value2) {
            addCriterion("created_time between", value1, value2, "created_time");
            return (Criteria) this;
        }

        public Criteria andCreated_timeNotBetween(Date value1, Date value2) {
            addCriterion("created_time not between", value1, value2, "created_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeIsNull() {
            addCriterion("last_modified_time is null");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeIsNotNull() {
            addCriterion("last_modified_time is not null");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeEqualTo(Date value) {
            addCriterion("last_modified_time =", value, "last_modified_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeNotEqualTo(Date value) {
            addCriterion("last_modified_time <>", value, "last_modified_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeGreaterThan(Date value) {
            addCriterion("last_modified_time >", value, "last_modified_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeGreaterThanOrEqualTo(Date value) {
            addCriterion("last_modified_time >=", value, "last_modified_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeLessThan(Date value) {
            addCriterion("last_modified_time <", value, "last_modified_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeLessThanOrEqualTo(Date value) {
            addCriterion("last_modified_time <=", value, "last_modified_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeIn(List<Date> values) {
            addCriterion("last_modified_time in", values, "last_modified_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeNotIn(List<Date> values) {
            addCriterion("last_modified_time not in", values, "last_modified_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeBetween(Date value1, Date value2) {
            addCriterion("last_modified_time between", value1, value2, "last_modified_time");
            return (Criteria) this;
        }

        public Criteria andLast_modified_timeNotBetween(Date value1, Date value2) {
            addCriterion("last_modified_time not between", value1, value2, "last_modified_time");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}