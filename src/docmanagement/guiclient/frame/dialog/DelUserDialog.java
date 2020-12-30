package docmanagement.guiclient.frame.dialog;

import docmanagement.guiclient.GUIClient;
import docmanagement.guiclient.frame.tool.DialogBuilder;
import docmanagement.shared.requestandmessage.DelUserMessage;
import docmanagement.shared.requestandmessage.DelUserRequest;

import javax.swing.*;

public class DelUserDialog extends DialogBuilder {

    public DelUserDialog(GUIClient client){
        super("删除用户");
        this.addLabelAndTextFiled("删除用户名");
        this.addOkAndCancelButton("删除","取消",
            e -> {
                var name = getText(0);
                var message = (DelUserMessage) client.connectToServer(
                        new DelUserRequest(client.getUser(), name));
                if(message.isOk()){
                    JOptionPane.showMessageDialog(this, "删除成功");
                    this.setVisible(false);
                    client.getOperateFrame().userTableFlush();
                }else{
                    JOptionPane.showMessageDialog(this, "删除失败");
                }
            },
            CLOSE_DIALOG
        );

        this.displayIni();
    }
}
