package com.yrt.project.api.business.surgery.plan.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.yrt.common.constant.Constants;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.GrnOdoDetailType;
import com.yrt.common.dict.OdoStatus;
import com.yrt.common.dict.PrdType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.InventoryException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.PinyinUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.order.service.ISaleRecordService;
import com.yrt.project.api.business.order.vo.order.BuildSaleOrderDetailForm;
import com.yrt.project.api.business.order.vo.order.BuildSaleOrderForm;
import com.yrt.project.api.business.order.vo.record.SaleRecordIDForm;
import com.yrt.project.api.business.organization.service.IOrgService;
import com.yrt.project.api.business.surgery.plan.vo.OperationPlanAll;
import com.yrt.project.api.business.surgery.plan.vo.PlanCondition;
import com.yrt.project.api.business.surgery.plan.vo.PlanKitAll;
import com.yrt.project.api.business.surgery.plan.vo.PlanOisPackageAll;
import com.yrt.project.api.business.warehouse.service.IOdoService;
import com.yrt.project.api.business.warehouse.service.InventoryLockService;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoDetailForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIds;
import com.yrt.project.api.business.warehouse.vo.odo.OdoListIDForm;
import com.yrt.project.modular.order.domain.Sl;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.domain.User;
import com.yrt.project.modular.organization.service.IUserService;
import com.yrt.project.modular.product.mapper.ProductMapper;
import com.yrt.project.modular.surgery.OisInstanceStatus;
import com.yrt.project.modular.surgery.OperationPlanStatus;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlan;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanKitInstanceExample;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanKitProductDetail;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanKitProductDetailExample;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisInstance;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisInstanceExample;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisProductDetail;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisProductDetailExample;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanTypeExample;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanTypeKey;
import com.yrt.project.modular.surgery.plan.PlanOisView;
import com.yrt.project.modular.surgery.plan.PlanSearchParam;
import com.yrt.project.modular.surgery.plan.mapper.OrthopaedicOperationPlanKitInstanceMapper;
import com.yrt.project.modular.surgery.plan.mapper.OrthopaedicOperationPlanKitProductDetailMapper;
import com.yrt.project.modular.surgery.plan.mapper.OrthopaedicOperationPlanMapper;
import com.yrt.project.modular.surgery.plan.mapper.OrthopaedicOperationPlanOisInstanceMapper;
import com.yrt.project.modular.surgery.plan.mapper.OrthopaedicOperationPlanOisProductDetailMapper;
import com.yrt.project.modular.surgery.plan.mapper.OrthopaedicOperationPlanTypeMapper;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.domain.Odo;

@Service
public class OperationPlanService {

	private static final Logger logger = LoggerFactory.getLogger(OperationPlanService.class);
	private static final int MAX_KEYWORDS_LEN = 100;

	/**
	 * 骨科助理锁定库存时 批号 (不分批号锁定)
	 */
//	static final String PRE_LOCK_BATCH_NUMBER = "PRE_LOCK";

	/**
	 * 骨科手术 出库单 odo_type
	 */
	static final long OPERATION_ODO_TYPE = 0;

	/**
	 * 骨科手术 出库单 odo_type name
	 */
	static final String OPERATION_ODO_TYPE_NAME = "销售出库";

	@Autowired
	private IUserService userService;

	@Autowired
	private IOrgService orgService;

	@Autowired
	private OrthopaedicOperationPlanMapper planMapper;

	@Autowired
	private OrthopaedicOperationPlanTypeMapper planTypeMapper;

	@Autowired
	private OrthopaedicOperationPlanOisInstanceMapper oisMapper;

	@Autowired
	private OrthopaedicOperationPlanOisProductDetailMapper oisDetailMapper;

	@Autowired
	private OrthopaedicOperationPlanKitInstanceMapper kitMapper;

	@Autowired
	private OrthopaedicOperationPlanKitProductDetailMapper kitDetailMapper;

	@Autowired
	private InventoryLockService inventoryLocker;

	@Autowired
	private ISaleRecordService saleService;
	@Autowired
	private IOdoService odoService;

	@Autowired
	private ProductMapper productMapper;

	/**
	 * save plan(all in one)
	 * 
	 * @param userInfo
	 * @param plan
	 * @return
	 */
	@Transactional
	public OperationPlanAll savePlanAll(ContextUserInfo userInfo, OperationPlanAll plan, boolean doPublish) {
		if (plan.getId() != null) { // edit
			// delete details first
			deletePlanDetails(plan.getId());
		}
		savePlan(userInfo, plan);
		plan.getDetails().forEach(e -> {
			e.setId(null);
			e.setPlan_id(plan.getId()); // parent
			this.savePackageAll(userInfo, e);
		});
		if (doPublish) {
			OrthopaedicOperationPlan masterRecord = this.publishPlan(userInfo, plan.getId());
			BeanUtils.copyProperties(masterRecord, plan);
		}
		return plan;
	}

	/**
	 * save plan(master table) & its types only
	 * 
	 * @param userInfo
	 * @param plan
	 * @return
	 */
	@Transactional
	public OrthopaedicOperationPlan savePlan(ContextUserInfo userInfo, OrthopaedicOperationPlan plan) {
		if (plan.getId() == null) {
			// Utils.assertNotEmpty(plan.getHospital_inner_sn(), "hospital_inner_sn不能为空");
			// OrgEmployee emp = orgService.getEmployeeInfoByUser(userInfo.getSystemType(),
			// userInfo.getUid());
			User user = userService.selectUserById(userInfo.getUid());

			logger.info("newPlan: contextUser={}, user={}, plan={}", userInfo, user, plan);

			plan.setId(CompressedIdGenerator.generateId());
			plan.setIs_delete(false);

			// hospital info由client填写
			if (userInfo.getSystemType().equals(SystemType.DEALER)) {
				plan.setStatus(OperationPlanStatus.CONFIRMED);
				Utils.assertNotEmpty(plan.getHospital_inner_sn(), "hospital_inner_sn不能为空");
				// hospital_department_inner_sn
			} else {
				plan.setStatus(OperationPlanStatus.DRAFT);
				plan.setHospital_inner_sn(userInfo.getOrgId());

			}

			plan.setCreated_time(new Date());
			plan.setCreator_org_inner_sn(userInfo.getOrgId());// user.getOrg_id()/emp.getId()
			plan.setCreator_org_type((byte) userInfo.getSystemType().value());
			plan.setCreator_user_inner_sn(userInfo.getUid()); // emp.getUser_inner_sn()
			plan.setCreator_user_name(user.getLongin_name()); // user.getLongin_name()
			plan.setLast_modified_time(plan.getCreated_time());

			if (Utils.isEmpty(plan.getKeywords())) {
				String s = Stream.of(plan.getName(), plan.getDoctor_name(), plan.getPatient_name())//
						.filter(Utils::isNotEmpty)//
						.reduce("", (a, b) -> a + " " + b);
				plan.setKeywords(PinyinUtils.getPy(s));
			}
			plan.setKeywords(Utils.maxLength(plan.getKeywords(), MAX_KEYWORDS_LEN));

			planMapper.insertSelective(plan);
			savePlanTypes(plan, false);
		} else {
			logger.info("updatePlan: contextUser={}, user={}, plan={}", userInfo, plan);
			OrthopaedicOperationPlan old = planMapper.selectByPrimaryKey(plan.getId());
			Utils.assertNotEmpty(old, "计划不存在: " + plan.getId());
			Utils.assertTrue(old.getStatus().canEdit(), "不允许修改：" + old.getStatus());

			plan.setStatus(null); // not allow change status in save
			plan.setIs_delete(null);
			plan.setLast_modified_time(new Date());
			planMapper.updateByPrimaryKeySelective(plan);
			savePlanTypes(plan, true);
		}
		//
		return plan;
	}

