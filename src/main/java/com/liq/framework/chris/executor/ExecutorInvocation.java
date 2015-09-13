package com.liq.framework.chris.executor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created by yuekuo on 15/9/9.
 */
public class ExecutorInvocation {
    private static final List<Executor> executorList = new ArrayList<Executor>();
    private Executor sqlExecutor;
    private Iterator<Executor> iterator;
    private Object result;
    private volatile boolean init;

    static {
        ServiceLoader<Executor> serviceLoader = ServiceLoader.load(Executor.class);
        Iterator<Executor> iterator = serviceLoader.iterator();

        while (iterator.hasNext()) {
            executorList.add(iterator.next());
        }
    }

    public Object doExecutor(InvocationContext invocationContext) throws ExecutorException {
        if (iterator.hasNext()) {
            result = iterator.next().execute(invocationContext);
        } else {
            result = this.sqlExecutor.execute(invocationContext);
        }
        return result;
    }

    public Object execute(Executor executor, String sql, Object[] args, String dsKey, boolean isReadOnly) throws SQLException {
        if (init) {
            throw new IllegalStateException("the execute method has been invoked already");
        }
        init(executor);
        InvocationContext invocationContext = new InvocationContext(this);
        invocationContext.setSql(sql);
        invocationContext.setArgs(args);
        invocationContext.setDsKey(dsKey);
        invocationContext.setReadOnly(isReadOnly);
        try {
            return doExecutor(invocationContext);
        } catch (ExecutorException e) {
            //这里单独处理SqlException,方便给spring等框架识别
            if (e.getException() instanceof SQLException) {
                throw (SQLException) e.getException();
            }
            throw new RuntimeException(e);
        }
    }

    private void init(Executor executor) {
        this.sqlExecutor = executor;
        this.iterator = executorList.iterator();
        init = true;
    }
}
