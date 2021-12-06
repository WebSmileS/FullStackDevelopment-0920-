package com.yrt.common.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PushBody implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long OID;
	private String ObjectID;
	private int SystemType;
	private String Title;
	private int Type;
	private List<Long> UIDS = new ArrayList<>();
	private List<String> Params = new ArrayList<>();
	
	public void addUids(Long uid) {
		UIDS.add(uid);
	}
	
	public void addParams(String param) {
		Params.add(param);
	}
	
	public static PushBody of(Long oid, int systemType, String orgName, String sn) {
		PushBody body = new PushBody();
		body.setOID(oid);
		body.setObjectID(sn);
		body.setSystemType(systemType);
		body.addParams(orgName);
		body.addParams(sn);
		return body;
	}
	
	public static PushBody of(Long oid, int systemType, String ...params) {
		PushBody body = new PushBody();
		body.setOID(oid);
		body.setSystemType(systemType);
		for (String string : params) {
			body.addParams(string);
		}
		return body;
	}
}
