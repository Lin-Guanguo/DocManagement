package docmanagement.shared.requestandmessage;

import docmanagement.shared.Doc;

import java.util.Collection;

public class ListFileMessage extends AbstractMessage {
    private final Collection<Doc> fileInformation;

    public ListFileMessage(Collection<Doc> fileInformation) {
        super(true);
        this.fileInformation = fileInformation;
    }

    @Override
    public ServerOperation getType() {
        return ServerOperation.LIST_FILE;
    }

    public Collection<Doc> getDocs() {
        return fileInformation;
    }
}
