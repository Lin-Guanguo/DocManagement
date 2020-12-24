package docmanagement.shared.requestandmessage;

public class UploadFileMessage extends AbstractMessage {
    public UploadFileMessage(boolean isOk) {
        super(isOk);
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.UPLOAD_FILE;
    }
}
