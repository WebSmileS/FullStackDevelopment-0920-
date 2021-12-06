package com.yrt.framework.async;

import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务管理器
 * 
 * @author Runner
 */
public class AsyncManager
{
    /**
     * 操作延迟10秒
     */
    private final int OPERATE_DELAY_TIME = 10000;

    /**
     * 异步操作任务调度线程池
     */
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);

    /**
     * 单例模式
     */
    private static AsyncManager me = new AsyncManager();

    public static AsyncManager me()
    {
        return me;
    }

    /**
     * 执行任务
     * 
     * @param 任务task
     */
    public void execute(TimerTask task)
    {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 立即执行
     * @param task
     * @return
     */
    public <T> Future<T> executeImediately(Callable<T> task) {
    	return executor.submit(task);
    }
}
