package com.yrt.project.modular.advance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yrt.project.modular.advance.domain.AdvanceSaleDetail;

import java.util.List;

public interface AdvanceSaleDetailService extends IService<AdvanceSaleDetail> {

    List<AdvanceSaleDetail> getDetailsByAdvanceSaleId(long advanceSaleId);

}


