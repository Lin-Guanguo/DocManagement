package docmanagement.shared.requestandmessage;

public class ChangePasswordMessage extends AbstractMessage {
    public ChangePasswordMessage(boolean isOk) {
        super(isOk);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.CHANGE_PASSWORD;
    }
}
