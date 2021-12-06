package com.yrt.project.modular.surgery.cust;

import java.util.ArrayList;
import java.util.List;

import com.yrt.project.api.business.surgery.cust.vo.SearchScope;
import com.yrt.project.modular.surgery.OisKitStatus;

import lombok.Getter;
import lombok.Setter;

public class OrthopaedicCustOisExample {
	
	/**
	 * selectByExampleExt: PALTFORM/PRIVATE/ALL(union platform & cust)
	 */
	@Getter @Setter
	private String searchScope = SearchScope.PRIVATE.name();
	
	/**
	 * selectByExampleExt, (for Dealer): only including owned cust-ois (effect on cust-ois part).
	 */
	@Getter @Setter
	private Long owner_dealer_inner_sn;
	
	/**
	 * selectByExampleExt, (for Hospital): join intention.
	 */
	@Getter @Setter
	private Long hospital_inner_sn;
	/**
	 * selectByExampleExt: join path or not 
	 */
	@Getter @Setter
	private Long op_inner_sn;
	
	/**
	 * selectByExampleExt: name/keywords like
	 */
	@Getter @Setter
	private String q;
	
	////////////////////////////////////////////////////
	
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OrthopaedicCustOisExample() {
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

        public Criteria andOwner_dealer_inner_snIsNull() {
            addCriterion("owner_dealer_inner_sn is null");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snIsNotNull() {
            addCriterion("owner_dealer_inner_sn is not null");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snEqualTo(Long value) {
            addCriterion("owner_dealer_inner_sn =", value, "owner_dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snNotEqualTo(Long value) {
            addCriterion("owner_dealer_inner_sn <>", value, "owner_dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snGreaterThan(Long value) {
            addCriterion("owner_dealer_inner_sn >", value, "owner_dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snGreaterThanOrEqualTo(Long value) {
            addCriterion("owner_dealer_inner_sn >=", value, "owner_dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snLessThan(Long value) {
            addCriterion("owner_dealer_inner_sn <", value, "owner_dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snLessThanOrEqualTo(Long value) {
            addCriterion("owner_dealer_inner_sn <=", value, "owner_dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snIn(List<Long> values) {
            addCriterion("owner_dealer_inner_sn in", values, "owner_dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snNotIn(List<Long> values) {
            addCriterion("owner_dealer_inner_sn not in", values, "owner_dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snBetween(Long value1, Long value2) {
            addCriterion("owner_dealer_inner_sn between", value1, value2, "owner_dealer_inner_sn");
            return (Criteria) this;
        }

        public Criteria andOwner_dealer_inner_snNotBetween(Long value1, Long value2) {
            addCriterion("owner_dealer_inner_sn not between", value1, value2, "owner_dealer_inner_sn");
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

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(OisKitStatus value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(OisKitStatus value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(OisKitStatus value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(OisKitStatus value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(OisKitStatus value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(OisKitStatus value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<OisKitStatus> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<OisKitStatus> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(OisKitStatus value1, OisKitStatus value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(OisKitStatus value1, OisKitStatus value2) {
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