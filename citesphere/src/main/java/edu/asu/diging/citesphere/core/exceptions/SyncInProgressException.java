package edu.asu.diging.citesphere.core.exceptions;

public class SyncInProgressException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SyncInProgressException() {
        super();
    }

    public SyncInProgressException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SyncInProgressException(String message, Throwable cause) {
        super(message, cause);
    }

    public SyncInProgressException(String message) {
        super(message);
    }

    public SyncInProgressException(Throwable cause) {
        super(cause);
    }

}
