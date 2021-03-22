package edu.asu.diging.citesphere.web.user.dto;

import java.util.List;

public class MoveItemsRequest {
    private String collectionId;
    private List<String> itemIds;

    public String getCollectionId() {
        return collectionId;
    }

    public List<String> getItemIds() {
        return itemIds;
    }
}
