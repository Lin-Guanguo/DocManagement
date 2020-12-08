package docmanagement.server.data;

import docmanagement.server.Server;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * 自制简易数据库连接池
 * 缓存数据库连接，60~120秒未使用则关闭
 */
public class DatabaseConnectPool implements DataSource {
    static final String DATABASE_DRIVER;
    static final String DATABASE_URL;
    static final String DATABASE_NAME;
    static final String DATABASE_PASSWORD;

    private static final long CACHED_LIVE_TIME = 60000;
    private static final int MAX_CONNECTION = Server.CONNECT_THREAD_NUMBER;

    static {
        var properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(Path.of("serverconfig", "mysqldata.properties")));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        DATABASE_DRIVER = properties.getProperty("driver");
        DATABASE_URL = properties.getProperty("url");
        DATABASE_NAME = properties.getProperty("name");
        DATABASE_PASSWORD = properties.getProperty("password");

        try {
            Class.forName(DATABASE_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private class CachedConnection{
        private final long createTime;
        private final Connection connection;

        public CachedConnection(long createTime, Connection connection) {
            this.createTime = createTime;
            this.connection = connection;
        }

        public Connection getConnection() {
            return connection;
        }

        /**
         * if this cache is expired then close it and return true;
         * @return is close
         */
        public boolean closeIfExpired(){
            boolean res = System.currentTimeMillis() > createTime + CACHED_LIVE_TIME;
            if(res){
                try {
                    connection.close();
                    usingConnectionCount.decrementAndGet();
                    cachedConnectionCount.decrementAndGet();
                    //LOG
                    System.out.println("关闭一个数据库连接, 现有连接数: " + usingConnectionCount);
                } catch (SQLException e) {
                    System.err.println("checkExpired close connection error");
                    e.printStackTrace();
                }
            }
            return res;
        }
    }

    private final AtomicInteger usingConnectionCount = new AtomicInteger();
    private final AtomicInteger cachedConnectionCount = new AtomicInteger();
    private final LinkedBlockingDeque<CachedConnection> cachedConnectionPool = new LinkedBlockingDeque<>();

    private Connection getConnectionFromPool() throws SQLException, InterruptedException {
        var cachedConnection = cachedConnectionPool.pollFirst();
        if(cachedConnection != null){
            cachedConnectionCount.decrementAndGet();
            return cachedConnection.getConnection();
        }else {
            if(usingConnectionCount.get() < MAX_CONNECTION){
                usingConnectionCount.incrementAndGet();
                //LOG
                System.out.println("创建一个数据库连接, 现有连接数: " + usingConnectionCount);
                return DriverManager.getConnection(DATABASE_URL, DATABASE_NAME, DATABASE_PASSWORD);
            }else{
                cachedConnection = cachedConnectionPool.takeFirst();
                cachedConnectionCount.decrementAndGet();
                return cachedConnection.getConnection();
            }
        }
    }

    private volatile boolean cacheCleanTimerIsStart = false;
    private final Timer cacheCleanTimer = new Timer();

    private void returnConnectionToPool(Connection connection){
        cachedConnectionPool.offerLast(new CachedConnection(System.currentTimeMillis(), connection));
        cachedConnectionCount.incrementAndGet();
        //防止与 CacheCleanTimerThread 内的操作产生竞争
        synchronized (this){
            if(!cacheCleanTimerIsStart){
                startCacheCleanTimer();
            }
        }
    }

    private void startCacheCleanTimer(){
        cacheCleanTimerIsStart = true;
        cacheCleanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                var mayExpired = cachedConnectionPool.pollFirst();
                while(mayExpired != null && mayExpired.closeIfExpired()){
                    mayExpired = cachedConnectionPool.pollFirst();
                }
                if(mayExpired != null) {
                    cachedConnectionPool.offerFirst(mayExpired);
                }
                synchronized (this) {
                    if (cachedConnectionPool.isEmpty()) {
                        cacheCleanTimerIsStart = false;
                    }else {
                        startCacheCleanTimer();
                    }
                }
            }
        }, CACHED_LIVE_TIME * 2);
    }

    private class ConnectionProxyHandler implements InvocationHandler{
        private final Connection target;

        public ConnectionProxyHandler(Connection t) {
            this.target = t;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(method.getName().equals("close")){
                returnConnectionToPool(target);
                return null;
            }else{
                return method.invoke(target, args);
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection rawConnection = null;
        try {
            rawConnection = getConnectionFromPool();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        var proxyHandler = new ConnectionProxyHandler(rawConnection);
        var proxy = (Connection) java.lang.reflect.Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{ Connection.class },
                proxyHandler
        );
        return proxy;
    }










    @Override
    @Deprecated
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    @Deprecated
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    @Deprecated
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    @Deprecated
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    @Deprecated
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    @Deprecated
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    @Deprecated
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    @Deprecated
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
