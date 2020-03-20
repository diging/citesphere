package edu.asu.diging.citesphere.core.exceptions;

public class DownloadExportException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DownloadExportException() {
        super();
    }

    public DownloadExportException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DownloadExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadExportException(String message) {
        super(message);
    }

    public DownloadExportException(Throwable cause) {
        super(cause);
    }

}
