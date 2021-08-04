package edu.asu.diging.citesphere.api.v1.model.impl;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;

/**
 * Data transfer object to fetch item details and its attachment info
 * 
 * @author Maulik Limbadiya
 */
public class ItemDetails {

    private ICitation item;
    private List<String> attachments;

    public ICitation getItem() {
        return item;
    }

    public void setItem(ICitation item) {
        this.item = item;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

}
