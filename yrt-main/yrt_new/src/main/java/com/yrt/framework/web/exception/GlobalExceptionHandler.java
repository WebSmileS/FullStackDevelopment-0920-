package com.yrt.framework.web.exception;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.LoginException;
import com.yrt.common.exception.RefException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.exception.user.UserException;
import com.yrt.framework.web.domain.AjaxResult;


/**
 * 全局异常处理器
 * 
 * @author Runner
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private Logger logger = LogManager.getLogger(GlobalExceptionHandler.class.getName());

    // 捕捉shiro的异常
	/*
	 * @ExceptionHandler(ShiroException.class) public AjaxResult handle401() {
	 * return AjaxResult.error(401, "您没有权限访问！"); }
	 */
    
    /**
	 * 请求方式不支持
	 */
	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	public AjaxResult handleException(HttpRequestMethodNotSupportedException e) {
		return AjaxResult.error("不支持' " + e.getMethod() + "'请求");
	}
	
	/**
	 * 验证错误的异常
	 */
	@ExceptionHandler({ ValidateException.class })
	public AjaxResult handleException(ValidateException e) {
		return AjaxResult.error(1, e.getMessage());
	}
	
	/**
	 * 没有获取登录人员ID错误
	 */
	@ExceptionHandler({ UserException.class })
	public AjaxResult handleException(UserException e) {
		return AjaxResult.error(401, "您没有权限访问！");
	}
	
	/**
	 * 缺少必要参数的错误
	 */
	@ExceptionHandler({ MissingServletRequestParameterException.class })
	public AjaxResult handleException(MissingServletRequestParameterException e) {
		return AjaxResult.error(400, "缺少必要参数" + e.getParameterName() + ", 类型:"+e.getParameterType());
	}
	
	/**
	 * 缺少必要参数的错误
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public AjaxResult handleException(MethodArgumentNotValidException e) {
		String errorMesssage = "缺少必要参数:\n";
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMesssage += fieldError.getDefaultMessage() + "\n";
        }
		return AjaxResult.error(400, errorMesssage);
	}
	
	/**
	 * 参数格式解析的错误
	 */
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	public AjaxResult handleException(HttpMessageNotReadableException e) {
		logger.error("", e);
		InvalidFormatException ex = ((InvalidFormatException)e.getCause());
		Reference path = ex.getPath().get(0);
		Object value = ((InvalidFormatException)e.getCause()).getValue();
		String errorMesssage = String.format("'%s'参数'%s'格式错误！", path.getFieldName(), value.toString());
		
		return AjaxResult.error(400, errorMesssage);
	}
	
	/**
	 * 登录错误的异常
	 */
	@ExceptionHandler({ LoginException.class })
	public AjaxResult handleException(LoginException e) {
		logger.error("", e);
		return AjaxResult.error(1, e.getMessage());
	}
	
	/**
	 * 验证错误的异常
	 */
	@ExceptionHandler({ BindException.class })
	public AjaxResult handleException(BindException e) {
		logger.error("", e);
		return AjaxResult.error(1, e.getFieldErrors().get(0).getDefaultMessage());
	}

	/**
	 * 拦截未知的运行时异常
	 */
	// @ExceptionHandler(RuntimeException.class)
	public AjaxResult notFount(RuntimeException e) {
		logger.error("", e);
		return AjaxResult.error("运行时异常:" + e.getMessage());
	}

	/**
	 * 系统异常
	 */
//	@ExceptionHandler(Exception.class)
	public AjaxResult globalException(HttpServletRequest request, Throwable ex) {
		logger.error("", ex);
		return AjaxResult.error(getStatus(request).value(), "访问出错，无法访问: " + ex.getMessage());
	}

	/**
	 * 业务异常
	 */
	@ExceptionHandler(BusinessException.class)
	public AjaxResult businessException(BusinessException e) {
		logger.error("", e);
		return AjaxResult.error(e.getMessage());
	}
	
	/**
	 * 存储过程异常
	 */
	@ExceptionHandler(DBException.class)
	public AjaxResult proceduresException(DBException e) {
		logger.error("", e);
		return AjaxResult.error(e.getMessage());
	}
	
	/**
	 * SQL语法异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler(SQLSyntaxErrorException.class)
	public AjaxResult proceduresException(SQLSyntaxErrorException e) {
		logger.error("", e);
		return AjaxResult.error(e.getMessage());
	}
	
	/**
	 * 主键重复异常
	 */
	@ExceptionHandler(DuplicateException.class)
	public AjaxResult proceduresException(DuplicateException e) {
		logger.error("", e);
		return AjaxResult.error(e.getMessage());
	}
	
	/**
	 * 引用删除或者修改异常
	 */
	@ExceptionHandler(RefException.class)
	public AjaxResult proceduresException(RefException e) {
		logger.error("", e);
		return AjaxResult.error(e.getMessage());
	}
	
	/**
	 * 调用存储过程时缺少必要参数的异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public AjaxResult missingParameterException(SQLException e) {
		logger.error("", e);
		return AjaxResult.error("数据库出错！").put("detail", e.getMessage());
	}
	
	@ExceptionHandler(SQLException.class)
	public AjaxResult proceduresException(SQLException e) {
		logger.error("", e);
		return AjaxResult.error("数据库出错！").put("detail", e.getMessage()).put("cause", e.getStackTrace());
	}

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
