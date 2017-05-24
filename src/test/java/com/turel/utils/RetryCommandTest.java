package com.turel.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Chaim on 24/05/2017.
 */
public class RetryCommandTest {
    public String SUCCESS = "success";
    public int MAXRETRIES = 3;

    @Test
    public void testRetryCommandShouldNotRetryCommandWhenSuccessful() {
        RetryCommand<String> retryCommand = new RetryCommand<>(MAXRETRIES);

        String result = retryCommand.run(() -> SUCCESS);

        assertEquals(SUCCESS, result);
        assertEquals(0, retryCommand.getRetryCounter());
    }

    @Test
    public void testRetryCommandShouldRetryOnceThenSucceedWhenFailsOnFirstCallButSucceedsOnFirstRetry() {
        RetryCommand<String> retryCommand = new RetryCommand<>(MAXRETRIES);

        String result = retryCommand.run(() -> {
            if (retryCommand.getRetryCounter() == 0) throw new RuntimeException("Command Failed");
            else return SUCCESS;
        });

        assertEquals(SUCCESS, result);
        assertEquals(1, retryCommand.getRetryCounter());
    }

    @Test
    public void testRetryCommandShouldThrowExceptionWhenMaxRetriesIsReached() {
        RetryCommand<String> retryCommand = new RetryCommand<>(MAXRETRIES);

        try {
            retryCommand.run(() -> {
                throw new RuntimeException("Failed");
            });
            fail("Should throw exception when max retries is reached");
        } catch (Exception e) {
        }
        assertEquals(MAXRETRIES, retryCommand.getRetryCounter());
    }
}
