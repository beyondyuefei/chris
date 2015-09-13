package com.liq.framework.chris.spi;

/**
 * Created by yuekuo on 15/8/24.
 */
public interface TransactionReadOnly {
    boolean isReadyOnly(String sql);

}
