package com.liq.framework.chris.spi.impl;

import com.liq.framework.chris.spi.TransactionReadOnly;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * 默认通过sql语句来判断是否只读事务
 * Created by yuekuo on 15/8/24.
 */
public class DefaultTransactionReadOnly implements TransactionReadOnly {
    @Override
    public boolean isReadyOnly(String sql) {
        //boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (!sql.trim().toLowerCase().startsWith("select")) {
            return false;
        }
        return true;
    }
}
