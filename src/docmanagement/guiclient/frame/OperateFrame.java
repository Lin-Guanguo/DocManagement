package docmanagement.guiclient.frame;

import docmanagement.guiclient.GUIClient;
import docmanagement.shared.requestandmessage.*;

import javax.swing.*;
import java.awt.*;

public class OperateFrame extends JFrame {
    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_HEIGHT= 400;
    public static final int DEFAULT_DISTANCE = 10;

    JTabbedPane tabbedPane = new JTabbedPane();

    private final JPanel personalInfoPanel = new JPanel();
    private final JPanel personalInfoButtonPanel = new JPanel();

    private JPanel userManagementPanel = null;
    private JPanel userManagementButtonPanel = null;
    private UserDialog addUserDialog  = null;
    private UserDialog modifyAllUserDialog = null;
    private UserDialog delUserDialog = null;
    private ManagementTable userTable = null;
    private JButton userTableFlushButton = null;

    private JPanel fileManagementPanel = null;
    private JPanel fileManagementButtonPanel = null;
    private FileDialog uploadFileDialog = null;
    private FileDialog downloadFileDialog = null;
    private FileDialog delFileDialog = null;
    private ManagementTable fileTable = null;
    private JButton fileTableFlushButton = null;



    private final GUIClient client;

    public OperateFrame(GUIClient client){
        this.client = client;

        this.setTitle("文件管理系统");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());

        tabbedPane.setBorder(BorderFactory.createEmptyBorder(DEFAULT_DISTANCE,DEFAULT_DISTANCE,DEFAULT_DISTANCE,DEFAULT_DISTANCE));
        this.add(tabbedPane, new GBC().setFill(GridBagConstraints.BOTH));

        createPersonalInfoPanel();
        iniOperatePanel();

