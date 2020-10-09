package edu.asu.diging.citesphere.api.v1.user;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.api.v1.model.impl.Group;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;

@Component
public class JsonUtil {

    public Group createGroup(ICitationGroup group) {
        Group jsonGroup = new Group();
        jsonGroup.setCreated(group.getCreated());
        jsonGroup.setDescription(group.getDescription());
        jsonGroup.setId(group.getGroupId());
        jsonGroup.setLastModified(group.getLastModified());
        jsonGroup.setName(group.getName());
        jsonGroup.setNumItems(group.getNumItems());
        jsonGroup.setOwner(group.getOwner());
        jsonGroup.setType(group.getType());
        jsonGroup.setUrl(group.getUrl());
        jsonGroup.setVersion(group.getMetadataVersion());
        
        return jsonGroup;
    }

}
