package com.yrt.common.client;

import lombok.Data;

@Data
public class ResultBody{
	private Integer result;
	private String result_msg;
	
	public ResultBody(Integer result, String result_msg) {
		this.result = result;
		this.result_msg = result_msg;
	}
}