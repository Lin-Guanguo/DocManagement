package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

import java.util.Collection;

public class ListUserMessage extends AbstractMessage {
    private final Collection<User> allUser;

    public ListUserMessage(Collection<User> allUser) {
        super(true);
        this.allUser = allUser;
    }

    public Collection<User> getAllUser() {
        return allUser;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.LIST_USER;
    }

}
