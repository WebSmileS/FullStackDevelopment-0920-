package com.yrt.project.api.business.warehouse.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.yrt.project.api.business.warehouse.vo.sas.*;
import com.yrt.project.modular.warehouse.domain.Sas;

public interface ISasService {

	public List<Sas> searchSasList(SearchSasForm form);
	
	public Sas getSasInfo(SasIDForm form);

	public Sas addSas(@Valid AddSasForm form);

	public int deleteSas(SasIDForm form);

	public Sas checkSas(CheckSasForm form);

	public List<Map<String,Object>> getNoHaveSasProductList(SasProductForm form);

	public int editSas(@Valid EditSasForm form);

	public Map<String,Object> getProductListByWarehouseBarcode(ScanBarcodeForm form);

	public Map<String,Object> getStorageListByProductBarcode(ScanBarcodeForm form);

	public Map<String,Object> getCheckStorageByProductBarcode(CheckBarcodeForm form);

	public int stockProduct(@Valid StockProductForm form);

	public int stopSas(SasIDForm form);
}
