package edu.asu.diging.citesphere.core.model.bib;

import java.time.OffsetDateTime;

import edu.asu.diging.citesphere.core.model.IUser;

public interface IConceptType {

    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    String getUri();

    void setUri(String uri);

    IUser getOwner();

    void setCreatedOn(OffsetDateTime createdOn);

    OffsetDateTime getCreatedOn();

    void setOwner(IUser owner);

}