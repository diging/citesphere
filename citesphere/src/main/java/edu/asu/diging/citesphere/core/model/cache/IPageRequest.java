package edu.asu.diging.citesphere.core.model.cache;

import java.util.List;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ZoteroObjectType;

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

}