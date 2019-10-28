package edu.asu.diging.citesphere.web.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.core.repository.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;

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
        
        String redirectUrl = "redirect:/auth/group/{zoteroGroupId}/items?page=" + page + "&sort=" + sort;
        columns = (columns.substring(1, columns.length()-1)).replace(" ",""); 
        if (groupOptional.isPresent()) {
            ICitationGroup group = groupOptional.get();
            if (collectionId == null || collectionId.trim().isEmpty()) {
                zoteroConnector.clearGroupItemsCache((IUser) authentication.getPrincipal(), zoteroGroupId, new Integer(page), sort, group.getVersion());
                if(!columns.equals("")) {
                    redirectUrl = "redirect:/auth/group/{zoteroGroupId}/items?page=" + page + "&sort=" + sort + "&columns=" + columns;
                }
            } else {
                zoteroConnector.clearCollectionItemsCache((IUser) authentication.getPrincipal(), zoteroGroupId, collectionId, new Integer(page), sort, group.getVersion());
                if(columns.equals("")) {
                    redirectUrl =  "redirect:/auth/group/{zoteroGroupId}/collection/{collectionId}/items?page=" + page + "&sort=" + sort;
                } else {
                    redirectUrl = "redirect:/auth/group/{zoteroGroupId}/collection/{collectionId}/items?page=" + page + "&sort=" + sort + "&columns=" + columns;
                }
            }
        }
        

        return redirectUrl;
    }
}
