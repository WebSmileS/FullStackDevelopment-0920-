package com.yrt.project.modular.surgery.plan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yrt.project.modular.surgery.OperationPlanStatus;

import lombok.Getter;
import lombok.Setter;

public class OrthopaedicOperationPlanExample {
	@Getter @Setter
	private String hospital_name;
	
	@Getter @Setter
	private Long hospital_inner_sn;
	@Getter @Setter
	private List<Byte> types;
	@Getter @Setter
	private List<OperationPlanStatus> statuses;
	@Getter @Setter
	private Date operation_time_from;
	@Getter @Setter
	private Date operation_time_to;
	@Getter @Setter
	private String q;
	///////////////////////////////////////////////
	
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OrthopaedicOperationPlanExample() {
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

        public Criteria and(String condition, Object value) {
        	if(value==null) {
        		addCriterion(condition);
        	} else {
        		 criteria.add(new Criterion(condition, value));
        	}
        	return  (Criteria) this;
        }
        
        public Criteria andBetween(String condition, Object value1, Object value2) {
        	criteria.add(new Criterion(condition, value1, value2));
        	return  (Criteria) this;
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

        public Criteria andCreator_user_inner_snIsNull() {
            addCriterion("creator_user_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snIsNotNull() {
            addCriterion("creator_user_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snEqualTo(Long value) {
            addCriterion("creator_user_inner_sn =", value, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snNotEqualTo(Long value) {
            addCriterion("creator_user_inner_sn <>", value, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snGreaterThan(Long value) {
            addCriterion("creator_user_inner_sn >", value, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("creator_user_inner_sn >=", value, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snLessThan(Long value) {
            addCriterion("creator_user_inner_sn <", value, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("creator_user_inner_sn <=", value, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snIn(List<Long> values) {
            addCriterion("creator_user_inner_sn in", values, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snNotIn(List<Long> values) {
            addCriterion("creator_user_inner_sn not in", values, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snBetween(Long value1, Long value2) {
            addCriterion("creator_user_inner_sn between", value1, value2, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("creator_user_inner_sn not between", value1, value2, "creator_user_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameIsNull() {
            addCriterion("creator_user_name is null");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameIsNotNull() {
            addCriterion("creator_user_name is not null");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameEqualTo(String value) {
            addCriterion("creator_user_name =", value, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameNotEqualTo(String value) {
            addCriterion("creator_user_name <>", value, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameGreaterThan(String value) {
            addCriterion("creator_user_name >", value, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameGreaterThanOrEqualTo(String value) {
            addCriterion("creator_user_name >=", value, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameLessThan(String value) {
            addCriterion("creator_user_name <", value, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameLessThanOrEqualTo(String value) {
            addCriterion("creator_user_name <=", value, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameLike(String value) {
            addCriterion("creator_user_name like", value, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameNotLike(String value) {
            addCriterion("creator_user_name not like", value, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameIn(List<String> values) {
            addCriterion("creator_user_name in", values, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameNotIn(List<String> values) {
            addCriterion("creator_user_name not in", values, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameBetween(String value1, String value2) {
            addCriterion("creator_user_name between", value1, value2, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_user_nameNotBetween(String value1, String value2) {
            addCriterion("creator_user_name not between", value1, value2, "creator_user_name");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snIsNull() {
            addCriterion("creator_org_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snIsNotNull() {
            addCriterion("creator_org_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snEqualTo(Long value) {
            addCriterion("creator_org_inner_sn =", value, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snNotEqualTo(Long value) {
            addCriterion("creator_org_inner_sn <>", value, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snGreaterThan(Long value) {
            addCriterion("creator_org_inner_sn >", value, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("creator_org_inner_sn >=", value, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snLessThan(Long value) {
            addCriterion("creator_org_inner_sn <", value, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("creator_org_inner_sn <=", value, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snIn(List<Long> values) {
            addCriterion("creator_org_inner_sn in", values, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snNotIn(List<Long> values) {
            addCriterion("creator_org_inner_sn not in", values, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snBetween(Long value1, Long value2) {
            addCriterion("creator_org_inner_sn between", value1, value2, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("creator_org_inner_sn not between", value1, value2, "creator_org_inner_sn");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeIsNull() {
            addCriterion("creator_org_type is null");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeIsNotNull() {
            addCriterion("creator_org_type is not null");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeEqualTo(Byte value) {
            addCriterion("creator_org_type =", value, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeNotEqualTo(Byte value) {
            addCriterion("creator_org_type <>", value, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeGreaterThan(Byte value) {
            addCriterion("creator_org_type >", value, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeGreaterThanOrEqualTo(Byte value) {
            addCriterion("creator_org_type >=", value, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeLessThan(Byte value) {
            addCriterion("creator_org_type <", value, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeLessThanOrEqualTo(Byte value) {
            addCriterion("creator_org_type <=", value, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeIn(List<Byte> values) {
            addCriterion("creator_org_type in", values, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeNotIn(List<Byte> values) {
            addCriterion("creator_org_type not in", values, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeBetween(Byte value1, Byte value2) {
            addCriterion("creator_org_type between", value1, value2, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andCreator_org_typeNotBetween(Byte value1, Byte value2) {
            addCriterion("creator_org_type not between", value1, value2, "creator_org_type");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snIsNull() {
            addCriterion("doctor_employee_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snIsNotNull() {
            addCriterion("doctor_employee_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snEqualTo(Long value) {
            addCriterion("doctor_employee_inner_sn =", value, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snNotEqualTo(Long value) {
            addCriterion("doctor_employee_inner_sn <>", value, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snGreaterThan(Long value) {
            addCriterion("doctor_employee_inner_sn >", value, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("doctor_employee_inner_sn >=", value, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snLessThan(Long value) {
            addCriterion("doctor_employee_inner_sn <", value, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("doctor_employee_inner_sn <=", value, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snIn(List<Long> values) {
            addCriterion("doctor_employee_inner_sn in", values, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snNotIn(List<Long> values) {
            addCriterion("doctor_employee_inner_sn not in", values, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snBetween(Long value1, Long value2) {
            addCriterion("doctor_employee_inner_sn between", value1, value2, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_employee_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("doctor_employee_inner_sn not between", value1, value2, "doctor_employee_inner_sn");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameIsNull() {
            addCriterion("doctor_name is null");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameIsNotNull() {
            addCriterion("doctor_name is not null");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameEqualTo(String value) {
            addCriterion("doctor_name =", value, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameNotEqualTo(String value) {
            addCriterion("doctor_name <>", value, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameGreaterThan(String value) {
            addCriterion("doctor_name >", value, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameGreaterThanOrEqualTo(String value) {
            addCriterion("doctor_name >=", value, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameLessThan(String value) {
            addCriterion("doctor_name <", value, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameLessThanOrEqualTo(String value) {
            addCriterion("doctor_name <=", value, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameLike(String value) {
            addCriterion("doctor_name like", value, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameNotLike(String value) {
            addCriterion("doctor_name not like", value, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameIn(List<String> values) {
            addCriterion("doctor_name in", values, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameNotIn(List<String> values) {
            addCriterion("doctor_name not in", values, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameBetween(String value1, String value2) {
            addCriterion("doctor_name between", value1, value2, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andDoctor_nameNotBetween(String value1, String value2) {
            addCriterion("doctor_name not between", value1, value2, "doctor_name");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snIsNull() {
            addCriterion("hospital_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snIsNotNull() {
            addCriterion("hospital_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snEqualTo(Long value) {
            addCriterion("hospital_inner_sn =", value, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snNotEqualTo(Long value) {
            addCriterion("hospital_inner_sn <>", value, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snGreaterThan(Long value) {
            addCriterion("hospital_inner_sn >", value, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("hospital_inner_sn >=", value, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snLessThan(Long value) {
            addCriterion("hospital_inner_sn <", value, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("hospital_inner_sn <=", value, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snIn(List<Long> values) {
            addCriterion("hospital_inner_sn in", values, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snNotIn(List<Long> values) {
            addCriterion("hospital_inner_sn not in", values, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snBetween(Long value1, Long value2) {
            addCriterion("hospital_inner_sn between", value1, value2, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("hospital_inner_sn not between", value1, value2, "hospital_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_nameIsNull() {
            addCriterion("hospital_name is null");
            return (Criteria) this;
        }

        public Criteria andHospital_nameIsNotNull() {
            addCriterion("hospital_name is not null");
            return (Criteria) this;
        }

        public Criteria andHospital_nameEqualTo(String value) {
            addCriterion("hospital_name =", value, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameNotEqualTo(String value) {
            addCriterion("hospital_name <>", value, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameGreaterThan(String value) {
            addCriterion("hospital_name >", value, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameGreaterThanOrEqualTo(String value) {
            addCriterion("hospital_name >=", value, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameLessThan(String value) {
            addCriterion("hospital_name <", value, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameLessThanOrEqualTo(String value) {
            addCriterion("hospital_name <=", value, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameLike(String value) {
            addCriterion("hospital_name like", value, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameNotLike(String value) {
            addCriterion("hospital_name not like", value, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameIn(List<String> values) {
            addCriterion("hospital_name in", values, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameNotIn(List<String> values) {
            addCriterion("hospital_name not in", values, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameBetween(String value1, String value2) {
            addCriterion("hospital_name between", value1, value2, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_nameNotBetween(String value1, String value2) {
            addCriterion("hospital_name not between", value1, value2, "hospital_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snIsNull() {
            addCriterion("hospital_department_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snIsNotNull() {
            addCriterion("hospital_department_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snEqualTo(Long value) {
            addCriterion("hospital_department_inner_sn =", value, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snNotEqualTo(Long value) {
            addCriterion("hospital_department_inner_sn <>", value, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snGreaterThan(Long value) {
            addCriterion("hospital_department_inner_sn >", value, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("hospital_department_inner_sn >=", value, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snLessThan(Long value) {
            addCriterion("hospital_department_inner_sn <", value, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("hospital_department_inner_sn <=", value, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snIn(List<Long> values) {
            addCriterion("hospital_department_inner_sn in", values, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snNotIn(List<Long> values) {
            addCriterion("hospital_department_inner_sn not in", values, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snBetween(Long value1, Long value2) {
            addCriterion("hospital_department_inner_sn between", value1, value2, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("hospital_department_inner_sn not between", value1, value2, "hospital_department_inner_sn");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameIsNull() {
            addCriterion("hospital_department_name is null");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameIsNotNull() {
            addCriterion("hospital_department_name is not null");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameEqualTo(String value) {
            addCriterion("hospital_department_name =", value, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameNotEqualTo(String value) {
            addCriterion("hospital_department_name <>", value, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameGreaterThan(String value) {
            addCriterion("hospital_department_name >", value, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameGreaterThanOrEqualTo(String value) {
            addCriterion("hospital_department_name >=", value, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameLessThan(String value) {
            addCriterion("hospital_department_name <", value, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameLessThanOrEqualTo(String value) {
            addCriterion("hospital_department_name <=", value, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameLike(String value) {
            addCriterion("hospital_department_name like", value, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameNotLike(String value) {
            addCriterion("hospital_department_name not like", value, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameIn(List<String> values) {
            addCriterion("hospital_department_name in", values, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameNotIn(List<String> values) {
            addCriterion("hospital_department_name not in", values, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameBetween(String value1, String value2) {
            addCriterion("hospital_department_name between", value1, value2, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andHospital_department_nameNotBetween(String value1, String value2) {
            addCriterion("hospital_department_name not between", value1, value2, "hospital_department_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameIsNull() {
            addCriterion("patient_name is null");
            return (Criteria) this;
        }

        public Criteria andPatient_nameIsNotNull() {
            addCriterion("patient_name is not null");
            return (Criteria) this;
        }

        public Criteria andPatient_nameEqualTo(String value) {
            addCriterion("patient_name =", value, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameNotEqualTo(String value) {
            addCriterion("patient_name <>", value, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameGreaterThan(String value) {
            addCriterion("patient_name >", value, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameGreaterThanOrEqualTo(String value) {
            addCriterion("patient_name >=", value, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameLessThan(String value) {
            addCriterion("patient_name <", value, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameLessThanOrEqualTo(String value) {
            addCriterion("patient_name <=", value, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameLike(String value) {
            addCriterion("patient_name like", value, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameNotLike(String value) {
            addCriterion("patient_name not like", value, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameIn(List<String> values) {
            addCriterion("patient_name in", values, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameNotIn(List<String> values) {
            addCriterion("patient_name not in", values, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameBetween(String value1, String value2) {
            addCriterion("patient_name between", value1, value2, "patient_name");
            return (Criteria) this;
        }

        public Criteria andPatient_nameNotBetween(String value1, String value2) {
            addCriterion("patient_name not between", value1, value2, "patient_name");
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

        public Criteria andOperation_timeIsNull() {
            addCriterion("operation_time is null");
            return (Criteria) this;
        }

        public Criteria andOperation_timeIsNotNull() {
            addCriterion("operation_time is not null");
            return (Criteria) this;
        }

        public Criteria andOperation_timeEqualTo(Date value) {
            addCriterion("operation_time =", value, "operation_time");
            return (Criteria) this;
        }

        public Criteria andOperation_timeNotEqualTo(Date value) {
            addCriterion("operation_time <>", value, "operation_time");
            return (Criteria) this;
        }

        public Criteria andOperation_timeGreaterThan(Date value) {
            addCriterion("operation_time >", value, "operation_time");
            return (Criteria) this;
        }

        public Criteria andOperation_timeGreaterThanOrEqualTo(Date value) {
            addCriterion("operation_time >=", value, "operation_time");
            return (Criteria) this;
        }

        public Criteria andOperation_timeLessThan(Date value) {
            addCriterion("operation_time <", value, "operation_time");
            return (Criteria) this;
        }

        public Criteria andOperation_timeLessThanOrEqualTo(Date value) {
            addCriterion("operation_time <=", value, "operation_time");
            return (Criteria) this;
        }

        public Criteria andOperation_timeIn(List<Date> values) {
            addCriterion("operation_time in", values, "operation_time");
            return (Criteria) this;
        }

        public Criteria andOperation_timeNotIn(List<Date> values) {
            addCriterion("operation_time not in", values, "operation_time");
            return (Criteria) this;
        }

        public Criteria andOperation_timeBetween(Date value1, Date value2) {
            addCriterion("operation_time between", value1, value2, "operation_time");
            return (Criteria) this;
        }

        public Criteria andOperation_timeNotBetween(Date value1, Date value2) {
            addCriterion("operation_time not between", value1, value2, "operation_time");
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

        public Criteria andStatusEqualTo(OperationPlanStatus value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(OperationPlanStatus value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(OperationPlanStatus value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(OperationPlanStatus value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(OperationPlanStatus value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(OperationPlanStatus value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<OperationPlanStatus> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<OperationPlanStatus> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(OperationPlanStatus value1, OperationPlanStatus value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(OperationPlanStatus value1, OperationPlanStatus value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andIs_deleteIsNull() {
            addCriterion("is_delete is null");
            return (Criteria) this;
        }

        public Criteria andIs_deleteIsNotNull() {
            addCriterion("is_delete is not null");
            return (Criteria) this;
        }

        public Criteria andIs_deleteEqualTo(Boolean value) {
            addCriterion("is_delete =", value, "is_delete");
            return (Criteria) this;
        }

        public Criteria andIs_deleteNotEqualTo(Boolean value) {
            addCriterion("is_delete <>", value, "is_delete");
            return (Criteria) this;
        }

        public Criteria andIs_deleteGreaterThan(Boolean value) {
            addCriterion("is_delete >", value, "is_delete");
            return (Criteria) this;
        }

        public Criteria andIs_deleteGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_delete >=", value, "is_delete");
            return (Criteria) this;
        }

        public Criteria andIs_deleteLessThan(Boolean value) {
            addCriterion("is_delete <", value, "is_delete");
            return (Criteria) this;
        }

        public Criteria andIs_deleteLessThanOrEqualTo(Boolean value) {
            addCriterion("is_delete <=", value, "is_delete");
            return (Criteria) this;
        }

        public Criteria andIs_deleteIn(List<Boolean> values) {
            addCriterion("is_delete in", values, "is_delete");
            return (Criteria) this;
        }

        public Criteria andIs_deleteNotIn(List<Boolean> values) {
            addCriterion("is_delete not in", values, "is_delete");
            return (Criteria) this;
        }

        public Criteria andIs_deleteBetween(Boolean value1, Boolean value2) {
            addCriterion("is_delete between", value1, value2, "is_delete");
            return (Criteria) this;
        }

        public Criteria andIs_deleteNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_delete not between", value1, value2, "is_delete");
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