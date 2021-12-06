package com.yrt.project.api.business.surgery.cust.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.PinyinUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.project.api.business.surgery.cust.vo.CustKitCondition;
import com.yrt.project.api.business.surgery.cust.vo.CustKitInfo;
import com.yrt.project.api.business.surgery.cust.vo.CustPackageCondition;
import com.yrt.project.api.business.surgery.cust.vo.CustPackageInfo;
import com.yrt.project.api.business.surgery.cust.vo.CustPackageInit;
import com.yrt.project.api.business.surgery.cust.vo.CustPackageRecord;
import com.yrt.project.api.business.surgery.cust.vo.SearchScope;
import com.yrt.project.api.platform.surgery.service.ISurgeryService;
import com.yrt.project.modular.surgery.OisKitStatus;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustKit;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustKitExample;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustKitProductDetail;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustKitProductDetailExample;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOis;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisExample;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisKit;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisKitExample;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisOrthopaedicOperationPathwayExample;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisOrthopaedicOperationPathwayKey;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisProductDetail;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisProductDetailExample;
import com.yrt.project.modular.surgery.cust.mapper.OrthopaedicCustKitMapper;
import com.yrt.project.modular.surgery.cust.mapper.OrthopaedicCustKitProductDetailMapper;
import com.yrt.project.modular.surgery.cust.mapper.OrthopaedicCustOisKitMapper;
import com.yrt.project.modular.surgery.cust.mapper.OrthopaedicCustOisMapper;
import com.yrt.project.modular.surgery.cust.mapper.OrthopaedicCustOisOrthopaedicOperationPathwayMapper;
import com.yrt.project.modular.surgery.cust.mapper.OrthopaedicCustOisProductDetailMapper;
import com.yrt.project.modular.surgery.domain.OrthopaedicKit;
import com.yrt.project.modular.surgery.domain.OrthopaedicPackage;

@Service
public class SurgeryCustService {

	private static final Logger logger = LoggerFactory.getLogger(SurgeryCustService.class);

	private static final int MAX_KEYWORDS_LEN = 100;
	private static final int MAX_NAME_LEN = 208;

	@Autowired
	private OrthopaedicCustOisMapper custOisMapper;
	@Autowired
	private OrthopaedicCustKitMapper custKitMapper;

	@Autowired
	private OrthopaedicCustOisProductDetailMapper oisDetailMapper;

	@Autowired
	private OrthopaedicCustKitProductDetailMapper kitDetailMapper;

	@Autowired
	private OrthopaedicCustOisKitMapper oisKitRelMapper;

	@Autowired
	private ISurgeryService platformService;

	@Autowired
	private OrthopaedicCustOisOrthopaedicOperationPathwayMapper opRel;

	// @Autowired
	// private IOrgService orgService;
	// private DealerMapper dealerMapper;

	/**
	 * 经销商 定制 手术包 操作 时列表
	 * 
	 * @param ownerDealer
	 * @param condition
	 * @param page
	 * @return
	 */
	public List<OrthopaedicCustOis> listPackageForDealer(long ownerDealer, CustPackageCondition condition,
			PageDomain page) {
		OrthopaedicCustOisExample example = new OrthopaedicCustOisExample();

		example.setOwner_dealer_inner_sn(ownerDealer);// only effect on cust-ois part
		example.setOp_inner_sn(condition.getOp_inner_sn());
		example.setSearchScope(Utils.ifNull(condition.getSearchScope(), SearchScope.ALL).name());
		if (StringUtils.isNotEmpty(condition.getQ())) {
			String likeValue = SqlUtil.containsLikeValue(condition.getQ());
			example.setQ(likeValue);
		}

		OrthopaedicCustOisExample.Criteria c = example.or();
		c.andIs_deleteEqualTo(false);
		if (StringUtils.isNotEmpty(condition.getOis_sn())) {
			c.andOis_snEqualTo(condition.getOis_sn());
		}
		if (StringUtils.isNotEmpty(condition.getTypes())) {
			c.andType_inner_snIn(condition.getTypes());
		}

		if (StringUtils.isNotEmpty(condition.getBrands())) {
			c.andBrand_inner_snIn(condition.getBrands());
		}

		example.setOrderByClause("name"); // platform_ois_inner_sn TODO: order by
		if (page != null) {
			PageHelper.startPage(page.getPageNum(), page.getPageSize(), example.getOrderByClause());
		}
		List<OrthopaedicCustOis> ret = custOisMapper.selectByExampleExt(example);
		logger.info("condition={}, result={}", condition, ret);
		return ret;
	}

