package docmanagement.guiclient.frame.filedialog;

import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.background.FileTask;
import docmanagement.guiclient.frame.tool.DialogBuilder;
import docmanagement.shared.Doc;
import docmanagement.shared.requestandmessage.UploadFileRequest;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;

public class UploadFileDialog extends DialogBuilder {
    public UploadFileDialog(GUIClient client) {
        super("上传文件");
        this.addLabelAndTextFiled("id");
        this.addLabelAndTextFiled("文件名");
        this.addLabelAndTextFiled("描述");
        this.addLabelAndPathFiled("路径", PathMode.OPEN);
        this.addOkAndCancelButton("上传", "取消",
                actionEvent -> {
                    int id;
                    try{
                        id = Integer.parseInt(getText(0));
                        if(id < 0) throw new NumberFormatException();
                    }catch (NumberFormatException e){
                        JOptionPane.showMessageDialog(this,"id格式错误","上传文件", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    var name = getText(1);
                    var description = getText(2);
                    var path = Path.of(getText(3)) ;

                    long fileSize;
                    try {
                        fileSize = Files.size(path);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this,
                                "文件无法访问", "上传文件", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    var toUp = new Doc(id,
                            client.getUser().getName(),
                            new Timestamp(System.currentTimeMillis()),
                            name,
                            description,
                            fileSize);

                    this.setVisible(false);
                    var task = new FileTask("Upload " + "id: " +id){
                        @Override
                        protected Void doInBackground() throws Exception {
                            client.connectToServer(new UploadFileRequest(client.getUser(), toUp),
                                    (message, socketIn, socketOut) -> {
                                        if (message.isOk()) {
                                            try (var input = new BufferedInputStream(Files.newInputStream(path))) {
                                                byte[] buf = new byte[1 << 10];
                                                int len;
                                                long transmitSize = 0;
                                                while ((len = input.read(buf)) != -1) {
                                                    socketOut.write(buf, 0, len);
                                                    transmitSize += len;

                                                    setProgress((int) (100 * transmitSize / fileSize));
                                                    if(isCancelled()) { return; }
                                                }
                                                setProgress(100);
                                            }
                                            JOptionPane.showMessageDialog(client.getOperateFrame(),
                                                    "上传成功 " + toUp.getFilename(), "上传文件", JOptionPane.PLAIN_MESSAGE);
                                            client.getOperateFrame().fileTableFlush();
                                        } else {
                                            JOptionPane.showMessageDialog(client.getOperateFrame(),
                                                    "服务器不接受该文件 " + toUp.getFilename(), "上传文件", JOptionPane.WARNING_MESSAGE);
                                        }
                                    });
                            return null;
                        }
                    };
                    var future = client.getBackgroundExecutor().submitFileTask(task);
                    client.getOperateFrame().getFileProgressPanel().addProgress(task);
                },
                CLOSE_DIALOG);
        this.displayIni();
    }
}
