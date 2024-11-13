package edu.asu.diging.citesphere.core.authority;

import java.util.Map;

public interface IImportedAuthority {

    String getName();

    void setName(String name);

    String getUri();

    void setUri(String uri);

    Map<String, Object> getProperties();

    void setProperties(Map<String, Object> properties);
    
    /**
     * Retrieves the identifier of the importer.
     * <p>Example: "VIAF", "Conceptpower", etc.</p>
     *
     * @return a String representing the identifier of the importer
     */
    public String getImporterId();

    /**
     * Sets the identifier for the importer.
     *
     * @param importerId a String specifying the identifier to be set for the importer
     */
    public void setImporterId(String importerId);
    
}