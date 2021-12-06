package com.yrt.project.modular.premium.service;

import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.premium.domain.PremiumRule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yrt.project.modular.warehouse.domain.GrnDetail;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;

import java.util.List;

public interface PremiumRuleService extends IService<PremiumRule>{
    /**
     * 设置溢价比例和售价
     * @param details
     * @return
     */
    List<TransferVoucherDetail> setPercentAndSalesPrice1(List<TransferVoucherDetail> details);

    /**
     * 设置溢价比例和售价
     * @param details
     * @return
     */
    List<GrnDetail> setPercentAndSalesPrice2(List<GrnDetail> details);


    /**
     * 设置溢价比例和售价,供已发放页面的申领单使用，由于申领单没有单价，所以需要先根据产品查出单价
     * @param details
     * @return
     */
    List<HospitalGrantVoucherDetail> setPercentAndSalesPrice4(List<HospitalGrantVoucherDetail> details);



}
