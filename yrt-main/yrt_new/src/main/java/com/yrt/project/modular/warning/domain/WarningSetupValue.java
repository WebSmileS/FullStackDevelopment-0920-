package com.yrt.project.modular.warning.domain;

import lombok.Data;

@Data
public class WarningSetupValue {

	private CertWarningSetup certWarningSetup;
	
	private ProductMdrfWarningSetup productMdrfWaringSetup;
	
	private ProductValidityWarningSetup productValidityWarningSetup;
}
