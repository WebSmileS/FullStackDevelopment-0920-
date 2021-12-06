package com.yrt.project.modular.warehouse.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DealerDealerGrnMapper extends GrnMapper {
    List<Map<String,Object>> selectGrnProductList4InvoiceNew(Map<String,Object> params);
}
