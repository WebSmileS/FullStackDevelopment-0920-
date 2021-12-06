package com.yrt.framework.web.domain;

import io.swagger.annotations.ApiModelProperty;

public class Result {

	@ApiModelProperty(value = "自定义状态码 0-成功 1-失败 和其他http状态码")
	private int code;
	@ApiModelProperty(value = "提示信息")
	private String msg;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	
	/*
	 * public static Result success(){ return Result.success("操作成功"); }
	 * 
	 * public static Result success(String msg){ Result json = new Result();
	 * json.setCode(0); json.setMsg(msg); return json; }
	 * 
	 * public static Result error(){ return Result.error(1, "操作失败"); }
	 * 
	 * public static Result error(String msg){ return Result.error(500, "操作失败"); }
	 * 
	 * public static Result error(int code, String msg){ Result json = new Result();
	 * json.setCode(1); json.setMsg(msg); return json; }
	 */
	
}
