package com.turel.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class RetryTimeoutCommandTest {
    public String SUCCESS = "success";
    public int ABORT_TIME = 3*1000;

    @Test
    public void testRetryCommandShouldNotRetryCommandWhenSuccessful() {
        RetryTimeoutCommand<String> retryCommand = new RetryTimeoutCommand<>(ABORT_TIME);

        String result = retryCommand.run(() -> SUCCESS);

        assertEquals(SUCCESS, result);
        assertEquals(0, retryCommand.getRetryCounter());
    }

    @Test
    public void testRetryCommandShouldRetryOnceThenSucceedWhenFailsOnFirstCallButSucceedsOnFirstRetry() {
        RetryTimeoutCommand<String> retryCommand = new RetryTimeoutCommand<>(ABORT_TIME);

        String result = retryCommand.run(() -> {
            if (retryCommand.getRetryCounter() == 0) throw new RuntimeException("Command Failed");
            else return SUCCESS;
        });

        assertEquals(SUCCESS, result);
        assertEquals(1, retryCommand.getRetryCounter());
    }

    @Test
    public void testRetryCommandShouldThrowExceptionWhenMaxRetriesIsReached() {
        RetryTimeoutCommand<String> retryCommand = new RetryTimeoutCommand<>(ABORT_TIME);

        try {
            retryCommand.run(() -> {
                throw new RuntimeException("Failed");
            });
            fail("Should throw exception when max retries is reached");
        } catch (Exception e) {
        }
        assertTrue(retryCommand.getRetryCounter()>0);
    }
}
