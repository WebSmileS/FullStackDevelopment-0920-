package com.yrt.system.quartz.task;

import org.springframework.stereotype.Component;

/**
 * 定时任务调度测试
 */
@Component("testTask")
public class TestTask
{
    public void hasParams(String params)
    {
        System.out.println("执行有参方法：" + params);
    }

    public void noHasParams()
    {
        System.out.println("执行无参方法");
    }
}
