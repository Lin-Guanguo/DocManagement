package docmanagement.guiclient.frame.filedialog;

import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.background.FileTask;
import docmanagement.guiclient.frame.DialogBuilder;
import docmanagement.shared.requestandmessage.DelFileMessage;
import docmanagement.shared.requestandmessage.DelFileRequest;
import docmanagement.shared.requestandmessage.DownloadFileMessage;
import docmanagement.shared.requestandmessage.DownloadFileRequest;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DelFileDialog extends DialogBuilder {
    public DelFileDialog(GUIClient client) {
        super("删除文件");
        this.addLabelAndTextFiled("id");
        this.addOkAndCancelButton("删除","取消",
                actionEvent -> {
                    int id;
                    try{
                        id = Integer.parseInt(getText(0));
                        if(id < 0) throw new NumberFormatException();
                    }catch (NumberFormatException e){
                        JOptionPane.showMessageDialog(this,"id格式错误","上传文件", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    var message = (DelFileMessage)client.connectToServer(
                            new DelFileRequest(client.getUser(), id));
                    if(message.isOk()){
                        JOptionPane.showMessageDialog(client.getOperateFrame(),
                                "删除成功","删除文件",JOptionPane.PLAIN_MESSAGE);
                        this.setVisible(false);
                        client.getOperateFrame().fileTableFlush();
                    }else{
                        JOptionPane.showMessageDialog(this,
                                "删除失败","删除文件",JOptionPane.WARNING_MESSAGE);
                    }
                },
                CLOSE_DIALOG);
        this.displayIni();
    }
}
