package it.unibz.emails.entities;

public class ErrorMessage {
    private final String title;
    private final String exception;
    private final String message;

    public ErrorMessage(Throwable exception) {
        this.title = "Error";
        this.exception = exception.getClass().getSimpleName();
        if (exception.getMessage() != null && !exception.getMessage().isBlank())
            this.message = exception.getMessage();
        else this.message = "";
    }

    public String getTitle() {
        return title;
    }

    public String getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }
}
