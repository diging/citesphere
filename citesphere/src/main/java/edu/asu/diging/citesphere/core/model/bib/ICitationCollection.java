package edu.asu.diging.citesphere.core.model.bib;

import java.time.OffsetDateTime;

public interface ICitationCollection {

    String getKey();

    void setKey(String key);

    long getVersion();

    void setVersion(long version);

    long getNumberOfCollections();

    void setNumberOfCollections(long numberOfCollections);

    long getNumberOfItems();

    void setNumberOfItems(long numberOfItems);

    String getName();

    void setName(String name);

    ICitationGroup getGroup();

    void setGroup(ICitationGroup group);

    void setParentCollectionKey(String parentCollectionKey);

    String getParentCollectionKey();

    void setLastSynced(OffsetDateTime lastSynced);

    OffsetDateTime getLastSynced();

}