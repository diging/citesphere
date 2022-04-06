package edu.asu.diging.citesphere.core.bib;

import java.util.List;

import edu.asu.diging.citesphere.core.model.bib.CitationVersion;
import edu.asu.diging.citesphere.model.bib.ICitation;

public interface ICitationVersionsDao {
    
    List<CitationVersion> getVersions(String groupId, String key, int page, int pageSize);
    
    int getTotalCount(String groupId, String key);
    
    ICitation getVersion(String groupId, String key, long version);

}
