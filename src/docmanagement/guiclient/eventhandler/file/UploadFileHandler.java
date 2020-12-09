package docmanagement.guiclient.eventhandler.file;

import docmanagement.guiclient.GUIClient;
import docmanagement.shared.Doc;
import docmanagement.shared.requestandmessage.UploadFileRequest;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;

public class UploadFileHandler implements FileHandler {
    private final GUIClient client;
    private final Window owner;

    public UploadFileHandler(GUIClient client, Window owner) {
        this.client = client;
        this.owner = owner;
    }

    @Override
    public void acceptFile(int id, String name, String description, Path path) {
        long fileSize;
        try {
            fileSize = Files.size(path);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(owner,
                    "文件无法访问", "上传文件", JOptionPane.WARNING_MESSAGE);
            return;
        }
        var toUp = new Doc(id,
                client.getUser().getName(),
                new Timestamp(System.currentTimeMillis()),
                name,
                description,
                fileSize);
        client.connectToServer(new UploadFileRequest(client.getUser(), toUp),
                (message, socketIn, socketOut) -> {
                    if (message.isOk()) {
                        try (var input = new BufferedInputStream(Files.newInputStream(path))) {
                            byte[] buf = new byte[1 << 10];
                            int len;
                            while ((len = input.read(buf)) != -1) {
                                socketOut.write(buf, 0, len);
                            }
                        }
                        JOptionPane.showMessageDialog(owner,
                                "上传成功 " + toUp.getFilename(), "上传文件", JOptionPane.PLAIN_MESSAGE);
                        client.getOperateFrame().fileTableFlush();
                        owner.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(owner,
                                "服务器不接受该文件 " + toUp.getFilename(), "上传文件", JOptionPane.WARNING_MESSAGE);
                    }
                });
    }
}