package com.yrt.project.api.business.applyvoucher.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yrt.project.modular.premium.service.PremiumRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.google.common.base.Objects;
import com.yrt.common.client.PushService;
import com.yrt.common.dict.ApplyDetailStatus;
import com.yrt.common.dict.ApplyRecordStatus;
import com.yrt.common.dict.PrdType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.applyvoucher.service.IHospitalApplyVoucherService;
import com.yrt.project.api.business.applyvoucher.vo.AddHospitalApplyVoucherForm;
import com.yrt.project.api.business.applyvoucher.vo.ApplyIDForm;
import com.yrt.project.api.business.applyvoucher.vo.ApplyVoucherDetailForm;
import com.yrt.project.api.business.applyvoucher.vo.EditHospitalApplyVoucherForm;
import com.yrt.project.api.business.applyvoucher.vo.SearchApplyVoucherForm;
import com.yrt.project.api.business.applyvoucher.vo.ViewApplyVoucherForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucherDetail;
import com.yrt.project.modular.applyvoucher.mapper.HospitalApplyVoucherMapper;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.product.domain.ApplyResponseProductInfo;

@Service
public class HospitalApplyVoucherServiceImpl implements IHospitalApplyVoucherService {

    @Autowired
    private HospitalApplyVoucherMapper mapper;
    @Autowired
    private HospitalMapper hMapper;

    @Autowired
    private VoucherNumberService numberUtils;
    @Autowired
    PushService pushService;
    @Autowired
    PremiumRuleService premiumRuleService;

    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    private Long getCurUserEmployeeId() {
        OrgEmployee curEmp = hMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
        Long id = null;
        if (curEmp != null) {
            id = curEmp.getEmployee_inner_sn();
        }
        return id;
    }

    private String getCurUserEmployeeName() {
        OrgEmployee curEmp = hMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
        String curEmpName = "";
        if (curEmp != null) {
            curEmpName = curEmp.getName();
        }
        return curEmpName;
    }

    @Override
    public List<HospitalApplyVoucher> searchHospitalApplyVoucherList(SearchApplyVoucherForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", form.getHospital_inner_sn());
        params.put("status", form.getStatus());
        params.put("voucher_sn", form.getVoucher_sn());
        params.put("uid", getCurUserEmployeeId());//只能查询登录人自己部门的申领单
        startPage();
        return mapper.searchHospitalApplyVoucherList(params);
    }

    @Override
    public List<HospitalApplyVoucher> searchHospitalApplyVoucherList4Phone(Long oid, String key, Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", oid);
        if (status == 0) {
            params.put("status", "1,2,3");//周老板说不包括草稿2020-02-27
        } else {
            params.put("status", status);
        }
        params.put("voucher_sn", key);
        params.put("proposer_name", key);
        params.put("department_name", key);
        params.put("uid", getCurUserEmployeeId());//只能查询登录人自己部门的申领单
        startPage();
        return mapper.appSearchHospitalApplyVoucherList(params);
    }

    @Override
    @Transactional
    public HospitalApplyVoucher addHospitalApplyVoucher(AddHospitalApplyVoucherForm form) {
        Map<String, Object> params = new HashMap<>();

        params.put("warehouse_inner_sn_i", form.getWarehouse_inner_sn());
        params.put("warehouse_inner_sn_name_i", form.getWarehouse_inner_sn_name());

        params.put("hospital_inner_sn_i", form.getHospital_inner_sn());
        params.put("department_inner_sn_i", form.getDepartment_inner_sn());
        params.put("proposer_inner_sn_i", getCurUserEmployeeId());//当前登录人员作为申领人
        params.put("status_i", form.getStatus());
        params.put("expect_date_i", form.getExpect_date());
        if (StringUtils.isEmpty(form.getVoucher_sn())) {
            params.put("av_sn_i", numberUtils.getMaxSn(VoucherType.APPLY, SystemType.HOSPITAL, form.getHospital_inner_sn())); //.getApplyMaxSn(form.getHospital_inner_sn()));
        } else {
            params.put("av_sn_i", form.getVoucher_sn());
        }
        params.put("description_i", form.getDescription());
        params.put("department_name_i", form.getDepartment_name());
        params.put("proposer_name_i", getCurUserEmployeeName());
        mapper.h_add_hospital_apply_voucher(params);
        if (Integer.valueOf(params.get("result_o").toString()) == 0) {
            Long aid = Long.valueOf(params.get("av_inner_sn_o").toString());
            List<ApplyVoucherDetailForm> details = form.getDetails();
            insertHospitalApplyVoucherDetailForm(form.getHospital_inner_sn(), aid, details, true);
            Map<String, Object> params1 = new HashMap<>();
            params1.put("hospital_inner_sn", form.getHospital_inner_sn());
            params1.put("av_inner_sn", aid);
            HospitalApplyVoucher info = mapper.selectHospitalApplyVoucherInfo(params1);
            if (info.getStatus().equals(1)) {
                pushService.sendApplyMessage(info.getHospital_inner_sn(), SystemType.HOSPITAL.value(), info.getDepartment_name(), info.getVoucher_sn());
            }
            return info;
        } else {
            if ("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
                throw new DuplicateException(MessageUtils.message("db.add.fail", "申领单", params.get("message_string_o")));
            } else {
                throw new DBException(MessageUtils.message("db.add.fail", "申领单", params.get("message_string_o")));
            }
        }
    }

