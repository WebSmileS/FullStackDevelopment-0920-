package com.yrt.project.api.business.warehouse.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.yrt.common.utils.*;
import com.yrt.project.modular.premium.service.PremiumRuleService;
import com.yrt.project.modular.premium.service.ProductPremiumPercentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.InspectionStatus;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.NotFoundUDIException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.bean.BeanUtils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.invoice.vo.SearchInvoiceProductForm;
import com.yrt.project.api.business.order.service.IOrderService;
import com.yrt.project.api.business.order.service.PurchasingRecordService;
import com.yrt.project.api.business.order.vo.purchasingRecord.SearchPurchasingRecordProductForm;
import com.yrt.project.api.business.sale.vo.SearchReturnProductForm;
import com.yrt.project.api.business.warehouse.service.AllotVoucherService;
import com.yrt.project.api.business.warehouse.service.CentralService;
import com.yrt.project.api.business.warehouse.service.IGrnService;
import com.yrt.project.api.business.warehouse.service.InspectionRecordService;
import com.yrt.project.api.business.warehouse.vo.ScanBarcodeForm;
import com.yrt.project.api.business.warehouse.vo.UsableProductInfo;
import com.yrt.project.api.business.warehouse.vo.allot.SearchAllotVoucherForm;
import com.yrt.project.api.business.warehouse.vo.central.AddNoticeForm;
import com.yrt.project.api.business.warehouse.vo.central.NoticeDetailForm;
import com.yrt.project.api.business.warehouse.vo.grn.AddGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.AppGrnSearchForm;
import com.yrt.project.api.business.warehouse.vo.grn.EditGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnDetailForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIDForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIds;
import com.yrt.project.api.business.warehouse.vo.grn.SearchGrnForm;
import com.yrt.project.api.business.warehouse.vo.inspection.InspectionRecordDetailForm;
import com.yrt.project.api.business.warehouse.vo.inspection.InspectionRecordForm;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.api.common.service.UDIService;
import com.yrt.project.api.common.service.VoucherLockService;
import com.yrt.project.api.common.vo.UDIDInfo;
import com.yrt.project.api.external.warehouse.SyncWarehouseService;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.api.vo.Quantities;
import com.yrt.project.api.vo.VoucherKey;
import com.yrt.project.modular.gsp.domain.GspSetup;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;
import com.yrt.project.modular.warehouse.domain.Grn;
import com.yrt.project.modular.warehouse.domain.Grn4External;
import com.yrt.project.modular.warehouse.domain.GrnDetail;
import com.yrt.project.modular.warehouse.domain.GrnDetail4External;
import com.yrt.project.modular.warehouse.domain.InventoryGrnParam;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.domain.TransferVoucher;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import com.yrt.project.modular.warehouse.mapper.AllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.DealerAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.DealerDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.DealerHospitalGrnMapper;
import com.yrt.project.modular.warehouse.mapper.DealerVendorGrnMapper;
import com.yrt.project.modular.warehouse.mapper.DealerWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.GrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalHospitalGrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalVendorGrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.VendorDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.VendorHospitalGrnMapper;
import com.yrt.project.modular.warehouse.mapper.VendorVendorGrnMapper;
import com.yrt.project.modular.warehouse.mapper.VendorWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.WareHouseMapper;

@Slf4j
@Service
public class GrnServiceImpl implements IGrnService {

    @Autowired
    private DealerDealerGrnMapper ddMapper;
    @Autowired
    private DealerHospitalGrnMapper dhMapper;
    @Autowired
    private DealerVendorGrnMapper dvMapper;
    @Autowired
    private HospitalHospitalGrnMapper hhMapper;
    @Autowired
    private HospitalDealerGrnMapper hdMapper;
    @Autowired
    private HospitalVendorGrnMapper hvMapper;
    @Autowired
    private VendorDealerGrnMapper vdMapper;
    @Autowired
    private VendorHospitalGrnMapper vhMapper;
    @Autowired
    private VendorVendorGrnMapper vvMapper;

    @Autowired
    DealerMapper dMapper;
    @Autowired
    HospitalMapper hMapper;
    @Autowired
    VendorMapper vMapper;

    @Autowired
    DealerWarehouseMapper dwMapper;
    @Autowired
    HospitalWarehouseMapper hwMapper;
    @Autowired
    VendorWarehouseMapper vwMapper;

    @Autowired
    IOrderService orderService;

    @Autowired
    SyncWarehouseService syncService;
    @Autowired
    InventoryServiceImpl inventoryService;
    @Autowired
    CommService commService;
    @Autowired
    CentralService centralService;
    @Autowired
    SysParameterService paramService;
    @Autowired
    PurchasingRecordService recordService;
    @Autowired
    InspectionRecordService inspectionService;
    @Autowired
    PremiumRuleService premiumRuleService;
    @Autowired
    ProductPremiumPercentService percentService;
    @Autowired
    PremiumRuleService ruleService;

    private WareHouseMapper getWarehouseMapper() {
        SystemType type = ServletUtils.getCurUserSystemType();
        switch (type) {
            case HOSPITAL:
                return hwMapper;
            case DEALER:
                return dwMapper;
            case VENDOR:
                return vwMapper;
            default:
        }
        return null;
    }

    private GrnMapper getMapper(BillsType billsType) {
        switch (billsType) {
            case DD:
                return ddMapper;
            case DV:
                return dvMapper;
            case DH:
                return dhMapper;
            case HH:
                return hhMapper;
            case HD:
                return hdMapper;
            case HV:
                return hvMapper;
            case VD:
                return vdMapper;
            case VH:
                return vhMapper;
            case VV:
                return vvMapper;
        }
        throw new DBException("当前机构不支持该功能!");
    }


    private int convertGrnTypeToOrganizationType(BillsType grn_type) {
        int organization_type = 0;//0-DealerDealer 1-DealerVendor 2-DealerHospital 3-HospitalHospital 4-HospitalDealer 5-HospitalVendor
        switch (grn_type) {
            case DD:
                organization_type = 2;
                break;
            case DV:
                organization_type = 3;
                break;
            case DH:
                organization_type = 1;
                break;
            case HH:
                organization_type = 1;
                break;
            case HD:
                organization_type = 2;
                break;
            case HV:
                organization_type = 3;
                break;
            case VD:
                organization_type = 2;
                break;
            case VH:
                organization_type = 1;
                break;
            case VV:
                organization_type = 3;
                break;
        }
        return organization_type;
    }

    //获取当年登录人仓库权限
    private List<Long> getCurUserWarehouseIds() {
        return ServletUtils.getCurUserInfo().getWarehouseModelList()
                .stream()
                .map(WarehouseModel::getWarehouse_model_inner_sn)
                .collect(Collectors.toList());
    }

