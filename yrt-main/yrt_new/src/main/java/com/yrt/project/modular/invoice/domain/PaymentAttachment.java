package com.yrt.project.modular.invoice.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PaymentAttachment {

	private Long id;
	
	private Long payment_id;
	
	private String url;
	
	private String file_name;
	
	private String created_by;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date created_time;
}
