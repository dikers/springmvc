package com.dikers.test;


import com.dikers.ioc.ApplicationContext;
import com.dikers.ioc.impl.ClassPathXMLApplicationContext;
import com.dikers.service.StudentService;

/**
 * 加载applicationContext.xml 测试代码
 *
 */
public class XmlIocTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXMLApplicationContext("applicationContext.xml");
        StudentService stuServ = (StudentService) context.getBean("StudentService");
        stuServ.getStudent().selfIntroDuction();
    }

}