package it.unibz.emails.exceptions;

public class NotFoundException extends APIException {
    public NotFoundException(String message) {
        super(message);
    }
}
