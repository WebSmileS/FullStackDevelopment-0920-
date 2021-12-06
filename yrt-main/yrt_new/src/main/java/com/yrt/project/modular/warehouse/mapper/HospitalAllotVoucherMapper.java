package com.yrt.project.modular.warehouse.mapper;

import com.yrt.project.modular.warehouse.domain.AllotVoucher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 医院调拨单记录
 * @author chenbing
 *
 */
@Repository
public interface HospitalAllotVoucherMapper extends AllotVoucherMapper{

    AllotVoucher searchByAvInnerSn(Long av_inner_sn);

    int updateApplyStatus(Map<String,Object> params);

    int updateApplyDetailStatus(Map<String,Object> params);

    List<Long> findGrnIdsByAvInnerSn(Map<String,Object> params);

    int delGrnDetailsByGrnIds1(Map<String,Object> params);

    int delGrnDetailsByGrnIds2(Map<String,Object> params);

    int delGrnByGrnIds(Map<String,Object> params);
}
