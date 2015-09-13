package com.liq.framework.chris;

import java.lang.reflect.Method;

/**
 * Created by yuekuo on 15/8/24.
 */
public class LazyMethodHolder {
    private Method lazyMethod;
    private Object[] args;

    public LazyMethodHolder(Method lazyMethod, Object[] args) {
        this.lazyMethod = lazyMethod;
        this.args = args;
    }

    public Method getLazyMethod() {
        return lazyMethod;
    }

    public void setLazyMethod(Method lazyMethod) {
        this.lazyMethod = lazyMethod;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
