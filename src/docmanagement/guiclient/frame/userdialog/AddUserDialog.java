package docmanagement.guiclient.frame.userdialog;

import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.frame.DialogBuilder;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.AddUserMessage;
import docmanagement.shared.requestandmessage.AddUserRequest;

import javax.swing.*;

public class AddUserDialog extends DialogBuilder {

    public AddUserDialog(GUIClient client){
        super("添加用户");
        this.addLabelAndTextFiled("用户名");
        this.addLabelAndPasswordFiled("密码");
        this.addRoleChooser();
        this.addOkAndCancelButton("添加", "取消",
                e -> {
                    var userToAdd = new User(
                            getText(0),
                            getText(1),
                            getChoosingRole(2)
                    );

                    var message = (AddUserMessage)client.connectToServer(
                            new AddUserRequest(client.getUser(), userToAdd));
                    if(message != null && message.isOk()){
                        JOptionPane.showMessageDialog(this, "添加成功");
                        this.setVisible(false);
                        client.getOperateFrame().userTableFlush();
                    }else{
                        JOptionPane.showMessageDialog(this, "添加失败");
                    }
                },
                CLOSE_DIALOG);
        this.displayIni();
    }
}
