package com.yrt.project.modular.surgery.cust;

import java.util.ArrayList;
import java.util.List;

public class OrthopaedicCustOisKitExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OrthopaedicCustOisKitExample() {
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

        public Criteria andOis_idIsNull() {
            addCriterion("ois_id is null");
            return (Criteria) this;
        }

        public Criteria andOis_idIsNotNull() {
            addCriterion("ois_id is not null");
            return (Criteria) this;
        }

        public Criteria andOis_idEqualTo(Long value) {
            addCriterion("ois_id =", value, "ois_id");
            return (Criteria) this;
        }

        public Criteria andOis_idNotEqualTo(Long value) {
            addCriterion("ois_id <>", value, "ois_id");
            return (Criteria) this;
        }

        public Criteria andOis_idGreaterThan(Long value) {
            addCriterion("ois_id >", value, "ois_id");
            return (Criteria) this;
        }

        public Criteria andOis_idGreaterThanOrEqualTo(Long value) {
            addCriterion("ois_id >=", value, "ois_id");
            return (Criteria) this;
        }

        public Criteria andOis_idLessThan(Long value) {
            addCriterion("ois_id <", value, "ois_id");
            return (Criteria) this;
        }

        public Criteria andOis_idLessThanOrEqualTo(Long value) {
            addCriterion("ois_id <=", value, "ois_id");
            return (Criteria) this;
        }

        public Criteria andOis_idIn(List<Long> values) {
            addCriterion("ois_id in", values, "ois_id");
            return (Criteria) this;
        }

        public Criteria andOis_idNotIn(List<Long> values) {
            addCriterion("ois_id not in", values, "ois_id");
            return (Criteria) this;
        }

        public Criteria andOis_idBetween(Long value1, Long value2) {
            addCriterion("ois_id between", value1, value2, "ois_id");
            return (Criteria) this;
        }

        public Criteria andOis_idNotBetween(Long value1, Long value2) {
            addCriterion("ois_id not between", value1, value2, "ois_id");
            return (Criteria) this;
        }

        public Criteria andKit_idIsNull() {
            addCriterion("kit_id is null");
            return (Criteria) this;
        }

        public Criteria andKit_idIsNotNull() {
            addCriterion("kit_id is not null");
            return (Criteria) this;
        }

        public Criteria andKit_idEqualTo(Long value) {
            addCriterion("kit_id =", value, "kit_id");
            return (Criteria) this;
        }

        public Criteria andKit_idNotEqualTo(Long value) {
            addCriterion("kit_id <>", value, "kit_id");
            return (Criteria) this;
        }

        public Criteria andKit_idGreaterThan(Long value) {
            addCriterion("kit_id >", value, "kit_id");
            return (Criteria) this;
        }

        public Criteria andKit_idGreaterThanOrEqualTo(Long value) {
            addCriterion("kit_id >=", value, "kit_id");
            return (Criteria) this;
        }

        public Criteria andKit_idLessThan(Long value) {
            addCriterion("kit_id <", value, "kit_id");
            return (Criteria) this;
        }

        public Criteria andKit_idLessThanOrEqualTo(Long value) {
            addCriterion("kit_id <=", value, "kit_id");
            return (Criteria) this;
        }

        public Criteria andKit_idIn(List<Long> values) {
            addCriterion("kit_id in", values, "kit_id");
            return (Criteria) this;
        }

        public Criteria andKit_idNotIn(List<Long> values) {
            addCriterion("kit_id not in", values, "kit_id");
            return (Criteria) this;
        }

        public Criteria andKit_idBetween(Long value1, Long value2) {
            addCriterion("kit_id between", value1, value2, "kit_id");
            return (Criteria) this;
        }

        public Criteria andKit_idNotBetween(Long value1, Long value2) {
            addCriterion("kit_id not between", value1, value2, "kit_id");
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