    private void insertHospitalApplyVoucherDetailForm(Long oid, Long aid, List<ApplyVoucherDetailForm> details, boolean isNew) {
        for (ApplyVoucherDetailForm detailForm : details) {
            detailForm.validated();
            Map<String, Object> params1 = new HashMap<>();
            params1.put("hospital_inner_sn_i", oid);
            params1.put("av_inner_sn_i", aid);
            params1.put("product_inner_sn_i", detailForm.getProduct_inner_sn());
            params1.put("specification_inner_sn_i", detailForm.getSpecification_inner_sn());
            params1.put("unit_inner_sn_i", detailForm.getUnit_inner_sn());
            params1.put("sort_number_i", detailForm.getSort_number());
            params1.put("batch_number_i", detailForm.getBatch_number());
            params1.put("plan_quantity_i", detailForm.getPlan_quantity());
            if (isNew) {
                params1.put("status_i", 0);
            } else {
                params1.put("status_i", detailForm.getStatus());
            }
            params1.put("product_name_i", detailForm.getProduct_name());
            params1.put("specification_i", detailForm.getSpecification());
            params1.put("unit_i", detailForm.getUnit_name());
            params1.put("batch_number_i", detailForm.getBatch_number());
//			params1.put("unit_price_i", detailForm.getUnit_price());
            params1.put("unit_price_i", BigDecimal.ZERO);
            if (detailForm.getProduct_type() == 0) {
                params1.put("product_dealer_inner_sn_i", detailForm.getDealer_inner_sn());
                params1.put("product_dealer_name_i", detailForm.getDealer_name());
                mapper.h_add_hospital_apply_voucher_dealer_product_detail(params1);
            } else {
                params1.put("product_vendor_inner_sn_i", detailForm.getVendor_inner_sn());
                params1.put("product_vendor_name_i", detailForm.getVendor_name());
                mapper.h_add_hospital_apply_voucher_product_detail(params1);
            }
        }
    }

