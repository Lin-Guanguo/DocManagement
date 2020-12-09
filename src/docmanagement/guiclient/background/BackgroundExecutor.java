package docmanagement.guiclient.background;

import docmanagement.guiclient.GUIClient;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class BackgroundExecutor {
    private final ThreadPoolExecutor backgroundThreadPool = (ThreadPoolExecutor)Executors.newCachedThreadPool();

    private final GUIClient client;

    public BackgroundExecutor(GUIClient client){
        this.client = client;
    }

    public Future<?> submitFileTask(FileTask task){
        return backgroundThreadPool.submit(task::execute);
    }
}
