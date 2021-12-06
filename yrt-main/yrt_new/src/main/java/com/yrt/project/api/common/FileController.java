package com.yrt.project.api.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.file.FileUploadUtils;
import com.yrt.common.utils.file.FileUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.config.ServerConfig;
import com.yrt.framework.config.YrtConfig;
import com.yrt.framework.web.domain.AjaxResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 通用请求处理
 * 
 * @author Runner
 */
@RestController
@Api(tags = "公用-文件上传下载接口")
public class FileController {
	private static final Logger log = LoggerFactory.getLogger(FileController.class);

	/**
	 * 文件上传路径
	 */
	public static final String UPLOAD_PATH = "/profile/upload/";

	@Autowired
	private ServerConfig serverConfig;

	/**
	 * 通用下载请求
	 * 
	 * @param fileName 文件名称
	 * @param delete   是否删除
	 */
	@Log(title = "common.file", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@GetMapping("/v1/comm/download")
	@ApiOperation(value = "文件下载", notes = "文件下载")
	public void fileDownload(String fileName, Boolean delete, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			if (!FileUtils.isValidFilename(fileName)) {
				throw new Exception(StringUtils.format(" 文件名称({})非法，不允许下载。 ", fileName));
			}
			String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
			String filePath = YrtConfig.getDownloadPath() + fileName;

			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition",
					"attachment;fileName=" + setFileDownloadHeader(request, realFileName));
			FileUtils.writeBytes(filePath, response.getOutputStream());
			if (StringUtils.isNotNull(delete) && delete) {
				FileUtils.deleteFile(filePath);
			}
		} catch (Exception e) {
			log.error("下载文件失败", e);
		}
	}

	/**
	 * 通用上传请求
	 */
	@Log(title = "common.file", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@PostMapping("/v1/comm/upload")
	@ApiOperation(value = "文件上传", notes = "文件上传")
	public AjaxResult uploadFile(MultipartFile file) throws Exception {
		try {
			// 上传文件路径
			String filePath = YrtConfig.getUploadPath();
			// 上传并返回新文件名称
			String fileName = FileUploadUtils.upload(filePath, file);
			String url = serverConfig.getUrl() + UPLOAD_PATH + fileName;
			AjaxResult ajax = AjaxResult.success();
			ajax.put("fileName", fileName);
			ajax.put("url", url);
			return ajax;
		} catch (Exception e) {
			return AjaxResult.error(e.getMessage());
		}
	}

	public String setFileDownloadHeader(HttpServletRequest request, String fileName)
			throws UnsupportedEncodingException {
		final String agent = request.getHeader("USER-AGENT");
		String filename = fileName;
		if (agent.contains("MSIE")) {
			// IE浏览器
			filename = URLEncoder.encode(filename, "utf-8");
			filename = filename.replace("+", " ");
		} else if (agent.contains("Firefox")) {
			// 火狐浏览器
			filename = new String(fileName.getBytes(), "ISO8859-1");
		} else if (agent.contains("Chrome")) {
			// google浏览器
			filename = URLEncoder.encode(filename, "utf-8");
		} else {
			// 其它浏览器
			filename = URLEncoder.encode(filename, "utf-8");
		}
		return filename;
	}
}