	/**
	 * for plan dealerInnerSn可选
	 * 
	 * @param hospitalInnerSn
	 * @param dealerInnerSn
	 * @param condition
	 * @return
	 */
	public List<OrthopaedicCustOis> listPackageForPlan(long hospitalInnerSn, Long dealerInnerSn,
			CustPackageCondition condition, PageDomain page) {
		OrthopaedicCustOisExample example = new OrthopaedicCustOisExample();

		example.setHospital_inner_sn(hospitalInnerSn);// for Intention
		example.setOwner_dealer_inner_sn(dealerInnerSn); // nullable
		example.setOp_inner_sn(condition.getOp_inner_sn());
		example.setSearchScope(Utils.ifNull(condition.getSearchScope(), SearchScope.ALL).name());

		if (StringUtils.isNotEmpty(condition.getQ())) {
			String likeValue = SqlUtil.containsLikeValue(condition.getQ());
			example.setQ(likeValue);
		}

		OrthopaedicCustOisExample.Criteria c = example.or();
		c.andIs_deleteEqualTo(false);
		if (StringUtils.isNotEmpty(condition.getOis_sn())) {
			c.andOis_snEqualTo(condition.getOis_sn());
		}
		if (StringUtils.isNotEmpty(condition.getTypes())) {
			c.andType_inner_snIn(condition.getTypes());
		}

		if (StringUtils.isNotEmpty(condition.getBrands())) {
			c.andBrand_inner_snIn(condition.getBrands());
		}

		example.setOrderByClause("name"); // platform_ois_inner_sn TODO: order by
		if (page != null) {
			PageHelper.startPage(page.getPageNum(), page.getPageSize(), example.getOrderByClause());
		}
		List<OrthopaedicCustOis> ret = custOisMapper.selectByExampleExt(example);

		logger.info("condition={}, result={}", condition, ret);
		return ret;
	}

	/**
	 * initiate cust OIS, copy platform content(kits deep copy) and save cust ois.
	 * info({@link #getPackage(long)})
	 * 
	 * @param initPackage
	 * @return whole package info
	 */
	@Transactional
	public CustPackageInfo initPackageFromPlatform(CustPackageInit initPackage) {
		logger.warn("init custOis: {}", initPackage);

		CustPackageInfo info = this.getPlatformPackage(initPackage.getSrc_id());
		info.setOwner_dealer_inner_sn(initPackage.getOrg_id());
		info.setOwner_dealer_name(initPackage.getOrg_name());
		info.setIs_cust(true);
		info.setIs_delete(false);
		info.setStatus(OisKitStatus.VALID);

		// init kit first
		info.getKits().forEach(k -> {
			CustPackageInit initKit = initPackage.copyWithNewSrcId(k.getPlatform_kit_inner_sn());
			this.initKitFromPlatform(initKit, k);
		});

		String name = info.getName() == null ? "" : info.getName();
		name = findFreeName(name, (n) -> custOisMapper.selectExistsName(info.getOwner_dealer_inner_sn(), n, null));
		info.setName(name);

		return this.savePackage(info, info.getDetails(),
				info.getKits().stream().map(k -> k.getId()).collect(Collectors.toList()));
	}

