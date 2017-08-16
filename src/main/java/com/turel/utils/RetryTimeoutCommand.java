package com.turel.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class RetryTimeoutCommand<T> {
    private static final Logger LOG = LoggerFactory.getLogger(RetryTimeoutCommand.class);
    private long abortTimeout;
    private int retryCounter = 0;
    private long startTime;

    public RetryTimeoutCommand(long abortTimeout) {
        this.abortTimeout = abortTimeout;
    }

    // Takes a function and executes it, if fails, passes the function to the retry command
    public T run(UncheckSupplier<T> function) {
        try {
            startTime = System.currentTimeMillis();
            return function.get();
        } catch (Exception e) {
            return retry(function);
        }
    }

    public long getAbortTimeout() {
        return abortTimeout;
    }

    public int getRetryCounter() {
        return retryCounter;
    }

    private T retry(UncheckSupplier<T> function) throws RuntimeException {
        retryCounter++;
        LOG.debug("Command failed, will be retried form " + abortTimeout + " time.");
        boolean stop = false;
        while (!stop) {
            try {
                stop = System.currentTimeMillis() - startTime > abortTimeout;
                if (!stop)
                    return function.get();
            } catch (Exception ex) {
            }
        }
        throw new RuntimeException("Command failed on all tries");
    }
}
