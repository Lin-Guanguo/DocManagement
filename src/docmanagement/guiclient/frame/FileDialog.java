package docmanagement.guiclient.frame;

import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.listener.DownloadFileHandler;
import docmanagement.guiclient.listener.FileHandler;
import docmanagement.guiclient.listener.UploadFileHandler;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class FileDialog extends JDialog {
    static final int DEFAULT_DISTANCE = 10;

    private final JLabel idLabel = new JLabel("ID: ");
    private final JLabel DescriptionLabel = new JLabel("描述: ");
    private final JLabel fileNameLabel = new JLabel("文件名: ");
    private final JLabel pathLabel = new JLabel("文件路径: ");

    private final JTextField idField = new JTextField(15);
    private final JTextField DescriptionField = new JTextField();
    private final JTextField fileNameField = new JTextField();
    private final JTextField pathField = new JTextField();

    private final JButton pathButton = new JButton("...");
    private final JPanel buttonPanel = new JPanel();
    private final JButton okButton = new JButton("确认");
    private final JButton cancelButton = new JButton("取消");

    private FileHandler fileHandler = null;

    private final GUIClient client;


    enum Type{
        UPLOAD_FILE, DOWNLOAD_FILE
    }

    public FileDialog(Frame owner, GUIClient client, Type type) {
        super(owner, null, ModalityType.APPLICATION_MODAL);
        this.client = client;
        iniType(type);
        iniLayout(type);
        iniListener(type);
    }

    public void display(){
        idField.setText("");
        DescriptionField.setText("");
        fileNameField.setText("");
        this.setVisible(true);
    }

    public void iniType(Type type){
        switch (type){
            case UPLOAD_FILE ->{
                this.setTitle("上传文件");
                fileHandler = new UploadFileHandler(client, this);
            }
            case DOWNLOAD_FILE -> {
                this.setTitle("下载文件");
                fileHandler = new DownloadFileHandler(client, this);
            }
        }
    }

    private void iniLayout(Type type){
        this.setLayout(new GridBagLayout());

        JLabel[] labels = null;
        JTextField[] textFields = null;
        switch (type){
            case UPLOAD_FILE -> {
                labels = new JLabel[]{
                        idLabel, DescriptionLabel, fileNameLabel, pathLabel};
                textFields = new JTextField[]{
                        idField, DescriptionField, fileNameField, pathField};
            }
            case DOWNLOAD_FILE -> {
                labels = new JLabel[]{
                        idLabel, pathLabel};
                textFields = new JTextField[]{
                        idField, pathField};
            }
            default -> System.out.println("ERROR");
        }


        final int OBJ_COUNT = labels.length;
        for(int i = 0; i < OBJ_COUNT; ++i){
            this.add(labels[i],
                    new GBC(0,i)
                            .setInsets(DEFAULT_DISTANCE)
                            .setWeight(0,GBC.DEFAULT_WEIGHTY));
            this.add(textFields[i],
                    new GBC(1,i,2,1)
                            .setFill(GridBagConstraints.HORIZONTAL));
        }

        this.add(pathButton,
                new GBC(3,OBJ_COUNT-1)
                        .setWeight(0,GBC.DEFAULT_WEIGHTY)
                        .setInsets(DEFAULT_DISTANCE));

        this.add(buttonPanel,
                new GBC(0,OBJ_COUNT,4,1)
                        .setInsets(0, DEFAULT_DISTANCE)
                        .setFill(GridBagConstraints.HORIZONTAL));
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(okButton,
                new GBC(0,0).setInsets(0,DEFAULT_DISTANCE));
        buttonPanel.add(cancelButton,
                new GBC(1,0).setInsets(0,DEFAULT_DISTANCE));

        this.pack();
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (int)((screenSize.getWidth() - this.getWidth()) / 2),
                (int)((screenSize.getHeight() - this.getHeight()) / 2)
        );
    }

    private void iniListener(Type type){
        switch (type){
            case UPLOAD_FILE -> {
                pathButton.addActionListener(event -> {
                    var fc = new JFileChooser();
                    var state = fc.showOpenDialog(this);
                    if (state == JFileChooser.APPROVE_OPTION) {
                        pathField.setText(fc.getSelectedFile().toString());
                    }
                });
            }
            case DOWNLOAD_FILE -> {
                pathButton.addActionListener(event -> {
                    var fc = new JFileChooser();
                    var state = fc.showSaveDialog(this);
                    if (state == JFileChooser.APPROVE_OPTION) {
                        pathField.setText(fc.getSelectedFile().toString());
                    }
                });
            }
        }

        cancelButton.addActionListener(event->{
            this.setVisible(false);
        });

        okButton.addActionListener(event->{
            try{
                int id = Integer.parseInt(idField.getText());
                if(id < 0) throw new NumberFormatException();
                fileHandler.acceptFile(id,
                        DescriptionField.getText(),
                        fileNameField.getText(),
                        Path.of(pathField.getText()));
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(this,"id格式错误","上传文件", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