	private void savePlanTypes(OrthopaedicOperationPlan plan, boolean isUpdate) {
		List<Byte> types = Stream.of(Utils.ifEmpty(plan.getTypes(), "").split(",")).filter(Utils::isNotEmpty)
				.map(Byte::parseByte).collect(Collectors.toList());
		// mobile side(without types provide)
		// Utils.assertNotEmpty(types, "计划至少需要指定一种手术类型");

		if (isUpdate) {
			OrthopaedicOperationPlanTypeExample typeExample = new OrthopaedicOperationPlanTypeExample();
			typeExample.or().andPlan_idEqualTo(plan.getId());
			planTypeMapper.deleteByExample(typeExample);
		}

		types.forEach(t -> {
			OrthopaedicOperationPlanTypeKey typeKey = new OrthopaedicOperationPlanTypeKey();
			typeKey.setPlan_id(plan.getId());
			typeKey.setType_inner_sn(t);
			planTypeMapper.insert(typeKey);
		});

	}

	@Transactional
	public OrthopaedicOperationPlan confirmPlan(ContextUserInfo userInfo, long id) {
		OrthopaedicOperationPlan plan = planMapper.selectByPrimaryKey(id);
		Utils.assertNotEmpty(plan, "计划不存在: " + id);
		Utils.assertTrue(plan.getStatus() == OperationPlanStatus.DRAFT, "无效状态: " + plan.getStatus());

		OrthopaedicOperationPlanOisInstanceExample oisExample = new OrthopaedicOperationPlanOisInstanceExample();
		oisExample.or().andPlan_idEqualTo(id);
		Utils.assertTrue(this.oisMapper.countByExample(oisExample) > 0, "没有手术包");

		updateStatus(plan, OperationPlanStatus.CONFIRMED);
		return plan;
	}

	@Transactional
	public OrthopaedicOperationPlan publishPlan(ContextUserInfo userInfo, long id) {
		OrthopaedicOperationPlan plan = planMapper.selectByPrimaryKey(id);
		Utils.assertNotEmpty(plan, "计划不存在: " + id);
		Utils.assertTrue(
				plan.getStatus() == OperationPlanStatus.CONFIRMED || plan.getStatus() == OperationPlanStatus.DRAFT,
				"无效状态: " + plan.getStatus());
		//

		// 经销商建立的计划 没有通过确认检查
		OrthopaedicOperationPlanOisInstanceExample oisExample = new OrthopaedicOperationPlanOisInstanceExample();
		oisExample.or().andPlan_idEqualTo(id);
		Utils.assertTrue(this.oisMapper.countByExample(oisExample) > 0, "没有手术包");

		oisExample = new OrthopaedicOperationPlanOisInstanceExample();
		oisExample.or().andPlan_idEqualTo(id).andDealer_inner_snIsNull();
		Utils.assertTrue(this.oisMapper.countByExample(oisExample) == 0, "手术包没有指定经销商");

		updateStatus(plan, OperationPlanStatus.PUBLISHED);
		return plan;
	}

	@Transactional
	public OrthopaedicOperationPlan closePlan(ContextUserInfo userInfo, long id) {
		return closePlan(userInfo, id, true);
	}

	/**
	 * 
	 * @param userInfo
	 * @param id
	 * @param errorIfHasPendingOis
	 *            if has pending ois(true: raise error , otherwise return saliently
	 *            without update).
	 * @return
	 */
	private OrthopaedicOperationPlan closePlan(ContextUserInfo userInfo, long id, boolean errorIfHasPendingOis) {
		OrthopaedicOperationPlan plan = planMapper.selectByPrimaryKey(id);
		Utils.assertNotEmpty(plan, "计划不存在: " + id);
		Utils.assertTrue(plan.getStatus() == OperationPlanStatus.PUBLISHED, "无效状态: " + plan.getStatus());

		OrthopaedicOperationPlanOisInstanceExample oisExample = new OrthopaedicOperationPlanOisInstanceExample();
		oisExample.or().andPlan_idEqualTo(id).andStatusNotIn(Arrays.asList(OisInstanceStatus.CLOSED));
		if (this.oisMapper.countByExample(oisExample) > 0) {
			if (errorIfHasPendingOis) {
				throw new RuntimeException("手术包还没有全部完结");
			}
			return plan;
		}

		updateStatus(plan, OperationPlanStatus.CLOSED);
		return plan;
	}

	private boolean updateStatus(OrthopaedicOperationPlan plan, OperationPlanStatus status) {
		OrthopaedicOperationPlan updator = new OrthopaedicOperationPlan();
		updator.setId(plan.getId());
		updator.setStatus(status);
		updator.setLast_modified_time(new Date());

		plan.setStatus(updator.getStatus());
		plan.setLast_modified_time(updator.getLast_modified_time());
		return planMapper.updateByPrimaryKeySelective(updator) == 1;
	}

	@Transactional
	public boolean deletePlan(ContextUserInfo userInfo, long id) {
		OrthopaedicOperationPlan plan = planMapper.selectByPrimaryKey(id);
		Utils.assertNotEmpty(plan, "计划不存在: " + id);
		Utils.assertTrue(plan.getStatus().canDelete(), "无效状态: " + plan.getStatus());

		OrthopaedicOperationPlan updator = new OrthopaedicOperationPlan();
		updator.setId(id);
		updator.setIs_delete(true);
		updator.setLast_modified_time(new Date());
		return planMapper.updateByPrimaryKeySelective(updator) == 1;
	}

	/**
	 * plan.status=DRAFT|CONFIRMED
	 * 
	 * @param planId
	 */
	private void deletePlanDetails(long planId) {
		this.kitDetailMapper.deleteByExample(this.kitDetailsByPlanId(planId));
		this.kitMapper.deleteByExample(this.kitByPlanId(planId));
		this.oisDetailMapper.deleteByExample(this.oisDetailsByPlanId(planId));
		this.oisMapper.deleteByExample(this.oisByPlanId(planId));
	}

