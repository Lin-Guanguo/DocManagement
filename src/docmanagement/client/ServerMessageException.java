package docmanagement.client;

import java.io.IOException;

public class ServerMessageException extends IOException {

    public ServerMessageException() {
    }

    public ServerMessageException(String message) {
        super(message);
    }

    public ServerMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerMessageException(Throwable cause) {
        super(cause);
    }
}
