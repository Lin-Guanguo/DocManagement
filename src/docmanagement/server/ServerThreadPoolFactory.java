package docmanagement.server;

import java.util.concurrent.*;

public class ServerThreadPoolFactory {
    public static ThreadPoolExecutor newThreadPool(int connectThreadNumber, int queueConnectNumber){
        var threadPool = new ThreadPoolExecutor(
                connectThreadNumber, connectThreadNumber, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(queueConnectNumber));
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return threadPool;
    }
}
