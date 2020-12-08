package docmanagement.guiclient.eventhandler;

import docmanagement.guiclient.GUIClient;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.LoginCheckMessage;
import docmanagement.shared.requestandmessage.LoginCheckRequest;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginHandler implements Consumer<User> {
    private final GUIClient client;
    private final Window owner;

    public LoginHandler(GUIClient client, Window owner) {
        this.client = client;
        this.owner = owner;
    }

    @Override
    public void accept(User user) {
        var message = (LoginCheckMessage) client.connectToServer(
                new LoginCheckRequest(user));
        if (!message.isOk()) {
            JOptionPane.showMessageDialog(null, "用户名或密码错误");
            owner.setVisible(true);
        } else {
            client.loginSucceed(new User(user.getName(), user.getPassword(), message.getRole()));
        }
    }
}
