package com.yrt.project.modular.advance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yrt.project.modular.advance.domain.AdvanceSale;
import com.yrt.project.modular.advance.dto.AddAdvanceSaleDtoReq;
import com.yrt.project.modular.advance.dto.GetAdvanceSaleConditionDtoReq;
import com.yrt.project.modular.advance.dto.GetAdvanceSaleDtoResp;
import com.yrt.project.modular.advance.dto.SalesDetailDtoReq;

import java.util.List;

public interface AdvanceSaleService extends IService<AdvanceSale> {


    GetAdvanceSaleDtoResp addOrUpdateAdvanceSale(AddAdvanceSaleDtoReq advanceSaleDto);


    /**
     * 保存预售单和明细出错
     *
     * @param advanceSaleId
     * @return
     */
    boolean checkAdvanceSale(Long advanceSaleId);

    /**
     * 查询预售单列表
     *
     * @param conditionDto
     * @return
     */
    List<GetAdvanceSaleDtoResp> getAdvanceSaleList(GetAdvanceSaleConditionDtoReq conditionDto);

    /**
     * 转化预售单
     * @param detailDtoReqs
     * @param optFlag 操作标识 0回库，1销售
     * @return
     */
    boolean transform(List<SalesDetailDtoReq> detailDtoReqs, int optFlag);

    /**
     * 删除预售单
     * @param advanceSaleId
     */
    void deleteAdvanceSale(Long advanceSaleId);
}


