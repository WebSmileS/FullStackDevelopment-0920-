package com.yrt.project.modular.warehouse.mapper;

import com.yrt.project.api.business.applyvoucher.vo.EditInventoryUseRecordForm;
import com.yrt.project.api.business.warehouse.vo.record.BatchNumResult;
import com.yrt.project.api.business.warehouse.vo.record.SearchBatchNumForm;
import com.yrt.project.api.business.warehouse.vo.record.SearchInventoryUseRecordForm;
import com.yrt.project.modular.warehouse.domain.InventoryUseRecord;
import com.yrt.project.modular.warehouse.domain.InventoryUseRecordDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 医院库存使用记录
 * @author chenbing
 *
 */
@Repository
public interface InventoryUseRecordMapper {
    List<BatchNumResult> searchDealerProductBatchNumList(SearchBatchNumForm form);

    List<BatchNumResult> searchVendorProductBatchNumList(SearchBatchNumForm form);

    InventoryUseRecord findInventoryUseRecordByRvInnerSn(long rv_inner_sn);

    List<InventoryUseRecord> findInventoryUseRecords(SearchInventoryUseRecordForm form);

    int updateInventoryUseRecord(Map<String,Object> params);

    int addInventoryUseRecord(Map<String, Object> params);

    int addInventoryUseRecordDetail(Map<String, Object> params1);

    InventoryUseRecord searchInventoryUseRecord(Map<String, Object> params);

    List<InventoryUseRecordDetail> searchInventoryUseRecordDetails(Map<String,Object> params);

    List<InventoryUseRecord> searchInventoryUseRecords(SearchInventoryUseRecordForm form);

    int deleteInventoryUseRecordDetails(Long rv_inner_sn);

    int reduceInventory(Map<String, Object> params1);
}
