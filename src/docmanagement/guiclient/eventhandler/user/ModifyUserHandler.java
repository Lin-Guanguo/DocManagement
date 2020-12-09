package docmanagement.guiclient.eventhandler.user;

import docmanagement.guiclient.GUIClient;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.ModifyUserMessage;
import docmanagement.shared.requestandmessage.ModifyUserRequest;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ModifyUserHandler implements Consumer<User> {
    private final GUIClient client;
    private final Window owner;

    public ModifyUserHandler(GUIClient client, Window owner) {
        this.client = client;
        this.owner = owner;
    }

    @Override
    public void accept(User toModify) {
        var message = (ModifyUserMessage)client.connectToServer(
                new ModifyUserRequest(client.getUser(), toModify));
        if(message != null && message.isOk()){
            if(toModify.getName().equals(client.getUser().getName()) ){
                JOptionPane.showMessageDialog(owner, "修改成功, 请重新登陆");
                owner.setVisible(false);
                client.switchUser();
            }else{
                JOptionPane.showMessageDialog(owner, "修改成功");
                owner.setVisible(false);
                client.getOperateFrame().userTableFlush();
            }
        }else{
            JOptionPane.showMessageDialog(owner, "添加失败");
        }
    }
}