	public static String findFreeName(String name, Function<String, String> nameGetter) {
		for (int i = 0; i < 100; i++) {
			if (nameGetter.apply(name) == null) {
				return name;
			}
			name = Utils.nextName(name, MAX_NAME_LEN);
		}
		return name; // give up try let it exception
	}

	/**
	 * convert platform record to cust format
	 * 
	 * @param src
	 * @return
	 */
	private CustPackageInfo platformOis2Cust(OrthopaedicPackage src) {
		if (src == null) {
			return null;
		}

		OrthopaedicCustOis ois = new OrthopaedicCustOis();
		BeanUtils.copyProperties(src, ois);
		ois.setId(null);
		// cust.setOwner_dealer_inner_sn(null);
		ois.setPlatform_ois_inner_sn(src.getOis_inner_sn());
		ois.setStatus(OisKitStatus.VALID);
		// ois.setIs_delete(false);
		ois.setIs_cust(false);
		if (StringUtils.isEmpty(ois.getKeywords())) {
			ois.setKeywords(PinyinUtils.getPy(ois.getName()));
		}

		AtomicInteger sort = new AtomicInteger();
		List<OrthopaedicCustOisProductDetail> details = src.getDetails().stream().map(srcOisDetail -> {
			OrthopaedicCustOisProductDetail oisDetail = new OrthopaedicCustOisProductDetail();
			BeanUtils.copyProperties(srcOisDetail, oisDetail);
			oisDetail.setId(null);
			// oisDetail.setOis_id(null); //parent
			oisDetail.setUnit(srcOisDetail.getUnit_name());// diff column=unit

			// platform changed vendor_name -> product_vendor_name
			// oisDetail.setProduct_vendor_name(srcOisDetail.getVendor_name()); // diff
			// column=product_vendor_name

			// oisDetail.setProduct_vendor_inner_sn(e.getProduct_vendor_inner_sn()); // same
			oisDetail.setSort_number(sort.getAndDecrement());
			return oisDetail;
		}).collect(Collectors.toList());

		List<CustKitInfo> kits = src.getKits().stream().map(this::platformKit2Cust).collect(Collectors.toList());

		return CustPackageInfo.of(ois, details, kits);
	}

	private CustKitInfo platformKit2Cust(OrthopaedicKit src) {
		if (src == null) {
			return null;
		}
		OrthopaedicCustKit kit = new OrthopaedicCustKit();
		BeanUtils.copyProperties(src, kit);
		// k.setId(kitId);
		kit.setId(null);
		// k.setOwner_dealer_inner_sn(ownerDealerId);
		kit.setPlatform_kit_inner_sn(src.getKit_inner_sn()); // src.getKit_inner_sn()
		kit.setStatus(OisKitStatus.VALID);
		// kit.setIs_delete(false);
		kit.setIs_cust(false); // pseudo field
		if (StringUtils.isEmpty(kit.getKeywords())) {
			kit.setKeywords(PinyinUtils.getPy(kit.getName()));
		}

		AtomicInteger sort = new AtomicInteger();
		List<OrthopaedicCustKitProductDetail> details = src.getDetails().stream().map(srcKitDetail -> {
			OrthopaedicCustKitProductDetail kitDetail = new OrthopaedicCustKitProductDetail();
			BeanUtils.copyProperties(srcKitDetail, kitDetail);
			kitDetail.setId(null);
			// kitDetail.setKit_id(null); //parent
			kitDetail.setUnit(srcKitDetail.getUnit_name());// diff column=unit

			// platform changed vendor_name -> product_vendor_name
			// kitDetail.setProduct_vendor_name(srcKitDetail.getVendor_name());// diff
			// column=product_vendor_name

			// kitDetail.setProduct_vendor_inner_sn(e.getProduct_vendor_inner_sn()); //same
			kitDetail.setSort_number(sort.getAndDecrement());
			return kitDetail;
		}).collect(Collectors.toList());

		return CustKitInfo.of(kit, details);
	}

