package com.example.demo.service;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName:
 * @Description:
 * @Author li
 * @Date 2018/12/12 15:45
 */
@Service
public class JobService {
    @Autowired
    Scheduler scheduler;

    /**
     * 添加任务
     *
     * @param jobId       任务id
     * @param triggerId   触发器id
     * @param group       任务组
     * @param jobClass    任务回调处理类
     * @param delaySecond 延迟触发(秒)
     * @param params      携带参数
     * @throws SchedulerException
     */
    public void addJob(String jobId, String triggerId, String group, Class<? extends Job> jobClass, Integer delaySecond, final Map<String, Object> params) throws SchedulerException {
        addJob(jobId, triggerId, group, jobClass, Calendar.SECOND, delaySecond, params);
    }

    /**
     * 添加任务
     *
     * @param jobId         任务id
     * @param triggerId     触发器id
     * @param group         任务组
     * @param jobClass      任务回调处理类
     * @param calendarField 延迟触发时间单位
     * @param amount        延迟触发(calendarField)
     * @param params        携带参数
     * @throws SchedulerException
     */
    public void addJob(String jobId, String triggerId, String group, Class<? extends Job> jobClass, Integer calendarField, Integer amount, final Map<String, Object> params) throws SchedulerException {
        // define the job and tie it to our MyJob class
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobId, group).build();
        // create cron
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(calendarField, amount);
        String cron = calendar.get(Calendar.SECOND) + " " + calendar.get(Calendar.MINUTE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " + (calendar.get(Calendar.MONTH) + 1) + " ? " + calendar.get(Calendar.YEAR);
        //create trigger
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerId, group).withSchedule(scheduleBuilder).build();
        //param
        jobDetail.getJobDataMap().putAll(params);
        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(jobDetail, trigger);

        System.out.println("cron = " + cron);
        System.out.println("scheduler start , job id = " + jobId);
    }

    /**
     * 删除任务
     *
     * @param jobId
     * @param group
     * @throws SchedulerException
     */
    public void delJob(String jobId, String group) throws SchedulerException {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobId, group));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobId, group));
        scheduler.deleteJob(JobKey.jobKey(jobId, group));
    }

    /**
     * 挂起任务
     *
     * @param jobId
     * @param group
     * @throws SchedulerException
     */
    public void pauseJob(String jobId, String group) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jobId, group));
    }

    /**
     * 唤醒任务
     *
     * @param jobId
     * @param group
     * @throws SchedulerException
     */
    public void resumeJob(String jobId, String group) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jobId, group));
    }

    /**
     * 修改任务
     *
     * @param jobId
     * @param group
     * @throws SchedulerException
     */
    public void updateJob(String jobId, String group, Integer delaySecond, final Map<String, Object> params) throws SchedulerException {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, delaySecond);
        String cron = calendar.get(Calendar.SECOND) + " " + calendar.get(Calendar.MINUTE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " + (calendar.get(Calendar.MONTH) + 1) + " ? " + calendar.get(Calendar.YEAR);
        updateJob(jobId, group, cron, params);
    }

    /**
     * 修改任务
     *
     * @param jobId
     * @param group
     * @throws SchedulerException
     */
    public void updateJob(String jobId, String group, String cron, Map<String, Object> params) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId, group);
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        //参数修改 todo validate
        if (params != null && params.keySet().size() > 0) {
            JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobId, group));
            jobDetail.getJobDataMap().putAll(params);
        }
        // 按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        // 按新的trigger重新设置job执行
        scheduler.rescheduleJob(triggerKey, trigger);
    }
}
