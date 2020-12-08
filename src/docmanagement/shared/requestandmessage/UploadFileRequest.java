package docmanagement.shared.requestandmessage;

import docmanagement.shared.Doc;
import docmanagement.shared.User;

public class UploadFileRequest extends AbstractRequest {
    private final Doc doc;

    public UploadFileRequest(User user, Doc doc) {
        super(user);
        this.doc = doc;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.UPLOAD_FILE;
    }

    public Doc getDoc() {
        return doc;
    }
}
