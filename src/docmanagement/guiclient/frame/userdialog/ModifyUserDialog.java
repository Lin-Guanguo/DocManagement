package docmanagement.guiclient.frame.userdialog;


import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.frame.DialogBuilder;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.ModifyUserMessage;
import docmanagement.shared.requestandmessage.ModifyUserRequest;

import javax.swing.*;

public class ModifyUserDialog extends DialogBuilder {

    public ModifyUserDialog(GUIClient client){
        super("修改用户信息");
        this.addLabelAndTextFiled("用户名");
        this.addLabelAndPasswordFiled("密码");
        this.addRoleChooser();
        this.addOkAndCancelButton("修改", "取消",
            e -> {
                var toModify = new User(
                        this.getText(0),
                        this.getText(1),
                        this.getChoosingRole(2)
                );
                var message = (ModifyUserMessage)client.connectToServer(
                        new ModifyUserRequest(client.getUser(), toModify));
                if(message != null && message.isOk()){
                    if(toModify.getName().equals(client.getUser().getName()) ){
                        JOptionPane.showMessageDialog(this, "修改成功, 请重新登陆");
                        this.setVisible(false);
                        client.switchUser();
                    }else{
                        JOptionPane.showMessageDialog(this, "修改成功");
                        this.setVisible(false);
                        client.getOperateFrame().userTableFlush();
                    }
                }else{
                    JOptionPane.showMessageDialog(this, "添加失败");
                }
            },
            CLOSE_DIALOG);
        this.displayIni();
    }
}
