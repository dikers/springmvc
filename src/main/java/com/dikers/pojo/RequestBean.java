package com.dikers.pojo;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class RequestBean {

    String path;
    String className;
    Method method;

    @Override
    public String toString() {
        return "RequestBean{" +
                "path='" + path + '\'' +
                ", className=" + className +
                ", method=" + method +
                '}';
    }
}
