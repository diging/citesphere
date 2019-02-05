package edu.asu.diging.citesphere.core.model.bib;

public interface IAffiliation {

    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getUri();

    void setUri(String uri);

    void setLocalAuthorityId(String localAuthorityId);

    String getLocalAuthorityId();

}