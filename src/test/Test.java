package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws IOException {
        /*var server = new ServerSocket(8005);
        for(;;){
            var socket = server.accept();
            System.out.println("socket connect");
            var socketScanner = new Scanner(socket.getInputStream());
            var socketWriter = new PrintWriter(socket.getOutputStream());

            while (socketScanner.hasNext()){
                var s = socketScanner.nextLine();
                socketWriter.println(s);
                socketWriter.flush();
                System.out.println("echo");
            }

            socket.close();
            System.out.println("socket close");
        }*/

    }
}






