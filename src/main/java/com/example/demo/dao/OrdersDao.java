package com.example.demo.dao;


import com.example.demo.entity.Orders;

import java.util.List;

public interface OrdersDao {

    int add(Orders orders);

    int update(Orders orders);

    int del(Orders orders);

    List<Orders> findList(Orders orders);

    Orders findById(Integer id);


}