	/**
	 * get platform package(convert to cust data format)
	 * 
	 * @param platformOisId
	 * @return
	 */
	public CustPackageInfo getPlatformPackage(long platformOisId) {
		OrthopaedicPackage p = platformService.selectOrthopaedicPackageInfoById(platformOisId);
		Utils.assertNotEmpty(p, "平台手术包不存在: " + platformOisId);
		CustPackageInfo ret = this.platformOis2Cust(p);
		return ret;
	}

	/**
	 * get package(cust data format) by platformOisId & dealerId
	 * 
	 * @param delaerId
	 * @param platformOisId
	 * @return
	 * @deprecated one platform-ois may has multi custois(for same dealer)
	 */
	public CustPackageInfo getPackageByPlatformId(long delaerId, long platformOisId) {
		OrthopaedicCustOisExample example = new OrthopaedicCustOisExample();
		example.or().andOwner_dealer_inner_snEqualTo(delaerId).andPlatform_ois_inner_snEqualTo(platformOisId);
		OrthopaedicCustOis record = custOisMapper.selectByExampleExt(example).stream().findFirst().orElse(null);
		if (record == null) {
			return getPlatformPackage(platformOisId);
		} else {
			List<OrthopaedicCustOisProductDetail> details = this.getPackageDetails(record.getId());
			List<CustKitInfo> kits = this.getPackageKitsByPid(record.getId());
			CustPackageInfo ret = CustPackageInfo.of(record, details, kits);
			return ret;
		}
	}

	/**
	 * load all
	 * 
	 * @param oisId
	 * @return
	 */
	public CustPackageInfo getPackage(long id) {
		// select1
		OrthopaedicCustOisExample example = new OrthopaedicCustOisExample();// default PRIVATE
		example.or().andIdEqualTo(id); // get single record(with type_name/brand_name)
		OrthopaedicCustOis record = custOisMapper.selectByExampleExt(example).stream().findFirst().orElse(null);
		if (record == null) {
			throw new RuntimeException("手术包不存在: " + id);
		}
		List<OrthopaedicCustOisProductDetail> details = this.getPackageDetails(record.getId());

		List<CustKitInfo> kits = this.getPackageKitsByPid(record.getId());
		CustPackageInfo ret = CustPackageInfo.of(record, details, kits);
		return ret;
	}

	private List<OrthopaedicCustOisProductDetail> getPackageDetails(long packageId) {
		// select2
		OrthopaedicCustOisProductDetailExample example = new OrthopaedicCustOisProductDetailExample();
		example.or().andOis_idEqualTo(packageId);
		example.setOrderByClause("sort_number");
		return oisDetailMapper.selectByExample(example);
	}

	private List<CustKitInfo> getPackageKitsByPid(long packageId) {
		// select3
		OrthopaedicCustOisKitExample relExample = new OrthopaedicCustOisKitExample();
		relExample.or().andOis_idEqualTo(packageId);
		relExample.setOrderByClause("sort_number");
		List<OrthopaedicCustOisKit> rels = oisKitRelMapper.selectByExample(relExample);
		List<Long> kids = rels.stream().map(r -> r.getKit_id()).collect(Collectors.toList());

		return getPackageKitsByIds(kids);
	}

