package docmanagement.shared.requestandmessage;

import java.io.Serializable;

abstract public class AbstractMessage implements Serializable {
    private final boolean isOk;

    public AbstractMessage(boolean isOk) {
        this.isOk = isOk;
    }

    public boolean isOk() {
        return isOk;
    }

    abstract public ServerOperation getType();

    @Override
    public String toString() {
        return this.getClass() + "{" +
                "isOk=" + isOk +
                '}';
    }
}
