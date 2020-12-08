package docmanagement.shared.requestandmessage;

public class UnknownRequestExceptionMessage extends AbstractMessage {
    public UnknownRequestExceptionMessage() {
        super(false);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.UNKNOWN_REQUEST_EXCEPTION;
    }
}