	private List<CustKitInfo> getPackageKitsByIds(List<Long> kids) {
		if (kids.isEmpty()) {
			return new ArrayList<>();
		}
		// select4
		OrthopaedicCustKitProductDetailExample kitsDetailsExample = new OrthopaedicCustKitProductDetailExample();
		kitsDetailsExample.or().andKit_idIn(kids);
		kitsDetailsExample.setOrderByClause("sort_number");
		List<OrthopaedicCustKitProductDetail> kitsDetails = this.kitDetailMapper.selectByExample(kitsDetailsExample);

		// select5
		OrthopaedicCustKitExample kitExample = new OrthopaedicCustKitExample();
		kitExample.or().andIdIn(kids);
		return custKitMapper.selectByExampleExt(kitExample).stream().map(k -> {
			List<OrthopaedicCustKitProductDetail> kDetails = kitsDetails.stream()
					.filter(e -> e.getKit_id().equals(k.getId())).collect(Collectors.toList());
			return CustKitInfo.of(k, kDetails);
		}).sorted((o1, o2) -> Integer.compare(kids.indexOf(o1.getId()), kids.indexOf(o2.getId())))
				.collect(Collectors.toList());
	}

	/**
	 * insert or update. To add new {@link #initPackageFromPlatform(long, long)}
	 * 
	 * @param record
	 * @return
	 */
	@Transactional
	public CustPackageInfo savePackage(CustPackageRecord record) {
		return savePackage(record, record.getDetails(), record.getKitIds());
	}

	private CustPackageInfo savePackage(OrthopaedicCustOis record, List<OrthopaedicCustOisProductDetail> details,
			List<Long> kitIds) {
		// record.getOwner_dealer_inner_sn();
		// record.getOwner_dealer_name();
		// record.getPlatform_ois_inner_sn();

		if (StringUtils.isEmpty(record.getKeywords())) {
			record.setKeywords(PinyinUtils.getPy(record.getName()));
		}
		record.setKeywords(Utils.maxLength(record.getKeywords(), MAX_KEYWORDS_LEN));

		if (record.getName() != null) {
			Utils.assertNull(
					this.custOisMapper.selectExistsName(record.getOwner_dealer_inner_sn(), record.getName(),
							record.getId()), // id=null for insert
					MessageUtils.message("db.update.fail", "骨科手术包", "骨科手术包名称重复"));
		}
		try {
			if (record.getId() == null) {
				record.setId(CompressedIdGenerator.generateId());
				this.custOisMapper.insertSelective(record);
			} else {
				if (this.custOisMapper.updateByPrimaryKeySelective(record) != 1) {
					throw new RuntimeException("手术包不存在：" + record.getId());
				}
				// details
				OrthopaedicCustOisProductDetailExample detailExample = new OrthopaedicCustOisProductDetailExample();
				detailExample.or().andOis_idEqualTo(record.getId());
				this.oisDetailMapper.deleteByExample(detailExample);

				// rels
				OrthopaedicCustOisKitExample relExample = new OrthopaedicCustOisKitExample();
				relExample.or().andOis_idEqualTo(record.getId());
				this.oisKitRelMapper.deleteByExample(relExample);
			}
		} catch (DuplicateKeyException e) {
			throw new DuplicateException(MessageUtils.message("db.update.fail", "骨科手术包", "骨科手术包名称重复"));
		}

		AtomicInteger sort = new AtomicInteger(0);
		// details
		details.forEach(e -> {
			e.setId(CompressedIdGenerator.generateId());
			e.setOis_id(record.getId());
			e.setSort_number(sort.getAndIncrement());
			this.oisDetailMapper.insertSelective(e);
		});

		// rels
		sort.set(0);
		kitIds.forEach(kid -> {
			OrthopaedicCustOisKit rel = new OrthopaedicCustOisKit();
			rel.setOis_id(record.getId());
			rel.setKit_id(kid);
			rel.setSort_number(sort.getAndIncrement());
			this.oisKitRelMapper.insertSelective(rel);
		});

		List<CustKitInfo> kits = this.getPackageKitsByIds(kitIds);

		return CustPackageInfo.of(record, details, kits);
	}

