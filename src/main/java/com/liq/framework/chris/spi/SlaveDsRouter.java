package com.liq.framework.chris.spi;

import java.util.List;

/**
 * Created by yuekuo on 15/8/24.
 */
public interface SlaveDsRouter {
    String routerDataSource(String sql,List<String> slaveDsKey);
}
