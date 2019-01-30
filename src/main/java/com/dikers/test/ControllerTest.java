package com.dikers.test;

import com.dikers.httpd.Headers;
import com.dikers.ioc.ApplicationContext;
import com.dikers.ioc.impl.AnnotationApplicationContext;
import com.dikers.pojo.RequestBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ControllerTest {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {



        ApplicationContext applicationContext = new AnnotationApplicationContext("com.dikers");


        RequestBean requestBean = applicationContext.getRequestBeanByPath("update");

        System.out.println("className: "+requestBean.getClassName());
        Object object = (Object) applicationContext.newInstanceBean(requestBean.getClassName());


        Method method = requestBean.getMethod();

        System.out.println("----------- invoke----------- ");
        Headers headers = new Headers();
        headers.setBody("body info ");
        headers.setParam("name", "张三");
        headers.setParam("age" , "24");
        method.invoke(object, new Object[]{headers});

    }


}
