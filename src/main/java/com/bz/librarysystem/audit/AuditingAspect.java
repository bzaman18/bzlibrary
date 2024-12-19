package com.bz.librarysystem.audit;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
class AuditingAspect {

    private final Logger logger = LoggerFactory.getLogger(AuditingAspect.class);

    @Around("execution(* com.bz.librarysystem..*(..))")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - startTime;
        logger.info("Exit: {}() with result = {} and time taken = {} ms", joinPoint.getSignature().getName(), result, timeTaken);
        return result;
    }

}