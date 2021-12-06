package com.yrt.framework.async.factory;

import java.util.TimerTask;

import com.yrt.common.utils.AddressUtils;
import com.yrt.common.utils.spring.SpringUtils;
import com.yrt.system.monitor.operlog.domain.SysOperLog;
import com.yrt.system.monitor.operlog.service.ISysOperLogService;


/**
 * 异步工厂（产生任务用）
 * 
 */
public class AsyncFactory
{
    /**
     * 操作日志记录
     * 
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
            }
        };
    }

}
