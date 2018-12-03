package edu.asu.diging.citesphere.core.zotero;

import java.util.List;

import org.springframework.social.zotero.api.Group;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;

public interface IZoteroManager {

    Group[] getGroups(IUser user);

    List<ICitation> getGroupItems(IUser user, String groupId, int page);

}