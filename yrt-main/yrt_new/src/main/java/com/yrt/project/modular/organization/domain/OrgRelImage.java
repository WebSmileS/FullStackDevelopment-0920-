package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrgRelImage implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 企业内部编号 */
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Long id;
	/** 企业相关图片内部编号 */
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Long org_related_image_inner_sn;
	/** 类别 */
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Integer type;
	/** 统一资源定位符 */
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private String url;
	/** 开始日期 */
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	/** 截止日期 */
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private String file_name;
}
