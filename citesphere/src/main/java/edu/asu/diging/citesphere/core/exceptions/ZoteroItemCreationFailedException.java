package edu.asu.diging.citesphere.core.exceptions;

import org.springframework.social.zotero.api.ItemCreationResponse;

public class ZoteroItemCreationFailedException extends Exception {
    
    private ItemCreationResponse response;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ZoteroItemCreationFailedException() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public ZoteroItemCreationFailedException(ItemCreationResponse response) {
        this.setResponse(response);
    }

    public ZoteroItemCreationFailedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public ZoteroItemCreationFailedException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ZoteroItemCreationFailedException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ZoteroItemCreationFailedException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public ItemCreationResponse getResponse() {
        return response;
    }

    public void setResponse(ItemCreationResponse response) {
        this.response = response;
    }

}
