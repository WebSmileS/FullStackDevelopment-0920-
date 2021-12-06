package com.yrt.project.modular.surgery.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrthopaedicType implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long type_inner_sn;
	private String name;
}
