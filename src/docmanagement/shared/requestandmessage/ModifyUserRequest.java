package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class ModifyUserRequest extends AbstractRequest {
    private final User toModify;

    public ModifyUserRequest(User user, User toModify) {
        super(user);
        this.toModify = toModify;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.MODIFY_USER;
    }

    public User getToModify() {
        return toModify;
    }
}
