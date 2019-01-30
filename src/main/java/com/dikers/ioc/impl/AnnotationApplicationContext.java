package com.dikers.ioc.impl;

import com.dikers.annotation.*;
import com.dikers.exception.DuplicateBeanException;
import com.dikers.ioc.ApplicationContext;
import com.dikers.pojo.RequestBean;
import com.dikers.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dikers
 * 实现一个简单的自动注入的功能， 简单模拟spring 注解注入bean的功能。
 */
public class AnnotationApplicationContext implements ApplicationContext {
    private String  packageName ;
    private Map<String , RequestBean> mRequestBeanMap ;
    private Map<String, Object>  mBeanMap;


    public AnnotationApplicationContext(String packageName) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        System.out.println("------------Application init start------------------------- ");
        this.packageName = packageName;
        mRequestBeanMap = new HashMap<>();
        mBeanMap = initBeans();
        System.out.println("====  autowired start ===== ");
        autowired(mBeanMap);
        System.out.println("====  autowired end   ===== \n");

        System.out.println("====  request mapping start ===== ");
        for(Map.Entry<String, RequestBean> entry: mRequestBeanMap.entrySet()){
            RequestBean requestBean = entry.getValue();
            System.out.println("/"+ requestBean.getPath() + " <-----> "+ requestBean.getClassName()+":"+requestBean.getMethod().getName() +"()");
        }
        System.out.println("====  request mapping end ===== \n");
        System.out.println("------------Application init done------------------------- \n");


    }


    private Map<String, Object>  initBeans() throws IllegalAccessException, InstantiationException {

        Map<String, Object> beanMap = new HashMap<String, Object>(16);
        // List<String> classNames = getClassName(packageName);
        List<String> classNames = ClassUtil.getClassName(packageName, true);

        if (classNames == null) {
            System.out.println("bean object is null.");
            return null;
        }

        for (String className : classNames) {

            Class<?> c = null;
            try {
                c = Class.forName(className);
            } catch (ClassNotFoundException e) {
                System.out.println(className + "  not found ");
                continue;
            }

            Annotation[] annotations = c.getAnnotations();

            if (annotations == null || annotations.length == 0) {
                continue;
            }

            for (Annotation annotation : annotations) {
                if (annotation instanceof Component ||
                        annotation instanceof Service) {

                    Object obj = c.newInstance();
                    String beanName = className.substring(className.lastIndexOf(".") + 1, className.length());
                    beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1, beanName.length());

                    System.out.println("\tbeanName: " + beanName +"  className: "+ className);
                    beanMap.put(beanName, obj);
                }else if (annotation instanceof Controller){
                    initRequestMapping(annotation, c);
                }


            }



        }
        return beanMap;
    }


    private void autowired(Map<String, Object> beanMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //        beanMap

        for(Map.Entry<String, Object> entry : beanMap.entrySet()){

            Object object = entry.getValue();
            Field[] fields = object.getClass().getDeclaredFields() ;

            if(fields == null){
                continue;
            }

            for(Field field: fields){
                Annotation annotation = field.getAnnotation(Autowired.class);
                if(annotation == null){
                    continue;
                }
                System.out.println( "class: "+object.getClass().getName() +"\t field: "+ field.getName());
                Object beanObject = beanMap.get(field.getName());
                String beanName = beanObject.getClass().getName().substring(beanObject.getClass().getName().lastIndexOf(".") +1 , beanObject.getClass().getName().length());

                Method setMethod =  object.getClass().getDeclaredMethod("set"+beanName, beanObject.getClass());
                setMethod.invoke(object, beanObject);
            }



        }
    }

    private void initRequestMapping(Annotation annotation , Class<?> c){
        if( annotation instanceof Controller){
            Method[] methods = c.getDeclaredMethods();
            if(methods == null){
                return ;
            }

            for(Method method: methods){
                RequestMapping requestMapping =  method.getAnnotation(RequestMapping.class);
                if(requestMapping != null){

                    String path = requestMapping.value();
                    if(mRequestBeanMap.get(path) != null){
                        throw new DuplicateBeanException(200, "'"+path+ "' is duplicated! ");
                    }

                    RequestBean requestBean = new RequestBean();
                    requestBean.setPath(path);
                    requestBean.setClassName(c.getName());
                    requestBean.setMethod(method);

                    mRequestBeanMap.put(path, requestBean);

                }
            }


        }
    }


    @Override
    public Object getBean(String name) {
        return mBeanMap.get(name);
    }

    @Override
    public RequestBean getRequestBeanByPath(String path) {
        return  mRequestBeanMap.get(path);
    }

    @Override
    public Object newInstanceBean(String className) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> c =  Class.forName(className);
        Field[] fields = c.getDeclaredFields() ;
        Object object = c.newInstance();
        if(fields == null){
            return null ;
        }

        for(Field field: fields){
            Annotation annotation = field.getAnnotation(Autowired.class);
            if(annotation == null){
                continue;
            }
            Object beanObject = mBeanMap.get(field.getName());
            String beanName = beanObject.getClass().getName().substring(beanObject.getClass().getName().lastIndexOf(".") +1 , beanObject.getClass().getName().length());

            Method setMethod =  c.getDeclaredMethod("set"+beanName, beanObject.getClass());

            setMethod.invoke(object, beanObject);

        }
        return object;
    }

}
