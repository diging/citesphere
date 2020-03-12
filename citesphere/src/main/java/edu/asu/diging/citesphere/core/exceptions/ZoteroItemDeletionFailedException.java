package edu.asu.diging.citesphere.core.exceptions;

import org.springframework.social.zotero.api.ItemCreationResponse;

public class ZoteroItemDeletionFailedException extends Exception {
    private ItemCreationResponse response;

    private static final long serialVersionUID = 1L;

    public ZoteroItemDeletionFailedException() {
        super();

    }

    public ZoteroItemDeletionFailedException(ItemCreationResponse response) {
        this.setResponse(response);
    }

    public ZoteroItemDeletionFailedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

    }

    public ZoteroItemDeletionFailedException(String message, Throwable cause) {
        super(message, cause);

    }

    public ZoteroItemDeletionFailedException(String message) {
        super(message);

    }

    public ZoteroItemDeletionFailedException(Throwable cause) {
        super(cause);

    }

    public ItemCreationResponse getResponse() {
        return response;
    }

    public void setResponse(ItemCreationResponse response) {
        this.response = response;
    }
}