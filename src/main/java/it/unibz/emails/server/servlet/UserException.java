package it.unibz.emails.server.servlet;

public class UserException extends RuntimeException {
    public final String redirectPath;

    public UserException(String message, String redirectPath) {
        super(message);
        this.redirectPath = redirectPath;
    }
}
