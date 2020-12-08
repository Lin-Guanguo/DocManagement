package docmanagement.shared.requestandmessage;

import docmanagement.shared.Doc;

public class DownloadFileMessage extends AbstractMessage {
    private final Doc doc;

    public DownloadFileMessage(boolean isOk, Doc doc) {
        super(isOk);
        this.doc = doc;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.DOWNLOAD_FILE;
    }

    public Doc getDoc() {
        return doc;
    }
}
