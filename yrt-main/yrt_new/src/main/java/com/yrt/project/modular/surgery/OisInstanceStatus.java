package com.yrt.project.modular.surgery;

import com.yrt.common.dict.BaseIntEnum;

/**
 * 计划手术包实例 状态
 */
public enum OisInstanceStatus implements BaseIntEnum {

	NORMAL(0, "确认"), // 医院/助理 初始状态
	LOCKED(1, "备货"), // (normal->locked)助理 预锁定库存(虚拟批次号) 指定 仓库
	OUT(2, "已出库"),  // (locked->out)仓库 出库
	BACK(3, "回库"),   // (out->back)仓库 填回库数量 明细批次号
	SALE_PENDING(4, "待确认结算"),  // (back->pending)助理 填单价和 销售/报损-原因
	SALE_CONFIRMED(5, "待出单"),    // (pending->confirmed)医院确认 销售 数据
	CLOSED(6, "关闭"),              // (pending->closed) 助理  完成手术包 并 生成销售单/出库单/报损单
	;

	private final int value;
	private final String text;

	private OisInstanceStatus(int value, String text) {
		this.value = value;
		this.text = text;
	}

	@Override
	public int value() {
		return value;
	}

	@Override
	public String text() {
		return text;
	}
	
	public boolean canDelete() {
		return  this == NORMAL; // || this==LOCKED;
	}
	
	public boolean canEdit() {
		return this == NORMAL; // || this==LOCKED;
	}

}