	/**
	 * list plan
	 * 
	 * @param userInfo
	 * @param condition
	 * @return
	 */
	public List<OrthopaedicOperationPlan> listPlan(ContextUserInfo userInfo, PlanCondition condition) {
		// User user = userService.selectUserById(uid);
		// condition.setCurrentOrgId(user.getOrg_id());
		PlanSearchParam param = initContextCondition(userInfo, condition);
		logger.debug("listPlan: contextUser={}, param={}", userInfo, param);
		switch (userInfo.getSystemType()) {
		case HOSPITAL:
			return planMapper.selectForHospital(param);
		case DEALER:
			return planMapper.selectForDealer(param);
		default:
			logger.warn("illegal systemType: contextUser={}", userInfo);
			return Collections.emptyList();
		}
	}

	/**
	 * load each plan details
	 * 
	 * @param userInfo
	 * @param condition
	 * @return
	 */
	public List<OperationPlanAll> listPlanAll(ContextUserInfo userInfo, PlanCondition condition) {
		List<OrthopaedicOperationPlan> list = listPlan(userInfo, condition);
		return loadPlanDetails(userInfo, list, condition.getPackageStatuses());
	}

	/**
	 * list oisView(including paln info, for dealer warehouse)
	 * 
	 * @param userInfo
	 * @param condition
	 * @return
	 */
	public List<PlanOisView> listPlanPackage(ContextUserInfo userInfo, PlanCondition condition, boolean aggMoney) {
		PlanSearchParam param = initContextCondition(userInfo, condition);
		logger.debug("listPlanPackage: contextUser={}, param={}", userInfo, param);
		param.setAggMoneyForSelectOisView(aggMoney);
		List<PlanOisView> list = planMapper.selectOisView(param);
		return list;
	}

	private PlanSearchParam initContextCondition(ContextUserInfo userInfo, PlanCondition condition) {
		condition.normallize();
		PlanSearchParam param = new PlanSearchParam();
		BeanUtils.copyProperties(condition, param);
		param.setCurrentUserId(userInfo.getUid());
		param.setCurrentOrgId(userInfo.getOrgId());
		param.setCurrentOrgType(userInfo.getSystemType());
		param.setIsHospital(userInfo.getSystemType().equals(SystemType.HOSPITAL));
		return param;
	}

	@SuppressWarnings("unchecked")
	private List<OperationPlanAll> loadPlanDetails(ContextUserInfo userInfo, List<OrthopaedicOperationPlan> list,
			List<OisInstanceStatus> packageStatuses) {
		List<OperationPlanAll> result;
		if (list instanceof Page) {
			Page<OrthopaedicOperationPlan> page = (Page<OrthopaedicOperationPlan>) list;
			result = Utils.clone(page, Page.class); // new Page<>(page.getPageNum(), page.getPageSize(),
													// page.isCount());
			result.clear();
		} else {
			result = new ArrayList<>();
		}
		list.forEach(e -> {
			OperationPlanAll a = Utils.clone(e, OperationPlanAll.class);
			planOis(userInfo, a, packageStatuses);
			result.add(a);
		});
		return result;
	}

	/**
	 * query plan ois and load its details where satisfy packageStatuses ([for
	 * dealer] and ois.dealer is current org)
	 * 
	 * @param userInfo
	 * @param plan
	 * @param packageStatuses
	 * @return
	 */
	private List<PlanOisPackageAll> planOis(ContextUserInfo userInfo, OperationPlanAll plan,
			List<OisInstanceStatus> packageStatuses) {
		long planId = plan.getId();

		boolean isHospital = Utils.equals(plan.getHospital_inner_sn(), userInfo.getOrgId());

		// 4 sql
		OrthopaedicOperationPlanOisInstanceExample oisExample = this.oisByPlanId(planId);
		if (!isHospital) {
			oisExample.getOredCriteria().get(0).andDealer_inner_snEqualTo(userInfo.getOrgId()); // dealer
		}
		if (Utils.isNotEmpty(packageStatuses)) {
			oisExample.getOredCriteria().get(0).andStatusIn(packageStatuses);
		}

		List<PlanOisPackageAll> ois = this.loadOis(oisExample);

		plan.setDetails(ois);
		return ois;
	}

	// load ois details
	private List<PlanOisPackageAll> loadOis(OrthopaedicOperationPlanOisInstanceExample example) {
		List<PlanOisPackageAll> ois = this.oisMapper.selectByExample(example) //
				.stream()//
				.map(e -> Utils.clone(e, PlanOisPackageAll.class)).collect(Collectors.toList());
		List<Long> oisIds = ois.stream().map(e -> e.getId()).collect(Collectors.toList());

		if (!oisIds.isEmpty()) {
			List<OrthopaedicOperationPlanOisProductDetail> oisPrd = this.oisDetailMapper
					.selectByExample(this.oisDetailsByOisIds(oisIds));

			List<PlanKitAll> kits = this.kitMapper.selectByExample(this.kitByOisIds(oisIds)).stream()
					.map(e -> Utils.clone(e, PlanKitAll.class)).collect(Collectors.toList());

			List<OrthopaedicOperationPlanKitProductDetail> kitPrd = kits.isEmpty() ? Collections.emptyList()
					: this.kitDetailMapper.selectByExample(
							this.kitDetailsByKitIds(kits.stream().map(e -> e.getId()).collect(Collectors.toList())));

			kits.forEach(k -> {
				k.setDetails(
						kitPrd.stream().filter(e -> e.getKit_ins_id().equals(k.getId())).collect(Collectors.toList()));
			});

			ois.forEach(p -> {
				p.setDetails(
						oisPrd.stream().filter(e -> e.getOis_ins_id().equals(p.getId())).collect(Collectors.toList()));
				p.setKits(kits.stream().filter(k -> k.getOis_ins_id().equals(p.getId())).collect(Collectors.toList()));
			});

		}
		return ois;
	}

