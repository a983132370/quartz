package com.example.demo.service;

import com.example.demo.dao.OrdersDao;
import com.example.demo.entity.Orders;
import com.example.demo.job.MyJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
/**
 * @ClassName:
 * @Description:
 * @Author li
 * @Date 2018/12/12 12:39
 */
@Service
public class OrderService {

    private String GROUP = "MYJOB";

    @Autowired
    JobService jobService;
    @Autowired
    OrdersDao ordersDao;
    @Value("${server.port}")
    String port;

    public void createOrder() throws SchedulerException {
        Orders orders = new Orders();
        orders.setStatus("1");
        orders.setCreateDate(new Date());
        ordersDao.add(orders);

        //携带参数
        Map<String,Object> param = new HashMap<>();
        param.put("id",orders.getId());
        param.put("port",port);
        jobService.addJob(orders.getId()+"",orders.getId()+"",GROUP,MyJob.class,10,param);
    }

    public void cancel(Integer id) throws SchedulerException {
        Orders orders = ordersDao.findById(id);
        orders.setStatus("2");
        orders.setCancelDate(new Date());
        ordersDao.update(orders);
        //携带参数
        Map<String,Object> param = new HashMap<>();
        param.put("id",orders.getId());
        jobService.delJob(id+"",GROUP);
    }

}
