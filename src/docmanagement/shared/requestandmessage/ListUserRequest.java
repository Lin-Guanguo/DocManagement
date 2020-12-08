package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class ListUserRequest extends AbstractRequest {
    public ListUserRequest(User user) {
        super(user);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.LIST_USER;
    }
}
