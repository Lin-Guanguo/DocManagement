package docmanagement.guiclient.eventhandler.user;

import docmanagement.guiclient.GUIClient;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.ModifyAllUserMessage;
import docmanagement.shared.requestandmessage.ModifyAllUserRequest;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ModifyAllUserHandler implements Consumer<User> {
    private final GUIClient client;
    private final Window owner;

    public ModifyAllUserHandler(GUIClient client, Window owner) {
        this.client = client;
        this.owner = owner;
    }

    @Override
    public void accept(User toModify) {
        var message = (ModifyAllUserMessage)client.connectToServer(
                new ModifyAllUserRequest(client.getUser(), toModify));
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