    @Override
    @Transactional
    public int updateAddHospitalApplyVoucher(EditHospitalApplyVoucherForm form) {
        if (form.isUpdate()) {
            Map<String, Object> params = form.getInfo();
            params.put("hospital_inner_sn", form.getHospital_inner_sn());
            params.put("av_inner_sn", form.getVoucher_inner_sn());
            try {
                mapper.updateHospitalApplyVoucher(params);
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException) {
                    throw new DuplicateException(MessageUtils.message("db.update.fail", "申领单", "申领单编码重复"));
                } else {
                    throw new DBException(MessageUtils.message("db.update.fail", "申领单", e.getMessage()));
                }
            }
        }
        List<ApplyVoucherDetailForm> details = form.getDetails();
        if (StringUtils.isNotEmpty(details) && details.get(0) != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("hospital_inner_sn", form.getHospital_inner_sn());
            params.put("av_inner_sn", form.getVoucher_inner_sn());
            mapper.deleteHospitalApplyVoucherDealerProduct(params);
            mapper.deleteHospitalApplyVoucherProduct(params);
            insertHospitalApplyVoucherDetailForm(form.getHospital_inner_sn(), form.getVoucher_inner_sn(), details, false);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("hospital_inner_sn", form.getHospital_inner_sn());
            params.put("av_inner_sn", form.getVoucher_inner_sn());
            mapper.deleteHospitalApplyVoucherDealerProduct(params);
            mapper.deleteHospitalApplyVoucherProduct(params);
        }
        Map<String, Object> params1 = new HashMap<>();
        params1.put("hospital_inner_sn", form.getHospital_inner_sn());
        params1.put("av_inner_sn", form.getVoucher_inner_sn());
        HospitalApplyVoucher info = mapper.selectHospitalApplyVoucherInfo(params1);
        if (info.getStatus().equals(1)) {
            pushService.sendApplyMessage(info.getHospital_inner_sn(), SystemType.HOSPITAL.value(), info.getHospital_name(), info.getVoucher_sn());
        }
        return 1;
    }

    @Override
    @Transactional
    public int deleteHospitalApplyVoucher(Long oid, Long aid) {
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", oid);
        params.put("av_inner_sn", aid);
        mapper.deleteHospitalApplyVoucherProduct(params);
        mapper.deleteHospitalApplyVoucherDealerProduct(params);

        return mapper.trueDeleteHospitalApplyVoucher(params);
    }

    @Override
    @Transactional
    public ViewApplyVoucherForm copyHospitalApplyVoucher(Long oid, Long aid) {
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", oid);
        params.put("av_inner_sn", aid);
        HospitalApplyVoucher ovoucher = mapper.selectHospitalApplyVoucherInfo(params);
        ViewApplyVoucherForm form = new ViewApplyVoucherForm();
        BeanUtils.copyProperties(ovoucher, form);
        form.setVoucher_inner_sn(null);
        form.setStatus(0);
        form.setVoucher_sn("");
        List<HospitalApplyVoucherDetail> details = mapper.copyHospitalApplyVoucherDetailList(params);
//		if(details != null && details.size() > 0) {
//			for(HospitalApplyVoucherDetail hav : details) {
//				hav.setStatus(0);
//			}
//		}
        form.setDetails(details);
        return form;
    }

    @Override
    @Transactional
    public int stopHospitalApplyVoucher(Long oid, Long aid) {
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", oid);
        params.put("av_inner_sn", aid);
        params.put("status", 3);//3-申领终止(人为终止)
        return mapper.updateHospitalApplyVoucher(params);
        /*
         * if(result > 0) { List<HospitalApplyVoucherDetail> details =
         * mapper.searchHospitalApplyVoucherDetailList(params); if(details != null &&
         * details.size() > 0) { for(HospitalApplyVoucherDetail hav : details) {
         * if(hav.getStatus() != 0) { continue; } params.put("status", 3);//3-申领终止(人为终止)
         * params.put("detail_inner_sn", hav.getDetail_inner_sn());
         * if(hav.getProduct_type() == 1) {
         * mapper.updateHospitalApplyVoucherProduct(params); }else {
         * mapper.updateHospitalApplyVoucherDealerProduct(params); } } } }
         * return result;
         */
    }

    @Override
    public List<ApplyResponseProductInfo> selectContractVendorProductList(Map<String, Object> params) {
        return mapper.selectContractVendorProductList(params);
    }

    @Override
    public List<ApplyResponseProductInfo> selectHospitalApplyResponseProductInfoByOidAndDid(Long oid, Long did, Integer productType) {
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", oid);
        params.put("department_inner_sn", did);

        List<HospitalApplyVoucherDetail> hospitalApplyVoucherDetails = mapper.searchHospitalApplyDetailListByOidAndDid(params);

        List<Long> voucherProductIds = hospitalApplyVoucherDetails.stream().filter(x -> x.getProduct_type().equals(productType)).map(HospitalApplyVoucherDetail::getProduct_inner_sn).collect(Collectors.toList());

        if(voucherProductIds.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Object> param1 = new HashMap<>();
        param1.put("key", StringUtils.join(voucherProductIds, ","));

        List<ApplyResponseProductInfo> productInfos = mapper.selectContractVendorProductListByIds(param1);

        return productInfos;
    }

//    @Override
//    public List<HospitalApplyVoucherDetail> selectHospitalApplyDealerDetailListByOidAndDid(Long oid, Long did) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("hospital_inner_sn", oid);
//        params.put("department_inner_sn", did);
//
//        List<HospitalApplyVoucherDetail> hospitalApplyVoucherDetails = mapper.searchHospitalApplyVoucherDetailListByOidAndDid(params);
//
//        List<Long> dealerProductIds = hospitalApplyVoucherDetails.stream().filter(x -> x.getProduct_type().equals(0)).map(HospitalApplyVoucherDetail::getProduct_inner_sn).collect(Collectors.toList());
//
//        Map<String, Object> param2 = new HashMap<>();
//        params.put("key", dealerProductIds);
//
//        List<ApplyResponseProductInfo> productInfos1 = mapper.selectContractDealerProductListByIds(param2);
//
//        List<Long> voucherProductIds = hospitalApplyVoucherDetails.stream().filter(x -> x.getProduct_type().equals(1)).map(HospitalApplyVoucherDetail::getProduct_inner_sn).collect(Collectors.toList());
//
//        Map<String, Object> param1 = new HashMap<>();
//        params.put("key", voucherProductIds);
//
//        List<ApplyResponseProductInfo> productInfos2 = mapper.selectContractVendorProductListByIds(param1);
//
//        return mapper.searchHospitalApplyVoucherDetailListByOidAndDid(params);
//    }

    @Override
    public List<ApplyResponseProductInfo> selectContractDealerProductList(Map<String, Object> params) {
        return mapper.selectContractDealerProductList(params);
    }

    @Override
    public ViewApplyVoucherForm selectHospitalApplyVoucherInfo(Long oid, Long aid) {
        Map<String, Object> params1 = new HashMap<>();
        params1.put("hospital_inner_sn", oid);
        params1.put("av_inner_sn", aid);
        HospitalApplyVoucher ovoucher = mapper.selectHospitalApplyVoucherInfo(params1);
        ViewApplyVoucherForm form = new ViewApplyVoucherForm();
        BeanUtils.copyProperties(ovoucher, form);
        List<HospitalApplyVoucherDetail> details = mapper.searchHospitalApplyVoucherDetailList(params1);
        form.setDetails(details);
        return form;
    }

    @Override
    public int deleteHospitalApplyVoucherDetail(ApplyIDForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", form.getHospital_inner_sn());
        params.put("av_inner_sn", form.getOrder_id());
        params.put("detail_inner_sn", form.getDetail_id());
        if (form.getProduct_type() == 1) {
            return mapper.deleteHospitalApplyVoucherProduct(params);
        } else {
            return mapper.deleteHospitalApplyVoucherDealerProduct(params);
        }
    }

    @Override
    @Transactional
    public int stopHospitalApplyVoucherDetail(ApplyIDForm form) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("hospital_inner_sn", form.getHospital_inner_sn());
        params.put("av_inner_sn", form.getOrder_id());
        params.put("status", ApplyDetailStatus.TERMINATED.value()); //明细状态：3-申领终止(人为终止)
        params.put("detail_inner_sn", form.getDetail_id());

        int result;
        if (form.getProduct_type().intValue() == PrdType.VENDOR_PRD.value()) {
            result = mapper.updateHospitalApplyVoucherProduct(params);
        } else {
            result = mapper.updateHospitalApplyVoucherDealerProduct(params);
        }

        params.remove("status");
        List<HospitalApplyVoucherDetail> newDetails = mapper.searchHospitalApplyVoucherDetailList(params);
        //明细 只有 “申领中” 是未结状态
        boolean hasPending = newDetails.stream().anyMatch(d -> Objects.equal(d.getStatus(), ApplyDetailStatus.APPLYING.value()));
        if (!hasPending) {
            Map<String, Object> params1 = new HashMap<>();
            params1.put("hospital_inner_sn", form.getHospital_inner_sn());
            params1.put("av_inner_sn", form.getOrder_id());
            params1.put("status", ApplyRecordStatus.TERMINATED.value()); //主记录状态: 3-申领终止(人为终止)
            mapper.updateHospitalApplyVoucher(params1);
        }

        return result;
    }

    @Override
    public List<String> selectBatchNumberList(Long oid, Long vid, Long pid, Long sid, Integer product_type) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("hospital_inner_sn", oid);
        params.put("product_vendor_inner_sn", vid);
        params.put("product_inner_sn", pid);
        params.put("specification_inner_sn", sid);
        if (product_type == 1) {
            return mapper.selectVendorBatchNumberList(params);
        }
        return mapper.selectDealerBatchNumberList(params);
    }


}