	public OperationPlanAll getPlan(ContextUserInfo userInfo, long id, List<OisInstanceStatus> packageStatus) {
		OrthopaedicOperationPlan plan = this.planMapper.selectByPrimaryKey(id);
		Utils.assertNotEmpty(plan, "手术计划不存在: " + id);
		OperationPlanAll a = Utils.clone(plan, OperationPlanAll.class);
		planOis(userInfo, a, packageStatus);
		return a;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// ois

	/**
	 * get a package with details
	 * 
	 * @param userInfo
	 * @param id
	 * @return
	 */
	public PlanOisPackageAll getPackageAll(ContextUserInfo userInfo, long id) {
		OrthopaedicOperationPlanOisInstanceExample example = new OrthopaedicOperationPlanOisInstanceExample();
		example.or().andIdEqualTo(id);
		return this.loadOis(example).stream().findFirst().orElse(null);
	}

	/**
	 * add or edit ois
	 * 
	 * @param userInfo
	 * @param record
	 * @return
	 */
	@Transactional
	public PlanOisPackageAll savePackageAll(ContextUserInfo userInfo, PlanOisPackageAll record) {
		Utils.assertNotEmpty(record.getPlan_id(), "计划id为空");
		OrthopaedicOperationPlan plan = this.planMapper.selectByPrimaryKey(record.getPlan_id());

		Utils.assertNotEmpty(plan, "计划 不存在");
		Utils.assertTrue(plan.getStatus().canAddOis(), "计划不允许加入手术包: " + plan.getStatus());

		if (record.getId() == null) {
			Utils.assertNotEmpty(record.getPlan_id(), "计划id为空");
			record.setId(CompressedIdGenerator.generateId());
			record.setStatus(OisInstanceStatus.NORMAL);
			record.setCreated_time(new Date());
			record.setLast_modified_time(new Date());
			if (Utils.isEmpty(record.getKeywords())) {
				String s = Stream.of(record.getName(), record.getAssistant_employee_name()) //
						.reduce("", (a, b) -> a + " " + b);
				record.setKeywords(PinyinUtils.getPy(s));
			}
			record.setKeywords(Utils.maxLength(record.getKeywords(), MAX_KEYWORDS_LEN));
			this.oisMapper.insertSelective(record);
			this.savePakageChildren(record, false);
		} else {
			Utils.assertTrue(record.getStatus().canEdit(), "不能修改: " + record.getStatus());
			record.setLast_modified_time(new Date());
			this.oisMapper.updateByPrimaryKeySelective(record);
			this.savePakageChildren(record, true);
		}
		return record;
	}

	private void savePakageChildren(PlanOisPackageAll ois, boolean isUpdate) {
		long planId = ois.getPlan_id();
		if (isUpdate) {
			deletePackageChildren(ois.getId());
		}

		ois.getDetails().forEach(e -> {
			e.setId(CompressedIdGenerator.generateId());
			e.setOis_ins_id(ois.getId()); // parent
			e.setPlan_id(planId);
			this.oisDetailMapper.insertSelective(e);
		});
		ois.getKits().forEach(kit -> {
			kit.setId(CompressedIdGenerator.generateId());
			kit.setOis_ins_id(ois.getId()); // parent
			kit.setPlan_id(planId);
			kit.setKeywords(Utils.maxLength(kit.getKeywords(), MAX_KEYWORDS_LEN));
			this.kitMapper.insertSelective(kit);
			kit.getDetails().forEach(e -> {
				e.setId(CompressedIdGenerator.generateId());
				e.setKit_ins_id(kit.getId()); // parent
				e.setPlan_id(planId);
				this.kitDetailMapper.insertSelective(e);
			});
		});
	}

	/**
	 * delete ois
	 * 
	 * @param userInfo
	 * @param oisId
	 * @return
	 */
	@Transactional
	public boolean deletePackage(ContextUserInfo userInfo, long oisId) {

		OrthopaedicOperationPlanOisInstance record = this.oisMapper.selectByPrimaryKey(oisId);
		Utils.assertTrue(record.getStatus().canDelete(), "不能删除: " + record.getStatus());
		// TODO check plan status
		/// OrthopaedicOperationPlan plan =
		// this.planMapper.selectByPrimaryKey(record.getPlan_id());
		// Utils.assertEquals(userInfo.getOrgId(), record.getDealer_inner_sn(), "");

		deletePackageChildren(oisId);
		return this.oisMapper.deleteByPrimaryKey(oisId) == 1;
	}

	private void deletePackageChildren(long oisId) {
		List<Long> kitIds = this.kitMapper.selectByExample(this.kitByOisId(oisId)).stream().map(k -> k.getId())
				.collect(Collectors.toList());
		if(!kitIds.isEmpty()) {
			this.kitDetailMapper.deleteByExample(this.kitDetailsByKitIds(kitIds));
		}
		this.kitMapper.deleteByExample(this.kitByOisId(oisId));

		this.oisDetailMapper.deleteByExample(this.oisDetailsByOisId(oisId));
	}

	/**
	 * 锁定库存
	 * 
	 * @param userInfo
	 * @param packageId
	 */
	@Transactional
	public OrthopaedicOperationPlanOisInstance lockPackage(ContextUserInfo userInfo,
			OrthopaedicOperationPlanOisInstance p) {
		Utils.assertNotEmpty(p.getId(), "手术包id为空");
		Utils.assertNotEmpty(p.getOut_warehouse_inner_sn(), "仓库id为空");
		Utils.assertNotEmpty(p.getOut_warehouse_name(), "仓库名称为空");

		OrgEmployee emp = this.orgService.getEmployeeInfoByUser(userInfo.getSystemType(), userInfo.getUid());
		long oisId = p.getId();

		OrthopaedicOperationPlanOisInstance updator = new OrthopaedicOperationPlanOisInstance();
		updator.setId(oisId);
		updator.setStatus(OisInstanceStatus.LOCKED);
		updator.setOut_warehouse_inner_sn(p.getOut_warehouse_inner_sn()); // 仓库
		updator.setOut_warehouse_name(p.getOut_warehouse_name());
		// if(p.getAssistant_employee_inner_sn()==null)
		updator.setAssistant_employee_inner_sn(emp.getEmployee_inner_sn());
		updator.setAssistant_employee_name(emp.getName());
		OrthopaedicOperationPlanOisInstance record = updateOisStatus(updator, OisInstanceStatus.NORMAL);
		// the status check is ok

		// 锁库存
		final Long dealerInnerSn = record.getDealer_inner_sn();

		getOisProductForInventory(oisId).forEach(e -> {
			InventoryKey lockKey = InventoryKey.ofProductForDealer(//
					dealerInnerSn, //
					null,
					e.getProduct_vendor_inner_sn().longValue(), //
					e.getProduct_inner_sn().longValue(), //
					e.getSpecification_inner_sn().longValue(), //
					Constants.PRE_LOCK_BATCH_NUMBER);
			InventoryKey inventoryQueryKey = InventoryKey.of(lockKey);
//			inventoryQueryKey.setBatchNumber(null); // 检查库存余量时，不区分仓库和批号(所有批号的 【库存量 - 锁定量】为可用余量)
			try {
				inventoryLocker.lockInventory(lockKey, inventoryQueryKey, e.getUnit_inner_sn().longValue(),
						e.getQuantity());
			} catch (InventoryException ex) {
				logger.error("", ex);
				String message = "库存不足: " + e.getProduct_name() + ">>" + e.getSpecification() + ">>" + e.getUnit() + "\\n" 
						+ "请求锁定量: " + ex.getRequestQuantity().longValue() + " 请求最小单位锁定量: " + ex.getRequestMinUnitQuantity().longValue() + "\\n"
						+ "库存余量(最小单位):" + ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE);
				throw new RuntimeException(message);
			}
		});

		return record;
	}

