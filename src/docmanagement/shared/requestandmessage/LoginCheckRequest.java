package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class LoginCheckRequest extends AbstractRequest {
    public LoginCheckRequest(User user) {
        super(user);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.LOGIN_CHECK;
    }
}
