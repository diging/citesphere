package edu.asu.diging.citesphere.core.authority.impl;

import java.util.Map;

import edu.asu.diging.citesphere.core.authority.IImportedAuthority;

public class ImportedAuthority implements IImportedAuthority {

    private String name;
    private String uri;
    private Map<String, Object> properties;
    private String importerId;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.IImportedAuthority#getName()
     */
    @Override
    public String getName() {
        return name;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.IImportedAuthority#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.IImportedAuthority#getUri()
     */
    @Override
    public String getUri() {
        return uri;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.IImportedAuthority#setUri(java.lang.String)
     */
    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.IImportedAuthority#getProperties()
     */
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.IImportedAuthority#setProperties(java.util.Map)
     */
    @Override
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.IImportedAuthority#getImporterId()
     */
    @Override
    public String getImporterId() {
        return importerId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.authority.impl.IImportedAuthority#setImporterId(java.lang.String)
     */
    @Override
    public void setImporterId(String importerId) {
        this.importerId = importerId;
    }
}
