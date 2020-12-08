package docmanagement.server.exception;

import java.io.IOException;

public class UnknownRequestException extends IOException {

    public UnknownRequestException() {
    }

    public UnknownRequestException(String message) {
        super(message);
    }

    public UnknownRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownRequestException(Throwable cause) {
        super(cause);
    }
}
