package edu.asu.diging.citesphere.core.authority;

import java.util.Map;

public interface IImportedAuthority {

    String getName();

    void setName(String name);

    /**
     * Returns the source of the imported authority.
     * Ex: Viaf, Conceptpower etc.,
     * @return a String representing the source of the imported authority.
     */
    String getSource();

    void setSource(String source);

    String getUri();

    void setUri(String uri);

    Map<String, Object> getProperties();

    void setProperties(Map<String, Object> properties);

}