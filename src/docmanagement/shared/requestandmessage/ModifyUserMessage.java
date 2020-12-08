package docmanagement.shared.requestandmessage;

public class ModifyUserMessage extends AbstractMessage {
    public ModifyUserMessage(boolean isOk) {
        super(isOk);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.MODIFY_USER;
    }
}
