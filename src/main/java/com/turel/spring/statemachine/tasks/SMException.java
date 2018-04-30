package com.turel.spring.statemachine.tasks;

import lombok.Getter;

public class SMException extends RuntimeException {
    @Getter
    private boolean logErrorMessage;

    public SMException(String msg) {
        super(msg);
        this.logErrorMessage = false;
    }

    public SMException(Throwable e) {
        super(e);
        this.logErrorMessage = true;
    }

}
