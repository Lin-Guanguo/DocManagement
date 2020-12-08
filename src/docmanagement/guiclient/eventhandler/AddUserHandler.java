package docmanagement.guiclient.eventhandler;

import docmanagement.guiclient.GUIClient;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.AddUserMessage;
import docmanagement.shared.requestandmessage.AddUserRequest;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class AddUserHandler implements Consumer<User> {
    private final GUIClient client;
    private final Window owner;

    public AddUserHandler(GUIClient client, Window owner) {
        this.client = client;
        this.owner = owner;
    }

    @Override
    public void accept(User user) {
        var message = (AddUserMessage)client.connectToServer(
                new AddUserRequest(client.getUser(), user));
        if(message != null && message.isOk()){
            JOptionPane.showMessageDialog(owner, "添加成功");
            owner.setVisible(false);
            client.getOperateFrame().userTableFlush();
        }else{
            JOptionPane.showMessageDialog(owner, "添加失败");
        }
    }
}
