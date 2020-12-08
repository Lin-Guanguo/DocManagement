package docmanagement.server;

import docmanagement.server.data.ServerData;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.*;

public class Server {
    public static final int PORT;
    public static final String FILE_PATH;

    public static final int CONNECT_THREAD_NUMBER = Runtime.getRuntime().availableProcessors() + 1;
    public static final int QUEUE_CONNECT_NUMBER = 8 * CONNECT_THREAD_NUMBER;

    static {
        var properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(Path.of("serverconfig", "serverdata.properties")));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        PORT = Integer.parseInt(properties.getProperty("port"));
        FILE_PATH = properties.getProperty("filepath");
    }

    private final ThreadPoolExecutor threadPool;
    private final ServerData serverData;

    Server(){
        threadPool = new ThreadPoolExecutor(
                CONNECT_THREAD_NUMBER, CONNECT_THREAD_NUMBER, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(QUEUE_CONNECT_NUMBER));
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        serverData = new ServerData();

        try(var accept = new ServerSocket(PORT)){
            for(;;){
                var socket = accept.accept();
                threadPool.execute(new ConnectHandler(socket, serverData));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        threadPool.shutdown();
    }

    public static void main(String[] args){
        new Server();
    }
}

