package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class GetPermissionRequest extends AbstractRequest{
    public GetPermissionRequest(User user) {
        super(user);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.GET_PERMISSION;
    }
}
