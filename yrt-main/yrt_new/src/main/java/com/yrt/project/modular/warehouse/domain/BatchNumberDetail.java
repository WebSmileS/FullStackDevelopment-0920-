package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class BatchNumberDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	private String batch_number;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	private BigDecimal quantity;

	private BigDecimal average_price;
}
