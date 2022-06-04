package it.unibz.emails.exceptions;

public class APIException extends RuntimeException {
    public APIException(String message) {
        super(message);
    }
}
