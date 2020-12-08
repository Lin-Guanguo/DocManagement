package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class LoginCheckMessage extends AbstractMessage {
    private final User.Role role;

    public LoginCheckMessage(boolean isOk, User.Role role) {
        super(isOk);
        this.role = role;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.LOGIN_CHECK;
    }

    public User.Role getRole() {
        return role;
    }
}
