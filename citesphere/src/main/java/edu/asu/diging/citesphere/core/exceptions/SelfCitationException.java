package edu.asu.diging.citesphere.core.exceptions;

public class SelfCitationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SelfCitationException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public SelfCitationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public SelfCitationException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public SelfCitationException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public SelfCitationException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
