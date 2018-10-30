package com.turel.utils;

import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Chaim on 28/02/2017.
 */
public class LogUtils {

	public static ThreadLocal<String> appPrefix = new ThreadLocal<>();

	public static String prefixLog(String logMsg) {
		if (appPrefix != null)
			return String.format("[Turel:%s] %s", appPrefix.get(), logMsg);
		else
			return "[Turel] " + logMsg;
	}

	public static String getError(Throwable e) {
		String message;
		if (e.getMessage() != null)
			message = e.getMessage();
		else {
			if (e instanceof InvocationTargetException) {
				message = ((InvocationTargetException) e).getTargetException().getMessage();
			}
			else
				message = e.getClass().getCanonicalName();
		}
		if (e.getCause() != null)
			message = message + ": " + e.getCause().getMessage();
		return message;
	}

	public static void log(Logger log, boolean warn, String format, Object... arguments) {
		if (warn) {
			log.warn(format, arguments);
		} else {
			log.info(format, arguments);
		}

	}

}
