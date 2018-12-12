package com.example.demo.job;

import com.example.demo.dao.OrdersDao;
import com.example.demo.service.OrderService;
import com.example.demo.util.ContextUtil;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName:
 * @Description:
 * @Author li
 * @Date 2018/12/12 14:34
 */
public class MyJob implements org.quartz.Job {

    @Autowired
    OrdersDao ordersDao;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail= context.getJobDetail();
        String id = String.valueOf(jobDetail.getJobDataMap().get("id"));
        String port = String.valueOf(jobDetail.getJobDataMap().get("port"));
        OrderService orderService =  ContextUtil.getBean(OrderService.class);
        try {
            orderService.cancel(Integer.valueOf(id));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
         System.out.println("job id = " + id +" job port = " + port + "    execute time = "+sdf.format(new Date()));
    }
}

