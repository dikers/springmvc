package com.dikers.service;

import com.dikers.annotation.Service;

@Service
public class MyService {

    public MyService() {
    }

    public int insert(String  param) {
        System.out.println("MyServiceImpl:" + "insert "+ param);
        return 0;
    }

    public int delete(String  param) {
        System.out.println("MyServiceImpl:" + "delete "+ param);
        return 0;
    }

    public int update(String  param) {
        System.out.println("MyServiceImpl:" + "update : "+ param);
        return 0;
    }

    public int select(String  param) {
        System.out.println("MyServiceImpl:" + "select : "+ param);
        return 0;
    }
}
