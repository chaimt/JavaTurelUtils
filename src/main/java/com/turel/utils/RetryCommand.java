package com.turel.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Wrapper for method call that needs retries
 * Created by Chaim on 24/05/2017.
 */
public class RetryCommand<T> {
    private static final Logger LOG = LoggerFactory.getLogger(RetryCommand.class);
    private int retryCounter;
    private int maxRetries;

    public RetryCommand(int maxRetries)
    {
        this.maxRetries = maxRetries;
    }

    // Takes a function and executes it, if fails, passes the function to the retry command
    public T run(Supplier<T> function) {
        try {
            return function.get();
        } catch (Exception e) {
            return retry(function);
        }
    }

    public int getRetryCounter() { return retryCounter; }

    private T retry(Supplier<T> function) throws RuntimeException {
        LOG.debug("Command failed, will be retried " + maxRetries + " times.");
        retryCounter = 0;
        while (retryCounter < maxRetries) {
            try {
                return function.get();
            } catch (Exception ex) {
                retryCounter++;
                LOG.warn("Command failed on retry " + retryCounter + " of " + maxRetries + " error: " + ex );
                if (retryCounter >= maxRetries) {
                    LOG.error("Max retries exceeded.");
                    break;
                }
            }
        }
        throw new RuntimeException("Command failed on all of " + maxRetries + " retries");
    }
}
