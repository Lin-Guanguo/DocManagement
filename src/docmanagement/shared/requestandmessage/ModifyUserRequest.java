package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class ModifyUserRequest extends AbstractRequest {
    private final String password;

    public ModifyUserRequest(User user, String password) {
        super(user);
        this.password = password;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.MODIFY_USER;
    }

    public String getNewPassword() {
        return password;
    }
}
