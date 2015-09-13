package com.liq.framework.chris.spi.factory;

import com.liq.framework.chris.spi.SlaveDsRouter;
import com.liq.framework.chris.spi.impl.DefaultSlaveDsRouter;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by yuekuo on 15/8/24.
 */
public class SlaveDsRouterFactory {
    private static SlaveDsRouter defaultSlaveDsRouter = new DefaultSlaveDsRouter();
    private static SlaveDsRouter slaveDsRouter;

    static {
        ServiceLoader<SlaveDsRouter> serviceLoader = ServiceLoader.load(SlaveDsRouter.class);
        Iterator<SlaveDsRouter> iterator = serviceLoader.iterator();
        //若有配置多个实现，则采用最后一个
        while (iterator.hasNext()) {
            slaveDsRouter = iterator.next();
        }
    }

    public static SlaveDsRouter getSlaveDsRouter() {
        return (slaveDsRouter == null ? defaultSlaveDsRouter : slaveDsRouter);
    }
}
