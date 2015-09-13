package com.liq.framework.chris;

import com.liq.framework.chris.executor.Executor;
import com.liq.framework.chris.executor.ExecutorException;
import com.liq.framework.chris.executor.ExecutorInvocation;
import com.liq.framework.chris.executor.InvocationContext;
import com.liq.framework.chris.spi.factory.SlaveDsRouterFactory;
import com.liq.framework.chris.spi.factory.TransactionReadOnlyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuekuo on 15/8/13.
 */
public class SqlExecutor {
    private static final Logger logger = LoggerFactory.getLogger(SqlExecutor.class);

    public static Object execute(ChrisPreparedStatement chrisPreparedStatement) throws SQLException {
        ChrisConnection chrisConnection = chrisPreparedStatement.getChrisConnection();
        boolean isReadOnly = TransactionReadOnlyFactory.getTransactionReadOnly().isReadyOnly(chrisPreparedStatement.getSql());
        Connection connection;
        String dsKey = null;
        if (isReadOnly) {
            Map<String, DataSource> slavesDataSource = chrisConnection.getChrisDataSource().getSlavesDataSource();
            dsKey = SlaveDsRouterFactory.getSlaveDsRouter().routerDataSource(chrisPreparedStatement.getSql(), new ArrayList<String>(slavesDataSource.keySet()));
            logger.info("the current slave dataSource Key is : " + dsKey);
            //路由到同一个ds只读数据源，则直接复用当前的Connection
            connection = ConnectionHolder.getSlaveConnection(dsKey);
            if (connection == null) {
                DataSource ds = slavesDataSource.get(dsKey);
                if (ds == null) {
                    throw new RuntimeException("dataSource is null ! can not find the dataSource mapping by dsKey : " + dsKey);
                }
                connection = ds.getConnection();
                initConnection(chrisConnection, connection);
                ConnectionHolder.setSlaveConnection(dsKey, connection);
            }
        } else {
            connection = ConnectionHolder.getMasterConnection();
            //如果connection不存在 则创建一个新的，否则直接复用Connection,保证 insert update delete的事务性
            if (connection == null) {
                connection = chrisConnection.getChrisDataSource().getMasterDataSource().getConnection();
                initConnection(chrisConnection, connection);
                ConnectionHolder.setMasterConnectionThreadLocal(connection);
            }
        }
        connection.setReadOnly(isReadOnly);

        logger.info("the current transaction  isReadOnly : " + isReadOnly);
        LazyMethodHolder methodHolder = chrisConnection.pollStatement();
        if (methodHolder == null) {
            logger.warn("there is no statement to execute ?");
            return null;
        }
        try {
            // TODO 传参太多，优化？
            return executeStatement(methodHolder.getLazyMethod(), methodHolder.getArgs(), chrisPreparedStatement, connection, dsKey, isReadOnly);
        } catch (Exception e) {
            logger.error("execute preparedStatement error ", e);
            //这里单独处理SqlException,方便给spring等框架识别
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new RuntimeException(e);
        }
    }

    private static Object executeStatement(Method method, Object[] args, ChrisPreparedStatement chrisPreparedStatement, Connection connection, String sql, boolean isReadOnly) throws SQLException, InvocationTargetException, IllegalAccessException {
        final Statement statement = (Statement) method.invoke(connection, args);
        final List<LazyMethodHolder> lazyMethodHolders = chrisPreparedStatement.getLazyMethods();
        final int size = lazyMethodHolders.size();
        for (int i = 0; i < size - 1; i++) {
            lazyMethodHolders.get(i).getLazyMethod().invoke(statement, lazyMethodHolders.get(i).getArgs());
        }
        // execute% 肯定是最后被添加到 lazyMethodHolders,所以最后执行即可
        return new ExecutorInvocation().execute(new Executor() {
            @Override
            public Object execute(InvocationContext invocationContext) throws ExecutorException {
                try {
                    return lazyMethodHolders.get(size - 1).getLazyMethod().invoke(statement, lazyMethodHolders.get(size - 1).getArgs());
                } catch (Exception e) {
                    logger.error("invoke method executeStatement error", e);
                    throw new ExecutorException(e);
                }
            }
        }, chrisPreparedStatement.getSql(), lazyMethodHolders.get(size - 1).getArgs(), sql, isReadOnly);
    }

    private static void initConnection(ChrisConnection chrisConnection, Connection connection) throws SQLException {
        List<LazyMethodHolder> lazyMethodHolders = chrisConnection.getLazyMethods();
        for (LazyMethodHolder lazyMethodHolder : lazyMethodHolders) {
            Method method = lazyMethodHolder.getLazyMethod();
            Object[] args = lazyMethodHolder.getArgs();
            try {
                //不用Spring的设置
                if ("setReadOnly".equals(method.getName())) {
                    continue;
                }
                method.invoke(connection, args);
            } catch (IllegalAccessException e) {
                logger.error(e.toString());
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                logger.error(e.toString());
                //遇到SQLException要抛出，方便上层处理，如:rollback()
                if (e.getCause() instanceof SQLException) {
                    throw (SQLException) e.getCause();
                }
            }
        }
    }
}