	/**
	 * remove customized package
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public boolean deleteMarkPackage(long oisId) {
		OrthopaedicCustOis record = new OrthopaedicCustOis();
		record.setId(oisId);
		record.setIs_delete(true);
		return this.custOisMapper.updateByPrimaryKeySelective(record) == 1;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	public List<OrthopaedicCustKit> listKitForDealer(long ownerDealer, CustKitCondition condition) {
		OrthopaedicCustKitExample example = new OrthopaedicCustKitExample();

		example.setOwner_dealer_inner_sn(ownerDealer);
		if (StringUtils.isNotEmpty(condition.getQ())) {
			String likeValue = SqlUtil.containsLikeValue(condition.getQ());
			example.setQ(likeValue);
		}

		OrthopaedicCustKitExample.Criteria c = example.or();
		c.andIs_deleteEqualTo(false);
		if (StringUtils.isNotEmpty(condition.getKit_sn())) {
			c.andKit_snLike(SqlUtil.containsLikeValue(condition.getKit_sn()));
		}
		if (StringUtils.isNotEmpty(condition.getTypes())) {
			c.andType_inner_snIn(condition.getTypes());
		}

		if (StringUtils.isNotEmpty(condition.getBrands())) {
			c.andBrand_inner_snIn(condition.getBrands());
		}
		if (condition.getSearchScope() != null) {
			switch (condition.getSearchScope()) {
			case PLATFORM:
				c.andIdIsNull();
				break;
			case PRIVATE:
				c.andIdIsNotNull();
				break;
			default: // all
			}
		}

		return custKitMapper.selectForDealerByExample(example);
	}

	/**
	 * copy platform kit, return a cust-kit data format which ready for
	 * {@link #saveKit(CustKitInfo)}
	 * 
	 * @param initKit
	 * @return
	 */
	@Transactional
	public CustKitInfo initKitFromPlatform(CustPackageInit initKit) {
		OrthopaedicKit src = platformService.selectOrthopaedicKitInfoById(initKit.getSrc_id());
		if (src == null) {
			throw new RuntimeException("平台工具包不存在: " + initKit.getSrc_id());
		}
		CustKitInfo info = this.platformKit2Cust(src);

		return this.initKitFromPlatform(initKit, info);
	}

	private CustKitInfo initKitFromPlatform(CustPackageInit initKit, CustKitInfo info) {
		info.setOwner_dealer_inner_sn(initKit.getOrg_id());
		info.setOwner_dealer_name(initKit.getOrg_name());
		info.setPlatform_kit_inner_sn(initKit.getSrc_id());

		info.setIs_cust(true);
		info.setIs_delete(false);
		info.setStatus(OisKitStatus.VALID);

		String name = info.getName() == null ? "" : info.getName();
		name = findFreeName(name, (n) -> custKitMapper.selectExistsName(info.getOwner_dealer_inner_sn(), n, null));
		info.setName(name);

		return this.saveKit(info);
	}

