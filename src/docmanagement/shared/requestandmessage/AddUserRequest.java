package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class AddUserRequest extends AbstractRequest {
    private final User toAdd;

    public AddUserRequest(User user, User toAdd) {
        super(user);
        this.toAdd = toAdd;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.ADD_USER;
    }

    public User getToAdd() {
        return toAdd;
    }
}
