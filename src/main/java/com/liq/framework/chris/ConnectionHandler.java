package com.liq.framework.chris;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Statement;

/**
 * Created by yuekuo on 15/8/10.
 */
public class ConnectionHandler implements InvocationHandler {
    private ChrisConnection chrisConnection;

    public ConnectionHandler(ChrisConnection chrisConnection) {
        this.chrisConnection = chrisConnection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Statement.class.isAssignableFrom(method.getReturnType())) {
            chrisConnection.offerStatement(method, args);
        } else {
            chrisConnection.addLazyMethod(method, args);
        }
        return method.invoke(chrisConnection, args);
    }
}
