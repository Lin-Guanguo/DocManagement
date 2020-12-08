package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class DelFileRequest extends AbstractRequest {
    private final int fileId;

    public DelFileRequest(User user, int fileId) {
        super(user);
        this.fileId = fileId;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.DEL_FILE;
    }

    public int getFileId() {
        return fileId;
    }
}
