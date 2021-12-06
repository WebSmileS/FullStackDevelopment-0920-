package com.yrt.common.client;
public class HttpResult {

    // 响应码
    private Integer code;

    // 响应体
    private ResultBody body;


    public HttpResult() {
        super();
    }

    public HttpResult(Integer code, ResultBody body) {
        super();
        this.code = code;
        this.body = body;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public ResultBody getBody() {
        return body;
    }

    public void setBody(ResultBody body) {
        this.body = body;
    }
}