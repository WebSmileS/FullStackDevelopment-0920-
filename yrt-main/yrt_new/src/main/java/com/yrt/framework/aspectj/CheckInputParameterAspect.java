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

import com.github.pagehelper.PageHelper;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.IllegalStrFilterUtil;

@Component
@Aspect
public class CheckInputParameterAspect {

    private static final Logger Logger = LoggerFactory.getLogger(CheckInputParameterAspect.class);

    // 存在SQL注入风险
    private static final String IS_SQL_INJECTION = "输入参数存在SQL注入风险";

    private static final String UNVALIDATED_INPUT = "输入参数含有非法字符";

    private static final String ERORR_INPUT = "输入参数含有非法字符";

    /**
     * 定义切入点:拦截controller层所有方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void params() {
    }

    /**
     * 定义环绕通知
     * 
     * @param joinPoint
     * @throws Throwable
     */
    @Around("params()")
    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
    	Logger.info("进入非法字符串检测......");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object[] args = joinPoint.getArgs();// 参数
        String str = String.valueOf(args);

        if (!IllegalStrFilterUtil.sqlStrFilter(str)) {
            Logger.info(IS_SQL_INJECTION);
            throw new ValidateException(ERORR_INPUT);
        }
        if (!IllegalStrFilterUtil.isIllegalStr(str)) {
            Logger.info(UNVALIDATED_INPUT);
            throw new ValidateException(ERORR_INPUT);
        }
        Object result = joinPoint.proceed();
        Logger.info("结束非法字符串检测，当前调用接口-[" + request.getRequestURL() + "]");
        PageHelper.clearPage();
        return result;
    }
}