	// private CustKitInfo initKitFromPlatform(CustPackageInit initKit, CustKitInfo
	// info) {
	//
	// OrthopaedicCustKitExample example = new OrthopaedicCustKitExample();
	// example.or().andOwner_dealer_inner_snEqualTo(ownerDealerId).andPlatform_kit_inner_snEqualTo(platformKitId);
	// OrthopaedicCustKit record = findExistOne
	// ? custKitMapper.selectByExampleExt(example).stream().findFirst().orElse(null)
	// : null;
	// if (record == null) {
	// record = new OrthopaedicCustKit();
	// BeanUtils.copyProperties(src, record);
	// long kitId = CompressedIdGenerator.generateId();
	// record.setId(kitId);
	// record.setOwner_dealer_inner_sn(ownerDealerId);
	// record.setOwner_dealer_name(initKit.getOrg_name());
	// record.setPlatform_kit_inner_sn(src.getKit_inner_sn()); //
	// src.getKit_inner_sn()
	// record.setStatus(OisKitStatus.VALID);
	// record.setIs_delete(false);
	// record.setIs_cust(true); // pseudo field
	// if (StringUtils.isEmpty(record.getKeywords())) {
	// record.setKeywords(PinyinUtils.getPy(record.getName()));
	// }
	//
	// custKitMapper.insertSelective(record);// no DuplicateKeyException
	// List<OrthopaedicCustKitProductDetail> custDetails =
	// src.getDetails().stream().map(e -> {
	// OrthopaedicCustKitProductDetail detail = new
	// OrthopaedicCustKitProductDetail();
	// BeanUtils.copyProperties(e, detail);
	// detail.setId(CompressedIdGenerator.generateId());
	// detail.setKit_id(kitId);
	// detail.setUnit(e.getUnit_name());
	// detail.setProduct_vendor_name(e.getVendor_name());
	// // detail.setProduct_vendor_inner_sn(e.getProduct_vendor_inner_sn());//same
	// kitDetailMapper.insertSelective(detail);
	// return detail;
	// }).collect(Collectors.toList());
	// return CustKitInfo.of(record, custDetails);
	//
	// } else if (record.getIs_delete()) { // recovery record
	// record.setStatus(OisKitStatus.VALID);
	// record.setIs_delete(false);
	// record.setIs_cust(true); // pseudo field
	// custKitMapper.updateByPrimaryKeySelective(record); // recovery record
	// }
	// return CustKitInfo.of(record, getKitDetails(record.getId()));
	// }

	private List<OrthopaedicCustKitProductDetail> getKitDetails(long kitId) {
		OrthopaedicCustKitProductDetailExample example = new OrthopaedicCustKitProductDetailExample();
		example.or().andKit_idEqualTo(kitId);
		example.setOrderByClause("sort_number");
		return kitDetailMapper.selectByExample(example);
	}

	//

	public CustKitInfo getPlatformKit(long platformKitId) {
		OrthopaedicKit p = platformService.selectOrthopaedicKitInfoById(platformKitId);
		Utils.assertNotEmpty(p, "平台工具包不存在: " + platformKitId);
		return this.platformKit2Cust(p);
	}

	public CustKitInfo getKit(long kitId) {
		OrthopaedicCustKitExample example = new OrthopaedicCustKitExample();// default PRIVATE
		example.or().andIdEqualTo(kitId); // by pk
		OrthopaedicCustKit record = this.custKitMapper.selectByExampleExt(example) // join: type_name/brand_name
				.stream().findFirst().orElseThrow(() -> new RuntimeException("工具包不存在: " + kitId));
		return CustKitInfo.of(record, this.getKitDetails(kitId));
	}

	/**
	 * insert or update kit(ALL).
	 * 
	 * @param record
	 * @return
	 */
	@Transactional
	public CustKitInfo saveKit(CustKitInfo record) {
		// id
		if (StringUtils.isEmpty(record.getKeywords())) {
			record.setKeywords(PinyinUtils.getPy(record.getName()));
		}
		record.setKeywords(Utils.maxLength(record.getKeywords(), MAX_KEYWORDS_LEN));

		if (record.getName() != null) {
			Utils.assertNull(//
					this.custKitMapper.selectExistsName(record.getOwner_dealer_inner_sn(), record.getName(),
							record.getId()), // id=null for insert
					MessageUtils.message("db.update.fail", "骨科工具包", "骨科工具包名称重复"));
		}
		try {
			if (record.getId() == null) {
				record.setId(CompressedIdGenerator.generateId());
				custKitMapper.insertSelective(record);
			} else {
				if (this.custKitMapper.updateByPrimaryKeySelective(record) != 1) {
					throw new RuntimeException("工具包不存在: " + record.getId());
				}
				OrthopaedicCustKitProductDetailExample detailExample = new OrthopaedicCustKitProductDetailExample();
				detailExample.or().andKit_idEqualTo(record.getId());
				this.kitDetailMapper.deleteByExample(detailExample);
			}
		} catch (DuplicateKeyException e) {
			throw new DuplicateException(MessageUtils.message("db.update.fail", "骨科工具包", "骨科工具包名称重复"));
		}

		// details
		AtomicInteger sort = new AtomicInteger(0);
		record.getDetails().forEach(e -> {
			e.setId(CompressedIdGenerator.generateId());
			e.setKit_id(record.getId()); // parent
			e.setSort_number(sort.getAndIncrement());
			this.kitDetailMapper.insertSelective(e);
		});
		return record;
	}

