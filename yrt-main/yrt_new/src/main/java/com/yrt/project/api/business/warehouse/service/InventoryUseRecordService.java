package com.yrt.project.api.business.warehouse.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.yrt.common.dict.AllotDetailStatus;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.applyvoucher.vo.EditInventoryUseRecordForm;
import com.yrt.project.api.business.warehouse.vo.AddInventoryUseRecordForm;
import com.yrt.project.api.business.warehouse.vo.record.AddOrEditInventoryUseRecordDetailForm;
import com.yrt.project.api.business.warehouse.vo.record.BatchNumResult;
import com.yrt.project.api.business.warehouse.vo.record.SearchBatchNumForm;
import com.yrt.project.api.business.warehouse.vo.record.SearchInventoryUseRecordForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.warehouse.domain.InventoryUseRecord;
import com.yrt.project.modular.warehouse.domain.InventoryUseRecordDetail;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import com.yrt.project.modular.warehouse.mapper.InventoryUseRecordMapper;

@Service
public class InventoryUseRecordService {
    @Autowired
    private InventoryUseRecordMapper mapper;
    @Autowired private HospitalMapper hMapper;

    @Autowired private VoucherNumberService numberUtils;

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
        OrgEmployee curEmp = hMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
        Long id = null;
        if(curEmp != null) {
            id = curEmp.getEmployee_inner_sn();
        }
        return id;
    }

    private String getCurUserEmployeeName() {
        OrgEmployee curEmp = hMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
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
    public InventoryUseRecord addInventoryUseRecord(AddInventoryUseRecordForm form) {
        long id = CompressedIdGenerator.generateId();

        Map<String,Object> params=new HashMap<>();
        params.put("hospital_inner_sn",form.getHospital_inner_sn());
        params.put("rv_inner_sn",id);
        params.put("rv_sn",numberUtils.getMaxSn(VoucherType.RECORD, SystemType.HOSPITAL, form.getHospital_inner_sn()));

        params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());//当前登录人员作为调拨人
        params.put("warehouse_name", form.getWarehouse_name());

        params.put("use_man", form.getUse_man());//使用人编号
        params.put("use_type", form.getUse_type());//使用类型

        params.put("proposer_inner_sn",getCurUserEmployeeId());
        params.put("proposer_name", getCurUserEmployeeName());

        params.put("medical_no", form.getMedical_no());//病历号号
        params.put("health_no", form.getHealth_no());//医保号

        int res= mapper.addInventoryUseRecord(params);

        if (res<1) throw new ValidateException(MessageUtils.message("db.add.fail","数据库异常,新增调拨单失败"));

        insertInventoryUseRecordDetails(form.getWarehouse_inner_sn(),form.getHospital_inner_sn(), id, form.getDetails(), true);

        return searchInventoryUseRecord(id);
    }

    private InventoryUseRecord searchInventoryUseRecord(long id) {
        List<Long> warehouseIds = getCurUserWarehouseIds();
        if (warehouseIds.isEmpty()) return null;

        Map<String,Object> params=new HashMap<>();
        params.put("hospital_inner_sn",ServletUtils.getCurUserOrgID());
        params.put("warehouseIds", warehouseIds);//只能查询登录人自己仓库权限
        params.put("rv_inner_sn",id);

        InventoryUseRecord r = mapper.searchInventoryUseRecord(params);
        if (Objects.isNull(r)) return null;
        r.setDetails(mapper.searchInventoryUseRecordDetails(params));
        return r;
    }


    @Autowired
    ProductUnitMapper puMapper;
    @Autowired
    DealerProductUnitMapper dpuMapper;

    @Transactional
    private void insertInventoryUseRecordDetails(Long warehouse_inner_sn,Long hospital_inner_sn, long rv_inner_sn, List<AddOrEditInventoryUseRecordDetailForm> details, boolean isNew) {
        for (AddOrEditInventoryUseRecordDetailForm detail : details) {
            Map<String, Object> params1 = new HashMap<>();
            params1.put("hospital_inner_sn", hospital_inner_sn);
            params1.put("detail_inner_sn",CompressedIdGenerator.generateId());
            params1.put("rv_inner_sn", rv_inner_sn);

            params1.put("vd_inner_sn",detail.getVd_inner_sn());
            params1.put("vd_name",detail.getVd_name());
            params1.put("product_type",detail.getProduct_type());

            params1.put("product_inner_sn", detail.getProduct_inner_sn());
            params1.put("product_name", detail.getProduct_name());
            params1.put("specification_inner_sn", detail.getSpecification_inner_sn());
            params1.put("specification", detail.getSpecification());

            params1.put("unit_price",detail.getUnit_price());

            params1.put("default_health_care_sn", detail.getDefault_health_care_sn());

            params1.put("unit_inner_sn", detail.getUnit_inner_sn());
            params1.put("unit_name", detail.getUnit_name());
            params1.put("batch_number", detail.getBatch_number());
            params1.put("quantity", detail.getQuantity());
            AllotDetailStatus status = isNew ? (Objects.nonNull(detail.getStatus()) ? detail.getStatus() : AllotDetailStatus.DRAFT ): detail.getStatus();
            params1.put("status",status.value());

            mapper.addInventoryUseRecordDetail(params1);

            if (1==status.value()){
                //确认，减库存数量
                params1.put("warehouse_inner_sn",warehouse_inner_sn);

                Map<String,Object> map=new HashMap<>();
                map.put("vendor_inner_sn_i", detail.getVd_inner_sn());
                map.put("product_inner_sn_i", detail.getProduct_inner_sn());
                map.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
                map.put("unit_inner_sn_i", detail.getUnit_inner_sn());

                BigDecimal bs;
                if (1==detail.getProduct_type()){
                    bs= puMapper.calculate_product_min_unit(map);
                }else {
                    bs = dpuMapper.calculate_product_min_unit(map);
                }

                params1.put("reduce_quantity",bs.multiply(detail.getQuantity()));
                mapper.reduceInventory(params1);
            }
        }
    }

    public List<BatchNumResult> searchProductBatchNumList(SearchBatchNumForm form) {
        if (form.getProduct_type()==0) return mapper.searchDealerProductBatchNumList(form);
        return mapper.searchVendorProductBatchNumList(form);
    }

    public List<InventoryUseRecordDetail> getInventoryUseRecords(SearchInventoryUseRecordForm form,boolean isPage) {
        List<Long> warehouseIds = getCurUserWarehouseIds();
        if (warehouseIds.size()==0) return new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", form.getHospital_inner_sn());
        if (StringUtils.isNotEmpty(form.getRv_inner_sn())) params.put("rv_inner_sn", Long.parseLong(form.getRv_inner_sn()));
        params.put("rv_sn", form.getRv_sn());
        if (StringUtils.isNotEmpty(form.getDetail_inner_sn())) params.put("detail_inner_sn", Long.parseLong(form.getDetail_inner_sn()));

        params.put("proposer_inner_sn", form.getProposer_inner_sn());
        params.put("proposer_name",form.getProposer_name());

        params.put("warehouseIds", warehouseIds);//只能查询登录人自己目标仓库权限的调拨单
        params.put("start_date", form.getStart_date());
        params.put("end_date", form.getEnd_date());
        if (isPage) startPage();
        return mapper.searchInventoryUseRecordDetails(params);
    }

    @Transactional
    public int updateInventoryUseRecord(EditInventoryUseRecordForm form) {
        Long rv_inner_sn = form.getRv_inner_sn();

        List<AddOrEditInventoryUseRecordDetailForm> details = form.getDetails();

        Map<String,Object> map=new HashMap<>();
        map.put("hospital_inner_sn",form.getOrg_id());
        map.put("warehouseIds", getCurUserWarehouseIds());//只能更新登录人自己仓库权限

        for (AddOrEditInventoryUseRecordDetailForm x:details){
            //只能修改草稿状态的
            if (Objects.nonNull(x.getDetail_inner_sn())){
                map.put("detail_inner_sn",x.getDetail_inner_sn());
                InventoryUseRecordDetail detail = mapper.searchInventoryUseRecordDetails(map).get(0);
                if (0!=detail.getStatus().value()) throw new ValidateException("只能修改草稿状态的记录。");
            }

            String batch_number=StringUtils.isEmpty(x.getBatch_number())?"空批号":x.getBatch_number();

            SearchBatchNumForm res =new SearchBatchNumForm(form.getOrg_id(), form.getWarehouse_inner_sn(), x.getVd_inner_sn(),
                    x.getProduct_inner_sn(), x.getSpecification_inner_sn(), x.getBatch_number(), x.getProduct_type());
            List<BatchNumResult> numResults = searchProductBatchNumList(res);
            if (numResults.isEmpty()) throw new BusinessException("产品:"+x.getProduct_name()+" 规格:"+x.getSpecification() +"  批号:"+batch_number+"  缺少库存");
            BatchNumResult result = numResults.get(0);

            Map<String,Object> m=new HashMap<>();
            m.put("vendor_inner_sn_i", x.getVd_inner_sn());
            m.put("product_inner_sn_i", x.getProduct_inner_sn());
            m.put("specification_inner_sn_i", x.getSpecification_inner_sn());
            m.put("unit_inner_sn_i", x.getUnit_inner_sn());

            BigDecimal bs;
            if (1==x.getProduct_type()){
                bs= puMapper.calculate_product_min_unit(m);
            }else {
                bs = dpuMapper.calculate_product_min_unit(m);
            }
            //最小包装单位数量
            BigDecimal quantity = bs.multiply(x.getQuantity());

            if (result.getQuantity().compareTo(quantity)<0){
                BigDecimal xx = quantity.subtract(result.getQuantity());
                throw new BusinessException("产品:"+x.getProduct_name()+" 规格:"+x.getSpecification() +"  批号:"+batch_number+"  超过可使用库存，超过数量:"+xx);
            }

        }

        Map<String,Object> params=new HashMap<>();
        params.put("rv_inner_sn",rv_inner_sn);
        params.put("warehouse_inner_sn",form.getWarehouse_inner_sn());
        params.put("use_man",form.getUse_man());
        params.put("use_type",form.getUse_type());

        params.put("proposer_inner_sn",getCurUserEmployeeId());
        params.put("proposer_name",getCurUserEmployeeName());

        params.put("medical_no",form.getMedical_no());
        params.put("health_no",form.getHealth_no());

        int res=mapper.updateInventoryUseRecord(params);

        if (res<1) throw new ValidateException(MessageUtils.message("db.update.fail","数据库异常，修改记录单失败"));

        //直接删除原来记录单下产品明细记录，插入一条新的明细记录
        mapper.deleteInventoryUseRecordDetails(rv_inner_sn);
        insertInventoryUseRecordDetails(form.getWarehouse_inner_sn(),form.getOrg_id(),rv_inner_sn,details,false);
        return res;
    }
}
