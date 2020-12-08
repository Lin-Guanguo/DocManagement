package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

public class DownloadFileRequest extends AbstractRequest {
    private final int id;

    public DownloadFileRequest(User user, int id) {
        super(user);
        this.id = id;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.DOWNLOAD_FILE;
    }

    public int getId() {
        return id;
    }
}
