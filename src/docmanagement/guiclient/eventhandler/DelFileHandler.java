package docmanagement.guiclient.eventhandler;

import docmanagement.guiclient.GUIClient;
import docmanagement.shared.requestandmessage.DelFileMessage;
import docmanagement.shared.requestandmessage.DelFileRequest;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class DelFileHandler implements FileHandler {
    private final GUIClient client;
    private final Window owner;

    public DelFileHandler(GUIClient client, Window owner) {
        this.client = client;
        this.owner = owner;
    }

    @Override
    public void acceptFile(int id, String name, String description, Path path) {
        var message = (DelFileMessage)client.connectToServer(
                new DelFileRequest(client.getUser(), id));
        if(message.isOk()){
            JOptionPane.showMessageDialog(owner,
                    "删除成功","删除文件",JOptionPane.PLAIN_MESSAGE);
            owner.setVisible(false);
            client.getOperateFrame().fileTableFlush();
        }else{
            JOptionPane.showMessageDialog(owner,
                    "删除失败","删除文件",JOptionPane.WARNING_MESSAGE);
        }
    }

}
