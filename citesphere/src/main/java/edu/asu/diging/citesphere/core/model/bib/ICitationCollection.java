package edu.asu.diging.citesphere.core.model.bib;

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

    Boolean getParentCollection();

    void setParentCollection(Boolean parentCollection);

    ICitationGroup getGroup();

    void setGroup(ICitationGroup group);

}