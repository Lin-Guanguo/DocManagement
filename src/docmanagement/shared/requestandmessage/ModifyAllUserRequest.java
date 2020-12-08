package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class ModifyAllUserRequest extends AbstractRequest {
    private final User toModify;

    public ModifyAllUserRequest(User user, User toModify) {
        super(user);
        this.toModify = toModify;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.MODIFY_ALL_USER;
    }

    public User getToModify() {
        return toModify;
    }
}
