package com.yrt.project.api.business.dealer.vo.tmpproduct;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TmpProductMDRFImageForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="图片地址",required=true)
	@NotBlank(message = "图片地址不能为空")
	private String url;
	@ApiModelProperty(value="文件名",required=true)
	@NotBlank(message = "文件名不能为空")
	private String file_name;
}
