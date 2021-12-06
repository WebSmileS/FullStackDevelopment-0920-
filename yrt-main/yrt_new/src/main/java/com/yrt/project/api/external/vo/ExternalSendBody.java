package com.yrt.project.api.external.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ExternalSendBody implements Serializable {
    private static final long serialVersionUID = 1L;

    private String as_code;
    
    private Object as_data;
    
    public static ExternalSendBody of(String code, Object date) {
    	ExternalSendBody sendBody = new ExternalSendBody();
    	sendBody.setAs_code(code);
    	sendBody.setAs_data(date);
    	return sendBody;
    }
    
}
