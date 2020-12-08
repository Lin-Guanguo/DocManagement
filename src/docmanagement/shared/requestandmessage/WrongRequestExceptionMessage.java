package docmanagement.shared.requestandmessage;

public class WrongRequestExceptionMessage extends AbstractMessage {

    public WrongRequestExceptionMessage() {
        super(false);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.WRONG_REQUEST_EXCEPTION;
    }
}
