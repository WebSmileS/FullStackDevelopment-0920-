package com.yrt.framework.aspectj;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yrt.common.utils.Utils;

@Component
@Aspect
public class TrimInputParameterAspect {

	private static final Logger logger = LoggerFactory.getLogger(TrimInputParameterAspect.class);

	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)"
			+ "||@annotation(org.springframework.web.bind.annotation.GetMapping)"
			+ "||@annotation(org.springframework.web.bind.annotation.PostMapping)"
			+ "||@annotation(org.springframework.web.bind.annotation.PutMapping)"
			+ "||@annotation(org.springframework.web.bind.annotation.DeleteMapping)"
			+ "||@annotation(org.springframework.web.bind.annotation.PatchMapping)")
	public void rest() {
	}

	/**
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("rest()")
	public Object doTrim(ProceedingJoinPoint joinPoint) throws Throwable {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		
		logger.debug("trim request param...: {}", request.getRequestURI());
		long t = System.currentTimeMillis();
		
		Object[] args = Utils.trimObject(joinPoint.getArgs(), false, "com.yrt");
		logger.debug("trim request elapse: {}", System.currentTimeMillis() - t);

		return joinPoint.proceed(args);
	}
}
