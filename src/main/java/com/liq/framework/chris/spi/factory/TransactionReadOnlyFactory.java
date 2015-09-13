package com.liq.framework.chris.spi.factory;

import com.liq.framework.chris.spi.TransactionReadOnly;
import com.liq.framework.chris.spi.impl.DefaultTransactionReadOnly;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by yuekuo on 15/8/24.
 */
public class TransactionReadOnlyFactory {
    private static TransactionReadOnly defaultTransactionReadOnly = new DefaultTransactionReadOnly();
    private static TransactionReadOnly transactionReadOnly;

    static {
        ServiceLoader<TransactionReadOnly> serviceLoader = ServiceLoader.load(TransactionReadOnly.class);
        Iterator<TransactionReadOnly> iterator = serviceLoader.iterator();
        //若有配置多个实现，则采用最后一个
        while (iterator.hasNext()) {
            transactionReadOnly = iterator.next();
        }
    }

    public static TransactionReadOnly getTransactionReadOnly() {
        return (transactionReadOnly == null ? defaultTransactionReadOnly : transactionReadOnly);
    }
}
