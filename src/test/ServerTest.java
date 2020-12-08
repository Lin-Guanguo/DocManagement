package test;

import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.*;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ServerTest {
    public static final String host;
    public static final int port;
    static {
        var properties = new Properties();
        try {
            properties.load(Files.newInputStream(Path.of("clientconfig", "clientdata.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        host = properties.getProperty("serverhost");
        port = Integer.parseInt(properties.getProperty("port"));
    }

    public static void main(String[] args){
        var t1 = new Thread(()->{
            for(int i = 0; i < 16; ++i){
                request(new ListUserRequest(new User("kate", "123", User.Role.ADMINISTRATOR)));
            }
        });
        var t2 = new Thread(()->{
            for(int i = 0; i < 16; ++i){
                request(new ListFileRequest(new User("kate", "123", User.Role.ADMINISTRATOR)));
            }
        });
        var t3 = new Thread(()->{
            for(int i = 0; i < 16; ++i){
                request(new DelUserRequest(new User("kate", "123", User.Role.ADMINISTRATOR), "none"));
            }
        });
        t1.start();
        t2.start();
        t3.start();
    }

    private static AbstractMessage request(AbstractRequest request){
        try(var socket = new Socket(host, port)){
            var in = socket.getInputStream();
            var out = socket.getOutputStream();

            new ObjectOutputStream(out).writeObject(request);
            var message = (AbstractMessage)new ObjectInputStream(in).readObject();
            return message;
        } catch (IOException e){
            JOptionPane.showMessageDialog(null, "服务器连接异常");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "服务器回复异常");
        }
        return null;
    }
}