    @Transactional
    void insertGrnDetail(GrnMapper mapper, GrnDetailForm form, Long oid, Long gid, BillsType billsType) {
        if (form.getVoucher_type_c().value() != RefVoucherType.NONE.value()
                && (StringUtils.isNull(form.getVoucher_inner_sn_c()) || StringUtils.isNull(form.getVoucher_detail_inner_sn_c()))) {
            throw new DBException("调单单据编号和单据明细编号不能为空!");
        }
//		int organization_type = convertGrnTypeToOrganizationType(billsType);
        Map<String, Object> params = new HashMap<>();
        params.put("grn_inner_sn_i", gid);
        params.put("product_inner_sn_i", form.getProduct_inner_sn());
        params.put("specification_inner_sn_i", form.getSpecification_inner_sn());
        params.put("unit_inner_sn_i", form.getUnit_inner_sn());
        params.put("unit_price_i", form.getUnit_price());
        params.put("quantity_i", form.getQuantity());
        params.put("produce_date_i", form.getProduce_date());
        params.put("overdue_date_i", form.getOverdue_date());
        params.put("type_i", form.getType());
        params.put("sort_number_i", form.getSort_number());
        params.put("batch_number_i", Utils.isEmpty(form.getBatch_number()) ? "" : form.getBatch_number());
        params.put("product_name_i", form.getProduct_name());
        params.put("specification_i", form.getSpecification());
        params.put("unit_i", form.getUnit_name());
        params.put("voucher_type_c_i", form.getVoucher_type_c());
        params.put("voucher_inner_sn_c_i", form.getVoucher_inner_sn_c());
        params.put("voucher_detail_inner_sn_c_i", form.getVoucher_detail_inner_sn_c());
        params.put("health_care_sn_i", StringUtils.isEmpty(form.getHealth_care_sn()) ? "" : form.getHealth_care_sn());
        params.put("org_id_i", oid);
        params.put("rate_i", StringUtils.isNull(form.getRate()) ? BigDecimal.ZERO : form.getRate());
        params.put("inspection_detail_id_i", form.getInspection_detail_id());
        if (form.getProduct_type() == 1) {//厂商产品
            params.put("product_vendor_inner_sn_i", StringUtils.isNull(form.getProduct_vendor_inner_sn()) ? form.getVendor_inner_sn() : form.getProduct_vendor_inner_sn());
            params.put("product_vendor_name_i", StringUtils.isEmpty(form.getProduct_vendor_name()) ? form.getVendor_name() : form.getProduct_vendor_name());
            if (StringUtils.isNotNull(form.getMdrf_inner_sn())) {
                params.put("mdrf_inner_sn_i", form.getMdrf_inner_sn());
                params.put("mdrf_sn_i", form.getMdrf_sn());
            } else {
                params.put("mdrf_sn_i", "");
            }
            mapper.insertGrnProductDetail(params);
        } else {
            params.put("product_dealer_inner_sn_i", form.getProduct_vendor_inner_sn());
            params.put("product_dealer_name_i", form.getProduct_vendor_name());
            mapper.insertGrnDealerProductDetail(params);
        }
        if (Integer.valueOf(params.get("result_o").toString()) != 0) {
            throw new DBException(MessageUtils.message("db.add.fail", "入库单明细", params.get("message_string_o")));
        }
    }

    @Transactional
    void updateGrnDetail(GrnMapper mapper, GrnDetailForm form, Long oid, Long gid, BillsType billsType) {
        BigDecimal quantity = form.getQuantity().multiply(new BigDecimal(1000));
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", oid);
        params.put("grn_inner_sn", gid);
        params.put("detail_inner_sn", form.getDetail_inner_sn());
        params.put("batch_number", form.getBatch_number());
        params.put("unit_inner_sn", form.getUnit_inner_sn());
        params.put("unit_name", form.getUnit_name());
        params.put("quantity", quantity);
        params.put("rate", form.getRate());
        params.put("unit_price", form.getUnit_price().multiply(new BigDecimal(10000)));
        params.put("produce_date", form.getProduce_date());
        params.put("overdue_date", form.getOverdue_date());
        params.put("type", form.getType());
        if (form.getProduct_type() == 1) {//厂商产品
            params.put("mdrf_inner_sn", form.getMdrf_inner_sn());
            params.put("mdrf_sn", form.getMdrf_sn());
            params.put("health_care_sn", StringUtils.isEmpty(form.getHealth_care_sn()) ? "" : form.getHealth_care_sn());
            mapper.updateGrnProductDetail(params);
        } else {
            mapper.updateGrnDealerProductDetail(params);
        }
    }


    @Transactional
    void deleteGrnDetail(GrnMapper mapper, GrnDetailForm form, Long oid, Long organization_inner_sn, Long gid, BillsType billsType, OrgEmployee makingPeople) {
        //删除入库单明细
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", oid);
        params.put("grn_inner_sn", gid);
        params.put("detail_inner_sn", form.getDetail_inner_sn());

        params.put("organization_type", convertGrnTypeToOrganizationType(billsType));
        if (form.getProduct_type() == 1) {//厂商产品
            mapper.deleteGrnProductDetail(params);
        } else {
            mapper.deleteGrnDealerProductDetail(params);
        }
        //如果存在上游单据则修改上游单据状态
        if (form.getVoucher_type_c() == RefVoucherType.ORDER_PO) {
            orderService.updateTransferDetail(oid, organization_inner_sn,
                    form.getVoucher_inner_sn_c(), form.getVoucher_detail_inner_sn_c(),
                    form.getUnit_inner_sn(), form.getQuantity(), billsType, true, "删除入库单明细调单");
        }
        if (form.getVoucher_type_c() == RefVoucherType.PO_RECORDS) {
            recordService.updatePurchasingRecordGrnQuantity(form.getVoucher_inner_sn_c(), form.getQuantity(), makingPeople.getName(), true);
            recordService.updatePurchasingRecordMDRF(form.getVoucher_inner_sn_c(), form.getMdrf_inner_sn(), form.getMdrf_sn(), form.getHealth_care_sn(), makingPeople.getName());
            inspectionService.updateInspectionRecordDetailFinishQuantity(form.getInspection_detail_id(), form.getQuantity(), true);
        }
    }

    @Override
    @Transactional
    public Grn insertGrn(AddGrnForm form, String sn, OrgEmployee makingPeople) {
        BillsType grn_type = form.getBillsType();
        if (grn_type == BillsType.HH) {
            if (form.getDepartment_inner_sn() == null) {
                throw new DBException(MessageUtils.message("db.add.fail", "入库单", "部门不能为空"));
            }
        }
        GrnMapper mapper = getMapper(grn_type);
        Integer status = form.getStatus();
        Map<String, Object> params = new HashMap<>();
        params.put("organization_inner_sn_i", form.getOrganization_inner_sn());
        params.put("organization_name_i", form.getOrganization_name());
        params.put("warehouse_inner_sn_i", form.getWarehouse_inner_sn());
        params.put("warehouse_name_i", form.getWarehouse_name());
        params.put("handler_inner_sn_i", form.getHandler_inner_sn());
        params.put("handler_name_i", form.getHandler_name());
        params.put("making_people_inner_sn_i", makingPeople.getEmployee_inner_sn());
        params.put("making_people_name_i", makingPeople.getName());
        params.put("auditor_name_i", "");
        if (status == 1) {
            params.put("auditor_inner_sn_i", makingPeople.getEmployee_inner_sn());
            params.put("auditor_name_i", makingPeople.getName());
        }
        params.put("warehouse_operator_inner_sn_i", form.getWarehouse_operator_inner_sn());
        params.put("warehouse_operator_name_i", form.getWarehouse_operator_name());
        params.put("type_inner_sn_i", form.getType_inner_sn());
        params.put("type_name_i", form.getType_name());
        params.put("department_inner_sn_i", form.getDepartment_inner_sn());
        params.put("department_name_i", form.getDepartment_name());
        params.put("status_i", status);
        params.put("in_time_i", null);
        if (StringUtils.isEmpty(form.getGrn_sn())) {
            params.put("grn_sn_i", sn);//.getPutInMaxSn(ServletUtils.getCurUserSystemType(), form.getOrg_id()));
        } else {
            params.put("grn_sn_i", form.getGrn_sn());
        }
        params.put("description_i", form.getDescription());
        params.put("org_id_i", form.getOrg_id());
        params.put("buyer_name_i", form.getBuyer_name());
        params.put("receiving_name_i", form.getReceiving_name());
        params.put("use_department_i", form.getUse_department());
        mapper.insertGrn(params);
        if (Integer.valueOf(params.get("result_o").toString()) == 0) {
            Long gid = Long.valueOf(params.get("grn_inner_sn_o").toString());
            List<GrnDetailForm> details = form.getDetails();
            for (GrnDetailForm grnDetailForm : details) {
                insertGrnDetail(mapper, grnDetailForm, form.getOrg_id(), gid, grn_type);
            }
            // 保存或者修改产品的溢价比例
            percentService.saveOrUpdateProductPremiumPercent(details);
            updateTransferDetailStatus(mapper, form.getOrg_id(), form.getOrganization_inner_sn(), gid, form.getBillsType(), makingPeople.getName(), false);
            GrnIDForm f1 = new GrnIDForm();
            f1.setOrg_id(form.getOrg_id());
            f1.setGrn_inner_sn(gid);
            f1.setBillsType(form.getBillsType());
            return selectGrnInfo(f1);
        } else {
            throw new DBException(MessageUtils.message("db.add.fail", "入库单", params.get("message_string_o")));
        }
    }

    private void checkStatus(Integer old_status) {
        if (old_status.compareTo(1) >= 0) {
            throw new ValidateException("只有正式状态的单据才可以修改!");
        }
    }

