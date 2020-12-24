package docmanagement.server;

import java.util.concurrent.*;

public class ServerThreadPoolFactory {
    public static ThreadPoolExecutor newThreadPool(int connectThreadNumber, int queueConnectNumber){
        return new ThreadPoolExecutor(
                connectThreadNumber, connectThreadNumber, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(queueConnectNumber));
    }
}
