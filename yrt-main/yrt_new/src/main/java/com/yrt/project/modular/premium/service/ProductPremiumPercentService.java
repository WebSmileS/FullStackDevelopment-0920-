package com.yrt.project.modular.premium.service;

import com.yrt.project.api.business.warehouse.vo.grn.GrnDetailForm;
import com.yrt.project.modular.premium.domain.ProductPremiumPercent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductPremiumPercentService extends IService<ProductPremiumPercent>{

    /**
     * 保存或者修改产品的溢价比例
     * @param forms
     */
    void saveOrUpdateProductPremiumPercent(List<GrnDetailForm> forms);
}
