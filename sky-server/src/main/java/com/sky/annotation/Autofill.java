package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//将注解加载在方法上
@Target(ElementType.METHOD)
//
@Retention(RetentionPolicy.RUNTIME)
//自定义注解
public @interface Autofill {

    //数据库操作类型；UPODATA,INSERT
   OperationType value();
}
