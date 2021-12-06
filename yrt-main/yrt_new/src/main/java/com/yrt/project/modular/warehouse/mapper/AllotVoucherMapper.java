package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yrt.project.modular.warehouse.domain.AllotVoucher;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;

public interface AllotVoucherMapper {
    int addAllotVoucher(Map<String, Object> params);

    void addAllotVoucherDetail(Map<String, Object> params1);

    List<AllotVoucherDetail> searchAllotVoucherList(Map<String, Object> params);

    AllotVoucher searchAllotVoucher(Map<String,Object> params);

    List<AllotVoucherDetail> searchAllotDetails(Long av_inner_sn);

    List<AllotVoucher> selectTransferAllotList(Map<String, Object> params);

    List<AllotVoucherDetail> selectOdoTransferAllotDetailList(Map<String, Object> params);

    List<AllotVoucherDetail> selectGrnTransferAllotDetailList(Map<String, Object> params);

    Integer getAllotDetailStatus(long parseLong);

    int updateAllotVoucher(Map<String, Object> params);

    void deleteAllotDetails(long parseLong);

    void deleteAllotVoucherDetail(long parseLong);

    List<AllotVoucherDetail> findOdo(Map<String,Object> params);

    List<AllotVoucherDetail> findGrn(Map<String,Object> params);

    List<AllotVoucherDetail> findFromOdo(Map<String,Object> params);

    List<AllotVoucherDetail> findFromGrn(Map<String,Object> params);

    int updateStatusAtPutOut(long detail_inner_sn);

    int updateStatusAtPutIn(long detail_inner_sn);

    Long findOdoIds(Map<String, Object> params);

    Long findGrnIds(Map<String, Object> params);

    List<AllotVoucherDetail> findOdoDetails(@Param("org_id") Long org_id,@Param("odo_inner_sn") Long odo_inner_sn);

    List<AllotVoucherDetail> findGrnDetails(@Param("org_id") Long org_id,@Param("grn_inner_sn") Long grn_inner_sn);
}