	//
	/**
	 * for inventory operations ois-prd & kit-prd with convert kit-prd to ois-prd
	 * record format(lost some info)
	 * 
	 * @param packageId
	 * @return
	 */
	private List<OrthopaedicOperationPlanOisProductDetail> getOisProductForInventory(long oisId) {
		List<OrthopaedicOperationPlanOisProductDetail> details = this.oisDetailMapper
				.selectByExample(this.oisDetailsByOisId(oisId));// ois_ins_id
		List<Long> kids = this.kitMapper.selectByExample(this.kitByOisId(oisId)).stream().map(e -> e.getId())
				.collect(Collectors.toList());
		if (!kids.isEmpty()) {
			this.kitDetailMapper // convert kit-prd as ois-prd
					.selectByExample(this.kitDetailsByKitIds(kids)).forEach(e -> {
						OrthopaedicOperationPlanOisProductDetail d = Utils.clone(e,
								OrthopaedicOperationPlanOisProductDetail.class);
						d.setOis_ins_id(e.getKit_ins_id()); // kit_ins_id -> ois_ins_id (no use)
						details.add(d); // merge
					});
		}
		return details;
	}

	/**
	 * release PRE_LOCK quantity for {@link #finishPackage(ContextUserInfo, long)}
	 * 
	 * @param record
	 * @param details
	 *            ois-details + kit-details
	 */
	private void unlockPackage(OrthopaedicOperationPlanOisInstance record,
			List<OrthopaedicOperationPlanOisProductDetail> details) {
		details.forEach(e -> {
			InventoryKey lockKey = InventoryKey.ofProductForDealer(//
					record.getDealer_inner_sn(), //
					null,
					e.getProduct_vendor_inner_sn().longValue(), //
					e.getProduct_inner_sn().longValue(), //
					e.getSpecification_inner_sn().longValue(), //
					Constants.PRE_LOCK_BATCH_NUMBER);
			// unlock 不检查库存余量时
			inventoryLocker.unlockInventory(lockKey, e.getUnit_inner_sn().longValue(), e.getQuantity());
		});
	}

	/**
	 * 出库
	 * 
	 * @param userInfo
	 * @param packageId
	 */
	@Transactional
	public OrthopaedicOperationPlanOisInstance outPackage(ContextUserInfo userInfo, long packageId) {
		// OrthopaedicOperationPlanOisInstance p
		OrgEmployee emp = this.orgService.getEmployeeInfoByUser(userInfo.getSystemType(), userInfo.getUid());
		OrthopaedicOperationPlanOisInstance updator = new OrthopaedicOperationPlanOisInstance();
		updator.setId(packageId);
		updator.setStatus(OisInstanceStatus.OUT);
		updator.setOut_employee_inner_sn(emp.getEmployee_inner_sn());
		updator.setOut_employee_name(emp.getName());
		updator.setOut_time(new Date());
		// 助理 锁库存时环节 指定了 仓库
		OrthopaedicOperationPlanOisInstance record = updateOisStatus(updator, OisInstanceStatus.LOCKED);
		return record;
	}

	/**
	 * 回库（回库数量)
	 * 
	 * @param userInfo
	 * @param record
	 */
	@Transactional
	public OrthopaedicOperationPlanOisInstance backPackage(ContextUserInfo userInfo, PlanOisPackageAll all) {
		OrgEmployee emp = this.orgService.getEmployeeInfoByUser(userInfo.getSystemType(), userInfo.getUid());
		OrthopaedicOperationPlanOisInstance updator = new OrthopaedicOperationPlanOisInstance();
		updator.setId(all.getId());
		updator.setStatus(OisInstanceStatus.BACK);
		updator.setBack_employee_inner_sn(emp.getEmployee_inner_sn());
		updator.setBack_employee_name(emp.getName());
		updator.setBack_time(new Date());

		boolean noSale = true;

		// 回库 带 所有明细 (回库数量不等于出库数量的，批次号必填)
		for (OrthopaedicOperationPlanOisProductDetail e : all.getDetails()) {
			Utils.assertNotEmpty(e.getBack_quantity(), "回库数量为空");
			OrthopaedicOperationPlanOisProductDetail d = new OrthopaedicOperationPlanOisProductDetail();
			d.setId(e.getId());
			d.setBack_quantity(e.getBack_quantity());
			if (!Utils.equals(e.getBack_quantity(), e.getQuantity())) {
//				Utils.assertNotEmpty(e.getProduce_date(), "生产日期 为空");
//				Utils.assertNotEmpty(e.getOverdue_date(), "过期日期 为空");
				d.setBatch_number(Utils.ifEmpty(e.getBatch_number(), "")); // 批次号可能为 ""
				d.setProduce_date(e.getProduce_date());
				d.setOverdue_date(e.getOverdue_date());
				noSale = false;
			}
			this.oisDetailMapper.updateByPrimaryKeySelective(d);
		}

		for (PlanKitAll k : all.getKits()) {
			for (OrthopaedicOperationPlanKitProductDetail e : k.getDetails()) {
				Utils.assertNotEmpty(e.getBack_quantity(), "回库数量为空");
				OrthopaedicOperationPlanKitProductDetail d = new OrthopaedicOperationPlanKitProductDetail();
				d.setId(e.getId());
				d.setBack_quantity(e.getBack_quantity());
				if (!Utils.equals(e.getBack_quantity(), e.getQuantity())) {
//					Utils.assertNotEmpty(e.getProduce_date(), "生产日期 为空");
//					Utils.assertNotEmpty(e.getOverdue_date(), "过期日期 为空");
					d.setBatch_number(Utils.ifEmpty(e.getBatch_number(), "")); // 批次号可能为 ""
					d.setBatch_number(e.getBatch_number());
					d.setProduce_date(e.getProduce_date());
					d.setOverdue_date(e.getOverdue_date());
					noSale = false;
				}
				this.kitDetailMapper.updateByPrimaryKeySelective(d);
			}
		}
		/*
		 * all.getKits().stream().flatMap(k -> k.getDetails().stream()).forEach(e -> {
		 * Utils.assertNotEmpty(e.getBack_quantity(), "回库数量为空");
		 * OrthopaedicOperationPlanKitProductDetail d = new
		 * OrthopaedicOperationPlanKitProductDetail(); d.setId(e.getId());
		 * d.setBack_quantity(e.getBack_quantity()); if
		 * (!Utils.equals(e.getBack_quantity(), e.getQuantity())) {
		 * Utils.assertNotEmpty(e.getProduce_date(), "生产日期 为空");
		 * Utils.assertNotEmpty(e.getOverdue_date(), "过期日期 为空");
		 * d.setBatch_number(Utils.ifEmpty(e.getBatch_number(), "")); // 批次号可能为 ""
		 * d.setBatch_number(e.getBatch_number()); }
		 * this.kitDetailMapper.updateByPrimaryKeySelective(d); });
		 */
		OrthopaedicOperationPlanOisInstance record = null;
		if(noSale) {
			updator.setStatus(OisInstanceStatus.CLOSED);
		}
		record = updateOisStatus(updator, OisInstanceStatus.OUT);
		if(noSale) {
			closePlan(userInfo, record.getPlan_id(), false);
			long packageId = record.getId();
			List<OrthopaedicOperationPlanOisProductDetail> details = this.getOisProductForInventory(packageId);
			this.unlockPackage(record, details);
		}
		return record;
	}

