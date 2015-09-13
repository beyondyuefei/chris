package com.liq.framework.chris.executor;

/**
 * Created by yuekuo on 15/9/9.
 */
public interface Executor {
    Object execute(InvocationContext invocationContext) throws ExecutorException;
}
