package com.example.demo.controller;

import com.example.demo.service.OrderService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName:
 * @Description:
 * @Author li
 * @Date 2018/9/14 17:34
 */
@RestController
public class IndexCtrl {

    @Autowired
    OrderService service;

    @RequestMapping("")
    public String test(){
        try {
            service.createOrder();
        } catch (SchedulerException e) {
            e.printStackTrace();
            return "create job 失败 : " + e.getMessage();
        }
        return "create job 成功";
    }

    @RequestMapping("del/{id}")
    public String del(@PathVariable("id") Integer id){
        try {
            service.cancel(id);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return "cancel job 失败 : " + e.getMessage();
        }
        return "cancel job 成功";
    }

}
