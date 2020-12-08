package docmanagement.guiclient.listener;

import docmanagement.guiclient.GUIClient;
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
        client.connectToServer(new DownloadFileRequest(client.getUser(), id),
                (message, socketIn, socketOut) -> {
                    if (message.isOk()) {
                        try (var fileOut = new BufferedOutputStream(Files.newOutputStream(path))) {
                            long fileSize = ((DownloadFileMessage) message).getDoc().getFileSize();
                            final int bufSize = 1 << 10;
                            byte[] buf = new byte[bufSize];
                            while (fileSize > bufSize) {
                                socketIn.readNBytes(buf, 0, bufSize);
                                fileOut.write(buf);
                                fileSize -= bufSize;
                            }
                            if (fileSize > 0) {
                                socketIn.readNBytes(buf, 0, (int) fileSize);
                                fileOut.write(buf, 0, (int) fileSize);
                            }
                        }
                        JOptionPane.showMessageDialog(owner,
                                "下载成功 " + ((DownloadFileMessage) message).getDoc().getFilename(), "下载文件", JOptionPane.PLAIN_MESSAGE);
                        owner.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(owner,
                                "下载失败 " + ((DownloadFileMessage) message).getDoc().getFilename(), "下载文件", JOptionPane.WARNING_MESSAGE);
                    }
                });
    }
}
