package com.liq.framework.chris;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuekuo on 15/8/10.
 */
public class ChrisDataSource implements DataSource, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(ChrisDataSource.class);
    private final DataSource masterDataSource;
    private final Map<String, DataSource> slavesDataSource;
    private final boolean masterCheck;
    private final boolean slavesCheck;

    public ChrisDataSource(DataSource masterDataSource, Map<String, DataSource> slavesDataSource) {
        this(masterDataSource, slavesDataSource, true, true);
    }

    public ChrisDataSource(DataSource masterDataSource, Map<String, DataSource> slavesDataSource, boolean masterCheck, boolean slavesCheck) {
        this.masterDataSource = masterDataSource;
        this.slavesDataSource = slavesDataSource;
        this.masterCheck = masterCheck;
        this.slavesCheck = slavesCheck;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return (Connection) Proxy.newProxyInstance(ChrisConnection.class.getClassLoader(), ChrisConnection.class.getInterfaces(), new ConnectionHandler(new ChrisConnection(this)));
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        logger.warn("Unsupported getConnection(username,password) , call getConnection()  instead!");
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("setLogWriter");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        throw new SQLException("DataSource of type [" + getClass().getName() +
                "] cannot be unwrapped as [" + iface.getName() + "]");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (masterCheck) {
            if (masterDataSource == null) {
                throw new IllegalArgumentException("masterDataSource is null");
            }
            checkDataBase("master", masterDataSource);
        }

        if (slavesCheck) {
            if (slavesDataSource == null ||
                    slavesDataSource.size() == 0) {
                logger.warn("the slavesDataSource is empty , chris will use the masterDataSource only!");
            } else {
                for (Iterator<Map.Entry<String, DataSource>> iterator = slavesDataSource.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, DataSource> entry = iterator.next();
                    checkDataBase(entry.getKey(), entry.getValue());
                }
            }
        }

        logger.info("chris dataSource init successfully ");
    }

    public DataSource getMasterDataSource() {
        return masterDataSource;
    }


    public boolean isMasterCheck() {
        return masterCheck;
    }


    public boolean isSlavesCheck() {
        return slavesCheck;
    }

    public Map<String, DataSource> getSlavesDataSource() {
        return slavesDataSource;
    }

    private void checkDataBase(String name, DataSource dataSource) throws SQLException {
        try {
            dataSource.getConnection().createStatement().execute("select user()");
        } catch (SQLException e) {
            logger.error("the " + name + " dataSource can not be connected , please check your internet or " + name + " database start success ?", e);
            throw e;
        }
    }
}
