package edu.asu.diging.citesphere.core.model.bib;

import java.time.OffsetDateTime;

import edu.asu.diging.citesphere.core.model.IUser;

public interface ICitationConcept {

    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    String getUri();

    void setUri(String uri);

    void setCreatedOn(OffsetDateTime createdOn);

    OffsetDateTime getCreatedOn();

    void setOwner(IUser owner);

    IUser getOwner();

}