package docmanagement.guiclient.frame.userdialog;

import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.frame.tool.DialogBuilder;
import docmanagement.shared.requestandmessage.ChangePasswordMessage;
import docmanagement.shared.requestandmessage.ChangePasswordRequest;

import javax.swing.*;

public class ChangePasswordDialog extends DialogBuilder {
    public ChangePasswordDialog(GUIClient client) {
        super("修改密码");
        this.addLabelAndPasswordFiled("新密码");
        this.addLabelAndPasswordFiled("确认新密码");

        this.addOkAndCancelButton("修改", "取消",
                e -> {
                    var password = getText(0);
                    if(password.equals(getText(1))){
                        var message = (ChangePasswordMessage)client.connectToServer(
                                new ChangePasswordRequest(client.getUser(), password));
                        if(message != null && message.isOk()){
                            JOptionPane.showMessageDialog(client.getOperateFrame(), "修改成功, 请重新登陆");
                            this.setVisible(false);
                            client.switchUser();
                        }else{
                            JOptionPane.showMessageDialog(this, "修改失败");
                        }
                    }else{
                        JOptionPane.showMessageDialog(this,"两次输入不一致","修改密码", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                },
                CLOSE_DIALOG);
        this.displayIni();
    }
}
