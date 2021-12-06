package com.yrt.system.quartz.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yrt.project.modular.contract.domain.Contract;
import com.yrt.project.modular.contract.mapper.DealerDealerContractMapper;
import com.yrt.project.modular.contract.mapper.DealerVendorContractMapper;
import com.yrt.project.modular.contract.mapper.HospitalDealerContractMapper;
import com.yrt.project.modular.contract.mapper.HospitalVendorContractMapper;


@Component("contractTask")
public class ContractTask {
		
	@Autowired
	private DealerDealerContractMapper ddMapper;
	
	@Autowired
	private DealerVendorContractMapper dvMapper;
	
	@Autowired
	private HospitalDealerContractMapper hdMapper;
	
	@Autowired
	private HospitalVendorContractMapper hvMapper;
	
	
	public void contract() throws Exception{
		strat();
		stop();
	}
	
	
	public void strat() throws Exception {
		System.err.println("开始履行合同，定时任务开始");
		startDealerDealerContract();
		startDealerVendorContract();
		startHospitalDealerContract();
		startHospitalVendorContract();
		System.err.println("开始履行合同，定时任务结束");
	}
	
	public void stop()throws Exception{
		System.err.println("终止过期合同,开始");
		stopDealerDealerContract();	
		stopDealerVendorContract();
		stopHospitalDealerContract();
		stopHospitalVendorContract();
		System.err.println("终止过期合同,结束");
	}
	
	public void stopDealerDealerContract() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Date date = df.parse(df.format(new Date()));
		List<Contract> list = ddMapper.selectAllContract();
		for(Contract form:list) {
			Date endDate=df.parse(df.format(form.getEnd_date()));
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY,23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 59);
			Date ReallyEndDate = cal.getTime();
			if (endDate != null && ReallyEndDate.getTime() < date.getTime()) {
				params.put("contract_inner_sn", form.getContract_inner_sn());
				params.put("dealer_a_inner_sn", form.getPartA());
				params.put("dealer_b_inner_sn", form.getPartB());
				ddMapper.stopContract(params);
			}
		}
	}
	
	public void stopDealerVendorContract() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Date date = df.parse(df.format(new Date()));
		List<Contract>list = dvMapper.selectAllContract();
		for(Contract form:list) {
			Date endDate=df.parse(df.format(form.getEnd_date()));
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY,23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 59);
			Date ReallyEndDate = cal.getTime();
			if (endDate != null && ReallyEndDate.getTime() < date.getTime()) {
				params.put("contract_inner_sn", form.getContract_inner_sn());
				params.put("dealer_inner_sn", form.getPartA());
				params.put("vendor_inner_sn", form.getPartB());
				dvMapper.stopContract(params);
			}
		}
	}
	
	public void stopHospitalDealerContract() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Date date = df.parse(df.format(new Date()));
		List<Contract>list = hdMapper.selectAllContract();
		for(Contract form:list) {
			Date endDate=df.parse(df.format(form.getEnd_date()));
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY,23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 59);
			Date ReallyEndDate = cal.getTime();
			if (endDate != null && ReallyEndDate.getTime() < date.getTime()) {
				params.put("contract_inner_sn", form.getContract_inner_sn());
				params.put("hospital_inner_sn", form.getPartA());
				params.put("dealer_inner_sn", form.getPartB());
				hdMapper.stopContract(params);
			}
		}
	}
	
	public void stopHospitalVendorContract() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Date date = df.parse(df.format(new Date()));
		List<Contract>list = hvMapper.selectAllContract();
		for(Contract form:list) {
			Date endDate=df.parse(df.format(form.getEnd_date()));
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY,23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 59);
			Date ReallyEndDate = cal.getTime();
			if (endDate != null && ReallyEndDate.getTime() < date.getTime()) {
				params.put("contract_inner_sn", form.getContract_inner_sn());
				params.put("hospital_inner_sn", form.getPartA());
				params.put("vendor_inner_sn", form.getPartB());
				hvMapper.stopContract(params);
			}
		}
	}
	
	/**
	 * 以下为开始的
	 * @throws Exception
	 */
	
	public void startDealerDealerContract() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Date date = df.parse(df.format(new Date()));
		List<Contract> list = ddMapper.selectAllContract();
		for(Contract form:list) {
			Date startDate=df.parse(df.format(form.getBegin_date()));
			if (startDate != null && startDate.getTime() <= date.getTime()) {
				params.put("contract_inner_sn", form.getContract_inner_sn());
				params.put("dealer_a_inner_sn", form.getPartA());
				params.put("dealer_b_inner_sn", form.getPartB());
				ddMapper.startContract(params);
			}
		}
	}
	
	public void startDealerVendorContract() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Date date = df.parse(df.format(new Date()));
		List<Contract> list = dvMapper.selectAllContract();
		for(Contract form:list) {
			Date startDate=df.parse(df.format(form.getBegin_date()));
			if (startDate != null && startDate.getTime() <= date.getTime()) {
				params.put("contract_inner_sn", form.getContract_inner_sn());
				params.put("dealer_inner_sn", form.getPartA());
				params.put("vendor_inner_sn", form.getPartB());
				dvMapper.startContract(params);
			}
		}
	}
	
	public void startHospitalDealerContract() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Date date = df.parse(df.format(new Date()));
		List<Contract> list = hdMapper.selectAllContract();
		for(Contract form:list) {
			Date startDate=df.parse(df.format(form.getBegin_date()));
			if (startDate != null && startDate.getTime() <= date.getTime()) {
				params.put("contract_inner_sn", form.getContract_inner_sn());
				params.put("dealer_inner_sn", form.getPartA());
				params.put("vendor_inner_sn", form.getPartB());
				hdMapper.startContract(params);
			}
		}
	}
	
	public void startHospitalVendorContract() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Date date = df.parse(df.format(new Date()));
		List<Contract> list = hvMapper.selectAllContract();
		for(Contract form:list) {
			Date startDate=df.parse(df.format(form.getBegin_date()));
			if (startDate != null && startDate.getTime() <= date.getTime()) {
				params.put("contract_inner_sn", form.getContract_inner_sn());
				params.put("dealer_inner_sn", form.getPartA());
				params.put("vendor_inner_sn", form.getPartB());
				hvMapper.startContract(params);
			}
		}
	}
}