	/**
	 * 待确认销售: 单价/销售数/报损数/报损原因
	 * 
	 * @param userInfo
	 * @param record
	 */
	@Transactional
	public OrthopaedicOperationPlanOisInstance pendingSalePackage(ContextUserInfo userInfo, PlanOisPackageAll all) {
		OrthopaedicOperationPlanOisInstance updator = new OrthopaedicOperationPlanOisInstance();
		updator.setId(all.getId());
		updator.setStatus(OisInstanceStatus.SALE_PENDING);

		OrthopaedicOperationPlanOisInstance record = updateOisStatus(updator, OisInstanceStatus.BACK,
				OisInstanceStatus.SALE_PENDING);// allow refill price etc.

		// 待销售: 仅回传 有 销售或报损的 明细 (回传所有字段)
		all.getDetails().forEach(e -> {
			Utils.assertNotEmpty(e.getId(), "明细id为空");
			Utils.assertNotEmpty(e.getSale_quantity(), "销售数量为空");
			Utils.assertNotEmpty(e.getLoss_quantity(), "报损数量为空");
			Utils.assertNotEmpty(e.getUnit_price(), "单价为空");
			// Utils.assertEquals(e.getQuantity(),
			// e.getSale_quantity().add(e.getLoss_quantity()).add(e.getBack_quantity()),
			// "数量不平");

			OrthopaedicOperationPlanOisProductDetail d = new OrthopaedicOperationPlanOisProductDetail();
			d.setId(e.getId());
			d.setSale_quantity(e.getSale_quantity());
			d.setLoss_quantity(e.getLoss_quantity());
			d.setLoss_reason(e.getLoss_reason());
			d.setUnit_price(e.getUnit_price());
			this.oisDetailMapper.updateByPrimaryKeySelective(d);
		});

		all.getKits().stream().flatMap(k -> k.getDetails().stream()).forEach(e -> {
			Utils.assertNotEmpty(e.getId(), "明细id为空");
			Utils.assertNotEmpty(e.getSale_quantity(), "销售数量为空");
			Utils.assertNotEmpty(e.getLoss_quantity(), "报损数量为空");
			Utils.assertNotEmpty(e.getUnit_price(), "单价为空");
			// Utils.assertEquals(e.getQuantity(),
			// e.getSale_quantity().add(e.getLoss_quantity()).add(e.getBack_quantity()),
			// "数量不平");

			OrthopaedicOperationPlanKitProductDetail d = new OrthopaedicOperationPlanKitProductDetail();
			d.setId(e.getId());
			d.setSale_quantity(e.getSale_quantity());
			d.setLoss_quantity(e.getLoss_quantity());
			d.setLoss_reason(e.getLoss_reason());
			d.setUnit_price(e.getUnit_price());
			this.kitDetailMapper.updateByPrimaryKeySelective(d);
		});
		return record;
	}

	/**
	 * 确认销售数据(医院)
	 * 
	 * @param userInfo
	 * @param packageId
	 * @return
	 */
	@Transactional
	public List<OrthopaedicOperationPlanOisInstance> confirmSalePackage(ContextUserInfo userInfo,
			List<Long> packageIds) {
		return packageIds.stream().map(packageId -> {
			OrthopaedicOperationPlanOisInstance updator = new OrthopaedicOperationPlanOisInstance();
			updator.setId(packageId);
			updator.setStatus(OisInstanceStatus.SALE_CONFIRMED);

			OrthopaedicOperationPlanOisInstance record = updateOisStatus(updator, OisInstanceStatus.SALE_PENDING);
			return record;
		}).collect(Collectors.toList());
	}

	/**
	 * 结束/关闭 手术包(助理 完成并出单)
	 * 
	 * @param userInfo
	 * @param record
	 */
	@Transactional
	public OrthopaedicOperationPlanOisInstance finishPackage(ContextUserInfo userInfo, long packageId) {
		OrthopaedicOperationPlanOisInstance updator = new OrthopaedicOperationPlanOisInstance();
		updator.setId(packageId);
		updator.setStatus(OisInstanceStatus.CLOSED);

		OrthopaedicOperationPlanOisInstance record = updateOisStatus(updator, OisInstanceStatus.SALE_CONFIRMED);

		createSaleOdo(record);

		closePlan(userInfo, record.getPlan_id(), false);

		return record;
	}

