package com.liq.framework.chris;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * Created by yuekuo.liq on 15/8/9.
 */
public class ChrisConnection implements Connection {
    private final List<LazyMethodHolder> lazyMethods = new ArrayList<LazyMethodHolder>();
    private final ChrisDataSource chrisDataSource;
    private boolean isClosed;
    private String catalog;
    private int transactionIsolationLevel;
    private Map<String, Class<?>> map;
    private int holdability;
    private int timeout;
    private String schema;
    private Properties clientInfos;
    private boolean isAutoCommit = true;
    private Queue<LazyMethodHolder> statementQueue = new LinkedList<LazyMethodHolder>();

    public ChrisConnection(ChrisDataSource chrisDataSource) {
        this.chrisDataSource = chrisDataSource;
    }

    public List<LazyMethodHolder> getLazyMethods() {
        return lazyMethods;
    }

    public void addLazyMethod(Method method, Object[] args) {
        lazyMethods.add(new LazyMethodHolder(method, args));
    }

    public void offerStatement(Method method, Object[] args) {
        statementQueue.offer(new LazyMethodHolder(method, args));
    }

    public LazyMethodHolder pollStatement() {
        return statementQueue.poll();
    }

    public ChrisDataSource getChrisDataSource() {
        return chrisDataSource;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return (PreparedStatement) Proxy.newProxyInstance(ChrisPreparedStatement.class.getClassLoader(), ChrisPreparedStatement.class.getInterfaces(), new PreparedStatementHandler(storePreparedStatement(null)));
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return (PreparedStatement) Proxy.newProxyInstance(ChrisPreparedStatement.class.getClassLoader(), ChrisPreparedStatement.class.getInterfaces(), new PreparedStatementHandler(storePreparedStatement(sql)));
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new UnsupportedOperationException("prepareCall");
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new UnsupportedOperationException("nativeSQL");
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (!isAutoCommit) {
            //兼容spring tx,spring会在释放connection时复位autoCommit
            ConnectionHolder.setAutoCommit(autoCommit);
        }
        isAutoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        //返回true，则spring就会执行setAutoCommit(false)
        return isAutoCommit;
    }

    @Override
    public void commit() throws SQLException {
        ConnectionHolder.commit();
    }

    @Override
    public void rollback() throws SQLException {
        ConnectionHolder.rollback();
    }

    @Override
    public void close() throws SQLException {
        //调用真实Connection#close(),若使用了连接池，则实际是将connection return到池中,
        //而ChrisConnection将被Spring从ThreadLocal中清除掉，被gc回收
        ConnectionHolder.close();
        ConnectionHolder.clearConnection();
        isClosed = true;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException("getMetaData");
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return ConnectionHolder.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.catalog = catalog;
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.catalog;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.transactionIsolationLevel = level;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return transactionIsolationLevel;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException("getWarnings");
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException("clearWarnings");
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException("createStatement");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return (PreparedStatement) Proxy.newProxyInstance(ChrisPreparedStatement.class.getClassLoader(), ChrisPreparedStatement.class.getInterfaces(), new PreparedStatementHandler(storePreparedStatement(sql)));

    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException("prepareCall");
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.map;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.map = map;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.holdability = holdability;
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.holdability;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new UnsupportedOperationException("setSavepoint");
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new UnsupportedOperationException("setSavepoint");
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("rollback(savepoint)");
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("releaseSavepoint");
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("createStatement");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return (PreparedStatement) Proxy.newProxyInstance(ChrisPreparedStatement.class.getClassLoader(), ChrisPreparedStatement.class.getInterfaces(), new PreparedStatementHandler(storePreparedStatement(sql)));
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("prepareCall");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return (PreparedStatement) Proxy.newProxyInstance(ChrisPreparedStatement.class.getClassLoader(), ChrisPreparedStatement.class.getInterfaces(), new PreparedStatementHandler(storePreparedStatement(sql)));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return (PreparedStatement) Proxy.newProxyInstance(ChrisPreparedStatement.class.getClassLoader(), ChrisPreparedStatement.class.getInterfaces(), new PreparedStatementHandler(storePreparedStatement(sql)));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return (PreparedStatement) Proxy.newProxyInstance(ChrisPreparedStatement.class.getClassLoader(), ChrisPreparedStatement.class.getInterfaces(), new PreparedStatementHandler(storePreparedStatement(sql)));
    }

    private ChrisPreparedStatement storePreparedStatement(String sql) {
        ChrisPreparedStatement chrisPreparedStatement = new ChrisPreparedStatement();
        chrisPreparedStatement.setSql(sql);
        chrisPreparedStatement.setChrisConnection(this);
        return chrisPreparedStatement;
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new UnsupportedOperationException("createClob");
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new UnsupportedOperationException("createBlob");
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new UnsupportedOperationException("createBlob");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new UnsupportedOperationException("createSQLXML");
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        //这里暂时用连接是否已关闭来标识
        return this.isClosed();
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        if (this.clientInfos == null) {
            this.clientInfos = new Properties();
        }
        this.clientInfos.put(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.clientInfos = properties;
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        if (this.clientInfos != null) {
            return (String) clientInfos.get(name);
        }
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.clientInfos;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new UnsupportedOperationException("createArrayOf");
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new UnsupportedOperationException("createStruct");
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.schema = schema;
    }

    @Override
    public String getSchema() throws SQLException {
        return schema;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new UnsupportedOperationException("abort");
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.timeout = milliseconds;
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return timeout;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("unwrap");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("isWrapperFor");
    }
}
