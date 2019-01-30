package com.dikers.ioc;

import com.dikers.pojo.RequestBean;

import java.lang.reflect.InvocationTargetException;

public interface ApplicationContext {
    /**
     * 获取指定的bean 对象
     * @param name
     * @return
     */
    Object getBean(String name);

    /**
     * 获取url 对应的controller 方法
     * @param path
     * @return
     */
    RequestBean getRequestBeanByPath(String path);

    /**
     * 初始化 controller 方法进行处理
     * @param className
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    Object newInstanceBean(String className) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException;
}
