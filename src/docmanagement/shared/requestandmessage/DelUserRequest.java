package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class DelUserRequest extends AbstractRequest {
    private final String delUserName;

    public DelUserRequest(User user, String delUserName) {
        super(user);
        this.delUserName = delUserName;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.DEL_USER;
    }

    public String getDelUserName() {
        return delUserName;
    }
}
