package edu.asu.diging.citesphere.web.user;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;

@Controller
public class SyncGroupItemsController {

    @Autowired
    private IZoteroConnector zoteroConnector;
    
    @Autowired
    private CitationGroupRepository groupRepository;
    
    @RequestMapping(value = {"/auth/group/{zoteroGroupId}/items/sync", "/auth/group/{zoteroGroupId}/collection/{collectionId}/items/sync"})
    public String getCitationsList(Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId,
            @PathVariable(value="collectionId", required=false) String collectionId,
            @RequestParam(required = false, value = "page") String page,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort,
            @RequestParam(required = false, value = "columns") String columns) throws GroupDoesNotExistException {
        Optional<CitationGroup> groupOptional = groupRepository.findById(new Long(zoteroGroupId));
        if (groupOptional.isPresent()) {
            ICitationGroup group = groupOptional.get();
            if (collectionId == null || collectionId.trim().isEmpty()) {
                zoteroConnector.clearGroupItemsCache((IUser) authentication.getPrincipal(), zoteroGroupId, new Integer(page), sort, group.getVersion());
            } else {
                zoteroConnector.clearCollectionItemsCache((IUser) authentication.getPrincipal(), zoteroGroupId, collectionId, new Integer(page), sort, group.getVersion());
                return "redirect:/auth/group/{zoteroGroupId}/collection/{collectionId}/items?page=" + page + "&sort=" + sort+ "&columns=" + columns;
            }
        }
        return "redirect:/auth/group/{zoteroGroupId}/items?page=" + page + "&sort=" + sort+ "&columns=" + columns;
    }
}
