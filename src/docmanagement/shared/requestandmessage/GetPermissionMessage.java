package docmanagement.shared.requestandmessage;

import java.util.Set;

public class GetPermissionMessage extends AbstractMessage{
    private final Set<ServerOperation> allowed;
    public GetPermissionMessage(Set<ServerOperation> allowed) {
        super(true);
        this.allowed = allowed;
    }

    public Set<ServerOperation> getAllowed() {
        return allowed;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.GET_PERMISSION;
    }
}