	/**
	 * 1. unlock PRE_LOCK 2. create sl 3. create odo
	 * 
	 * @param record
	 */
	private void createSaleOdo(OrthopaedicOperationPlanOisInstance record) {
		long packageId = record.getId();

		OrthopaedicOperationPlan plan = this.planMapper.selectByPrimaryKey(record.getPlan_id());

		/**
		 * 骨科助理
		 */
		OrgEmployee assistantEmployee = orgService.selectOrgEmployeeInfoById(SystemType.DEALER,
				record.getDealer_inner_sn(), record.getAssistant_employee_inner_sn());
		Utils.assertNotEmpty(assistantEmployee, "骨科助理不存在: id=" + record.getAssistant_employee_inner_sn());
		Utils.assertNotEmpty(assistantEmployee.getDepartment_inner_sn(),
				"骨科助理 没有部门: name=" + record.getAssistant_employee_name());

		// query details(ois & kit)
		List<OrthopaedicOperationPlanOisProductDetail> details = this.getOisProductForInventory(packageId);

		/**
		 * unlock inventory（cancel PRE_LOCK）
		 */
		this.unlockPackage(record, details);

		/**
		 * 销售单
		 */
		BuildSaleOrderForm saleForm = new BuildSaleOrderForm(); // 销售单
		// 订单类型(非存储字段，用于判断 操作的数据库表)
		saleForm.setBillsType(BillsType.DH); /** dealer->hospital */
		saleForm.setPo_so_inner_sn(record.getPlan_id()); // 上游单据: 计划id(一个计划可建多个销售单)
		saleForm.setStatus(2);// 2-已审核

		saleForm.setOrg_id(record.getDealer_inner_sn()); //

		saleForm.setOrganization_inner_sn(plan.getHospital_inner_sn());// 相关机构
		saleForm.setOrganization_name(plan.getHospital_name());

		saleForm.setSeller_inner_sn(record.getAssistant_employee_inner_sn());
		saleForm.setSeller_name(record.getAssistant_employee_name());
		saleForm.setAuditor_inner_sn(record.getAssistant_employee_inner_sn());
		saleForm.setAuditor_name(record.getAssistant_employee_name());

		// 部门, 卖方 MUST
		saleForm.setDepartment_id(Long.valueOf(assistantEmployee.getDepartment_inner_sn().split(",")[0]));// 第一个部门
		saleForm.setDepartment_name(Utils.ifEmpty(assistantEmployee.getDepartment_name(), "").split(",")[0]);

		// saleForm.setCollection_date(collection_date);
		// saleForm.setPurchaser_linkman(purchaser_linkman);
		// saleForm.setPurchaser_phone(purchaser_phone);
		// saleForm.setSeller_linkman(seller_linkman);
		// saleForm.setSeller_phone(seller_phone);
		saleForm.setDescription("由骨科手术：" + plan.getName() + ", 手术包: " + record.getName() + "自动生成");

		details.stream().filter(e -> e.getSale_quantity().compareTo(BigDecimal.ZERO) > 0).forEach(e -> {
			BuildSaleOrderDetailForm sd = new BuildSaleOrderDetailForm();
			sd.setVoucher_type(RefVoucherType.ORTHOPAEDIC);
			sd.setProduct_type(PrdType.VENDOR_PRD.value());
			sd.setDetail_inner_sn(e.getId()); // 上游单据明细id(prd-detail-id)

			sd.setProduct_vendor_inner_sn(e.getProduct_vendor_inner_sn().longValue());
			sd.setProduct_vendor_name(e.getProduct_vendor_name());

			sd.setProduct_inner_sn(e.getProduct_inner_sn().longValue());
			sd.setProduct_name(e.getProduct_name());

			sd.setSpecification_inner_sn(e.getSpecification_inner_sn().longValue());
			sd.setSpecification(e.getSpecification());

			sd.setUnit_inner_sn(e.getUnit_inner_sn().longValue());
			sd.setUnit(e.getUnit());

			sd.setBatch_number(e.getBatch_number());
			sd.setUnit_price(e.getUnit_price());
			sd.setQuantity(e.getSale_quantity()); // 销售数量
			// sd.setDiscount(1);
			// sd.setRate(0);

			// sd.setMdrf_inner_sn(?); //null
			// sd.setMdrf_sn(?); //must
			this.setSdMdrf(e, sd);

			// sd.setSort_number(0);
			// sd.setMin_unit_for_sale_quantity(min_unit_for_sale_quantity); //no use

			saleForm.getDetails().add(sd); // sr details
		});

		if (saleForm.getDetails().isEmpty()) {
			logger.info("no any prd to be sale for this plan-ois: {}", packageId);
			return;
		}

		// 制单人=assistantEmployee
		long slId = saleService.buildSaleOrder(saleForm, SystemType.DEALER); /** add 销售单(锁定库存：按实际库存) */
		SaleRecordIDForm slidForm = new SaleRecordIDForm();
		slidForm.setOrg_id(saleForm.getOrg_id());
		slidForm.setBillsType(saleForm.getBillsType());
		slidForm.setSl_inner_sn(slId);
		Sl sl = saleService.selectSaleRecordInfo(slidForm);
		logger.info("result sl({}): {}", slId, sl);

		/**
		 * 出库单
		 */
		AddOdoForm odoForm = new AddOdoForm(); // 出库单
		odoForm.setOrg_id(record.getDealer_inner_sn());
		// 已审核
		odoForm.setStatus(OdoStatus.AUDITED.value());// OdoStatus.OUT_POSTED;

		// 出库单类型? 0-DealerDealer 1-DealerVendor 2-DealerHospital 3-HospitalHospital
		// 4-HospitalDealer 5-HospitalVendor
		// 出库单类型(非存储字段，仅用于判断 操作的数据库表)
		odoForm.setBillsType(BillsType.DH); /** dealer->hospital */

		// 出库单类型(固定常量) 预置类型
		odoForm.setType_inner_sn(OPERATION_ODO_TYPE);
		odoForm.setType_name(OPERATION_ODO_TYPE_NAME);

		// odoForm.setOdo_sn(odo_sn); // createOdo generate it.

		odoForm.setOrganization_inner_sn(plan.getHospital_inner_sn());// 相关机构?
		odoForm.setOrganization_name(plan.getHospital_name());

		odoForm.setWarehouse_inner_sn(record.getOut_warehouse_inner_sn());
		odoForm.setWarehouse_name(record.getOut_warehouse_name());

		odoForm.setHandler_inner_sn(record.getAssistant_employee_inner_sn());
		odoForm.setHandler_name(record.getAssistant_employee_name());

		odoForm.setWarehouse_operator_inner_sn(record.getOut_employee_inner_sn());
		odoForm.setWarehouse_operator_name(record.getOut_employee_name());

		// 开单部门
		odoForm.setDepartment_inner_sn(saleForm.getDepartment_id());
		odoForm.setDepartment_name(saleForm.getDepartment_name());
		odoForm.setDescription("骨科出库");

		sl.getDetails().forEach(sd -> {
			// 出库单明细
			OdoDetailForm od = new OdoDetailForm();
			od.setVoucher_detail_inner_sn_c(sd.getDetail_inner_sn());
			od.setVoucher_type_c(RefVoucherType.SALE_RECORD);
			od.setVoucher_inner_sn_c(sl.getSl_inner_sn());
			od.setProduct_type(sd.getProduct_type()); // 厂商/自建

			od.setProduct_vendor_inner_sn(sd.getProduct_vendor_inner_sn());
			od.setProduct_vendor_name(sd.getProduct_vendor_name());
			od.setProduct_inner_sn(sd.getProduct_inner_sn());
			od.setProduct_name(sd.getProduct_name());
			od.setSpecification_inner_sn(sd.getSpecification_inner_sn());
			od.setSpecification(sd.getSpecification());
			od.setUnit_inner_sn(sd.getUnit_inner_sn());
			od.setUnit_name(sd.getUnit_name());
			// mdrf MUST
			od.setMdrf_inner_sn(sd.getMdrf_inner_sn());// null
			od.setMdrf_sn(sd.getMdrf_sn()); // must
			od.setUnit_price(sd.getUnit_price());
			od.setQuantity(sd.getQuantity());
//			od.setQuantity_old(BigDecimal.ZERO);
			// TODO:生产日期/失效日期
			// od.setProduce_date(?); //must
			// od.setOverdue_date(?); //must
			
			// 上游单据明细id: sd.getDetail_inner_sn()
			OrthopaedicOperationPlanOisProductDetail originItem = details.stream()//
					.filter(d -> d.getId().equals(sd.getVoucher_detail_inner_sn_c())) // 上游单据明细id: sd.getDetail_inner_sn()
					.findFirst().orElseThrow(() -> new RuntimeException("上游明细单据不存在？")); // saleService.buildSaleOrder
			od.setProduce_date(originItem.getProduce_date());
			od.setOverdue_date(originItem.getOverdue_date());
			//od.setProduce_date(new Date());
			//od.setOverdue_date(new Date());

			// 类别(常规/赠品)
			od.setType(GrnOdoDetailType.NORMAL.value());
			od.setBatch_number(sd.getBatch_number());
			od.setSort_number(sd.getSort_number());
//			od.setAction(1);//新增
			odoForm.getDetails().add(od);
		});

		Odo odo = odoService.insertOdo(assistantEmployee, SystemType.DEALER, odoForm); /** add出库单(解锁buildSaleOrder，减库存) */
		logger.info("result odo: {}", odo);
		OdoListIDForm odoIDForm = new OdoListIDForm();
		odoIDForm.setOrg_id(record.getDealer_inner_sn());
		OdoIds ids = new OdoIds();
		ids.setOdo_inner_sn(odo.getOdo_inner_sn());
		ids.setWarehouse_inner_sn(odo.getWarehouse_inner_sn());
		ids.setBillsType(odo.getBillsType());
		List<OdoIds> list = new ArrayList<>();
		list.add(ids);
		odoIDForm.setIds(list);
		odoService.putout(odoIDForm);
		logger.info("put out success!!");
	}