        this.pack();
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (int)((screenSize.getWidth() - this.getWidth()) / 2),
                (int)((screenSize.getHeight() - this.getHeight()) / 2)
        );
    }

    private void iniOperatePanel(){
        client.getPermissions().forEach(serverOperation -> {
            switch (serverOperation){
                case GET_PERMISSION ->{}
                case ADD_USER -> addUserHandler();
                case DEL_USER -> delUserHandler();
                case MODIFY_USER -> modifyUserHandler();
                case CHANGE_PASSWORD -> ChangePasswordHandler();
                case LIST_USER -> listUserHandler();
                case UPLOAD_FILE -> uploadFileHandler();
                case DOWNLOAD_FILE -> downloadFileHandler();
                case DEL_FILE -> delFileHandler();
                case LIST_FILE -> listFilesHandler();
                default -> System.err.println("客户端未处理的报文类型");
            }
        });
    }

    private void createPersonalInfoPanel(){
        var contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        tabbedPane.add("个人信息", contentPanel);

        personalInfoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        personalInfoPanel.setLayout(new GridBagLayout());
        contentPanel.add(personalInfoPanel, new GBC().setInsets(4*DEFAULT_DISTANCE).setFill(GridBagConstraints.BOTH));

        var user = client.getUser();
        personalInfoPanel.add(new JLabel("用户名: " + user.getName()),
                new GBC(0,0));
        personalInfoPanel.add(new JLabel("  身份: " + user.getRole().toString()),
                new GBC(0,1));
        personalInfoPanel.add(personalInfoButtonPanel,
                new GBC(0,5)
                        .setFill(GridBagConstraints.HORIZONTAL)
                        .setInsets(4 * DEFAULT_DISTANCE, DEFAULT_DISTANCE));

        personalInfoButtonPanel.setLayout(new GridBagLayout());
        var exitButton = new JButton("退出");
        exitButton.addActionListener(event-> System.exit(0));
        var switchUserButton = new JButton("切换账号");
        switchUserButton.addActionListener(event->client.switchUser());

        personalInfoButtonPanel.add(exitButton, new GBC(5,0));
        personalInfoButtonPanel.add(switchUserButton, new GBC(4,0));
    }

    private void ChangePasswordHandler(){
        var modifyPasswordButton = new JButton("修改密码");
        personalInfoButtonPanel.add(modifyPasswordButton, new GBC(0,0));
        modifyPasswordButton.addActionListener(event->{
            var newPassword = JOptionPane.showInputDialog(this, "新密码","修改密码", JOptionPane.PLAIN_MESSAGE);
            if(newPassword != null){
                var message = (ChangePasswordMessage)client.connectToServer(
                        new ChangePasswordRequest(client.getUser(), newPassword));
                if(message != null && message.isOk()){
                    JOptionPane.showMessageDialog(this.getOwner(), "修改成功, 请重新登陆");
                    client.switchUser();
                }else{
                    JOptionPane.showMessageDialog(this, "修改失败");
                }
            }
        });
    }



    private void createUserManagementPanel(){
        if(userManagementPanel == null){
            userManagementPanel = new JPanel();
            userManagementPanel.setLayout(new GridBagLayout());
            tabbedPane.addTab("用户管理", userManagementPanel);

            userManagementButtonPanel = new JPanel();
            userManagementButtonPanel.setLayout(new GridBagLayout());
            userManagementPanel.add(userManagementButtonPanel,
                    new GBC(0,1,3,1)
                            .setWeight(100,0)
                            .setInsets(0, DEFAULT_DISTANCE)
                            .setFill(GridBagConstraints.HORIZONTAL));
        }
    }

    private void addUserHandler(){
        createUserManagementPanel();
        var addButton = new JButton(ServerOperation.ADD_USER.show());
        userManagementButtonPanel.add(addButton,
                new GBC(0,0));
        addUserDialog = new UserDialog(this,client, UserDialog.Type.ADD_USER);
        addButton.addActionListener(event-> addUserDialog.display());
    }

    private void delUserHandler(){
        createUserManagementPanel();
        var delButton = new JButton(ServerOperation.DEL_USER.show());
        userManagementButtonPanel.add(delButton,
                new GBC(1,0));
        delUserDialog = new UserDialog(this, client, UserDialog.Type.DEL_USER);
        delButton.addActionListener(event-> delUserDialog.display());
    }

    private void modifyUserHandler(){
        createUserManagementPanel();
        var modifyButton = new JButton(ServerOperation.MODIFY_USER.show());
        userManagementButtonPanel.add(modifyButton,
                new GBC(2,0));
        modifyAllUserDialog = new UserDialog(this, client, UserDialog.Type.MODIFY_ALL_USER);
        modifyButton.addActionListener(event->modifyAllUserDialog.display());
    }

    private void listUserHandler(){
        createUserManagementPanel();
        userTable = new ManagementTable(ManagementTable.TableType.USER_TABLE);
        var scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "用户信息"));
        userManagementPanel.add(scrollPane,
                new GBC().setInsets(DEFAULT_DISTANCE)
                        .setFill(GridBagConstraints.BOTH));

        userTableFlushButton = new JButton("刷新");
        userManagementButtonPanel.add(userTableFlushButton, new GBC(4,0));
        userTableFlushButton.addActionListener(event ->{
            var message = (ListUserMessage)client.connectToServer(
                    new ListUserRequest(client.getUser()));
            userTable.update(message.getAllUser());
        });
        userTableFlushButton.doClick();
    }

    public void userTableFlush(){
        if(userTableFlushButton != null){
            userTableFlushButton.doClick();
        }
    }

    private void createFileManagementPanel(){
        if(fileManagementPanel == null){
            fileManagementPanel = new JPanel();
            fileManagementPanel.setLayout(new GridBagLayout());
            tabbedPane.addTab("文件管理", fileManagementPanel);

            fileManagementButtonPanel = new JPanel();
            fileManagementButtonPanel.setLayout(new GridBagLayout());
            fileManagementPanel.add(fileManagementButtonPanel,
                    new GBC(0,1,3,1)
                            .setWeight(100,0)
                            .setInsets(0, DEFAULT_DISTANCE)
                            .setFill(GridBagConstraints.HORIZONTAL));
        }
    }

    private void uploadFileHandler(){
        createFileManagementPanel();
        var uploadButton = new JButton(ServerOperation.UPLOAD_FILE.show());
        fileManagementButtonPanel.add(uploadButton,
                new GBC(0,0));

        uploadFileDialog = new FileDialog(this, client, FileDialog.Type.UPLOAD_FILE);
        uploadButton.addActionListener(event-> uploadFileDialog.display());
    }

    private void downloadFileHandler(){
        createFileManagementPanel();
        var downloadButton = new JButton(ServerOperation.DOWNLOAD_FILE.show());
        fileManagementButtonPanel.add(downloadButton,
                new GBC(1,0));

        downloadFileDialog = new FileDialog(this, client, FileDialog.Type.DOWNLOAD_FILE);
        downloadButton.addActionListener(event-> downloadFileDialog.display());
    }

    private void delFileHandler(){
        createFileManagementPanel();
        var delFileButton = new JButton(ServerOperation.DEL_FILE.show());
        fileManagementButtonPanel.add(delFileButton,
                new GBC(2,0));
        delFileDialog = new FileDialog(this, client, FileDialog.Type.DEL_FILE);
        delFileButton.addActionListener(event-> delFileDialog.display());
    }

    private void listFilesHandler(){
        createFileManagementPanel();
        fileTable = new ManagementTable(ManagementTable.TableType.FILE_TABLE);
        var scrollPane = new JScrollPane(fileTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"文件信息"));
        fileManagementPanel.add(scrollPane,
                new GBC().setInsets(DEFAULT_DISTANCE)
                        .setFill(GridBagConstraints.BOTH));

        fileTableFlushButton = new JButton("刷新");
        fileManagementButtonPanel.add(fileTableFlushButton, new GBC(4,0));
        fileTableFlushButton.addActionListener(event ->{
            var message = (ListFileMessage)client.connectToServer(
                    new ListFileRequest(client.getUser()));
            fileTable.update(message.getDocs());
        });
        fileTableFlushButton.doClick();
    }

    public void fileTableFlush(){
        if(fileTableFlushButton != null){
            fileTableFlushButton.doClick();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}