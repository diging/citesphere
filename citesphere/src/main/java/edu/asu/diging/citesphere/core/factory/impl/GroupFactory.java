package edu.asu.diging.citesphere.core.factory.impl;

import org.springframework.social.zotero.api.Group;
import org.springframework.stereotype.Component;
import edu.asu.diging.citesphere.core.factory.IGroupFactory;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;

@Component
public class GroupFactory implements IGroupFactory {

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.factory.impl.IGroupFactory#createGroup(org.springframework.social.zotero.api.Group)
     */
    @Override
    public ICitationGroup createGroup(Group group) {
        ICitationGroup citGroup = new CitationGroup();
        citGroup.setId(group.getId());
        citGroup.setCreated(group.getMeta().getCreated());
        citGroup.setLastModified(group.getMeta().getLastModified());
        citGroup.setVersion(group.getVersion());
        citGroup.setName(group.getData().getName());
        citGroup.setDescription(group.getData().getDescription());
        citGroup.setFileEditing(group.getData().getFileEditing());
        citGroup.setLibraryEditing(group.getData().getLibraryEditing());
        citGroup.setLibraryReading(group.getData().getLibraryReading());
        citGroup.setOwner(group.getData().getOwner());
        citGroup.setType(group.getData().getType());
        citGroup.setUrl(group.getData().getUrl());
        return citGroup;
    }
}
