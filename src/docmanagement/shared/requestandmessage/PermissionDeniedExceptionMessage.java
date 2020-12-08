package docmanagement.shared.requestandmessage;

import java.io.Serializable;

public class PermissionDeniedExceptionMessage extends AbstractMessage implements Serializable {

    public PermissionDeniedExceptionMessage() {
        super(false);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.PERMISSION_DENIED_EXCEPTION;
    }
}
