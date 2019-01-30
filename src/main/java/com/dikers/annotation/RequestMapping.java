package com.dikers.annotation;

import java.lang.annotation.*;


/**
 * Created by dikers on 2019/1/30.
 */
@Target({ ElementType.METHOD }) // 在方法上的注解
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}