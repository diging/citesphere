package edu.asu.diging.citesphere.web.user;

import java.util.List;
import java.util.Set;

import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;

public class FoundAuthorities {

    private List<IAuthorityEntry> userAuthorityEntries;
    private Set<IAuthorityEntry> datasetAuthorityEntries;
    private IAuthorityEntry importedAuthority;
    
    public List<IAuthorityEntry> getUserAuthorityEntries() {
        return userAuthorityEntries;
    }
    public void setUserAuthorityEntries(List<IAuthorityEntry> userAuthorityEntries) {
        this.userAuthorityEntries = userAuthorityEntries;
    }
    public Set<IAuthorityEntry> getDatasetAuthorityEntries() {
        return datasetAuthorityEntries;
    }
    public void setDatasetAuthorityEntries(Set<IAuthorityEntry> datasetAuthorityEntries) {
        this.datasetAuthorityEntries = datasetAuthorityEntries;
    }
    public IAuthorityEntry getImportedAuthority() {
        return importedAuthority;
    }
    public void setImportedAuthority(IAuthorityEntry importedAuthority) {
        this.importedAuthority = importedAuthority;
    }    
}
