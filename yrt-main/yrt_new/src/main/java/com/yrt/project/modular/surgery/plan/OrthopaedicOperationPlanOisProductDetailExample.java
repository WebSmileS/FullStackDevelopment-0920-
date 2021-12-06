package com.yrt.project.modular.surgery.plan;

import java.util.ArrayList;
import java.util.List;

public class OrthopaedicOperationPlanOisProductDetailExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OrthopaedicOperationPlanOisProductDetailExample() {
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

        public Criteria andOis_ins_idIsNull() {
            addCriterion("ois_ins_id is null");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idIsNotNull() {
            addCriterion("ois_ins_id is not null");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idEqualTo(Long value) {
            addCriterion("ois_ins_id =", value, "ois_ins_id");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idNotEqualTo(Long value) {
            addCriterion("ois_ins_id <>", value, "ois_ins_id");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idGreaterThan(Long value) {
            addCriterion("ois_ins_id >", value, "ois_ins_id");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idGreaterThanOrEqualTo(Long value) {
            addCriterion("ois_ins_id >=", value, "ois_ins_id");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idLessThan(Long value) {
            addCriterion("ois_ins_id <", value, "ois_ins_id");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idLessThanOrEqualTo(Long value) {
            addCriterion("ois_ins_id <=", value, "ois_ins_id");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idIn(List<Long> values) {
            addCriterion("ois_ins_id in", values, "ois_ins_id");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idNotIn(List<Long> values) {
            addCriterion("ois_ins_id not in", values, "ois_ins_id");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idBetween(Long value1, Long value2) {
            addCriterion("ois_ins_id between", value1, value2, "ois_ins_id");
            return (Criteria) this;
        }

        public Criteria andOis_ins_idNotBetween(Long value1, Long value2) {
            addCriterion("ois_ins_id not between", value1, value2, "ois_ins_id");
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

        public Criteria andProduct_vendor_inner_snIsNull() {
            addCriterion("product_vendor_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snIsNotNull() {
            addCriterion("product_vendor_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snEqualTo(Integer value) {
            addCriterion("product_vendor_inner_sn =", value, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snNotEqualTo(Integer value) {
            addCriterion("product_vendor_inner_sn <>", value, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snGreaterThan(Integer value) {
            addCriterion("product_vendor_inner_sn >", value, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snGreaterThanOrEqualTo(Integer value) {
            addCriterion("product_vendor_inner_sn >=", value, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snLessThan(Integer value) {
            addCriterion("product_vendor_inner_sn <", value, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snLessThanOrEqualTo(Integer value) {
            addCriterion("product_vendor_inner_sn <=", value, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snIn(List<Integer> values) {
            addCriterion("product_vendor_inner_sn in", values, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snNotIn(List<Integer> values) {
            addCriterion("product_vendor_inner_sn not in", values, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snBetween(Integer value1, Integer value2) {
            addCriterion("product_vendor_inner_sn between", value1, value2, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_inner_snNotBetween(Integer value1, Integer value2) {
            addCriterion("product_vendor_inner_sn not between", value1, value2, "product_vendor_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snIsNull() {
            addCriterion("product_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snIsNotNull() {
            addCriterion("product_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snEqualTo(Integer value) {
            addCriterion("product_inner_sn =", value, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snNotEqualTo(Integer value) {
            addCriterion("product_inner_sn <>", value, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snGreaterThan(Integer value) {
            addCriterion("product_inner_sn >", value, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snGreaterThanOrEqualTo(Integer value) {
            addCriterion("product_inner_sn >=", value, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snLessThan(Integer value) {
            addCriterion("product_inner_sn <", value, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snLessThanOrEqualTo(Integer value) {
            addCriterion("product_inner_sn <=", value, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snIn(List<Integer> values) {
            addCriterion("product_inner_sn in", values, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snNotIn(List<Integer> values) {
            addCriterion("product_inner_sn not in", values, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snBetween(Integer value1, Integer value2) {
            addCriterion("product_inner_sn between", value1, value2, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andProduct_inner_snNotBetween(Integer value1, Integer value2) {
            addCriterion("product_inner_sn not between", value1, value2, "product_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snIsNull() {
            addCriterion("specification_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snIsNotNull() {
            addCriterion("specification_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snEqualTo(Short value) {
            addCriterion("specification_inner_sn =", value, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snNotEqualTo(Short value) {
            addCriterion("specification_inner_sn <>", value, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snGreaterThan(Short value) {
            addCriterion("specification_inner_sn >", value, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snGreaterThanOrEqualTo(Short value) {
            addCriterion("specification_inner_sn >=", value, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snLessThan(Short value) {
            addCriterion("specification_inner_sn <", value, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snLessThanOrEqualTo(Short value) {
            addCriterion("specification_inner_sn <=", value, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snIn(List<Short> values) {
            addCriterion("specification_inner_sn in", values, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snNotIn(List<Short> values) {
            addCriterion("specification_inner_sn not in", values, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snBetween(Short value1, Short value2) {
            addCriterion("specification_inner_sn between", value1, value2, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andSpecification_inner_snNotBetween(Short value1, Short value2) {
            addCriterion("specification_inner_sn not between", value1, value2, "specification_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snIsNull() {
            addCriterion("unit_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snIsNotNull() {
            addCriterion("unit_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snEqualTo(Short value) {
            addCriterion("unit_inner_sn =", value, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snNotEqualTo(Short value) {
            addCriterion("unit_inner_sn <>", value, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snGreaterThan(Short value) {
            addCriterion("unit_inner_sn >", value, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snGreaterThanOrEqualTo(Short value) {
            addCriterion("unit_inner_sn >=", value, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snLessThan(Short value) {
            addCriterion("unit_inner_sn <", value, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snLessThanOrEqualTo(Short value) {
            addCriterion("unit_inner_sn <=", value, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snIn(List<Short> values) {
            addCriterion("unit_inner_sn in", values, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snNotIn(List<Short> values) {
            addCriterion("unit_inner_sn not in", values, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snBetween(Short value1, Short value2) {
            addCriterion("unit_inner_sn between", value1, value2, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andUnit_inner_snNotBetween(Short value1, Short value2) {
            addCriterion("unit_inner_sn not between", value1, value2, "unit_inner_sn");
            return (Criteria) this;
        }

        public Criteria andQuantityIsNull() {
            addCriterion("quantity is null");
            return (Criteria) this;
        }

        public Criteria andQuantityIsNotNull() {
            addCriterion("quantity is not null");
            return (Criteria) this;
        }

        public Criteria andQuantityEqualTo(Long value) {
            addCriterion("quantity =", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityNotEqualTo(Long value) {
            addCriterion("quantity <>", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityGreaterThan(Long value) {
            addCriterion("quantity >", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityGreaterThanOrEqualTo(Long value) {
            addCriterion("quantity >=", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityLessThan(Long value) {
            addCriterion("quantity <", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityLessThanOrEqualTo(Long value) {
            addCriterion("quantity <=", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityIn(List<Long> values) {
            addCriterion("quantity in", values, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityNotIn(List<Long> values) {
            addCriterion("quantity not in", values, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityBetween(Long value1, Long value2) {
            addCriterion("quantity between", value1, value2, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityNotBetween(Long value1, Long value2) {
            addCriterion("quantity not between", value1, value2, "quantity");
            return (Criteria) this;
        }

        public Criteria andUnit_priceIsNull() {
            addCriterion("unit_price is null");
            return (Criteria) this;
        }

        public Criteria andUnit_priceIsNotNull() {
            addCriterion("unit_price is not null");
            return (Criteria) this;
        }

        public Criteria andUnit_priceEqualTo(Long value) {
            addCriterion("unit_price =", value, "unit_price");
            return (Criteria) this;
        }

        public Criteria andUnit_priceNotEqualTo(Long value) {
            addCriterion("unit_price <>", value, "unit_price");
            return (Criteria) this;
        }

        public Criteria andUnit_priceGreaterThan(Long value) {
            addCriterion("unit_price >", value, "unit_price");
            return (Criteria) this;
        }

        public Criteria andUnit_priceGreaterThanOrEqualTo(Long value) {
            addCriterion("unit_price >=", value, "unit_price");
            return (Criteria) this;
        }

        public Criteria andUnit_priceLessThan(Long value) {
            addCriterion("unit_price <", value, "unit_price");
            return (Criteria) this;
        }

        public Criteria andUnit_priceLessThanOrEqualTo(Long value) {
            addCriterion("unit_price <=", value, "unit_price");
            return (Criteria) this;
        }

        public Criteria andUnit_priceIn(List<Long> values) {
            addCriterion("unit_price in", values, "unit_price");
            return (Criteria) this;
        }

        public Criteria andUnit_priceNotIn(List<Long> values) {
            addCriterion("unit_price not in", values, "unit_price");
            return (Criteria) this;
        }

        public Criteria andUnit_priceBetween(Long value1, Long value2) {
            addCriterion("unit_price between", value1, value2, "unit_price");
            return (Criteria) this;
        }

        public Criteria andUnit_priceNotBetween(Long value1, Long value2) {
            addCriterion("unit_price not between", value1, value2, "unit_price");
            return (Criteria) this;
        }

        public Criteria andSort_numberIsNull() {
            addCriterion("sort_number is null");
            return (Criteria) this;
        }

        public Criteria andSort_numberIsNotNull() {
            addCriterion("sort_number is not null");
            return (Criteria) this;
        }

        public Criteria andSort_numberEqualTo(Short value) {
            addCriterion("sort_number =", value, "sort_number");
            return (Criteria) this;
        }

        public Criteria andSort_numberNotEqualTo(Short value) {
            addCriterion("sort_number <>", value, "sort_number");
            return (Criteria) this;
        }

        public Criteria andSort_numberGreaterThan(Short value) {
            addCriterion("sort_number >", value, "sort_number");
            return (Criteria) this;
        }

        public Criteria andSort_numberGreaterThanOrEqualTo(Short value) {
            addCriterion("sort_number >=", value, "sort_number");
            return (Criteria) this;
        }

        public Criteria andSort_numberLessThan(Short value) {
            addCriterion("sort_number <", value, "sort_number");
            return (Criteria) this;
        }

        public Criteria andSort_numberLessThanOrEqualTo(Short value) {
            addCriterion("sort_number <=", value, "sort_number");
            return (Criteria) this;
        }

        public Criteria andSort_numberIn(List<Short> values) {
            addCriterion("sort_number in", values, "sort_number");
            return (Criteria) this;
        }

        public Criteria andSort_numberNotIn(List<Short> values) {
            addCriterion("sort_number not in", values, "sort_number");
            return (Criteria) this;
        }

        public Criteria andSort_numberBetween(Short value1, Short value2) {
            addCriterion("sort_number between", value1, value2, "sort_number");
            return (Criteria) this;
        }

        public Criteria andSort_numberNotBetween(Short value1, Short value2) {
            addCriterion("sort_number not between", value1, value2, "sort_number");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameIsNull() {
            addCriterion("product_vendor_name is null");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameIsNotNull() {
            addCriterion("product_vendor_name is not null");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameEqualTo(String value) {
            addCriterion("product_vendor_name =", value, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameNotEqualTo(String value) {
            addCriterion("product_vendor_name <>", value, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameGreaterThan(String value) {
            addCriterion("product_vendor_name >", value, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameGreaterThanOrEqualTo(String value) {
            addCriterion("product_vendor_name >=", value, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameLessThan(String value) {
            addCriterion("product_vendor_name <", value, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameLessThanOrEqualTo(String value) {
            addCriterion("product_vendor_name <=", value, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameLike(String value) {
            addCriterion("product_vendor_name like", value, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameNotLike(String value) {
            addCriterion("product_vendor_name not like", value, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameIn(List<String> values) {
            addCriterion("product_vendor_name in", values, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameNotIn(List<String> values) {
            addCriterion("product_vendor_name not in", values, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameBetween(String value1, String value2) {
            addCriterion("product_vendor_name between", value1, value2, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_vendor_nameNotBetween(String value1, String value2) {
            addCriterion("product_vendor_name not between", value1, value2, "product_vendor_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameIsNull() {
            addCriterion("product_name is null");
            return (Criteria) this;
        }

        public Criteria andProduct_nameIsNotNull() {
            addCriterion("product_name is not null");
            return (Criteria) this;
        }

        public Criteria andProduct_nameEqualTo(String value) {
            addCriterion("product_name =", value, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameNotEqualTo(String value) {
            addCriterion("product_name <>", value, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameGreaterThan(String value) {
            addCriterion("product_name >", value, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameGreaterThanOrEqualTo(String value) {
            addCriterion("product_name >=", value, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameLessThan(String value) {
            addCriterion("product_name <", value, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameLessThanOrEqualTo(String value) {
            addCriterion("product_name <=", value, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameLike(String value) {
            addCriterion("product_name like", value, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameNotLike(String value) {
            addCriterion("product_name not like", value, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameIn(List<String> values) {
            addCriterion("product_name in", values, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameNotIn(List<String> values) {
            addCriterion("product_name not in", values, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameBetween(String value1, String value2) {
            addCriterion("product_name between", value1, value2, "product_name");
            return (Criteria) this;
        }

        public Criteria andProduct_nameNotBetween(String value1, String value2) {
            addCriterion("product_name not between", value1, value2, "product_name");
            return (Criteria) this;
        }

        public Criteria andSpecificationIsNull() {
            addCriterion("specification is null");
            return (Criteria) this;
        }

        public Criteria andSpecificationIsNotNull() {
            addCriterion("specification is not null");
            return (Criteria) this;
        }

        public Criteria andSpecificationEqualTo(String value) {
            addCriterion("specification =", value, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationNotEqualTo(String value) {
            addCriterion("specification <>", value, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationGreaterThan(String value) {
            addCriterion("specification >", value, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationGreaterThanOrEqualTo(String value) {
            addCriterion("specification >=", value, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationLessThan(String value) {
            addCriterion("specification <", value, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationLessThanOrEqualTo(String value) {
            addCriterion("specification <=", value, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationLike(String value) {
            addCriterion("specification like", value, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationNotLike(String value) {
            addCriterion("specification not like", value, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationIn(List<String> values) {
            addCriterion("specification in", values, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationNotIn(List<String> values) {
            addCriterion("specification not in", values, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationBetween(String value1, String value2) {
            addCriterion("specification between", value1, value2, "specification");
            return (Criteria) this;
        }

        public Criteria andSpecificationNotBetween(String value1, String value2) {
            addCriterion("specification not between", value1, value2, "specification");
            return (Criteria) this;
        }

        public Criteria andUnitIsNull() {
            addCriterion("unit is null");
            return (Criteria) this;
        }

        public Criteria andUnitIsNotNull() {
            addCriterion("unit is not null");
            return (Criteria) this;
        }

        public Criteria andUnitEqualTo(String value) {
            addCriterion("unit =", value, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitNotEqualTo(String value) {
            addCriterion("unit <>", value, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitGreaterThan(String value) {
            addCriterion("unit >", value, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitGreaterThanOrEqualTo(String value) {
            addCriterion("unit >=", value, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitLessThan(String value) {
            addCriterion("unit <", value, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitLessThanOrEqualTo(String value) {
            addCriterion("unit <=", value, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitLike(String value) {
            addCriterion("unit like", value, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitNotLike(String value) {
            addCriterion("unit not like", value, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitIn(List<String> values) {
            addCriterion("unit in", values, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitNotIn(List<String> values) {
            addCriterion("unit not in", values, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitBetween(String value1, String value2) {
            addCriterion("unit between", value1, value2, "unit");
            return (Criteria) this;
        }

        public Criteria andUnitNotBetween(String value1, String value2) {
            addCriterion("unit not between", value1, value2, "unit");
            return (Criteria) this;
        }

        public Criteria andBack_quantityIsNull() {
            addCriterion("back_quantity is null");
            return (Criteria) this;
        }

        public Criteria andBack_quantityIsNotNull() {
            addCriterion("back_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andBack_quantityEqualTo(Long value) {
            addCriterion("back_quantity =", value, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andBack_quantityNotEqualTo(Long value) {
            addCriterion("back_quantity <>", value, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andBack_quantityGreaterThan(Long value) {
            addCriterion("back_quantity >", value, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andBack_quantityGreaterThanOrEqualTo(Long value) {
            addCriterion("back_quantity >=", value, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andBack_quantityLessThan(Long value) {
            addCriterion("back_quantity <", value, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andBack_quantityLessThanOrEqualTo(Long value) {
            addCriterion("back_quantity <=", value, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andBack_quantityIn(List<Long> values) {
            addCriterion("back_quantity in", values, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andBack_quantityNotIn(List<Long> values) {
            addCriterion("back_quantity not in", values, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andBack_quantityBetween(Long value1, Long value2) {
            addCriterion("back_quantity between", value1, value2, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andBack_quantityNotBetween(Long value1, Long value2) {
            addCriterion("back_quantity not between", value1, value2, "back_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityIsNull() {
            addCriterion("sale_quantity is null");
            return (Criteria) this;
        }

        public Criteria andSale_quantityIsNotNull() {
            addCriterion("sale_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andSale_quantityEqualTo(Long value) {
            addCriterion("sale_quantity =", value, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityNotEqualTo(Long value) {
            addCriterion("sale_quantity <>", value, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityGreaterThan(Long value) {
            addCriterion("sale_quantity >", value, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityGreaterThanOrEqualTo(Long value) {
            addCriterion("sale_quantity >=", value, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityLessThan(Long value) {
            addCriterion("sale_quantity <", value, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityLessThanOrEqualTo(Long value) {
            addCriterion("sale_quantity <=", value, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityIn(List<Long> values) {
            addCriterion("sale_quantity in", values, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityNotIn(List<Long> values) {
            addCriterion("sale_quantity not in", values, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityBetween(Long value1, Long value2) {
            addCriterion("sale_quantity between", value1, value2, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andSale_quantityNotBetween(Long value1, Long value2) {
            addCriterion("sale_quantity not between", value1, value2, "sale_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityIsNull() {
            addCriterion("loss_quantity is null");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityIsNotNull() {
            addCriterion("loss_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityEqualTo(Long value) {
            addCriterion("loss_quantity =", value, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityNotEqualTo(Long value) {
            addCriterion("loss_quantity <>", value, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityGreaterThan(Long value) {
            addCriterion("loss_quantity >", value, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityGreaterThanOrEqualTo(Long value) {
            addCriterion("loss_quantity >=", value, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityLessThan(Long value) {
            addCriterion("loss_quantity <", value, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityLessThanOrEqualTo(Long value) {
            addCriterion("loss_quantity <=", value, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityIn(List<Long> values) {
            addCriterion("loss_quantity in", values, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityNotIn(List<Long> values) {
            addCriterion("loss_quantity not in", values, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityBetween(Long value1, Long value2) {
            addCriterion("loss_quantity between", value1, value2, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_quantityNotBetween(Long value1, Long value2) {
            addCriterion("loss_quantity not between", value1, value2, "loss_quantity");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonIsNull() {
            addCriterion("loss_reason is null");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonIsNotNull() {
            addCriterion("loss_reason is not null");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonEqualTo(String value) {
            addCriterion("loss_reason =", value, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonNotEqualTo(String value) {
            addCriterion("loss_reason <>", value, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonGreaterThan(String value) {
            addCriterion("loss_reason >", value, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonGreaterThanOrEqualTo(String value) {
            addCriterion("loss_reason >=", value, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonLessThan(String value) {
            addCriterion("loss_reason <", value, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonLessThanOrEqualTo(String value) {
            addCriterion("loss_reason <=", value, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonLike(String value) {
            addCriterion("loss_reason like", value, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonNotLike(String value) {
            addCriterion("loss_reason not like", value, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonIn(List<String> values) {
            addCriterion("loss_reason in", values, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonNotIn(List<String> values) {
            addCriterion("loss_reason not in", values, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonBetween(String value1, String value2) {
            addCriterion("loss_reason between", value1, value2, "loss_reason");
            return (Criteria) this;
        }

        public Criteria andLoss_reasonNotBetween(String value1, String value2) {
            addCriterion("loss_reason not between", value1, value2, "loss_reason");
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