    @Override
    @Transactional
    public Grn updateGrn(EditGrnForm form, OrgEmployee makingPeople) {
        GrnMapper mapper = getMapper(form.getBillsType());
        Map<String, Object> oldParams = new HashMap<>();
        oldParams.put("org_id", form.getOrg_id());
        oldParams.put("grn_inner_sn", form.getGrn_inner_sn());
        Grn oldGrn = mapper.selectGrnInfo(oldParams);
        //检查状态
        checkStatus(oldGrn.getStatus());

        Grn newGrn = new Grn();
        int result = 1;
        Integer status = form.getStatus();
        Map<String, Object> params = form.getInfo();
        if (status == 1) {
            params.put("auditor_inner_sn", makingPeople.getEmployee_inner_sn());
            params.put("auditor_name", makingPeople.getName());
        }
        //没有更改Grn_type则继续修改
        if (form.getBillsType() == form.getBillsType_old()
                && form.getOrganization_inner_sn().compareTo(form.getOrganization_inner_sn_old()) == 0) {
            try {
                result = result & mapper.updateGrn(params);
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException) {
                    throw new DuplicateException(MessageUtils.message("db.update.fail", "入库单", "入库单编码重复"));
                } else {
                    throw new DBException(MessageUtils.message("db.update.fail", "入库单", e.getMessage()));
                }
            }
            if (result > 0) {
                Map<String, Object> tmp = new HashMap<>();
                tmp.put("org_id", form.getOrg_id());
                tmp.put("grn_inner_sn", form.getGrn_inner_sn());
                //TODO: 需要修改
                List<GrnDetailForm> details = form.getDetails();
                for (GrnDetailForm grnDetailForm : details) {
                    int action = grnDetailForm.getAction();
                    switch (action) {
                        case 0://0-删除
                            deleteGrnDetail(mapper, grnDetailForm, form.getOrg_id(), form.getOrganization_inner_sn(), form.getGrn_inner_sn(), form.getBillsType(), makingPeople);
                            break;
                        case 1:// 1-新增
                            insertGrnDetail(mapper, grnDetailForm, form.getOrg_id(), form.getGrn_inner_sn(), form.getBillsType());
                            break;
                        case 2:// 2-修改
                            updateGrnDetail(mapper, grnDetailForm, form.getOrg_id(), form.getGrn_inner_sn(), form.getBillsType());
                            break;
                    }
                }
                // 保存或者修改产品的溢价比例
                percentService.saveOrUpdateProductPremiumPercent(details);
                updateTransferDetailStatus(mapper, form.getOrg_id(), form.getOrganization_inner_sn(), form.getGrn_inner_sn(), form.getBillsType(), makingPeople.getName(), false);
                GrnIDForm f1 = new GrnIDForm();
                f1.setOrg_id(form.getOrg_id());
                f1.setGrn_inner_sn(form.getGrn_inner_sn());
                f1.setBillsType(form.getBillsType());
                return selectGrnInfo(f1);
            } else {
                throw new DBException(MessageUtils.message("db.update.fail", "入库单"));
            }
            //更改Grn_type了则新增
        } else {
            //删除form
            GrnIDForm delForm = new GrnIDForm(form.getOrg_id(), form.getGrn_inner_sn(), form.getBillsType_old());
            result = result & deleteGrn(delForm, makingPeople);
            //新增form
            AddGrnForm addForm = new AddGrnForm();
            BeanUtils.copyBeanProp(addForm, form);
            newGrn = insertGrn(addForm, oldGrn.getGrn_sn(), makingPeople);
        }
        return newGrn;
    }

    private void updateTransferDetailStatus(GrnMapper mapper, Long org_id, Long organization_inner_sn,
                                            Long grn_inner_sn, BillsType billsType, String update_by, boolean rollback) {
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", org_id);
        params.put("grn_inner_sn", grn_inner_sn);
        List<GrnDetail> details = mapper.selectGrnProductDetails(params);
        Map<VoucherKey, Quantities> mergerMap = new HashMap<>();// 合并明细中同类产品的数量
        List<Long> inspectionIdList = new ArrayList<>();
        for (GrnDetail detail : details) {
            setMergerData(detail, billsType, mergerMap);
            Long inspectionDetailId = detail.getInspection_detail_id();
            if (inspectionDetailId != null && !inspectionIdList.contains(inspectionDetailId)) {
                inspectionIdList.add(inspectionDetailId);
            }
        }
        for (Quantities value : mergerMap.values()) {
            params.clear();
            params.put("org_id", org_id);
            params.put("voucher_type_c", value.getVoucher_type_c());
            params.put("voucher_inner_sn_c", value.getVoucher_inner_sn());
            params.put("voucher_detail_inner_sn_c", value.getVoucher_details_inner_sn());
            details = mapper.selectGrnProductDetails(params);
            Map<VoucherKey, Quantities> mergerMap1 = new HashMap<>();// 合并其他入库单明细中同类产品的数量
            for (GrnDetail detail : details) {
                setMergerData(detail, billsType, mergerMap1);
            }

            for (Quantities value1 : mergerMap1.values()) {
                if (value1.getVoucher_type_c() == RefVoucherType.ORDER_PO) {
                    orderService.updateTransferDetail(org_id, organization_inner_sn,
                            value1.getVoucher_inner_sn(), value1.getVoucher_details_inner_sn(),
                            value1.getUnitId(), value1.getLockedQuantity(), billsType, rollback, update_by);
                } else {
                    if (value1.getVoucher_type_c() == RefVoucherType.PO_RECORDS) {
                        recordService.updatePurchasingRecordGrnQuantity(value1.getVoucher_inner_sn(), value1.getLockedQuantity(), update_by, rollback);
                        recordService.updatePurchasingRecordMDRF(value1.getVoucher_inner_sn(), value1.getMdrf_inner_sn(), value1.getMdrf_sn(), value1.getHealth_care_sn(), update_by);
                    }
                }
            }
        }
        //TODO: 更新入库数量到对应的验货单
        inspectionIdList.forEach((key) -> {
            BigDecimal value = mapper.sumGrnDetailByInspection(key);
            inspectionService.updateInspectionRecordDetailFinishQuantity(key, value, rollback);
        });
    }

    private void setMergerData(GrnDetail detail, BillsType billsType, Map<VoucherKey, Quantities> mergerMap) {
        if (detail.getVoucher_type_c() == RefVoucherType.ORDER_PO
                || detail.getVoucher_type_c() == RefVoucherType.PO_RECORDS) {
            VoucherKey delKey = new VoucherKey();
            delKey.voucherType = detail.getVoucher_type_c();
            delKey.voucherId = detail.getVoucher_inner_sn_c();
            delKey.voucherDetailId = detail.getVoucher_detail_inner_sn_c();
            if (mergerMap.containsKey(delKey)) {
                Quantities value = mergerMap.get(delKey);
                value.addLockedQuantity(detail.getQuantity());
            } else {
                Quantities value = new Quantities();
                value.setBillsType(billsType);
                value.setVoucher_type_c(detail.getVoucher_type_c());
                value.setVoucher_inner_sn(detail.getVoucher_inner_sn_c());
                value.setVoucher_details_inner_sn(detail.getVoucher_detail_inner_sn_c());
                value.setVendorId(detail.getVendor_inner_sn());
                value.setProductId(detail.getProduct_inner_sn());
                value.setProductName(detail.getProduct_name());
                value.setSpecificationId(detail.getSpecification_inner_sn());
                value.setSpecification(detail.getSpecification());
                value.setUnitId(detail.getUnit_inner_sn());
                value.setUnitName(detail.getUnit_name());
                value.setProductType(detail.getProduct_type());
                value.setMdrf_inner_sn(detail.getMdrf_inner_sn());
                value.setMdrf_sn(detail.getMdrf_sn());
                value.setHealth_care_sn(detail.getHealth_care_sn());
                value.addLockedQuantity(detail.getQuantity());
                mergerMap.put(delKey, value);
            }
        }
    }

    @Override
    @Transactional
    public int deleteGrn(GrnIDForm form, OrgEmployee makingPeople) {
        GrnMapper mapper = getMapper(form.getBillsType());
        int organization_type = convertGrnTypeToOrganizationType(form.getBillsType());
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", form.getOrg_id());
        params.put("grn_inner_sn", form.getGrn_inner_sn());
        Grn info = mapper.selectGrnInfo(params);
        List<Long> inspectionIdList = new ArrayList<>();
        List<GrnDetail> details = mapper.selectGrnProductDetails(params);
        for (GrnDetail grnDetail : details) {
            Map<String, Object> params1 = new HashMap<>();
            params1.put("org_id", grnDetail.getOrg_id());
            params1.put("organization_type", organization_type);
            params1.put("grn_inner_sn", grnDetail.getGrn_inner_sn());
            params1.put("grn_detail_inner_sn", grnDetail.getDetail_inner_sn());
            GrnDetailForm detailForm = new GrnDetailForm();
            BeanUtils.copyBeanProp(detailForm, grnDetail);
            detailForm.setAction(0);
            detailForm.setQuantity_old(detailForm.getQuantity());
            deleteGrnDetail(mapper, detailForm, form.getOrg_id(), info.getOrganization_inner_sn(), form.getGrn_inner_sn(), form.getBillsType(), makingPeople);
            Long inspectionDetailId = grnDetail.getInspection_detail_id();
            if (inspectionDetailId != null && !inspectionIdList.contains(inspectionDetailId)) {
                inspectionIdList.add(inspectionDetailId);
            }
        }
        //TODO: 更新入库数量到对应的验货单
        inspectionIdList.forEach((key) -> {
            BigDecimal value = mapper.sumGrnDetailByInspection(key);
            inspectionService.updateInspectionRecordDetailFinishQuantity(key, value, false);
        });
        return getMapper(form.getBillsType()).deleteGrn(params);
    }

    @Override
    public List<Grn> searchGrnList(SearchGrnForm form, List<Long> warehouseIds) {
        GrnMapper mapper = null;
        SystemType systemType = ServletUtils.getCurUserSystemType();
        switch (systemType) {
            case HOSPITAL:
                mapper = getMapper(BillsType.HH);
                break;
            case DEALER:
                mapper = getMapper(BillsType.DD);
                break;
            case VENDOR:
                mapper = getMapper(BillsType.VD);
                break;
            default:
        }
        Map<String, Object> params = form.getInfo();

        params.put("warehouseIds", warehouseIds);

        return mapper.searchGrnList(params);
    }

    @Override
    public List<Grn> searchGrnListGlobal(SearchGrnForm form) {
        GrnMapper mapper = null;
        SystemType systemType = ServletUtils.getCurUserSystemType();
        switch (systemType) {
            case HOSPITAL:
                mapper = getMapper(BillsType.HH);
                break;
            case DEALER:
                mapper = getMapper(BillsType.DD);
                break;
            case VENDOR:
                mapper = getMapper(BillsType.VD);
                break;
            default:
        }
        Map<String, Object> params = form.getInfo();

        //取消仓库限制
        params.put("warehouseIds", new ArrayList<>());
        return mapper.searchGrnList(params);
    }

    @Override
    public List<Grn> searchGrnList4Phone(AppGrnSearchForm form) {
        GrnMapper mapper = null;
        SystemType systemType = ServletUtils.getCurUserSystemType();
        switch (systemType) {
            case HOSPITAL:
                mapper = getMapper(BillsType.HH);
                break;
            case DEALER:
                mapper = getMapper(BillsType.DD);
                break;
            case VENDOR:
                mapper = getMapper(BillsType.VD);
                break;
            default:
        }
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", form.getOrg_id());
        params.put("key", form.getKey());
        if (form.getStatus().intValue() == 3) {
            params.put("status", "0,1,2");
        } else if (form.getStatus().intValue() == 1) {// 待入库包括正式和已审核两种状态
            params.put("status", "0,1");
        } else {
            params.put("status", form.getStatus());
        }

        //加入仓库限制
        List<Long> warehouseIds = getCurUserWarehouseIds();
        if (warehouseIds.size() == 0) return new ArrayList<>();
        params.put("warehouseIds", getCurUserWarehouseIds());

        return mapper.searchGrnList(params);
    }

    @Override
    public Grn selectGrnInfo(GrnIDForm form) {
        GrnMapper mapper = getMapper(form.getBillsType());
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", form.getOrg_id());
        params.put("grn_inner_sn", form.getGrn_inner_sn());
        Grn grn = mapper.selectGrnInfo(params);
        if (grn != null) {
            List<Integer> refList = mapper.selectGrnRefVoucherType(params);
            for (Integer ref : refList) {
                if (RefVoucherType.NONE.value() != ref.intValue()) {
                    grn.setVoucher_type_c(RefVoucherType.intToEnum(ref.intValue()));
                    break;
                }
            }
            List<GrnDetail> details = mapper.selectGrnProductDetails(params);
            ruleService.setPercentAndSalesPrice2(details);
            grn.setDetails(details);
        }
        return grn;
    }

    @Transactional
    void putin(Long orgId, Long grnId, BillsType billsType, ContextUserInfo curUser, Boolean isRed, Boolean isReturn) {
        boolean isLease = false;
        String setupStr = paramService.getGSPSetup(curUser.getSystemType(), curUser.getOrgId());
        GspSetup gsp = JSONObject.parseObject(setupStr, GspSetup.class);
        boolean isControl = gsp.getProcurementProcess().isStrictControl();
        AddNoticeForm notice = null;
        List<NoticeDetailForm> noticeDetails = new ArrayList<>();
        InspectionRecordForm inspection = null;
        List<InspectionRecordDetailForm> inspectionDetails = new ArrayList<>();
        if (centralService.isLease(orgId) != null && curUser != null) {
            isLease = true;
        }
        String lockGrnId = String.format("%s-%s-%s", orgId, grnId, billsType.value());
        if (VoucherLockService.isExist(lockGrnId)) {
            throw new ValidateException("系统繁忙，请稍后再试!");
        }

        try {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("org_id", orgId);
            tmp.put("grn_inner_sn", grnId);
            Grn grn = getMapper(billsType).selectGrnInfo(tmp);
            if (grn == null) {
                throw new DBException("入库单号错误，请检查后重试!");
            }

            if (!isRed && !(grn.getStatus().intValue() == 1)) {
                VoucherLockService.remove(lockGrnId);
                throw new DBException("只有已审核状态的入库单才能入库!");
            } else {
                tmp.put("status", 2);
                tmp.put("in_time", LocalDateTime.now());
                getMapper(billsType).updateGrn(tmp);
            }
            if (isLease) {
                notice = new AddNoticeForm();
                notice.setDealer_inner_sn(orgId);
                notice.setDealer_name(grn.getOrg_name());
                notice.setCompany_name(grn.getOrganization_name());
                notice.setBills_type(grn.getBillsType());
                notice.setWarehouse_name(grn.getWarehouse_name());
                notice.setRecord_inner_sn(grn.getGrn_inner_sn());
                notice.setSn(grn.getGrn_sn());
            }
            if (!isControl && !isRed && !isReturn) {
                inspection = new InspectionRecordForm();
                inspection.setBills_type(grn.getBillsType());
                inspection.setOrganization_inner_sn(grn.getOrganization_inner_sn());
                inspection.setOrganization_name(grn.getOrganization_name());
                inspection.setWarehouse_inner_sn(grn.getWarehouse_inner_sn());
                inspection.setWarehouse_name(grn.getWarehouse_name());
                inspection.setDescription(grn.getDescription());
                inspection.setInspection_status(InspectionStatus.INSPECTED);//正式状态的验货单
            }
            List<GrnDetail> details = getMapper(billsType).selectGrnProductDetails(tmp);
            for (GrnDetail detail : details) {
                if (isLease) {
                    NoticeDetailForm noticeDetail = new NoticeDetailForm();
                    noticeDetail.setProduct_vendor_inner_sn(detail.getVendor_inner_sn());
                    noticeDetail.setProduct_vendor_name(detail.getVendor_name());
                    noticeDetail.setProduct_inner_sn(detail.getProduct_inner_sn());
                    noticeDetail.setProduct_name(detail.getProduct_name());
                    noticeDetail.setSpecification(detail.getSpecification());
                    noticeDetail.setSpecification_inner_sn(detail.getSpecification_inner_sn());
                    noticeDetail.setUnit_inner_sn(detail.getUnit_inner_sn());
                    noticeDetail.setUnit(detail.getUnit_name());
                    noticeDetail.setMdrf_inner_sn(detail.getMdrf_inner_sn());
                    noticeDetail.setMdrf_sn(detail.getMdrf_sn());
                    noticeDetail.setBatch_number(detail.getBatch_number());
                    noticeDetail.setProduce_date(detail.getProduce_date());
                    noticeDetail.setOverdue_date(detail.getOverdue_date());
                    noticeDetail.setQuantity(detail.getQuantity());
                    noticeDetail.setRate(detail.getRate());
                    noticeDetail.setUnit_price(detail.getUnit_price());
                    noticeDetail.setProduct_type(detail.getProduct_type());
                    noticeDetails.add(noticeDetail);
                }
                if (!isControl) {
                    if (RefVoucherType.PO_RECORDS.equals(detail.getVoucher_type_c())) {
                        if (!inspectionService.isExistInspectionRecord(detail.getVoucher_inner_sn_c(), RefVoucherType.PO_RECORDS, null)) {//没有生成过验货单
                            InspectionRecordDetailForm inspectionDetail = new InspectionRecordDetailForm();
                            inspectionDetail.setProduct_vendor_inner_sn(detail.getVendor_inner_sn());
                            inspectionDetail.setProduct_vendor_name(detail.getVendor_name());
                            inspectionDetail.setProduct_inner_sn(detail.getProduct_inner_sn());
                            inspectionDetail.setProduct_name(detail.getProduct_name());
                            inspectionDetail.setSpecification(detail.getSpecification());
                            inspectionDetail.setSpecification_inner_sn(detail.getSpecification_inner_sn());
                            inspectionDetail.setUnit_inner_sn(detail.getUnit_inner_sn());
                            inspectionDetail.setUnit(detail.getUnit_name());
                            inspectionDetail.setMdrf_inner_sn(detail.getMdrf_inner_sn());
                            inspectionDetail.setMdrf_sn(detail.getMdrf_sn());
                            inspectionDetail.setBatch_number(detail.getBatch_number());
                            inspectionDetail.setProduce_date(detail.getProduce_date());
                            inspectionDetail.setOverdue_date(detail.getOverdue_date());
                            inspectionDetail.setArrival_quantity(detail.getQuantity());//收货数量=入库数量
                            inspectionDetail.setQualified_quantity(detail.getQuantity());//合格数量=入库数量
                            inspectionDetail.setUnqualified_quantity(BigDecimal.ZERO);//没有不合格产品
                            inspectionDetail.setProduct_type(detail.getProduct_type());
                            inspectionDetail.setVoucher_inner_sn_c(detail.getVoucher_inner_sn_c());
                            inspectionDetail.setVoucher_type_c(detail.getVoucher_type_c());
                            inspectionDetails.add(inspectionDetail);
                        }
                    }
                }
                BigDecimal measure = commService.getProductUnitMeasure(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
                        detail.getSpecification_inner_sn(), detail.getUnit_inner_sn(), detail.getProduct_type().intValue());
                BigDecimal minUnitQuantity = detail.getQuantity().multiply(measure);
                BigDecimal minUnitPrice = detail.getUnit_price().divide(measure, 4, RoundingMode.HALF_UP);
                String minUnitName = commService.getProductMinUnitName(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
                        detail.getSpecification_inner_sn(), detail.getUnit_inner_sn(), detail.getProduct_type().intValue());
                Long minUnitId = commService.getProductMinUnitId(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
                        detail.getSpecification_inner_sn(), detail.getUnit_inner_sn(), detail.getProduct_type().intValue());
                InventoryKey key = InventoryKey.of(orgId, grn.getWarehouse_inner_sn(), detail.getVendor_inner_sn(),
                        detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
                        detail.getBatch_number(), billsType);
                InventoryGrnParam param = InventoryGrnParam.of(key, detail.getVendor_name(), detail.getProduct_name(),
                        detail.getSpecification(), minUnitName, minUnitId, detail.getProduce_date(),
                        detail.getOverdue_date(), minUnitQuantity, minUnitPrice);
                inventoryService.insertOrUpdateInventoryProduct(param, ServletUtils.getCurUserSystemType(), isRed, isReturn);
            }
            //判断是否为租户，如果是租户则发入库通知
            if (isLease) {
                notice.setDetails(noticeDetails);
                centralService.insertGrnNotice(notice, curUser);
            }
            if (!isControl && inspection != null && Utils.isNotEmpty(inspectionDetails)) {
                inspection.setDetails(inspectionDetails);
                inspectionService.insertInspectionRecord(inspection, curUser, true, gsp.getProcurementProcess().getEmployment(inspection.getWarehouse_inner_sn())[1]);
            }
            VoucherLockService.remove(lockGrnId);
        } catch (Exception e) {
            log.error("入库异常", e);
            throw e;
        } finally {
            VoucherLockService.remove(lockGrnId);
        }
    }

    @Autowired
    HospitalAllotVoucherMapper haMapper;
    @Autowired
    DealerAllotVoucherMapper daMapper;
    @Autowired
    AllotVoucherService avService;
    @Autowired
    ProductUnitMapper puMapper;
    @Autowired
    DealerProductUnitMapper dpuMapper;

    private AllotVoucherMapper getAllotVoucherMapper() {
        SystemType systemType = ServletUtils.getCurUserSystemType();
        switch (systemType) {
            case DEALER:
                return daMapper;
            default:
                return haMapper;
        }
    }

    //通过出库单id，算出每个明细的产品总数量(最小包装单位转化)
    private Map<Long, BigDecimal> detailMinUnitQuantityByGrnInnerSn(Long org_id, Long grn_inner_sn) {
        HashMap<Long, BigDecimal> res = new HashMap<>();

        List<AllotVoucherDetail> details = getAllotVoucherMapper().findGrnDetails(org_id, grn_inner_sn);

        for (AllotVoucherDetail detail : details) {
            Map<String, Object> map = new HashMap<>();
            map.put("vendor_inner_sn_i", detail.getVd_inner_sn());
            map.put("product_inner_sn_i", detail.getProduct_inner_sn());
            map.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
            map.put("unit_inner_sn_i", detail.getUnit_inner_sn());

            BigDecimal bs;
            if (1 == detail.getProduct_type()) {
                bs = puMapper.calculate_product_min_unit(map);

            } else {
                bs = dpuMapper.calculate_product_min_unit(map);
            }
            res.put(detail.getDetail_inner_sn(), bs.multiply(detail.getInOfStock_quantity()));
        }
        return res;
    }

    //通过调拨单id，算出每个明细的产品总数量(最小包装单位转化)
    private Map<Long, BigDecimal> detailMinUnitQuantityByAvInnerSn(Long org_id, Long av_inner_sn) {
        Map<Long, BigDecimal> res = new HashMap<>();

        //查到调拨单
        SearchAllotVoucherForm avForm = new SearchAllotVoucherForm();
        avForm.setOrg_id(org_id);
        avForm.setTab_id(0);
        avForm.setAv_inner_sn(String.valueOf(av_inner_sn));
        List<AllotVoucherDetail> details = avService.searchAllotVoucherList(avForm, false);
        for (AllotVoucherDetail detail : details) {
            Map<String, Object> map = new HashMap<>();
            map.put("vendor_inner_sn_i", detail.getVd_inner_sn());
            map.put("product_inner_sn_i", detail.getProduct_inner_sn());
            map.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
            map.put("unit_inner_sn_i", detail.getUnit_inner_sn());

            BigDecimal bs;
            if (1 == detail.getProduct_type()) {
                bs = puMapper.calculate_product_min_unit(map);

            } else {
                bs = dpuMapper.calculate_product_min_unit(map);
            }
            res.put(detail.getDetail_inner_sn(), bs.multiply(detail.getAllot_quantity()));
        }
        return res;
    }

    @Override
    public Map<String, Object> putin(GrnIds form, ContextUserInfo curUser) {
        return putin(form, curUser, false);
    }

    @Override
    @Transactional
    public Map<String, Object> putin(GrnIds form, ContextUserInfo curUser, Boolean isReturn) {
        if (form.getGrn_inner_sn() == null) {
            throw new DBException("没有选中的入库单需要入库!");
        }
//		List<GrnIds> list = form.getIds();
        StringBuffer msg = new StringBuffer("");
//		GrnMapper mapper = getMapperBySystemType();
        BillsType billsType = form.getBillsType();

        //查这条由调拨单生成的入库单，若有记录则执行下面流程
        AllotVoucherMapper avMapper = getAllotVoucherMapper();
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", form.getOrg_id());
        params.put("grn_inner_sn", form.getGrn_inner_sn());
        Long av_inner_sn = avMapper.findGrnIds(params);

        if (Objects.isNull(av_inner_sn) && Objects.nonNull(form.getVoucher_inner_sn()))
            av_inner_sn = form.getVoucher_inner_sn();

        if (Objects.nonNull(av_inner_sn)) {
            //查出这条入库单的相关入库单，且入库单status=2(同一调拨单关联的入库单)
            List<AllotVoucherDetail> grnList = avMapper.findGrn(params);    //查入库单

            List<Map<Long, BigDecimal>> xs = new ArrayList<>(grnList.size());

            //将上面已入库的入库单，和当前传入的入库单，按调拨明细合并产品(需要同时将调拨单明细数量和入库单明细数量转为最小包装单位数量)
            for (AllotVoucherDetail grn : grnList) {
                Map<Long, BigDecimal> putInMap = detailMinUnitQuantityByGrnInnerSn(form.getOrg_id(), grn.getAv_inner_sn());//明细，数量
                xs.add(putInMap);
            }
            Map<Long, BigDecimal> curMap = detailMinUnitQuantityByGrnInnerSn(form.getOrg_id(), form.getGrn_inner_sn());//明细，数量

            //计算调拨单的Map<Long, BigDecimal>
            Map<Long, BigDecimal> allotMap = detailMinUnitQuantityByAvInnerSn(form.getOrg_id(), av_inner_sn);

            //遍历调拨Map
            for (Long detail_inner_sn : allotMap.keySet()) {
                boolean shouldPutIn = true;

                BigDecimal allotQuantity = allotMap.get(detail_inner_sn);

                BigDecimal curQuantity = curMap.get(detail_inner_sn);
                if (Objects.isNull(curQuantity)) curQuantity = BigDecimal.ZERO;

                BigDecimal inQuantity = xs.stream().map(x -> {
                    BigDecimal decimal = x.get(detail_inner_sn);
                    if (Objects.isNull(decimal)) return BigDecimal.ZERO;
                    return decimal;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                //当前入库数量和已入库数量之和，如果一旦小于调拨数量，则置为false
                if (curQuantity.add(inQuantity).compareTo(allotQuantity) < 0) shouldPutIn = false;

                if (shouldPutIn) {
                    getAllotVoucherMapper().updateStatusAtPutIn(detail_inner_sn);
                }
            }
        }

        putin(form.getOrg_id(), form.getGrn_inner_sn(), form.getBillsType(), curUser, false, isReturn);

        Map<String, Object> result = new HashMap<>();
        msg.append(sync(form.getOrg_id(), form.getGrn_inner_sn(), billsType));//同步到需要的HIS系统

        result.put("msg", msg.toString());
        return result;
    }

    @Override
    public Map<String, Object> purchaseReturnPutIn(GrnIds form, ContextUserInfo curUser) {
        if (form.getGrn_inner_sn() == null) {
            throw new DBException("没有选中的入库单需要入库!");
        }
        BillsType billsType = form.getBillsType();
        Grn grn = getGrn(form);

        for (GrnDetail detail : grn.getDetails()) {
            BigDecimal measure = commService.getProductUnitMeasure(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
                    detail.getSpecification_inner_sn(), detail.getUnit_inner_sn(), detail.getProduct_type().intValue());
            BigDecimal minUnitQuantity = detail.getQuantity().multiply(measure);
            BigDecimal minUnitPrice = detail.getUnit_price().divide(measure, 4, RoundingMode.HALF_UP);
            String minUnitName = commService.getProductMinUnitName(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
                    detail.getSpecification_inner_sn(), detail.getUnit_inner_sn(), detail.getProduct_type().intValue());
            Long minUnitId = commService.getProductMinUnitId(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
                    detail.getSpecification_inner_sn(), detail.getUnit_inner_sn(), detail.getProduct_type().intValue());
            InventoryKey key = InventoryKey.of(form.getOrg_id(), grn.getWarehouse_inner_sn(), detail.getVendor_inner_sn(),
                    detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
                    detail.getBatch_number(), billsType);
            InventoryGrnParam param = InventoryGrnParam.of(key, detail.getVendor_name(), detail.getProduct_name(),
                    detail.getSpecification(), minUnitName, minUnitId, detail.getProduce_date(),
                    detail.getOverdue_date(), minUnitQuantity, minUnitPrice);
            inventoryService.subInventoryProduct(param, ServletUtils.getCurUserSystemType());
        }

        StringBuffer msg = new StringBuffer();
        Map<String, Object> result = new HashMap<>();
        msg.append(sync(form.getOrg_id(), form.getGrn_inner_sn(), billsType));  //同步到需要的HIS系统
        result.put("msg", msg.toString());
        return result;
    }

    private Grn getGrn(GrnIds form) {
        BillsType billsType = form.getBillsType();
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("org_id", form.getOrg_id());
        tmp.put("grn_inner_sn", form.getGrn_inner_sn());
        Grn grn = getMapper(billsType).selectGrnInfo(tmp);
        if (grn == null) {
            throw new DBException("入库单号错误，请检查后重试!");
        }

        if (grn.getStatus().intValue() != 1) {
            throw new DBException("只有已审核状态的入库单才能入库!");
        } else {
            tmp.put("status", 2);
            tmp.put("in_time", LocalDateTime.now());
            getMapper(billsType).updateGrn(tmp);
        }
        List<GrnDetail> details = getMapper(billsType).selectGrnProductDetails(tmp);
        grn.setDetails(details);
        return grn;
    }

    @Override
    @Transactional
    public Grn grnRed(@Valid GrnIDForm form, ContextUserInfo curUser, Boolean isReturn) {
        OrgEmployee makingPeople = curUser.getEmployee();
        GrnMapper mapper = getMapper(form.getBillsType());
        //查出需要红冲的原始单据
        Grn old_info = selectGrnInfo(form);
        if (2 != old_info.getStatus().intValue()) {
            throw new DBException("只允许已入库状态的单据进行红冲!");
        }
        List<GrnDetail> details = old_info.getDetails();
        //复制原始单据内容为全负数的红冲单据
        Map<String, Object> params = new HashMap<>();
        params.put("organization_inner_sn_i", old_info.getOrganization_inner_sn());
        params.put("organization_name_i", old_info.getOrganization_name());
        params.put("warehouse_inner_sn_i", old_info.getWarehouse_inner_sn());
        params.put("warehouse_name_i", old_info.getWarehouse_name());
        params.put("handler_inner_sn_i", old_info.getHandler_inner_sn());
        params.put("handler_name_i", old_info.getHandler_name());
        params.put("making_people_inner_sn_i", makingPeople.getEmployee_inner_sn());
        params.put("making_people_name_i", makingPeople.getName());
        params.put("auditor_inner_sn_i", makingPeople.getEmployee_inner_sn());
        params.put("auditor_name_i", makingPeople.getName());
        params.put("warehouse_operator_inner_sn_i", old_info.getWarehouse_operator_inner_sn());
        params.put("warehouse_operator_name_i", old_info.getWarehouse_operator_name());
        params.put("type_inner_sn_i", old_info.getType_inner_sn());
        params.put("type_name_i", old_info.getType_name());
        params.put("department_inner_sn_i", old_info.getDepartment_inner_sn());
        params.put("department_name_i", old_info.getDepartment_name());
        params.put("status_i", 5);
        params.put("in_time_i", LocalDateTime.now());
        params.put("grn_sn_i", String.format("%s-冲", old_info.getGrn_sn()));
        params.put("description_i", String.format("红冲%s", old_info.getGrn_sn()));
        params.put("org_id_i", form.getOrg_id());
        params.put("buyer_name_i", old_info.getBuyer_name());
        params.put("use_department_i", old_info.getUse_department());
        mapper.insertGrn(params);
        if (Integer.valueOf(params.get("result_o").toString()) == 0) {
            Long gid = Long.valueOf(params.get("grn_inner_sn_o").toString());
            for (GrnDetail detail : details) {
                Map<String, Object> params1 = new HashMap<>();
                params1.put("grn_inner_sn_i", gid);
                params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
                params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
                params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
                params1.put("unit_price_i", detail.getUnit_price());
                params1.put("quantity_i", detail.getQuantity().multiply(new BigDecimal(-1)));
                params1.put("produce_date_i", detail.getProduce_date());
                params1.put("overdue_date_i", detail.getOverdue_date());
                params1.put("type_i", detail.getType());
                params1.put("sort_number_i", detail.getSort_number());
                params1.put("batch_number_i", detail.getBatch_number());
                params1.put("product_name_i", detail.getProduct_name());
                params1.put("specification_i", detail.getSpecification());
                params1.put("unit_i", detail.getUnit_name());
                params1.put("voucher_type_c_i", detail.getVoucher_type_c());
                params1.put("voucher_inner_sn_c_i", detail.getVoucher_inner_sn_c());
                params1.put("voucher_detail_inner_sn_c_i", detail.getVoucher_detail_inner_sn_c());
                params1.put("inspection_detail_id_i", detail.getInspection_detail_id());
                params1.put("health_care_sn_i", detail.getHealth_care_sn());
                params1.put("org_id_i", form.getOrg_id());
                params1.put("rate_i", detail.getRate());
                if (detail.getProduct_type() == 1) {//厂商产品
                    params1.put("product_vendor_inner_sn_i", detail.getVendor_inner_sn());
                    params1.put("product_vendor_name_i", detail.getVendor_name());
                    params1.put("mdrf_inner_sn_i", detail.getMdrf_inner_sn());
                    params1.put("mdrf_sn_i", detail.getMdrf_sn());
                    mapper.insertGrnProductDetail(params1);
                } else {
                    params1.put("product_dealer_inner_sn_i", detail.getVendor_inner_sn());
                    params1.put("product_dealer_name_i", detail.getVendor_name());
                    mapper.insertGrnDealerProductDetail(params1);
                }
                if (Integer.valueOf(params1.get("result_o").toString()) != 0) {
                    throw new DBException(MessageUtils.message("db.add.fail", "入库单明细", params.get("message_string_o")));
                }
            }
            //红冲单据入库
            putin(form.getOrg_id(), gid, form.getBillsType(), curUser, true, isReturn);

            //修改关联的上游单据状态
            updateTransferDetailStatus(mapper, form.getOrg_id(), old_info.getOrganization_inner_sn(), gid, form.getBillsType(), makingPeople.getName(), false);

            //红冲单据修改状态为5-红冲
            Map<String, Object> params1 = new HashMap<>();
            Map<String, Object> params2 = new HashMap<>();
            params2.put("org_id", old_info.getOrg_id());
            params2.put("grn_inner_sn", gid);
            params2.put("status", 5);
            mapper.updateGrn(params2);
            //原始单据修改状态为4-作废
            params1.put("org_id", old_info.getOrg_id());
            params1.put("grn_inner_sn", old_info.getGrn_inner_sn());
            params1.put("status", 4);
            mapper.updateGrn(params1);

            GrnIDForm f1 = new GrnIDForm();
            f1.setOrg_id(form.getOrg_id());
            f1.setGrn_inner_sn(gid);
            f1.setBillsType(form.getBillsType());
            return selectGrnInfo(f1);
        } else {
            throw new DBException(MessageUtils.message("db.add.fail", "入库单", params.get("message_string_o")));
        }
    }

    @Override
    @Transactional
    public void unReview(@Valid GrnIDForm form) {
        GrnMapper mapper = getMapper(form.getBillsType());
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", form.getOrg_id());
        params.put("grn_inner_sn", form.getGrn_inner_sn());
        params.put("auditor_inner_sn", null);
        params.put("auditor_name", "");
        Grn old_info = mapper.selectGrnInfo(params);
        if (old_info == null || 1 != old_info.getStatus().intValue()) {
            throw new DBException("只允许已审核状态的单据进行反审核!");
        }
        params.put("status", 0);
        mapper.updateGrn(params);
    }

    private String sync(Long org_id, Long grn_inner_sn, BillsType billsType) {
        GrnMapper mapper = getMapper(billsType);
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", org_id);
        params.put("grn_inner_sn", grn_inner_sn);
        Grn4External grn = mapper.selectGrnInfo4External(params);
        if (grn != null) {
            List<GrnDetail4External> details = mapper.selectGrnProductDetails4External(params);
            grn.setDetails(details);
        }
        return syncService.sendPutin(org_id, ServletUtils.getCurUserSystemType(), grn);
    }

    @Override
    public List<Map<String, Object>> getWarehouseEmployeeList(Long oid, Long wid) {
        GrnMapper mapper = null;
        SystemType systemType = ServletUtils.getCurUserSystemType();
        switch (systemType) {
            case HOSPITAL:
                mapper = getMapper(BillsType.HH);
                break;
            case DEALER:
                mapper = getMapper(BillsType.DD);
                break;
            case VENDOR:
                mapper = getMapper(BillsType.VD);
                break;
            default:
        }
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", oid);
        params.put("warehouse_model_inner_sn", wid);
        WareHouseMapper whMapper = getWarehouseMapper();
        String wIds = whMapper.selectAllChildrenWarehouseModeIDS(params);
        params.put("warehouse_model_inner_sn", wIds);
        return mapper.selectWarehouseEmployeeList(params);
    }

    @Override
    public List<TransferVoucher> getTransferVoucherList(SystemType systemType, long orgId, String sn, String organizationName, String warehouse_name,
                                                        RefVoucherType vType, String key) {
        if (!vType.equals(RefVoucherType.ODO) && !vType.equals(RefVoucherType.ORDER_PO)) {
            throw new DBException("该功能不支持'" + vType.text() + "'调单类型!");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", orgId);
        params.put("voucher_sn", StringUtils.isNotEmpty(sn) ? sn : null);
        params.put("organization_name", StringUtils.isNotEmpty(organizationName) ? organizationName : null);
        params.put("warehouse_name", StringUtils.isNotEmpty(warehouse_name) ? warehouse_name : null);
        params.put("product_name", StringUtils.isNotEmpty(key) ? key : null);
        //贺小波要求调单类型为出库单时,只支持销售出库单,不支持赠送单 2021-05-07
        switch (systemType) {
            case HOSPITAL:
                if (vType.equals(RefVoucherType.ODO)) {
                    return getMapper(BillsType.HD).selectTransferOdoList(params);
                } else {
                    return getMapper(BillsType.HD).selectTransferPoList(params);
                }
            case DEALER:
                if (vType.equals(RefVoucherType.ODO)) {
                    return getMapper(BillsType.DD).selectTransferOdoList(params);
                } else {
                    return getMapper(BillsType.DD).selectTransferPoList(params);
                }
            default:
                break;
        }
        throw new DBException("当前机构不支持该功能!");
    }

    @Override
    public List<TransferVoucherDetail> getTransferVoucherDetailList(long orgId, Long odoId, Long organization_inner_sn, BillsType grn_type, RefVoucherType vType) {
        if (grn_type.equals(BillsType.DH) || grn_type.equals(BillsType.HH)
                || grn_type.equals(BillsType.VD) || grn_type.equals(BillsType.VH)
                || grn_type.equals(BillsType.VV)) {
            throw new DBException("当前机构不支持该功能!");
        }
        if (!vType.equals(RefVoucherType.ODO) && !vType.equals(RefVoucherType.ORDER_PO)) {
            throw new DBException("该功能不支持'" + vType.text() + "'调单类型!");
        }
        GrnMapper mapper = getMapper(grn_type);
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", orgId);
        params.put("voucher_inner_sn", odoId);
        params.put("organization_inner_sn", organization_inner_sn);
        if (vType.equals(RefVoucherType.ODO)) {
            List<TransferVoucherDetail> details = mapper.selectTransferOdoDetailList(params);
            details = premiumRuleService.setPercentAndSalesPrice1(details);
            return details;
        } else {
            List<TransferVoucherDetail> details = mapper.selectTransferPoDetailList(params);
            details = premiumRuleService.setPercentAndSalesPrice1(details);
            return details;
        }
    }



    @Override
    public BigDecimal getSumGrnDetailRefPurchaseOrder(Long org_id, Long organization_inner_sn, Long orderId,
                                                      Long orderDetailId, BillsType type) {
        if (BillsType.DD.equals(type) || BillsType.DV.equals(type)) {
            GrnMapper mapper = getMapper(type);
            Map<String, Object> params = new HashMap<>();
            params.put("org_id", org_id);
            params.put("organization_inner_sn", organization_inner_sn);
            params.put("voucher_inner_sn_c", orderId);
            params.put("voucher_detail_inner_sn_c", orderDetailId);
            BigDecimal sum = mapper.sumGrnDetailRefPurchaseOrder(params);
            if (sum == null) {
                sum = BigDecimal.ZERO;
            }
            return sum;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<Map<String, Object>> getGrnProductList4Return(SearchReturnProductForm form, List<Long> warehouseIds) {
        GrnMapper mapper = getMapper(form.getBillsType());
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", form.getOrg_id());
        params.put("organization_inner_sn", form.getOrganization_inner_sn());
        params.put("product_vendor_name", form.getProduct_vendor_name());
        params.put("product_name", form.getName());
        params.put("specification", form.getSpecification());
        params.put("sn", form.getSn());
        params.put("begin_date", form.getBegin_date());
        params.put("end_date", form.getEnd_date());
        params.put("warehouseIds", warehouseIds);
        return mapper.selectGrnProductList4Return(params);
    }

    @Override
    public List<Map<String, Object>> getGrnProductList4Invoice(SearchInvoiceProductForm form, List<Long> warehouseIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", form.getOrg_id());
        params.put("organization_inner_sn", form.getOrganization_inner_sn());
        params.put("product_vendor_name", form.getProduct_vendor_name());
        params.put("product_name", form.getName());
        params.put("specification", form.getSpecification());
        params.put("sn", form.getSn());
        params.put("begin_date", form.getBegin_date());
        params.put("end_date", form.getEnd_date());
        params.put("warehouseIds", warehouseIds);
        List<Map<String, Object>> grnProductList;
        if (form.getBillsType() != null) {
            GrnMapper mapper = getMapper(form.getBillsType());
            grnProductList = mapper.selectGrnProductList4Invoice(params);
        } else {
            grnProductList = ddMapper.selectGrnProductList4InvoiceNew(params);
        }
        return grnProductList;
    }

    @Override
    public boolean isExistGrn(Long org_id, Long organization_inner_sn, Long voucher_inner_sn_c,
                              RefVoucherType voucher_type_c, BillsType billsType, Long rollbackDetailId) {
        GrnMapper mapper = getMapper(billsType);
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", org_id);
        params.put("organization_inner_sn", organization_inner_sn);
        params.put("voucher_inner_sn_c", voucher_inner_sn_c);
        params.put("voucher_type_c", voucher_type_c.value());
        params.put("inspection_detail_id", rollbackDetailId);
        return mapper.isExistGrnRecord(params);
    }

    @Override
    public List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(SearchPurchasingRecordProductForm form,
                                                                              ContextUserInfo curUser, VoucherType voucherType, List<Long> warehouseIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", curUser.getOrgId());
        params.put("system_type", curUser.getSystemType());
        params.put("bills_type", form.getBills_type());
        params.put("organization_inner_sn", form.getOrganization_inner_sn());
        params.put("voucherType", voucherType.value());

        params.put("product_vendor_name", form.getProduct_vendor_name());

        params.put("product_name", form.getProduct_name());
        params.put("specification", form.getSpecification());
        params.put("sn", form.getSn());
        params.put("begin_date", form.getBegin_date());
        params.put("end_date", form.getEnd_date());
        params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
        params.put("warehouseIds", warehouseIds);

        return getUsablePurchasingRecordProductList(params, curUser, voucherType, form.getBills_type());
    }

    public List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(Map<String, Object> params,
                                                                              ContextUserInfo curUser, VoucherType voucherType, BillsType billsType) {
        String setupStr = paramService.getGSPSetup(curUser.getSystemType(), curUser.getOrgId());
        GspSetup gsp = JSONObject.parseObject(setupStr, GspSetup.class);
        boolean isControl = gsp.getProcurementProcess().isStrictControl();
        startPage();
        List<PurchasingRecordProduct> productList = new ArrayList<>();
        if (isControl) {
            if (billsType == null) {
                productList = recordService.getControlUsablePurchasingRecordList(params);
            } else {
                GrnMapper mapper = getMapper(billsType);
                productList = mapper.selectControlUsablePurchasingRecordList(params);
            }

//			for (PurchasingRecordProduct product : productList) {
//				CheckPurchasingRecord record = inspectionService.getExistInspectionRecord(product.getId(), product.getBatch_number());
//				if(record.isExist()) {
//					product.setOrder_quantity(record.getQualified_quantity());
//					product.setWarehouse_inner_sn(record.getWarehouse_inner_sn());
//					product.setWarehouse_name(record.getWarehouse_name());
//					product.setMdrf_inner_sn(record.getMdrf_inner_sn());
//					product.setMdrf_sn(record.getMdrf_sn());
//					product.setProduce_date(record.getProduce_date());
//					product.setOverdue_date(record.getOverdue_date());
//					product.setInspection_detail_id(record.getInspection_detail_id());
//				}
//			}

        } else {
            productList = recordService.getUsablePurchasingRecordProductList(params, curUser, voucherType);
        }
        return productList;
    }

    @Autowired
    UDIService udiService;

    @Override
    public UsableProductInfo scanProductByBarcode(@Validated ScanBarcodeForm form, ContextUserInfo curUser, List<Long> warehouseIds) {
        UsableProductInfo result = new UsableProductInfo();
        try {
            //解析条码
            UDIDInfo udiInfo = udiService.parseBarcode(form.getBarcode(), true);
            if (udiInfo.getUdi_barcode() == null) {//不包含产品信息
                result.setOnlyOtherInfo(true);
                result.setExistUsabelProduct(false);
            } else {//包含产品信息,则查询是否存在可用产品信息
                Map<String, Object> params = new HashMap<>();
                params.put("org_id", curUser.getOrgId());
                params.put("system_type", curUser.getSystemType());
                params.put("bills_type", form.getBills_type());
                params.put("organization_inner_sn", form.getOrganization_inner_sn());
                params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
                params.put("voucherType", VoucherType.PUT_IN.value());

                params.put("product_vendor_inner_sn", udiInfo.getProduct_vendor_inner_sn());
                params.put("product_inner_sn", udiInfo.getProduct_inner_sn());
                params.put("specification_inner_sn", udiInfo.getSpecification_inner_sn());
                params.put("warehouseIds", warehouseIds);
                List<PurchasingRecordProduct> records = getUsablePurchasingRecordProductList(params, curUser, VoucherType.PUT_IN, form.getBills_type());
                if (StringUtils.isNotEmpty(records)) {
//					for (PurchasingRecordProduct record : records) {
//						record.setBatch_number(udiInfo.getBatch_number());
//						record.setProduce_date(udiInfo.getProduce_date());
//						record.setOverdue_date(udiInfo.getOverdue_date());
//					}
                    result.setUsableProductList(records);
                } else {
                    result.setExistUsabelProduct(false);
                }
            }
            result.setProductBaseInfo(udiInfo);
        } catch (NotFoundUDIException e) {
            result.setExistUDI(false);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @Override
    public void updatePurchaseGrnDetailInspectionDetailId(Long org_id, Long grn_id, Long grn_detail_id, Long inspectionDetailId,
                                                          BillsType billsType) {
        GrnMapper mapper = getMapper(billsType);
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", org_id);
        params.put("grn_id", grn_id);
        params.put("grn_detail_id", grn_detail_id);
        params.put("inspection_detail_id", inspectionDetailId);
        mapper.updatePurchaseGrnDetailInspectionDetailId(params);
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        } else {
            PageHelper.clearPage();
        }
    }
}
