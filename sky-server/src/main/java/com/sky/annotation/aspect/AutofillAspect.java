package com.sky.annotation.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
public class AutofillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..))&& @annotation(com.sky.annotation.Autofill)")
    public void autofillPointCut()
    {

    }

    /**
     * 前置通知
     */
    @Before("autofillPointCut()")
    public void aopBefore(JoinPoint joinPoint)
    {
        log.info("开始进行公共字段自动填充...");
    }
}
