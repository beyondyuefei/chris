package com.liq.framework.chris.spi.impl;

import com.liq.framework.chris.spi.SlaveDsRouter;

import java.util.List;
import java.util.Random;

/**
 * 默认的从库路由策略: 随机路由
 * Created by yuekuo on 15/8/24.
 */
public class DefaultSlaveDsRouter implements SlaveDsRouter {
    private Random random = new Random();

    @Override
    public String routerDataSource(String sql, List<String> slaveDsKey) {
        return slaveDsKey.get(random.nextInt(slaveDsKey.size()));
    }
}
