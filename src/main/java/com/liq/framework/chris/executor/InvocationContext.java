package com.liq.framework.chris.executor;

/**
 * Created by yuekuo on 15/9/10.
 */
public class InvocationContext {
    private ExecutorInvocation executorInvocation;
    private String sql;
    private Object[] args;
    private String dsKey;
    private boolean isReadOnly;

    public ExecutorInvocation getExecutorInvocation() {
        return executorInvocation;
    }

    public InvocationContext(ExecutorInvocation executorInvocation) {
        this.executorInvocation = executorInvocation;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getDsKey() {
        return dsKey;
    }

    public void setDsKey(String dsKey) {
        this.dsKey = dsKey;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }
}
