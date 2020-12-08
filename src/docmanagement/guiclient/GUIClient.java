package docmanagement.guiclient;

import docmanagement.guiclient.frame.OperateFrame;
import docmanagement.guiclient.frame.UserDialog;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

public class GUIClient {
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

    private UserDialog loginFrame = null;
    private OperateFrame operateFrame = null;
    private User user = null;
    private Set<ServerOperation> permissions = null;

    public GUIClient() {
        loginFrame = new UserDialog(null, this, UserDialog.Type.LOGIN);
        EventQueue.invokeLater(() -> {
            loginFrame.setVisible(true);
        });
    }

    public void loginSucceed(User user){
        this.user = user;
        var message = (GetPermissionMessage)connectToServer(
                new GetPermissionRequest(user));
        if(message == null){ return; }
        permissions = message.getAllowed();

        EventQueue.invokeLater(()->{
            EventQueue.invokeLater(()->loginFrame.setVisible(false));
            operateFrame = new OperateFrame(this);
            EventQueue.invokeLater(()->operateFrame.setVisible(true));
        });
    }

    public void switchUser(){
        EventQueue.invokeLater(()-> {
            operateFrame.setVisible(false);
            operateFrame = null;
            user = null;
            EventQueue.invokeLater(()->loginFrame.setVisible(true));
        });
    }

    public AbstractMessage connectToServer(AbstractRequest request){
        return connectToServer(request, null);
    }

    @FunctionalInterface
    public interface ConnectMoreAction{
        void run(AbstractMessage message, InputStream socketIn, OutputStream socketOut) throws IOException;
    }

    public AbstractMessage connectToServer(AbstractRequest request, ConnectMoreAction doMore){
        try(var socket = new Socket(host, port)){
            var in = socket.getInputStream();
            var out = socket.getOutputStream();

            new ObjectOutputStream(out).writeObject(request);
            var message = (AbstractMessage)new ObjectInputStream(in).readObject();

            if(doMore != null){
                doMore.run(message, in, out);
            }
            return message;
        } catch (IOException e){
            JOptionPane.showMessageDialog(null, "服务器连接异常");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "服务器回复异常");
        }
        return null;
    }



    public User getUser() {
        return user;
    }

    public Set<ServerOperation> getPermissions() {
        return permissions;
    }

    public OperateFrame getOperateFrame() {
        return operateFrame;
    }

    public static void main(String[] args){
        new GUIClient();
    }
}