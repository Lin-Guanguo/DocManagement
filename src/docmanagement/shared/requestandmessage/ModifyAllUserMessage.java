package docmanagement.shared.requestandmessage;

public class ModifyAllUserMessage extends AbstractMessage {
    public ModifyAllUserMessage(boolean isOk) {
        super(isOk);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.MODIFY_ALL_USER;
    }
}
