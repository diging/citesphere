package edu.asu.diging.citesphere.core.zotero;

import org.springframework.social.zotero.api.Group;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;

public interface IZoteroManager {

    Group[] getGroups(IUser user);

    CitationResults getGroupItems(IUser user, String groupId, int page);

}