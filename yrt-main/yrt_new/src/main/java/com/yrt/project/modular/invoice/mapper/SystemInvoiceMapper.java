package com.yrt.project.modular.invoice.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.invoice.domain.*;

public interface SystemInvoiceMapper {

	public List<SystemInvoice> selectSystemInvoiceList(Map<String,Object> params);
	public List<SystemInvoiceDetail> selectSystemInvoiceDetailList(Map<String,Object> params);
	public SystemInvoice selectSystemInvoiceInfo(Map<String,Object> params);
	
	
	public void insertSystemInvoice(SystemInvoice params);
	public void updateSystemInvoice(Map<String,Object> params);
	public void deteleSystemInvoice(Map<String,Object> params);
	
	public void insertSystemInvoiceDetail(SystemInvoiceDetail params);
	public void deteleSystemInvoiceDetail(Map<String,Object> params);
}
