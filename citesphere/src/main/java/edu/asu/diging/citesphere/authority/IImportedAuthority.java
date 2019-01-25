package edu.asu.diging.citesphere.authority;

import java.util.Map;

public interface IImportedAuthority {

    String getName();

    void setName(String name);

    String getUri();

    void setUri(String uri);

    Map<String, Object> getProperties();

    void setProperties(Map<String, Object> properties);

}