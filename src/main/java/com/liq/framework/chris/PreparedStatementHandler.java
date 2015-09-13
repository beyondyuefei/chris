package com.liq.framework.chris;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by yuekuo on 15/8/11.
 */
public class PreparedStatementHandler implements InvocationHandler {
    private ChrisPreparedStatement chrisPreparedStatement;

    public PreparedStatementHandler(ChrisPreparedStatement chrisPreparedStatement) {
        this.chrisPreparedStatement = chrisPreparedStatement;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        chrisPreparedStatement.addLazyMethod(method,args);
        return method.invoke(chrisPreparedStatement,args);
    }
}
