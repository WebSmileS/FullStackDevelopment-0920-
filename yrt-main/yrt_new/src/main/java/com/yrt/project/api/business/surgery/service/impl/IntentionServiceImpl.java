package com.yrt.project.api.business.surgery.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.lang.KeyValue;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.surgery.service.IIntentionService;
import com.yrt.project.api.business.surgery.vo.SearchIntentionForm;
import com.yrt.project.modular.surgery.domain.IntentionRelBrand;
import com.yrt.project.modular.surgery.domain.OrthopaedicIntention;
import com.yrt.project.modular.surgery.domain.OrthopaedicIntentionKey;
import com.yrt.project.modular.surgery.mapper.OrthopaedicIntentionMapper;

@Service
public class IntentionServiceImpl implements IIntentionService {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(IntentionServiceImpl.class);

	@Autowired
	private OrthopaedicIntentionMapper mapper;

	@Override
	@Transactional
	public OrthopaedicIntention addOrthopaedicIntention(OrthopaedicIntention form) {
		try {
			mapper.insertOrthopaedicIntention(form);
		} catch(DuplicateKeyException e) {
			throw new DBException(MessageUtils.message("db.add.fail", "骨科合作意向", "已存在(医院--经销商)不能重复添加"));
		}
		OrthopaedicIntentionKey key = OrthopaedicIntentionKey.of(form.getHospital_inner_sn(),
				form.getDealer_inner_sn());

		saveRelTypes(form, false);
		saveRelBrands(key, form.getRel_brands(), false);

		OrthopaedicIntention ret = mapper.selectIntentionInfo(key);
		return parseRelBrandsJson(ret);
	}

	@Override
	@Transactional
	public OrthopaedicIntention editOrthopaedicIntention(OrthopaedicIntention form) {
		if (mapper.updateIntention(form) != 1) {
			throw new DBException(MessageUtils.message("db.update.fail", "骨科合作意向"));
		}
		OrthopaedicIntentionKey key = OrthopaedicIntentionKey.of(form.getHospital_inner_sn(),
				form.getDealer_inner_sn());

		saveRelTypes(form, true);
		saveRelBrands(key, form.getRel_brands(), true);

		OrthopaedicIntention ret = mapper.selectIntentionInfo(key);
		return parseRelBrandsJson(ret);
	}

	@Override
	@Transactional
	public int delOrthopaedicIntention(Long hid, Long did) {
		OrthopaedicIntentionKey key = OrthopaedicIntentionKey.of(hid, did);
		mapper.deleteIntentionRefType(key); // 关联 手术类型
		mapper.deleteIntentionRefBrand(key);// 关联 品牌
		return mapper.deleteIntention(key);
	}

	@Override
	public List<OrthopaedicIntention> searchOrthopaedicIntentionList(SearchIntentionForm form) {
		OrthopaedicIntention condition = new OrthopaedicIntention();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case HOSPITAL:
			condition.setHospital_inner_sn(form.getOrg_id());
			condition.setDealer_name(form.getSearch_name());
			break;
		case DEALER:
			condition.setDealer_inner_sn(form.getOrg_id());
			condition.setHospital_name(form.getSearch_name());
			condition.setHospital_alias(form.getSearch_alias());
			break;
		default:
			break;
		}
		List<OrthopaedicIntention> list = mapper.searchIntention(condition);
		list.forEach(this::parseRelBrandsJson);
		return list;
	}

	@Override
	public OrthopaedicIntention selectOrthopaedicIntentionInfo(Long hid, Long did) {
		OrthopaedicIntention ret = mapper.selectIntentionInfo(OrthopaedicIntentionKey.of(hid, did));
		return this.parseRelBrandsJson(ret);
	}

	private void saveRelTypes(OrthopaedicIntention intention, boolean isEdit) {
		Long hid = intention.getHospital_inner_sn();
		Long did = intention.getDealer_inner_sn();

		if (isEdit) {
			OrthopaedicIntentionKey key = OrthopaedicIntentionKey.of(hid, did);
			mapper.deleteIntentionRefType(key);
		}
		mapper.insertIntentionRefType(hid, did, 0L, intention.getT0_status());
		mapper.insertIntentionRefType(hid, did, 1L, intention.getT1_status());
		mapper.insertIntentionRefType(hid, did, 2L, intention.getT2_status());
		mapper.insertIntentionRefType(hid, did, 3L, intention.getT3_status());
	}

	private void saveRelBrands(OrthopaedicIntentionKey key, List<IntentionRelBrand> rel_brands, boolean isEdit) {
		if (isEdit) {
			mapper.deleteIntentionRefBrand(key);
		}
		if (rel_brands != null) {
			rel_brands.forEach(r -> {
				mapper.insertIntentionRefBrand(key.getHospital_inner_sn(), key.getDealer_inner_sn(),
						r.getBrand_inner_sn(), Utils.ifNull(r.getStatus(), 1));//status(no use)
			});
		}
	}

	private OrthopaedicIntention parseRelBrandsJson(OrthopaedicIntention obj) {
		if (obj != null && obj.getRel_brands_json() != null) {
			obj.setRel_brands(JSONObject.parseArray(obj.getRel_brands_json(), IntentionRelBrand.class));
		}
		return obj;
	}

	@Override
	public List<KeyValue<Long, String>> getAvailableDealer(long hospitalId, long operationTypeId, long brandId) {
		return mapper.selectAvailableDealer(hospitalId, operationTypeId, brandId);
	}
	
	@Override
	public List<KeyValue<Long, String>> getAvailableBrand(long hospitalId) {
		return mapper.selectAvailableBrand(hospitalId);
	}
	
	@Override
	public List<KeyValue<Long, String>> getAvailableType(long hospitalId) {
		return mapper.selectAvailableType(hospitalId);
	}

	@Override
	public List<KeyValue<Long, String>> getAvailableHospital(long dealerId) {
		return mapper.selectAvailableHospital(dealerId);
	}
}
