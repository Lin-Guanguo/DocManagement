package docmanagement.guiclient.eventhandler.user;

import docmanagement.guiclient.GUIClient;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.DelUserMessage;
import docmanagement.shared.requestandmessage.DelUserRequest;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DelUserHandler implements Consumer<User> {
    private final GUIClient client;
    private final Window owner;

    public DelUserHandler(GUIClient client, Window owner) {
        this.client = client;
        this.owner = owner;
    }
    @Override
    public void accept(User user) {
        var message = (DelUserMessage)client.connectToServer(
                new DelUserRequest(client.getUser(), user.getName()));
        if(message.isOk()){
            JOptionPane.showMessageDialog(owner, "删除成功");
            owner.setVisible(false);
            client.getOperateFrame().userTableFlush();
        }else{
            JOptionPane.showMessageDialog(owner, "删除失败");
        }
    }
}
