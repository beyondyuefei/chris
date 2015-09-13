package com.liq.framework.chris.executor;

/**
 * Created by yuekuo on 15/9/13.
 */
public class ExecutorException extends RuntimeException {
    private Throwable exception;

    public ExecutorException(Throwable cause) {
        super(cause);
        this.exception = cause;
    }

    public Throwable getException() {
        return exception;
    }


}
