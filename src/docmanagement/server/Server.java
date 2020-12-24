package docmanagement.server;

import docmanagement.server.data.DataProcessing;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.*;

public class Server {
    public static final int PORT;
    public static final String FILE_PATH;

    public static final int CONNECT_THREAD_NUMBER = 8 * Runtime.getRuntime().availableProcessors();
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
    private final DataProcessing dataProcessing;

    Server(){
        threadPool = ServerThreadPoolFactory.newThreadPool(CONNECT_THREAD_NUMBER, QUEUE_CONNECT_NUMBER);

        dataProcessing = new DataProcessing();

        try(var accept = new ServerSocket(PORT)){
            for(;;){
                var socket = accept.accept();

                System.out.println(socket.getRemoteSocketAddress() +
                        " : connect, " + new Timestamp(System.currentTimeMillis()).toString());

                threadPool.execute(new ConnectHandler(socket, dataProcessing));
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


