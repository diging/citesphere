package edu.asu.diging.citesphere.core.authority;

import java.util.Map;

public interface IImportedAuthority {

    String getName();

    void setName(String name);

    String getSource();

    void setSource(String source);

    String getUri();

    void setUri(String uri);

    Map<String, Object> getProperties();

    void setProperties(Map<String, Object> properties);

}