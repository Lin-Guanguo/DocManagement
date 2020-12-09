package docmanagement.guiclient.background;

import docmanagement.guiclient.GUIClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BackgroundExecutor {
    private final ThreadPoolExecutor backgroundThreadPool = (ThreadPoolExecutor)Executors.newCachedThreadPool();

    private final GUIClient client;

    public BackgroundExecutor(GUIClient client){
        this.client = client;
    }

    public void submitFileTask(FileTask task){
        backgroundThreadPool.submit(task::doInBackground);
    }
}
