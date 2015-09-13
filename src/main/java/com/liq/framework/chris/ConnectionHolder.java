package com.liq.framework.chris;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Spring的ThreadLocal()中绑定的是ChrisConnection,
 * 这里才是持有真实的Connection
 * Created by yuekuo.liq on 15/8/28.
 */
public class ConnectionHolder {
    private static ThreadLocal<HashMap<String, Connection>> slaveConnectionThreadLocal = new ThreadLocal<HashMap<String, Connection>>() {
        @Override
        protected HashMap<String, Connection> initialValue() {
            return new HashMap<String, Connection>();
        }
    };

    private static ThreadLocal<Connection> masterConnectionThreadLocal = new ThreadLocal<Connection>();


    public static void setSlaveConnection(String dsKey, Connection connection) {
        slaveConnectionThreadLocal.get().put(dsKey, connection);
    }

    public static Connection getSlaveConnection(String dsKey) {
        return slaveConnectionThreadLocal.get().get(dsKey);
    }

    public static void setMasterConnectionThreadLocal(Connection connection) {
        masterConnectionThreadLocal.set(connection);
    }

    public static Connection getMasterConnection() {
        return masterConnectionThreadLocal.get();
    }


    public static void clearConnection() {
        slaveConnectionThreadLocal.remove();
        masterConnectionThreadLocal.remove();
    }

    public static void close() throws SQLException {
        if (getMasterConnection() != null) {
            getMasterConnection().close();
        }
        for (Iterator<Map.Entry<String, Connection>> iterator = slaveConnectionThreadLocal.get().entrySet().iterator(); iterator.hasNext(); ) {
            iterator.next().getValue().close();
        }
    }

    public static void commit() throws SQLException {
        if (getMasterConnection() != null) {
            getMasterConnection().commit();
        }

        for (Iterator<Map.Entry<String, Connection>> iterator = slaveConnectionThreadLocal.get().entrySet().iterator(); iterator.hasNext(); ) {
            iterator.next().getValue().commit();
        }
    }

    public static void rollback() throws SQLException {
        if (getMasterConnection() != null) {
            getMasterConnection().rollback();
        }
        for (Iterator<Map.Entry<String, Connection>> iterator = slaveConnectionThreadLocal.get().entrySet().iterator(); iterator.hasNext(); ) {
            iterator.next().getValue().rollback();
        }
    }

    public static boolean isReadOnly() {
        return getMasterConnection() == null ? true : false;
    }

    public static void setAutoCommit(boolean autoCommit) throws SQLException {
        if (getMasterConnection() != null) {
            getMasterConnection().setAutoCommit(autoCommit);
        }
        for (Iterator<Map.Entry<String, Connection>> iterator = slaveConnectionThreadLocal.get().entrySet().iterator(); iterator.hasNext(); ) {
            iterator.next().getValue().setAutoCommit(autoCommit);
        }
    }
}
