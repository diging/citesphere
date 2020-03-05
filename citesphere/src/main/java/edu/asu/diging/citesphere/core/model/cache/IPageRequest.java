package edu.asu.diging.citesphere.core.model.cache;

import java.time.OffsetDateTime;
import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ZoteroObjectType;
import edu.asu.diging.citesphere.user.IUser;

public interface IPageRequest {

    String getId();

    void setId(String id);

    ZoteroObjectType getZoteroObjectType();

    void setZoteroObjectType(ZoteroObjectType zoteroObjectType);

    int getPageNumber();

    void setPageNumber(int pageNumber);

    int getPageSize();

    void setPageSize(int pageSize);

    IUser getUser();

    void setUser(IUser user);

    String getObjectId();

    void setObjectId(String objectId);

    long getVersion();

    void setVersion(long version);

    void setCitations(List<ICitation> citations);

    List<ICitation> getCitations();

    void setSortBy(String sortBy);

    String getSortBy();

    void setLastUpdated(OffsetDateTime lastUpdated);

    OffsetDateTime getLastUpdated();

    void setTotalNumResults(long totalNumResults);

    long getTotalNumResults();

}