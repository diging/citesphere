package edu.asu.diging.citesphere.core.model.bib;

import java.util.Set;

public interface IPerson {

    String getName();

    void setName(String name);

    String getUri();

    void setUri(String uri);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    int getPositionInList();

    void setPositionInList(int order);

    void setAffiliations(Set<IAffiliation> affiliations);

    Set<IAffiliation> getAffiliations();

    void setId(String id);

    String getId();
}