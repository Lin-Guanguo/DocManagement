package docmanagement.guiclient.frame;

import docmanagement.shared.User;

import javax.swing.*;
import java.awt.*;
import java.util.EnumSet;
import java.util.function.Consumer;

public class UserDialog extends JDialog {
    public static final int DEFAULT_DISTANCE = 10;

    private final JLabel nameLabel = new JLabel("用户名: ");
    private final JLabel passwordLabel = new JLabel("密码: ");
    private final JLabel roleLabel = new JLabel("身份: ");

    private final JTextField nameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField();

    private JButton okButton;
    private JButton cancelButton;
    private final JPanel buttonPanel = new JPanel();

    private User.Role choosingRole = User.Role.IGNORE;

    private final Consumer<User> okButtonListener;
    private final Runnable cancelButtonListener;

    public enum Type{
        ADD_USER, MODIFY_USER, LOGIN
    }

    public UserDialog(Frame owner, Type type, Consumer<User> okButtonListener, Runnable cancelButtonListener) {
        super(owner);
        this.okButtonListener = okButtonListener;
        this.cancelButtonListener = cancelButtonListener;
        iniType(type);
        iniLayout(type);
        iniListener(type);
    }

    private void iniType(Type type){
        switch (type){
            case ADD_USER -> {
                okButton = new JButton("添加");
                cancelButton = new JButton("取消");
                this.setTitle("添加用户");
            }
            case MODIFY_USER -> {
                okButton = new JButton("修改");
                cancelButton = new JButton("取消");
                this.setTitle("修改用户信息");
            }
            case LOGIN -> {
                okButton = new JButton("登录");
                cancelButton = new JButton("退出");
                this.setTitle("登录档案管理系统");
            }
        }
    }

    public void display(){
        nameField.setText("");
        passwordField.setText("");
        this.setVisible(true);
    }

    void iniLayout(Type type){
        this.setLayout(new GridBagLayout());

        this.add(nameLabel,
                new GBC(0,0)
                        .setWeight(0,0)
                        .setInsets(DEFAULT_DISTANCE,DEFAULT_DISTANCE));
        this.add(passwordLabel,
                new GBC(0,1)
                        .setWeight(0,0)
                        .setInsets(DEFAULT_DISTANCE,DEFAULT_DISTANCE));

        this.add(nameField,
                new GBC(1,0,1,1)
                        .setFill(GridBagConstraints.HORIZONTAL)
                        .setInsets(DEFAULT_DISTANCE,0));
        this.add(passwordField,
                new GBC(1,1,1,1)
                        .setFill(GridBagConstraints.HORIZONTAL)
                        .setInsets(DEFAULT_DISTANCE,0));

        if(type != Type.LOGIN){
            var buttonGroup = new ButtonGroup();
            var roleButtonPanel = new JPanel();
            this.add(roleButtonPanel, new GBC(0,2,2,1).setFill(GridBagConstraints.BOTH));
            roleButtonPanel.add(roleLabel, new GBC(0,0).setInsets(0,DEFAULT_DISTANCE));

            boolean isFirst = true;
            for(var role : EnumSet.allOf(User.Role.class)) {
                if(role != User.Role.IGNORE){
                    var button = new JRadioButton(role.toString());
                    button.addActionListener(event->{
                        choosingRole = role;
                    });
                    roleButtonPanel.add(button, new GBC(1,0));
                    buttonGroup.add(button);
                    if(isFirst){
                        buttonGroup.setSelected(button.getModel(), true);
                        choosingRole = role;
                        isFirst = false;
                    }
                }
            };
        }

        this.add(buttonPanel, new GBC(0,3,2,1).setFill(GridBagConstraints.BOTH));
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(okButton, new GBC(0,0).setInsets(0,DEFAULT_DISTANCE));
        buttonPanel.add(cancelButton, new GBC(1,0).setInsets(0,DEFAULT_DISTANCE));

        this.pack();
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (int)((screenSize.getWidth() - this.getWidth()) / 2),
                (int)((screenSize.getHeight() - this.getHeight()) / 2)
        );
    }

    void iniListener(Type type){
        cancelButton.addActionListener(event->{
            this.setVisible(false);
            if(cancelButtonListener != null){
                cancelButtonListener.run();
            }
        });
        okButton.addActionListener(event->{
            this.setVisible(false);
            User readUser = new User(
                    nameField.getText(),
                    passwordField.getText(),
                    choosingRole);
            if(okButtonListener != null){
                okButtonListener.accept(readUser);
            }
        });
    }
}
