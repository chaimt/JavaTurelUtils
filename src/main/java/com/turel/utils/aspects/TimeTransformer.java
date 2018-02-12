package com.turel.utils.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;

import java.util.Optional;

@Aspect
public class TimeTransformer {
	@Pointcut("call(public static native long java.lang.System.currentTimeMillis())")
	public static boolean modifySystemTime() {
		return true;
	}

	@Around(value = "modifySystemTime()")
	public Object aroundModifySystemTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Long currentTime = (Long)proceedingJoinPoint.proceed();

		return Optional.ofNullable(MDC.get("time"))
				.map(timeAsString -> addTime(timeAsString, currentTime))
				.orElse(currentTime);
	}

	private Long addTime(String timeToAdd, Long currentTime) {
		return currentTime + Long.parseLong(timeToAdd);
	}
}
