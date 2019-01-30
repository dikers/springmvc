package com.dikers.annotation;


import java.lang.annotation.*;
/**
 * Created by dikers on 2019/1/30.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}