	private void setSdMdrf(OrthopaedicOperationPlanOisProductDetail pd, BuildSaleOrderDetailForm sd) {
		Map<String, Object> param = new HashMap<>();
		param.put("vendor_inner_sn", pd.getProduct_vendor_inner_sn());
		param.put("product_inner_sn", pd.getProduct_inner_sn());

		List<Map<String,Object>> mdrfs = productMapper.selectNewestRegister_sn(param);
		if(StringUtils.isEmpty(mdrfs)) {
			sd.setMdrf_inner_sn(null);
			sd.setMdrf_sn("");
		}else {
			sd.setMdrf_inner_sn(Long.valueOf(mdrfs.get(0).get("mdrf_inner_sn").toString()));
			sd.setMdrf_sn(mdrfs.get(0).get("register_sn").toString());
		}
	}

	/**
	 * 
	 * @param updator
	 *            must set id
	 * @param preStatus
	 * @return
	 */
	private OrthopaedicOperationPlanOisInstance updateOisStatus(OrthopaedicOperationPlanOisInstance updator,
			OisInstanceStatus... preStatus) {
		long oisId = updator.getId();
		List<OisInstanceStatus> statuses = Arrays.asList(preStatus);

		OrthopaedicOperationPlanOisInstance record = this.oisMapper.selectByPrimaryKey(oisId);
		Utils.assertNotEmpty(record, "手术包不存在");
		Utils.assertTrue(statuses.contains(record.getStatus()), //
				String.format("手术包不允许此操作：从【%s】到【%s】", record.getStatus(), updator.getStatus()) //
		);

		OrthopaedicOperationPlanOisInstanceExample example = new OrthopaedicOperationPlanOisInstanceExample();
		example.or().andIdEqualTo(oisId).andStatusIn(statuses); //

		updator.setLast_modified_time(new Date());

		Utils.assertEquals(1, this.oisMapper.updateByExampleSelective(updator, example), "手术包状态已被改变(请刷新)");// concurrent
		com.yrt.common.utils.bean.BeanUtils.copyBeanProp(record, updator, true);
		return record;
	}

	//////////////////////// examples
	private OrthopaedicOperationPlanOisInstanceExample oisByPlanId(long planId) {
		OrthopaedicOperationPlanOisInstanceExample example = new OrthopaedicOperationPlanOisInstanceExample();
		example.or().andPlan_idEqualTo(planId);
		return example;
	}

	@SuppressWarnings("unused")
	private OrthopaedicOperationPlanOisInstanceExample oisByOisIds(List<Long> oisIds) {
		OrthopaedicOperationPlanOisInstanceExample example = new OrthopaedicOperationPlanOisInstanceExample();
		example.or().andIdIn(oisIds);
		return example;
	}

	private OrthopaedicOperationPlanOisProductDetailExample oisDetailsByPlanId(long planId) {
		OrthopaedicOperationPlanOisProductDetailExample example = new OrthopaedicOperationPlanOisProductDetailExample();
		example.or().andPlan_idEqualTo(planId);
		return example;
	}

	private OrthopaedicOperationPlanOisProductDetailExample oisDetailsByOisId(long oisId) {
		OrthopaedicOperationPlanOisProductDetailExample example = new OrthopaedicOperationPlanOisProductDetailExample();
		example.or().andOis_ins_idEqualTo(oisId);
		return example;
	}

	private OrthopaedicOperationPlanOisProductDetailExample oisDetailsByOisIds(List<Long> oisIds) {
		OrthopaedicOperationPlanOisProductDetailExample example = new OrthopaedicOperationPlanOisProductDetailExample();
		example.or().andOis_ins_idIn(oisIds);
		return example;
	}

	private OrthopaedicOperationPlanKitInstanceExample kitByPlanId(long planId) {
		OrthopaedicOperationPlanKitInstanceExample example = new OrthopaedicOperationPlanKitInstanceExample();
		example.or().andPlan_idEqualTo(planId);
		return example;
	}

	// parent
	private OrthopaedicOperationPlanKitInstanceExample kitByOisId(long oisId) {
		OrthopaedicOperationPlanKitInstanceExample example = new OrthopaedicOperationPlanKitInstanceExample();
		example.or().andOis_ins_idEqualTo(oisId);
		return example;
	}

	private OrthopaedicOperationPlanKitInstanceExample kitByOisIds(List<Long> oisIds) {
		OrthopaedicOperationPlanKitInstanceExample example = new OrthopaedicOperationPlanKitInstanceExample();
		example.or().andOis_ins_idIn(oisIds);
		return example;
	}

	private OrthopaedicOperationPlanKitProductDetailExample kitDetailsByPlanId(long planId) {
		OrthopaedicOperationPlanKitProductDetailExample example = new OrthopaedicOperationPlanKitProductDetailExample();
		example.or().andPlan_idEqualTo(planId);
		return example;
	}

	// parent
	// private OrthopaedicOperationPlanKitProductDetailExample
	// kitDetailsByKitId(long kitId) {
	// OrthopaedicOperationPlanKitProductDetailExample example = new
	// OrthopaedicOperationPlanKitProductDetailExample();
	// example.or().andKit_ins_idEqualTo(kitId);
	// return example;
	// }

	// parent
	private OrthopaedicOperationPlanKitProductDetailExample kitDetailsByKitIds(List<Long> kitIds) {
		OrthopaedicOperationPlanKitProductDetailExample example = new OrthopaedicOperationPlanKitProductDetailExample();
		example.or().andKit_ins_idIn(kitIds);
		return example;
	}

	///////////////////////////////////////////////////////////////////////

}
