package com.yrt.project.modular.advance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yrt.common.constant.Constants;
import com.yrt.common.dict.*;
import com.yrt.common.utils.*;
import com.yrt.common.utils.bean.BeanUtils;
import com.yrt.project.api.business.warehouse.service.IOdoService;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoDetailForm;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.vo.BaseQuantities;
import com.yrt.project.modular.advance.domain.AdvanceSale;
import com.yrt.project.modular.advance.domain.AdvanceSaleDetail;
import com.yrt.project.modular.advance.dto.*;
import com.yrt.project.modular.advance.mapper.AdvanceSaleMapper;
import com.yrt.project.modular.advance.service.AdvanceSaleDetailService;
import com.yrt.project.modular.advance.service.AdvanceSaleService;
import com.yrt.project.modular.inventory.domain.MinUnitInventory;
import com.yrt.project.modular.inventory.dto.ProductUniqueProperties;
import com.yrt.project.modular.inventory.service.impl.MinUnitInventoryService;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.domain.ProductUnitConvertKey;
import com.yrt.project.modular.warehouse.mapper.InventoryLockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvanceSaleServiceImpl extends ServiceImpl<AdvanceSaleMapper, AdvanceSale> implements AdvanceSaleService {

    private final AdvanceSaleDetailService detailService;
    private final VoucherNumberService numberUtils;
    private final InventoryLockMapper locker;
    private final CommService commService;
    private final IOdoService odoService;
    private final MinUnitInventoryService inventoryService;

    @Override
    @Transactional
    public GetAdvanceSaleDtoResp addOrUpdateAdvanceSale(AddAdvanceSaleDtoReq advanceSaleDto) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        try {
            // true新增，false修改
            boolean isAdd = advanceSaleDto.getId() == null ? true : false;
            // 保存主表和明细表
            AdvanceSale advanceSale = BeanUtils.clone(advanceSaleDto, AdvanceSale.class);
            if (!isAdd) {
                // 修改，先删除原来的明细
                AdvanceSale advanceSaleCur =  this.getById(advanceSaleDto.getId());
                advanceSale.setAdvanceSaleNo(advanceSaleCur.getAdvanceSaleNo());
                advanceSale.setCreateUserId(advanceSaleCur.getCreateUserId());
                advanceSale.setCreateUserName(advanceSaleCur.getCreateUserName());
                advanceSale.setSellersId(curUser.getOrgId());
                advanceSale.setSellersName(curUser.getEmployee().getOrg_name());
                advanceSale.setAuditStatus(advanceSaleCur.getAuditStatus());
                LambdaQueryWrapper<AdvanceSaleDetail> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(AdvanceSaleDetail::getAdvanceSaleId, advanceSaleDto.getId());
                detailService.remove(wrapper);
            }else{
                // 新增
                String sn = numberUtils.getMaxSn(VoucherType.ADVANCE_ORDER, ServletUtils.getCurUserSystemType(), curUser.getOrgId());
                advanceSale.setAdvanceSaleNo(sn);
                advanceSale.setSellersId(curUser.getOrgId());
                advanceSale.setSellersName(curUser.getEmployee().getOrg_name());
                advanceSale.setCreateUserId(curUser.getUid());
                advanceSale.setCreateUserName(curUser.getEmployee().getName());
                advanceSale.setAuditStatus(AdvanceSaleCheckStatusEnum.UNCHECKED);
            }

            Boolean saveFlag = this.saveOrUpdate(advanceSale);
            Utils.assertTrue(saveFlag, "保存失败");
            saveDetail(advanceSaleDto, advanceSale);
            GetAdvanceSaleDtoResp dto = BeanUtils.clone(advanceSale, GetAdvanceSaleDtoResp.class);
            dto.setStatus(AdvanceSaleStatusEnum.UNDONE);
            return dto;
        } catch (Exception e) {
            log.error("保存预售单和明细出错", e);
            throw new RuntimeException("保存预售单和明细出错");
        }
    }

    @Override
    @Transactional
    public boolean checkAdvanceSale(Long advanceSaleId) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        AdvanceSale advanceSale = this.getById(advanceSaleId);
        LambdaQueryWrapper<AdvanceSaleDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdvanceSaleDetail::getAdvanceSaleId, advanceSaleId);
        List<AdvanceSaleDetail> details = detailService.list(wrapper);

        // 检查库存余量
        for (AdvanceSaleDetail detail : details) {
            ProductUniqueProperties productUniqueProperties = new ProductUniqueProperties(curUser.getOrgId(), detail.getProductId(),
                    detail.getProductVendorId(), detail.getSpecificationId(), advanceSale.getWarehouseId(), detail.getBatchNumber(), detail.getProductName(), detail.getProductVendorName(), detail.getSpecificationName(), advanceSale.getWarehouseName());
            MinUnitInventory minUnitInventory = inventoryService.getMinUnitInventory(ServiceTypeUtil.getFirstType(advanceSale.getBillsType()), productUniqueProperties);
            BigDecimal requestQuantity = detail.getQuantity().multiply(Constants.QUANTITY_INT_SCALE);

            if (requestQuantity.compareTo(BigDecimal.ZERO) == 0 || requestQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException(String.format("{}的数量必须大于0", detail.getProductName()));
            }

            // convert to min-unit
            BigDecimal requestQuantityMinUnit = locker.getUnitToMinUnitScale(ProductUnitConvertKey.of(PrdType.VENDOR_PRD,
                    detail.getProductVendorId(), detail.getProductId(), detail.getSpecificationId(), detail.getUnitId()))
                    .multiply(requestQuantity);

            if (requestQuantityMinUnit.compareTo(new BigDecimal(minUnitInventory.getQuantity())) > 0) {
                String message = String.format("库存不足: %s>>%s>>%s, 批号：%s, 请求扣减量：%s，请求最小单位扣减量：%s，库存余量（最小单位）%s",
                        detail.getProductName(), detail.getSpecificationName(), detail.getUnitName(), detail.getBatchNumber(),
                        detail.getQuantity().longValue(), requestQuantityMinUnit.divide(Constants.QUANTITY_INT_SCALE), minUnitInventory.getQuantity() / 1000);
                throw new RuntimeException(message);
            }
        }
        try {
            // 减少库存
            updateInventoryQuantity(advanceSale, curUser, details, "sub");

            // 修改状态为已确认
            advanceSale.setAuditStatus(AdvanceSaleCheckStatusEnum.APPROVED);
            advanceSale.setAuditTime(new Date());
            advanceSale.setAuditUserId(curUser.getUid());
            advanceSale.setAuditUserName(curUser.getEmployee().getName());
            this.updateById(advanceSale);
        } catch (Exception e) {
            log.error("预售单确认出错", e);
            throw new RuntimeException("预售单确认出错");
        }
        return true;
    }

    /**
     * 修改库存
     *
     * @param advanceSale
     * @param curUser
     * @param details
     * @param type        add加库存，sub减库存
     */
    private void updateInventoryQuantity(AdvanceSale advanceSale, ContextUserInfo curUser, List<AdvanceSaleDetail> details, String type) {
        for (AdvanceSaleDetail detail : details) {
            BigDecimal measure = commService.getProductUnitMeasure(detail.getProductVendorId(), detail.getProductId(),
                    detail.getSpecificationId(), detail.getUnitId(), 1);

            BigDecimal quantity = detail.getQuantity().multiply(measure);
            BigDecimal minUnitQuantity = quantity.multiply(new BigDecimal(1000));
            if ("sub".equals(type)) {
                minUnitQuantity = minUnitQuantity.multiply(new BigDecimal(-1));
            }

            ProductUniqueProperties productUniqueProperties = new ProductUniqueProperties(curUser.getOrgId(), detail.getProductId(),
                    detail.getProductVendorId(), detail.getSpecificationId(), advanceSale.getWarehouseId(), detail.getBatchNumber(), detail.getProductName(), detail.getProductVendorName(), detail.getSpecificationName(), advanceSale.getWarehouseName());

            inventoryService.changeQuantity(ServiceTypeUtil.getFirstType(advanceSale.getBillsType()), productUniqueProperties, minUnitQuantity.longValue());
        }
    }

    /**
     * 保存预售明细
     *
     * @param advanceSaleDto
     * @param advanceSale
     * @return
     */
    private List<AdvanceSaleDetail> saveDetail(AddAdvanceSaleDtoReq advanceSaleDto, AdvanceSale advanceSale) {
        // 新增
        List<AddAdvanceSaleDetailDtoReq> dtoDetails = advanceSaleDto.getDetails();
        List<AdvanceSaleDetail> details = dtoDetails.stream().map(e -> {
            AdvanceSaleDetail detail = BeanUtils.clone(e, AdvanceSaleDetail.class);
            detail.setAdvanceSaleId(advanceSale.getId());
            detail.setStatus(AdvanceSaleStatusEnum.UNDONE);
            return detail;
        }).collect(Collectors.toList());
        detailService.saveBatch(details);
        return details;
    }

    @Override
    public List<GetAdvanceSaleDtoResp> getAdvanceSaleList(GetAdvanceSaleConditionDtoReq conditionDto) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(StringUtils.isNotEmpty(conditionDto.getAdvanceSaleNo()),AdvanceSale.COL_ADVANCE_SALE_NO, conditionDto.getAdvanceSaleNo());
        queryWrapper.eq(conditionDto.getBuyerId() != null && conditionDto.getBuyerId() != 0L, AdvanceSale.COL_BUYER_ID, conditionDto.getBuyerId());
        queryWrapper.like(StringUtils.isNotEmpty(conditionDto.getBuyerName()), AdvanceSale.COL_BUYER_NAME, conditionDto.getBuyerName());
        queryWrapper.eq(AdvanceSale.COL_SELLERS_ID, curUser.getOrgId());
        queryWrapper.eq(conditionDto.getAuditStatus()!=null, AdvanceSale.COL_AUDIT_STATUS, conditionDto.getAuditStatus());
        List<AdvanceSale> list = this.list(queryWrapper);
        List<GetAdvanceSaleDtoResp> advanceSales = list.stream().map(e -> {
            GetAdvanceSaleDtoResp saleRespDto = BeanUtils.clone(e, GetAdvanceSaleDtoResp.class);
            // 查询销售状态
            QueryWrapper detailQw = new QueryWrapper();
            detailQw.eq(AdvanceSaleDetail.COL_ADVANCE_SALE_ID, e.getId());
            List<AdvanceSaleDetail> details = detailService.list(detailQw);
            saleRespDto.setStatus(GetAdvanceSaleDtoResp.getSaleStatusByDetails(details));
            return saleRespDto;
        }).collect(Collectors.toList());
        return advanceSales;
    }

    @Override
    @Transactional
    public boolean transform(List<SalesDetailDtoReq> detailDtoReq, int optFlag) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        List<AdvanceSaleDetail> details = detailService.listByIds(detailDtoReq.stream().map(SalesDetailDtoReq::getSaleDetailId).collect(Collectors.toList()));
        List<AdvanceSaleDetail> detailsFilter = details.stream().filter(e -> e.getStatus().equals(AdvanceSaleStatusEnum.COMPLETED)).collect(Collectors.toList());
        if (detailsFilter.size() > 0) {
            throw new RuntimeException("只能操作未完成的数据");
        }

        // 检查可回库数量是否足够
        for (AdvanceSaleDetail detail : details) {
            // 剩余可操作量
            BigDecimal surplus = detail.getQuantity().subtract(new BigDecimal(detail.getSoldQuantity())).subtract(new BigDecimal(detail.getRespondQuantity()));
            for (SalesDetailDtoReq detailsReq : detailDtoReq) {
                if (detail.getId().equals(detailsReq.getSaleDetailId())) {
                    if (surplus.compareTo(detailsReq.getQuantity()) == -1) {
                        throw new RuntimeException(String.format("产品[%s]可操作数量不足， 本次操作数量为[%s], 可操作数量为[%s]", detail.getProductName(), detailsReq.getQuantity(), detail.getQuantity()));
                    } else if (surplus.compareTo(detailsReq.getQuantity()) == 0) {
                        detail.setStatus(AdvanceSaleStatusEnum.COMPLETED);
                    } else {
                        detail.setStatus(AdvanceSaleStatusEnum.UNDONE);
                    }
                    if (optFlag == 0) {
                        detail.setRespondQuantity(detail.getRespondQuantity() + detailsReq.getQuantity().intValue());
                    } else {
                        detail.setSoldQuantity(detail.getSoldQuantity() + detailsReq.getQuantity().intValue());
                    }
                }
            }
        }

        // 修改明细表状态
        detailService.updateBatchById(details);

        // 分组
        Map<Long, List<AdvanceSaleDetail>> detailsMap = details.stream().collect(Collectors.groupingBy(AdvanceSaleDetail::getAdvanceSaleId));
        detailsMap.forEach((k, v) -> {
            AdvanceSale advanceSale = this.getById(k);
            if (advanceSale.getAuditStatus().equals(AdvanceSaleCheckStatusEnum.UNCHECKED)) {
                throw new RuntimeException(String.format("预售单[%s]未确认，只能操作确认状态的数据"));
            }

            // 设置操作数量
            v.forEach(e -> {
                detailDtoReq.forEach(e2 -> {
                    if (e.getId().equals(e2.getSaleDetailId())) {
                        e.setQuantity(e2.getQuantity());
                    }
                });
            });

            if (optFlag == 0) {
                // 恢复库存
                updateInventoryQuantity(advanceSale, curUser, v, "add");
            }else{
                // 生成出库单据
                AddOdoForm form = packageOdoForm(details, curUser, detailDtoReq);
                String sn = numberUtils.getMaxSn(VoucherType.PUT_OUT, ServletUtils.getCurUserSystemType(), form.getOrg_id());
                Map<InventoryKey, BaseQuantities> toBeSpecialLock = new HashMap<>();
                Map<InventoryKey, BaseQuantities> toBePreLock = new HashMap<>();
                odoService.insertOdo(curUser.getEmployee(), curUser.getSystemType(), form, sn, toBeSpecialLock, toBePreLock);
            }
        });
        return true;
    }

    @Override
    @Transactional
    public void deleteAdvanceSale(Long advanceSaleId) {
        this.removeById(advanceSaleId);
        LambdaQueryWrapper<AdvanceSaleDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdvanceSaleDetail::getAdvanceSaleId, advanceSaleId);
        detailService.remove(wrapper);
    }

    private AddOdoForm packageOdoForm(List<AdvanceSaleDetail> details, ContextUserInfo curUser, List<SalesDetailDtoReq> detailDtoReq) {
        AdvanceSale advanceSale = this.getById(details.get(0).getAdvanceSaleId());
        AddOdoForm odoForm = new AddOdoForm();

        odoForm.setOrg_id(advanceSale.getSellersId());
        odoForm.setOrganization_inner_sn(advanceSale.getBuyerId());
        odoForm.setOrganization_name(advanceSale.getBuyerName());
        odoForm.setWarehouse_inner_sn(advanceSale.getWarehouseId());
        odoForm.setWarehouse_name(advanceSale.getWarehouseName());
        odoForm.setHandler_inner_sn(advanceSale.getAgentUserId());
        odoForm.setHandler_name(advanceSale.getAgentUserName());
        odoForm.setWarehouse_operator_inner_sn(advanceSale.getWarehouseUserId());
        odoForm.setWarehouse_operator_name(advanceSale.getWarehouseUserName());
        odoForm.setType_inner_sn(0l);
        odoForm.setType_name("销售出库");
        odoForm.setDepartment_inner_sn(advanceSale.getSaleDepartmentId());
        odoForm.setDepartment_name(advanceSale.getSaleDepartmentName());
        odoForm.setUse_department(advanceSale.getUseDepartment());
        odoForm.setStatus(2);
        odoForm.setOdoTime(new Date());
        odoForm.setOdo_sn(null);
        odoForm.setDescription("");
        odoForm.setBillsType(advanceSale.getBillsType());

        List<OdoDetailForm> detailForms = details.stream().map(e -> {
            SalesDetailDtoReq req = detailDtoReq.stream().filter(e2->e.getId().equals(e2.getSaleDetailId())).collect(Collectors.toList()).get(0);
            OdoDetailForm odoDetailForm = new OdoDetailForm();
            odoDetailForm.setDetail_inner_sn(e.getId());
            odoDetailForm.setProduct_vendor_inner_sn(e.getProductVendorId());
            odoDetailForm.setProduct_vendor_name(e.getProductVendorName());
            odoDetailForm.setProduct_inner_sn(e.getProductId());
            odoDetailForm.setProduct_name(e.getProductName());
            odoDetailForm.setSpecification_inner_sn(e.getSpecificationId());
            odoDetailForm.setSpecification(e.getSpecificationName());
            odoDetailForm.setUnit_inner_sn(e.getUnitId());
            odoDetailForm.setUnit_name(e.getUnitName());
            odoDetailForm.setUnit_price(req.getUnitPrice());
            odoDetailForm.setQuantity(e.getQuantity());
            odoDetailForm.setRate(req.getRate());
            odoDetailForm.setType(e.getType().value());
            odoDetailForm.setSort_number(e.getSortNumber());
            odoDetailForm.setBatch_number(e.getBatchNumber());
            odoDetailForm.setVoucher_type_c(RefVoucherType.ADVANCE_SALE);
            odoDetailForm.setVoucher_inner_sn_c(e.getAdvanceSaleId());
            odoDetailForm.setVoucher_detail_inner_sn_c(e.getId());
            odoDetailForm.setProduct_type(e.getProductType());

            // 获取库里这个产品的信息
            ProductUniqueProperties productUniqueProperties = new ProductUniqueProperties(curUser.getOrgId(), e.getProductId(),
                    e.getProductVendorId(), e.getSpecificationId(), advanceSale.getWarehouseId(), e.getBatchNumber(), e.getProductName(), e.getProductVendorName(), e.getSpecificationName(), advanceSale.getWarehouseName());
            MinUnitInventory minUnitInventory = inventoryService.getMinUnitInventory(ServiceTypeUtil.getFirstType(advanceSale.getBillsType()), productUniqueProperties);
            // 由于价格是从库里查询出来的，是最小包装单位的价格，所以这里要*单位换算，才是最终的单价
            BigDecimal measure = commService.getProductUnitMeasure(e.getProductVendorId(), e.getProductId(),
                    e.getSpecificationId(), e.getUnitId(), PrdType.VENDOR_PRD.value());
            odoDetailForm.setBuying_price(new BigDecimal(minUnitInventory.getAveragePrice()).multiply(measure).divide(new BigDecimal(10000), BigDecimal.ROUND_HALF_UP));
            odoDetailForm.setMdrf_inner_sn(e.getMdrfInnerSn());
            odoDetailForm.setMdrf_sn(e.getMdrfSn());
            odoDetailForm.setProduce_date(minUnitInventory.getProduceDate());
            odoDetailForm.setOverdue_date(minUnitInventory.getOverdueDate());
            odoDetailForm.setHealth_care_sn(e.getHealthCareSn());
            return odoDetailForm;
        }).collect(Collectors.toList());
        odoForm.setDetails(detailForms);
        return odoForm;
    }
}


