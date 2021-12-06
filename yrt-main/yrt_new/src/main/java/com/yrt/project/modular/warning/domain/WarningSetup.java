package com.yrt.project.modular.warning.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public abstract class WarningSetup implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer cew_day = 5;

}
