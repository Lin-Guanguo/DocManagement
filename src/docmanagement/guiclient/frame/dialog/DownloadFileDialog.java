package docmanagement.guiclient.frame.dialog;

import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.background.FileTask;
import docmanagement.guiclient.frame.tool.DialogBuilder;
import docmanagement.shared.requestandmessage.DownloadFileMessage;
import docmanagement.shared.requestandmessage.DownloadFileRequest;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadFileDialog extends DialogBuilder {
    public DownloadFileDialog(GUIClient client) {
        super("下载文件");
        this.addLabelAndTextFiled("id");
        this.addLabelAndPathFiled("保存路径", PathMode.SAVE);
        this.addOkAndCancelButton("下载","取消",
                actionEvent -> {
                    int id;
                    try{
                        id = Integer.parseInt(getText(0));
                        if(id < 0) {
                            throw new NumberFormatException();
                        }
                    }catch (NumberFormatException e){
                        JOptionPane.showMessageDialog(this,"id格式错误","上传文件", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    var path = Path.of(getText(1));

                    this.setVisible(false);
                    var task = new FileTask("Download " + "id: " +id) {
                        @Override
                        protected Void doInBackground(){
                            client.connectToServer(new DownloadFileRequest(client.getUser(), id),
                                    (message, socketIn, socketOut) -> {
                                        if (message.isOk()) {
                                            try (var fileOut = new BufferedOutputStream(Files.newOutputStream(path))) {
                                                long filesize = ((DownloadFileMessage) message).getDoc().getFileSize();
                                                long transmitSize = 0;
                                                final int bufSize = 1 << 10;
                                                byte[] buf = new byte[bufSize];
                                                while (transmitSize < filesize - bufSize) {
                                                    socketIn.readNBytes(buf, 0, bufSize);
                                                    fileOut.write(buf);
                                                    transmitSize += bufSize;

                                                    setProgress((int) (100 * transmitSize / filesize));
                                                    if(isCancelled()) { return; }
                                                }
                                                if (filesize - transmitSize > 0) {
                                                    socketIn.readNBytes(buf, 0, (int) (filesize - transmitSize));
                                                    fileOut.write(buf, 0, (int) (filesize - transmitSize));
                                                }
                                                setProgress(100);
                                            }
                                            JOptionPane.showMessageDialog(client.getOperateFrame(),
                                                    "下载成功 " + ((DownloadFileMessage) message).getDoc().getFilename(), "下载文件", JOptionPane.PLAIN_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(client.getOperateFrame(),
                                                    "下载失败 " + ((DownloadFileMessage) message).getDoc().getFilename(), "下载文件", JOptionPane.WARNING_MESSAGE);
                                        }
                                    });
                            return null;
                        }
                    };
                    client.getBackgroundExecutor().submitFileTask(task);
                    client.getOperateFrame().getFileProgressPanel().addProgress(task);
                }, CLOSE_DIALOG);
        this.displayIni();
    }
}
