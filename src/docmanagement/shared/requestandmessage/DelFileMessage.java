package docmanagement.shared.requestandmessage;

import docmanagement.shared.Doc;
import docmanagement.shared.User;

public class DelFileMessage extends AbstractMessage {
    private final Doc del;

    public DelFileMessage(boolean isOk, Doc del) {
        super(isOk);
        this.del = del;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.DEL_FILE;
    }

    public Doc getDel() {
        return del;
    }
}