	/**
	 * remove customized package
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public boolean deleteMarkKit(long kitId) {
		OrthopaedicCustKit record = new OrthopaedicCustKit();
		record.setId(kitId);
		record.setIs_delete(true);
		return this.custKitMapper.updateByPrimaryKeySelective(record) == 1;
	}

	// test only
	@Transactional
	public boolean deletePackage(long pid) {
		deleteOpRel(pid);
		OrthopaedicCustOisProductDetailExample detailExample = new OrthopaedicCustOisProductDetailExample();
		detailExample.or().andOis_idEqualTo(pid);
		this.oisDetailMapper.deleteByExample(detailExample);

		OrthopaedicCustOisKitExample relExample = new OrthopaedicCustOisKitExample();
		relExample.or().andOis_idEqualTo(pid);
		this.oisKitRelMapper.deleteByExample(relExample);

		return this.custOisMapper.deleteByPrimaryKey(pid) == 1;
	}

	// test only
	@Transactional
	public boolean deleteKit(long kitId) {

		// pre-require delete ois->kit
		OrthopaedicCustOisKitExample relExample = new OrthopaedicCustOisKitExample();
		relExample.or().andKit_idEqualTo(kitId);
		// this.oisKitRelMapper.deleteByExample(relExample);
		List<Long> ois = this.oisKitRelMapper.selectByExample(relExample).stream().map(e -> e.getOis_id())
				.collect(Collectors.toList());
		if (!ois.isEmpty()) {
			throw new RuntimeException("不能删除工具包, 已被手术包使用: " + ois);
		}

		OrthopaedicCustKitProductDetailExample detailExample = new OrthopaedicCustKitProductDetailExample();
		detailExample.or().andKit_idEqualTo(kitId);
		this.kitDetailMapper.deleteByExample(detailExample);

		try {
			return this.custKitMapper.deleteByPrimaryKey(kitId) == 1;
		} catch (DataAccessException e) {
			throw new RuntimeException("不能删除工具包，已被手术包使用", e);
		}
	}

	// ois <-> operation-pathway

	private void deleteOpRel(long pid) {
		OrthopaedicCustOisOrthopaedicOperationPathwayExample example = new OrthopaedicCustOisOrthopaedicOperationPathwayExample();
		example.or().andCust_ois_idEqualTo(pid);
		opRel.deleteByExample(example);
	}

	@Transactional
	public int removePackageFromPath(long pathId, List<Long> custOisIds) {
		AtomicInteger count = new AtomicInteger();
		custOisIds.forEach(pid -> {
			OrthopaedicCustOisOrthopaedicOperationPathwayKey key = new OrthopaedicCustOisOrthopaedicOperationPathwayKey();
			key.setCust_ois_id(pid);
			key.setOp_inner_sn(pathId);
			count.addAndGet(opRel.deleteByPrimaryKey(key));
		});

		return count.get();
	}

	@Transactional
	public int addPackageIntoPath(long pathId, List<Long> custOisIds) {
		AtomicInteger count = new AtomicInteger();
		custOisIds.forEach(pid -> {
			OrthopaedicCustOisOrthopaedicOperationPathwayKey key = new OrthopaedicCustOisOrthopaedicOperationPathwayKey();
			key.setCust_ois_id(pid);
			key.setOp_inner_sn(pathId);
			try {
				opRel.insert(key);
				count.incrementAndGet();
			} catch (DuplicateKeyException e) {

			}
		});

		return count.get();
	}

}
