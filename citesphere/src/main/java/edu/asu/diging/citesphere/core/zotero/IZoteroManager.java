package edu.asu.diging.citesphere.core.zotero;

import java.util.List;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;

public interface IZoteroManager {

    List<ICitationGroup> getGroups(IUser user);

    CitationResults getGroupItems(IUser user, String groupId, int page);

    ICitation getGroupItem(IUser user, String groupId, String itemKey);

}