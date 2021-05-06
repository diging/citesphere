package edu.asu.diging.citesphere.core.exceptions;

public class AuthorityImporterNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public AuthorityImporterNotFoundException() {
        super();
    }

    public AuthorityImporterNotFoundException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AuthorityImporterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorityImporterNotFoundException(String message) {
        super(message);
    }

    public AuthorityImporterNotFoundException(Throwable cause) {
        super(cause);
    }
}
