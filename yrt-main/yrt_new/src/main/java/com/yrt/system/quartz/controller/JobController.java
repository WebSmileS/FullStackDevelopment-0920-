package com.yrt.system.quartz.controller;

import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.exception.job.TaskException;
import com.yrt.common.utils.poi.ExcelUtil;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.*;
import com.yrt.system.quartz.domain.SysJob;
import com.yrt.system.quartz.service.ISysJobService;
import com.yrt.system.quartz.vo.JobSearchForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/admin/monitor/job")
@Api(tags = "系统-定时任务管理接口")
public class JobController extends BaseController {

	@Autowired
    private ISysJobService jobService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看定时任务列表", notes = "查看定时任务列表")
	public AjaxResult getJobList(
			@Validated JobSearchForm form){
		SysJob job = new SysJob();
		job.setStatus(form.getStatus());
		job.setJobName(form.getJobName());
		job.setMethodName(form.getMethodName());
		return success().put("list", jobService.selectJobList(job));
	}
	
	@RequestMapping(value = "/info/{jobId}", method = RequestMethod.GET)
	@ApiOperation(value = "查看定时任务信息", notes = "查看定时任务信息")
	public ResultInfo<SysJob> getJobInfo(
			@ApiParam(name = "jobId", value = "定时任务ID", required = true) @PathVariable Long jobId){
		return ResultInfo.success(jobService.selectJobById(jobId));
	}
	
	@RequestMapping(value = "/changeStatus/{jobId}/{status}", method = RequestMethod.PUT)
	@ApiOperation(value = "修改定时任务状态", notes = "修改定时任务状态")
	public AjaxResult changeStatus(
			@ApiParam(name = "jobId", value = "定时任务ID", required = true) @PathVariable Long jobId,
			@ApiParam(name = "status", value = "状态0-正常 1-暂停", required = true) @PathVariable String status)  throws SchedulerException {
		SysJob job = new SysJob();
		job.setStatus(status);
		job.setJobId(jobId);
		return toAjax(jobService.changeStatus(job));
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增定时任务", notes = "新增定时任务")
	public AjaxResult addJob(SysJob job)  throws SchedulerException, TaskException {
		return toAjax(jobService.insertJobCron(job));
	}
	
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "批量删除定时任务", notes = "批量删除定时任务")
	public AjaxResult deleteJob(String ids)  throws SchedulerException {
		jobService.deleteJobByIds(ids);
		return success();
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改定时任务", notes = "修改定时任务")
	public AjaxResult editJob(SysJob job) throws SchedulerException, TaskException {
		return toAjax(jobService.updateJobCron(job));
	}
	
    @RequestMapping(value = "/checkCronExpressionIsValid", method = RequestMethod.GET)
	@ApiOperation(value = "校验cron表达式是否有效", notes = "校验cron表达式是否有效")
    public boolean checkCronExpressionIsValid(SysJob job)
    {
        return jobService.checkCronExpressionIsValid(job.getCronExpression());
    }
    
    @RequestMapping(value = "/run", method = RequestMethod.PUT)
	@ApiOperation(value = "定时任务立即执行一次", notes = "定时任务立即执行一次")
    public AjaxResult run(SysJob job) throws SchedulerException {
    	jobService.run(job);
    	return success();
    }
    
    @RequestMapping(value = "/export", method = RequestMethod.PUT)
	@ApiOperation(value = "导出定时任务列表", notes = "导出定时任务列表")
    public AjaxResult export(@Validated JobSearchForm form) {
    	SysJob job = new SysJob();
		job.setStatus(form.getStatus());
		job.setJobName(form.getJobName());
		job.setMethodName(form.getMethodName());
    	List<SysJob> jobs = jobService.selectJobList(job);
    	ExcelUtil<SysJob> util = new ExcelUtil<>(SysJob.class);
    	return util.exportExcel(jobs, "定时任务列表");
    }
}
