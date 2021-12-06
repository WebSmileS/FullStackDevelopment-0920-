package com.yrt.project.api.business.warehouse.vo.grn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.common.service.VoucherNumberService;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditGrnForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(hidden = true)
	@Autowired private VoucherNumberService numberUtils;
	
	/** 当前机构ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	@ApiModelProperty(value="入库单ID",required=true)
	@NotNull(message = "入库单ID不能为空")
	private Long grn_inner_sn;
	/** 相关机构ID */
	@ApiModelProperty(value="相关机构ID",required=false)
	private Long organization_inner_sn;
	private Long organization_inner_sn_old;
	@ApiModelProperty(value="相关机构名称",required=false)
	private String organization_name;
	private String organization_name_old;
	/** 仓库内部编号 */
	@ApiModelProperty(value="仓库ID",required=false)
	private Long warehouse_inner_sn;
	private Long warehouse_inner_sn_old;
	@ApiModelProperty(value="仓库名称",required=false)
	private String warehouse_name;
	private String warehouse_name_old;
	/** 经手人内部编号 */
	@ApiModelProperty(value="经手人ID",required=false)
	private Long handler_inner_sn;
	private Long handler_inner_sn_old;
	@ApiModelProperty(value="经手人名称",required=false)
	private String handler_name;
	private String handler_name_old;
	/** 仓库人员内部编号 */
	@ApiModelProperty(value="仓库人员ID",required=false)
	private Long warehouse_operator_inner_sn;
	private Long warehouse_operator_inner_sn_old;
	@ApiModelProperty(value="仓库人员名称",required=false)
	private String warehouse_operator_name;
	private String warehouse_operator_name_old;
	/** 类型内部编号 */
	@ApiModelProperty(value="入库单类型ID",required=false)
	private Long type_inner_sn;
	private Long type_inner_sn_old;
	@ApiModelProperty(value="入库单类型名称",required=false)
	private String type_name;
	private String type_name_old;
	/** 开单部门内部编号 */
	@ApiModelProperty(value="开单部门ID",required=false)
	private Long department_inner_sn;
	private Long department_inner_sn_old;
	@ApiModelProperty(value="开单部门名称",required=false)
	private String department_name;
	private String department_name_old;
	/** 状态 */
	@ApiModelProperty(value="状态 0-正式 1-已审核 2-已入账(已过账)",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	/** 入库单编号 */
	@ApiModelProperty(value="单据编号",required=false)
	private String grn_sn;
	private String grn_sn_old;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	@ApiModelProperty(value="新单据类型",required=true)
	@NotNull(message = "新单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="旧单据类型",required=true)
	@NotNull(message = "旧单据类型不能为空")
	private BillsType billsType_old;
	
	@ApiModelProperty(value="新采购员")
	private String buyer_name;
	@ApiModelProperty(value="旧采购员")
	private String buyer_name_old;
	
	@ApiModelProperty(value="入库单明细",required=false)
	private List<GrnDetailForm> details = new ArrayList<>();
	
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo(){
		Map<String,Object> info = new HashMap<>();
		info.put("org_id", org_id);
		info.put("grn_inner_sn", grn_inner_sn);
		info.put("status", status);
		if(StringUtils.isNotNull(organization_inner_sn) || StringUtils.isNotNull(organization_inner_sn_old)) {
			if(StringUtils.isNull(organization_inner_sn)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "相关机构ID"));
			}
			info.put("organization_inner_sn", organization_inner_sn);
			info.put("organization_name", organization_name);
		}
		if(StringUtils.isNotNull(warehouse_inner_sn) || StringUtils.isNotNull(warehouse_inner_sn_old)) {
			if(StringUtils.isNull(warehouse_inner_sn)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "仓库ID"));
			}
			info.put("warehouse_inner_sn", warehouse_inner_sn);
			info.put("warehouse_name", warehouse_name);
		}
		if(StringUtils.isNotNull(handler_inner_sn) || StringUtils.isNotNull(handler_inner_sn_old)) {
			if(StringUtils.isNull(handler_inner_sn)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "经手人ID"));
			}
			info.put("handler_inner_sn", handler_inner_sn);
			info.put("handler_name", handler_name);
		}
		if(StringUtils.isNotNull(type_inner_sn) || StringUtils.isNotNull(type_inner_sn_old)) {
			if(StringUtils.isNull(type_inner_sn)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "入库单类型ID"));
			}
			info.put("type_inner_sn", type_inner_sn);
			info.put("type_name", type_name);
		}
		if(StringUtils.isNotNull(warehouse_operator_inner_sn) || StringUtils.isNotNull(warehouse_operator_inner_sn_old)) {
			if(StringUtils.isNull(warehouse_operator_inner_sn)) {
				info.put("warehouse_operator_inner_sn_null", 1);
				warehouse_operator_name = "";
			}
			info.put("warehouse_operator_inner_sn", warehouse_operator_inner_sn);
			info.put("warehouse_operator_name", warehouse_operator_name);
		}
		if(StringUtils.isNotNull(department_inner_sn) || StringUtils.isNotNull(department_inner_sn_old)) {
			if(StringUtils.isNull(department_inner_sn)) {
				info.put("department_inner_sn_null", 1);
				department_name = "";
			}
			info.put("department_inner_sn", department_inner_sn);
			info.put("department_name", department_name);
		}
		if(StringUtils.isNotEmpty(grn_sn) || StringUtils.isNotEmpty(grn_sn_old)) {
			if(StringUtils.isEmpty(grn_sn)) {
				grn_sn = numberUtils.getMaxSn(VoucherType.PUT_IN, ServletUtils.getCurUserSystemType(), org_id);//.getPutInMaxSn(ServletUtils.getCurUserSystemType(), org_id);
			}
			info.put("grn_sn", grn_sn);
		}
		if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)) {
			info.put("description", description);
		}
		if(StringUtils.isNotEmpty(buyer_name) || StringUtils.isNotEmpty(buyer_name_old)) {
			info.put("buyer_name", buyer_name);
		}
		
		return info;
	}
}
