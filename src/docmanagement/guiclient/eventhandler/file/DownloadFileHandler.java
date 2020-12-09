package docmanagement.guiclient.eventhandler.file;

import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.background.FileTask;
import docmanagement.shared.requestandmessage.DownloadFileMessage;
import docmanagement.shared.requestandmessage.DownloadFileRequest;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadFileHandler implements FileHandler {
    private final GUIClient client;
    private final Window owner;

    public DownloadFileHandler(GUIClient client, Window owner) {
        this.client = client;
        this.owner = owner;
    }

    @Override
    public void acceptFile(int id, String name, String description, Path path) {
        var task = new FileTask("Download " + "id: " +id) {
            @Override
            protected Void doInBackground() throws Exception {
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
                                JOptionPane.showMessageDialog(owner,
                                        "下载成功 " + ((DownloadFileMessage) message).getDoc().getFilename(), "下载文件", JOptionPane.PLAIN_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(owner,
                                        "下载失败 " + ((DownloadFileMessage) message).getDoc().getFilename(), "下载文件", JOptionPane.WARNING_MESSAGE);
                            }
                        });
                return null;
            }
        };
        client.getBackgroundExecutor().submitFileTask(task);
        client.getOperateFrame().getFileProgressPanel().addProgress(task);
    }
}
