package it.unibz.emails.server.persistence.exceptions;

public class FatalException extends RuntimeException {
    public FatalException() {
        super("Fatal exception");
    }

    public FatalException(String message) {
        super(message);
    }

    public FatalException(String message, Throwable cause) {
        super(message, cause);
    }
}
