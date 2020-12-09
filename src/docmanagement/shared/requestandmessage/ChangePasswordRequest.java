package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class ChangePasswordRequest extends AbstractRequest {
    private final String password;

    public ChangePasswordRequest(User user, String password) {
        super(user);
        this.password = password;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.CHANGE_PASSWORD;
    }

    public String getNewPassword() {
        return password;
    }
}
