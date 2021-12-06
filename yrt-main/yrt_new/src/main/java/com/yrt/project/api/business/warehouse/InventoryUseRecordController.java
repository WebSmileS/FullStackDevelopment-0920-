package com.yrt.project.api.business.warehouse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.exception.BusinessException;
import com.yrt.common.utils.StringUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.applyvoucher.vo.EditInventoryUseRecordForm;
import com.yrt.project.api.business.warehouse.service.InventoryUseRecordService;
import com.yrt.project.api.business.warehouse.vo.AddInventoryUseRecordForm;
import com.yrt.project.api.business.warehouse.vo.record.AddOrEditInventoryUseRecordDetailForm;
import com.yrt.project.api.business.warehouse.vo.record.BatchNumResult;
import com.yrt.project.api.business.warehouse.vo.record.SearchBatchNumForm;
import com.yrt.project.api.business.warehouse.vo.record.SearchInventoryUseRecordForm;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;
import com.yrt.project.modular.warehouse.domain.InventoryUseRecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/business/hospital/use_record")
@Api(tags = "业务-医院库存使用记录接口")
public class InventoryUseRecordController extends BaseController {

    @Autowired
    private InventoryUseRecordService service;

    @Autowired
    ProductUnitMapper pMapper;
    @Autowired
    DealerProductUnitMapper dpMapper;

    @Log(title = "business.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/batch/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取仓库内产品批号列表", notes = "获取仓库内产品批号列表")
    public AjaxResult getProductBatchNumList(@Validated SearchBatchNumForm form) {
        return success().put("list", service.searchProductBatchNumList(form));
    }

    @Log(title = "business.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation(value = "添加库存使用记录", notes = "添加库存使用记录")
    public ResultInfo<InventoryUseRecord> addInventoryUseRecord(@RequestBody @Valid AddInventoryUseRecordForm form) {
        List<AddOrEditInventoryUseRecordDetailForm> details = form.getDetails();
        //校验
        for (AddOrEditInventoryUseRecordDetailForm detail:details){
            SearchBatchNumForm x = new SearchBatchNumForm(form.getHospital_inner_sn(), form.getWarehouse_inner_sn(), detail.getVd_inner_sn(),
                    detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getBatch_number(), detail.getProduct_type());
            List<BatchNumResult> numResults = service.searchProductBatchNumList(x);
            if (numResults.isEmpty()) return ResultInfo.error("产品:"+detail.getProduct_name()+" 规格:"+detail.getSpecification() +"批号:"+detail.getBatch_number()+"缺少库存");
            BatchNumResult result = numResults.get(0);

            String batch_number=StringUtils.isEmpty(result.getBatch_number())?"空批号":result.getBatch_number();

            Map<String,Object> m=new HashMap<>();
            m.put("vendor_inner_sn_i", detail.getVd_inner_sn());
            m.put("product_inner_sn_i", detail.getProduct_inner_sn());
            m.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
            m.put("unit_inner_sn_i", detail.getUnit_inner_sn());

            BigDecimal bs;
            if (1==x.getProduct_type()){
                bs= pMapper.calculate_product_min_unit(m);

            }else {
                bs = dpMapper.calculate_product_min_unit(m);
            }
            //最小包装单位数量
            BigDecimal quantity = bs.multiply(detail.getQuantity());

            if (result.getQuantity().compareTo(quantity)<0){
                BigDecimal xx = quantity.subtract(result.getQuantity());
                throw new BusinessException("产品:"+detail.getProduct_name()+" 规格:"+detail.getSpecification() +"  批号:"+batch_number+"  超过可使用库存，超过数量:"+xx);
            }
        }
        //添加逻辑
        return ResultInfo.success(service.addInventoryUseRecord(form));
    }

    @SuppressWarnings("unchecked")
	@Log(title = "business.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "获取库存使用记录列表(分页,创建时间倒序)", notes = "获取库存使用记录列表(分页,创建时间倒序)，包含通过明细id检索单条明细")
    public TableDataInfo<AllotVoucherDetail> getAllotVoucherList(
            @Validated SearchInventoryUseRecordForm form) {
        return getDataTable(service.getInventoryUseRecords(form,true));
    }

    @Log(title = "business.allot", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ApiOperation(value = "修改库存使用记录单信息", notes = "修改库存使用记录单信息")
    public AjaxResult updateInventoryUseRecord(
            @RequestBody @Valid EditInventoryUseRecordForm form) {
        return toAjax(service.updateInventoryUseRecord(form));
    }
}
