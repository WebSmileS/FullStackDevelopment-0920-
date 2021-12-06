package com.yrt.project.api.platform.service;

public interface IOrgAuthService {

	public int orgAuthPass(int systemType, Long id);
	
	public int orgAuthNoPass(int systemType, Long id, String reason);
	
}
