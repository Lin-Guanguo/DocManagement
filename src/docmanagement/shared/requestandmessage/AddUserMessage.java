package docmanagement.shared.requestandmessage;

public class AddUserMessage extends AbstractMessage {
    public AddUserMessage(boolean isOk) {
        super(isOk);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.ADD_USER;
    }
}
