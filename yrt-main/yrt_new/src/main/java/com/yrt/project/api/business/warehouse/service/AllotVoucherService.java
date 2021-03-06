package com.yrt.project.api.business.warehouse.service;

import com.github.pagehelper.PageHelper;
import com.yrt.common.dict.AllotDetailStatus;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.*;
import com.yrt.common.utils.bean.BeanUtils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.warehouse.vo.allot.AddAllotVoucherForm;
import com.yrt.project.api.business.warehouse.vo.allot.AddOrEditProductDetailForm;
import com.yrt.project.api.business.warehouse.vo.allot.EditAllotVoucherForm;
import com.yrt.project.api.business.warehouse.vo.allot.SearchAllotVoucherForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucherDetail;
import com.yrt.project.modular.applyvoucher.mapper.HospitalApplyVoucherMapper;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.OrgMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.premium.service.PremiumRuleService;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.warehouse.domain.*;
import com.yrt.project.modular.warehouse.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AllotVoucherService {
    @Autowired HospitalMapper hMapper;
    @Autowired DealerMapper dMapper;
    @Autowired VendorMapper vMapper;

    @Autowired HospitalAllotVoucherMapper haMapper;
    @Autowired DealerAllotVoucherMapper daMapper;
    @Autowired VendorAllotVoucherMapper vaMapper;

    @Autowired
    private HospitalApplyVoucherMapper havMapper;

    @Autowired
    ProductUnitMapper pMapper;
    @Autowired
    DealerProductUnitMapper dpMapper;

    @Autowired private VoucherNumberService numberUtils;
    @Autowired
    PremiumRuleService premiumRuleService;

    private OrgMapper getOrgMapper(){
        SystemType systemType = ServletUtils.getCurUserSystemType();
        switch (systemType) {
            case DEALER:
                return dMapper;
            case VENDOR:
                return vMapper;
            default:
                return hMapper;
        }
    }

    private AllotVoucherMapper getAllotVoucherMapper() {
        SystemType systemType = ServletUtils.getCurUserSystemType();
        switch (systemType) {
            case DEALER:
                return daMapper;
            case VENDOR:
                return vaMapper;
            default:
                return haMapper;
        }

    }

    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    private Long getCurUserEmployeeId() {
        OrgEmployee curEmp = getOrgMapper().selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
        Long id = null;
        if(curEmp != null) {
            id = curEmp.getEmployee_inner_sn();
        }
        return id;
    }

    private String getCurUserEmployeeName() {
        OrgEmployee curEmp = getOrgMapper().selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
        String curEmpName = "";
        if(curEmp != null) {
            curEmpName = curEmp.getName();
        }
        return curEmpName;
    }

    private List<Long> getCurUserWarehouseIds() {
        return ServletUtils.getCurUserInfo().getWarehouseModelList()
                .stream()
                .map(WarehouseModel::getWarehouse_model_inner_sn)
                .collect(Collectors.toList());
    }

    @Transactional
    public AllotVoucher addAllotVoucher(AddAllotVoucherForm form) {
        Long av_inner_sn= CompressedIdGenerator.generateId();	//????????????????????????????????????
        Long aid = null;
        String apply_inner_sn;
        if (StringUtils.isNotEmpty(apply_inner_sn=form.getApply_voucher_inner_sn())) aid=Long.valueOf(apply_inner_sn);

        Map<String, Object> params = new HashMap<>();
        params.put("org_id", form.getOrg_id());
        params.put("av_inner_sn", av_inner_sn);
        //????????????
        params.put("av_sn", StringUtils.isEmpty(form.getAv_sn())?numberUtils.getMaxSn(VoucherType.ALLOT, ServletUtils.getCurUserSystemType(), form.getOrg_id()):form.getAv_sn());

        params.put("apply_voucher_inner_sn",aid);

        params.put("poster_inner_sn", getCurUserEmployeeId());//?????????????????????????????????
        params.put("poster_name", getCurUserEmployeeName());

        params.put("affirmant_inner_sn", form.getAffirmant_inner_sn());//???????????????
        params.put("affirmant_name", form.getAffirmant_name());//???????????????

        params.put("source_warehouse_inner_sn", form.getSource_warehouse_inner_sn());
        params.put("target_warehouse_inner_sn", form.getTarget_warehouse_inner_sn());

        params.put("expect_date", form.getExpect_date());
        params.put("description", form.getDescription());//??????

        int res= getAllotVoucherMapper().addAllotVoucher(params);

        if (res<1) throw new ValidateException(MessageUtils.message("db.add.fail","???????????????,?????????????????????"));

        insertAllotVoucherDetails(form.getOrg_id(), av_inner_sn, form.getDetails(), true);

        if (StringUtils.isNotEmpty(form.getApply_voucher_inner_sn()) && ServletUtils.getCurUserSystemType().equals(SystemType.HOSPITAL) ){
            //?????????????????????

            //??????????????????????????????????????????????????????????????????????????????????????????

            //?????????????????????????????????????????????????????????
            SearchAllotVoucherForm xForm = new SearchAllotVoucherForm();
            xForm.setOrg_id(form.getOrg_id());
            xForm.setTab_id(-1);
            xForm.setApply_voucher_inner_sn(aid);
            List<AllotVoucherDetail> avDetails = searchAllotVoucherList(xForm, false);//????????????????????????

            Map<String, List<AllotVoucherDetail>> avMap = avDetails.stream()
                    .peek(x->{
                        Map<String,Object> m=new HashMap<>();
                        m.put("vendor_inner_sn_i", x.getVd_inner_sn());
                        m.put("product_inner_sn_i", x.getProduct_inner_sn());
                        m.put("specification_inner_sn_i", x.getSpecification_inner_sn());
                        m.put("unit_inner_sn_i", x.getUnit_inner_sn());

                        BigDecimal bs;
                        if (1==x.getProduct_type()){
                            bs= pMapper.calculate_product_min_unit(m);

                        }else {
                            bs = dpMapper.calculate_product_min_unit(m);
                        }

                        //????????????????????????
                        x.setAllot_quantity(bs.multiply(x.getAllot_quantity()));
                    })
                    .collect(Collectors.groupingBy(x ->   x.getVd_inner_sn() +"-"+ x.getProduct_inner_sn()+ "-" + x.getSpecification_inner_sn()+"-"+x.getProduct_type()));

//            List<GrantSendDetailForm> detailForms = form.getDetails();
            //????????????details

            Map<String,Object> map=new HashMap<>();
            map.put("hospital_inner_sn",form.getOrg_id());
            map.put("av_inner_sn", aid);
            map.put("status", "0,1");
            map.put("onlyUnGrant", 1);
            map.put("uid", getCurUserEmployeeId());//?????????????????????????????????????????????
            List<HospitalApplyVoucherDetail> details = havMapper.searchHospitalApplyVoucherDetailList(map);
            if (details.isEmpty()) throw new ValidateException("???????????????????????????????????????");

            Map<String, List<HospitalApplyVoucherDetail>> applyMap = details.stream()
                    .peek(x -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("vendor_inner_sn_i", x.getVendor_inner_sn());
                        m.put("product_inner_sn_i", x.getProduct_inner_sn());
                        m.put("specification_inner_sn_i", x.getSpecification_inner_sn());
                        m.put("unit_inner_sn_i", x.getUnit_inner_sn());

                        BigDecimal bs;
                        if (1 == x.getProduct_type()) {
                            bs = pMapper.calculate_product_min_unit(m);

                        } else {
                            m.put("vendor_inner_sn_i", x.getDealer_inner_sn());
                            bs = dpMapper.calculate_product_min_unit(m);
                        }

                        //????????????????????????
                        x.setPlan_quantity(bs.multiply(x.getPlan_quantity()));
                    })
                    .collect(Collectors.groupingBy(x -> (1==x.getProduct_type()?x.getVendor_inner_sn():x.getDealer_inner_sn()) + "-" + x.getProduct_inner_sn() + "-" + x.getSpecification_inner_sn() + "-" + x.getProduct_type()));

            boolean flag=true;

            for (Map.Entry<String, List<AllotVoucherDetail>> avEntry:avMap.entrySet()){
                String key = avEntry.getKey();
                List<AllotVoucherDetail> avList = avMap.get(key);
                List<HospitalApplyVoucherDetail> applyList = applyMap.get(key);

                BigDecimal avSum = avList.stream().map(AllotVoucherDetail::getAllot_quantity).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal applySum = applyList.stream().map(HospitalApplyVoucherDetail::getPlan_quantity).reduce(BigDecimal.ZERO, BigDecimal::add);

                if (avSum.compareTo(applySum)>0) throw new BusinessException("????????????????????????!");

                if (avSum.compareTo(applySum)<0) flag=false;

                if (avSum.compareTo(applySum)==0){
                    //??????????????????????????????1-???????????????
                    for (HospitalApplyVoucherDetail detail:applyList){
                        params.put("detail_inner_sn",detail.getDetail_inner_sn());
                        haMapper.updateApplyDetailStatus(params);
                    }
                }
            }

            if(avMap.size()<applyMap.size()) flag=false;

            if (flag){
                //??????????????????????????????
                params.put("status",2);
            }else {
                //??????1-??????,	"?????? 0-?????? 1-?????? 2-????????????(????????????) 3-????????????(????????????)")
                params.put("status",1);
            }
            haMapper.updateApplyStatus(params);
        }

        return searchAllotVoucherDetails(av_inner_sn);
    }

    public List<AllotVoucherDetail> searchAllotVoucherList(SearchAllotVoucherForm form, boolean isPage) {
        List<Long> warehouseIds = getCurUserWarehouseIds();
        if (warehouseIds.size()==0) return new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", form.getOrg_id());
        if (StringUtils.isNotEmpty(form.getAv_inner_sn())) params.put("av_inner_sn", Long.parseLong(form.getAv_inner_sn()));
        params.put("av_sn", form.getAv_sn());
        if (StringUtils.isNotEmpty(form.getDetail_inner_sn())) params.put("detail_inner_sn", Long.parseLong(form.getDetail_inner_sn()));
        params.put("product_name",form.getProduct_name());
        params.put("apply_voucher_inner_sn",form.getApply_voucher_inner_sn());
        params.put("poster_inner_sn", form.getPoster_inner_sn());
        params.put("poster_name",form.getPoster_name());

        params.put("affirmant_inner_sn", form.getAffirmant_inner_sn());
        params.put("affirmant_name",form.getAffirmant_name());

        params.put("tab_id",form.getTab_id());

        params.put("warehouseIds", warehouseIds);//?????????????????????????????????????????????????????????
        params.put("start_date", form.getStart_date());
        params.put("end_date", form.getEnd_date());
        if (isPage) startPage();
        List<AllotVoucherDetail> details = getAllotVoucherMapper().searchAllotVoucherList(params);
        // ?????????????????????????????????????????????????????????????????????????????????
        details.forEach(e->e.setSalesPrice(e.getUnit_price()));
        return details;
    }

    public AllotVoucher searchAllotVoucherDetails(Long av_inner_sn) {
        List<Long> warehouseIds = getCurUserWarehouseIds();
        if (warehouseIds.size()==0) return null;

        Map<String,Object> params=new HashMap<>();
        params.put("warehouseIds", warehouseIds);//??????????????????????????????/??????????????????????????????
        params.put("av_inner_sn",av_inner_sn);

        AllotVoucher av = getAllotVoucherMapper().searchAllotVoucher(params);

        if (Objects.isNull(av)) return null;

        av.setDetails(getAllotVoucherMapper().searchAllotDetails(av_inner_sn));
        return av;
    }

    @Transactional
    public int updateAllotVoucherDetails(List<AllotVoucherDetail> details) {
        for (AllotVoucherDetail detail:details){
            if (Objects.isNull(detail.getDetail_inner_sn())) throw new ValidateException("??????ID????????????");
            Integer status=getAllotVoucherMapper().getAllotDetailStatus(detail.getDetail_inner_sn());
            if (Objects.isNull(status)) throw new ValidateException(MessageUtils.message("db.update.fail","?????????"+detail.getAv_sn()+"????????????????????????"));
            if (status==4) throw new ValidateException(MessageUtils.message("db.update.fail","?????????"+detail.getAv_sn()+"?????????????????????"));

            //?????????????????????????????? ?????????????????????
            if (status==2 || status==3) throw new ValidateException(MessageUtils.message("db.update.fail","???????????????????????????????????????????????????"));
            if (status==1 && !AllotDetailStatus.DISCARD.equals(detail.getStatus())) throw new ValidateException(MessageUtils.message("db.update.fail","????????????????????????????????????"));

            //?????????????????????????????????????????????????????????????????????????????????????????????
            getAllotVoucherMapper().deleteAllotVoucherDetail(detail.getDetail_inner_sn());

            AddOrEditProductDetailForm form = new AddOrEditProductDetailForm();
            BeanUtils.copyBeanProp(form,detail);
            insertAllotVoucherDetails(detail.getOrg_id(),detail.getAv_inner_sn(),Collections.singletonList(form),false);
        }
        return 1;
    }

    @Transactional
    public int updateAllotVoucher(EditAllotVoucherForm form) {
        Long av_inner_sn=Long.parseLong(form.getAv_inner_sn());

        List<AddOrEditProductDetailForm> details = form.getDetails();

//        for (AddOrEditProductDetailForm detail:details){
//            Integer status=getAllotVoucherMapper().getAllotDetailStatus(detail.getDetail_inner_sn());
//            if (Objects.isNull(status) || status!=0) throw new DBException(MessageUtils.message("db.update.fail","?????????????????????????????????"));
//        }

        Map<String,Object> params=new HashMap<>();
        params.put("av_inner_sn",av_inner_sn);
        params.put("av_sn",form.getAv_sn());

        params.put("affirmant_inner_sn",getCurUserEmployeeId());
        params.put("affirmant_name",StringUtils.isEmpty(form.getAffirmant_name())?getCurUserEmployeeName():form.getAffirmant_name());

        params.put("source_warehouse_inner_sn",form.getSource_warehouse_inner_sn());
        params.put("target_warehouse_inner_sn",form.getTarget_warehouse_inner_sn());
        params.put("description",form.getDescription());
        params.put("expect_date",form.getExpect_date());
        int res=getAllotVoucherMapper().updateAllotVoucher(params);

        if (res<1) throw new DBException(MessageUtils.message("db.update.fail","???????????????????????????????????????"));

        //?????????????????????????????????????????????????????????????????????????????????
        getAllotVoucherMapper().deleteAllotDetails(av_inner_sn);
        insertAllotVoucherDetails(form.getOrg_id(),av_inner_sn,details,false);
        return res;
    }

    public List<AllotVoucher> getTransferAllotVoucherList(long orgId, String key, String key1,Integer status) {
        //??????????????????
        List<Long> warehouseIds = getCurUserWarehouseIds();
        if (warehouseIds.size()==0) return new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("org_id", orgId);
        params.put("av_sn", key);
        params.put("status", status);
        params.put("product_name",StringUtils.isNotEmpty(key1)?key1:null);
        params.put("warehouseIds", getCurUserWarehouseIds());
        startPage();
        return getAllotVoucherMapper().selectTransferAllotList(params);
    }

    public List<AllotVoucherDetail> getTransferAllotVoucherDetailList(long orgId, Long avid,Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", orgId);
        params.put("status", status);
        params.put("av_inner_sn", avid);
        if (Objects.nonNull(status)&&2==status){
            //????????????
            return getAllotVoucherMapper().selectGrnTransferAllotDetailList(params);
        }
        return getAllotVoucherMapper().selectOdoTransferAllotDetailList(params);
    }

    @Transactional
    void insertAllotVoucherDetails(Long org_id, Long av_inner_sn, List<AddOrEditProductDetailForm> details, boolean isNew) {
        for (AddOrEditProductDetailForm detail : details) {
            Long detail_inner_sn= CompressedIdGenerator.generateId();
            Map<String, Object> params1 = new HashMap<>();
            params1.put("org_id", org_id);
            params1.put("av_inner_sn", av_inner_sn);
            params1.put("detail_inner_sn",detail_inner_sn);

            params1.put("vd_inner_sn",detail.getVd_inner_sn());
            params1.put("vd_name",detail.getVd_name());
            params1.put("product_type",detail.getProduct_type());

            params1.put("product_inner_sn", detail.getProduct_inner_sn());
            params1.put("product_name", detail.getProduct_name());
            params1.put("specification_inner_sn", detail.getSpecification_inner_sn());
            params1.put("unit_inner_sn", detail.getUnit_inner_sn());
            params1.put("unit_name", detail.getUnit_name());
            params1.put("sort_number", detail.getSort_number());
            params1.put("batch_number", detail.getBatch_number());
            params1.put("allot_quantity", detail.getAllot_quantity());
            AllotDetailStatus status = isNew ? (Objects.nonNull(detail.getStatus()) ? detail.getStatus() : AllotDetailStatus.DRAFT ): detail.getStatus();
            params1.put("status",status.value());
            params1.put("specification", detail.getSpecification());
            params1.put("unit_price", detail.getUnit_price());

            params1.put("produce_date", detail.getProduce_date());
            params1.put("overdue_date", detail.getOverdue_date());

            getAllotVoucherMapper().addAllotVoucherDetail(params1);
        }
    }
}
