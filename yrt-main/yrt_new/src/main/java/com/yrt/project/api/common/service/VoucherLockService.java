package com.yrt.project.api.common.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//@Component
//@Scope("singleton")
public class VoucherLockService {

	private static List<String> voucherLockList = Collections.synchronizedList(new ArrayList<>());
	
	public static synchronized boolean isExist(String id) {
		if(voucherLockList.contains(id)){
			return true;
		}
		voucherLockList.add(id);
		System.err.println(voucherLockList);
		return false;
	}
	
	public static synchronized void remove(String id) {
		voucherLockList.remove(id);
	}
}
