package com.yrt.common.utils;

import java.util.List;

import com.yrt.common.dict.SystemType;
import com.yrt.project.modular.organization.domain.OrgDept;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;

import lombok.Data;

@Data(staticConstructor = "of")
public class ContextUserInfo {

	private final long uid;

	private final long orgId;
	
	private final SystemType systemType;

	private final OrgEmployee employee;

	private final List<OrgDept> deptList;

	private final List<WarehouseModel> warehouseModelList;
}
