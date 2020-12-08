package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class ListFileRequest extends AbstractRequest {
    public ListFileRequest(User user) {
        super(user);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.LIST_FILE;
    }
}
