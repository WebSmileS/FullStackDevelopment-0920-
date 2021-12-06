package com.yrt.project.modular.advance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yrt.project.modular.advance.domain.AdvanceSaleDetail;
import com.yrt.project.modular.advance.mapper.AdvanceSaleDetailMapper;
import com.yrt.project.modular.advance.service.AdvanceSaleDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvanceSaleDetailServiceImpl extends ServiceImpl<AdvanceSaleDetailMapper, AdvanceSaleDetail> implements AdvanceSaleDetailService {

    @Override
    public List<AdvanceSaleDetail> getDetailsByAdvanceSaleId(long advanceSaleId) {
        LambdaQueryWrapper<AdvanceSaleDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdvanceSaleDetail::getAdvanceSaleId, advanceSaleId);
        List<AdvanceSaleDetail> details = this.list(wrapper);
        return details;
    }
}


