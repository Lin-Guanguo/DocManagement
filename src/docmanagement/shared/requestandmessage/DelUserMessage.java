package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class DelUserMessage extends AbstractMessage {
    private final User del;

    public DelUserMessage(boolean isOk, User del) {
        super(isOk);
        this.del = del;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.DEL_USER;
    }

    public User getDel() {
        return del;
    }
}
