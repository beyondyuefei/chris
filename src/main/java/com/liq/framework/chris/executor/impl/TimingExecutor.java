package com.liq.framework.chris.executor.impl;

import com.liq.framework.chris.executor.Executor;
import com.liq.framework.chris.executor.ExecutorException;
import com.liq.framework.chris.executor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuekuo on 15/9/13.
 */
public class TimingExecutor implements Executor {
    private final Logger logger = LoggerFactory.getLogger(TimingExecutor.class);

    @Override
    public Object execute(InvocationContext invocationContext) throws ExecutorException {
        long startTime = System.currentTimeMillis();
        Object result = invocationContext.getExecutorInvocation().doExecutor(invocationContext);
        long stopTime = System.currentTimeMillis();
        logger.info(String.format("the sql %s execute consume : %l milliseconds","",stopTime - startTime));
        return result;
    }
}
