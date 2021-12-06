package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

public interface DealerDealerOdoMapper extends OdoMapper {
    List<Map<String, Object>> selectOdoProductList4InvoiceNew(Map<String, Object> params);
}
