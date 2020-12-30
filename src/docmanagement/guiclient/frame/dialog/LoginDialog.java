package docmanagement.guiclient.frame.dialog;

import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.frame.tool.DialogBuilder;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.LoginCheckMessage;
import docmanagement.shared.requestandmessage.LoginCheckRequest;

import javax.swing.*;

public class LoginDialog extends DialogBuilder {

    public LoginDialog(GUIClient client){
        super("登录 档案管理系统");
        this.addLabelAndTextFiled("用户名");
        this.addLabelAndPasswordFiled("密码");
        this.addOkAndCancelButton("登录", "取消",
                e -> {
                    var user = new User(
                            getText(0),
                            getText(1),
                            User.Role.IGNORE
                    );
                    var message = (LoginCheckMessage) client.connectToServer(
                            new LoginCheckRequest(user));
                    if (!message.isOk()) {
                        JOptionPane.showMessageDialog(null, "用户名或密码错误");
                        this.setVisible(true);
                    } else {
                        client.loginSucceed(new User(user.getName(), user.getPassword(), message.getRole()));
                    }
                },
                e -> System.exit(0));
        this.displayIni();
    }

}
