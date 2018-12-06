package edu.asu.diging.citesphere.core.model.bib;

import java.time.OffsetDateTime;
import java.util.List;

public interface ICitationGroup {

    long getId();

    void setId(long id);

    long getVersion();

    void setVersion(long version);

    String getCreated();

    void setCreated(String created);

    String getLastModified();

    void setLastModified(String lastModified);

    long getNumItems();

    void setNumItems(long numItems);

    List<ICitation> getCitations();

    void setCitations(List<ICitation> citations);

    void setName(String name);

    String getName();

    void setFileEditing(String fileEditing);

    String getFileEditing();

    void setLibraryReading(String libraryReading);

    String getLibraryReading();

    void setLibraryEditing(String libraryEditing);

    String getLibraryEditing();

    void setUrl(String url);

    String getUrl();

    void setDescription(String description);

    String getDescription();

    void setType(String type);

    String getType();

    void setOwner(long owner);

    long getOwner();

    void setUpdatedOn(OffsetDateTime updatedOn);

    OffsetDateTime getUpdatedOn();

}