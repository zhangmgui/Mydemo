package com.xytest.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/05/20.
 */
@Aspect
@Component
public class Aop {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    // 控制层调用切入点
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void controllerPointcut() {
    }
   //service com.xytest.service.CacheService
   @Pointcut("execution(* com.xytest.service..*.*(..))")
    public void servicePointcut() {
    }

    //database
    @Pointcut("execution(* com.xyauto.mapper..*.*(..))")
    public void databasePointcut() {
    }

    @Around("databasePointcut()")
    public Object doDatabasePointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return writeLog("database", pjp);
    }
    @Around("servicePointcut()")
   public Object doServicePointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("service aop ahead");
        Object proceed = pjp.proceed();
        System.out.println("service aop after");
        return proceed;
    }

    @Around("controllerPointcut()")
    public Object doControllerPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return writeLog("controller", pjp);
    }

    private Object writeLog(String level, ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = pjp.proceed();
        logger.info(">>> {level:{} method:{} speed:{}} <<<", level, pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName(), (System.currentTimeMillis() - startTime));
        return result;
    }

}
