package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.search.service.SearchEngine;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.impl.CitationPage;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemController {

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private SearchEngine engine;
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}")
    public String getItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(defaultValue = "", required = false, value = "searchTerm") String searchTerm, @RequestParam(required = false, value = "index") String index, 
            @RequestParam(defaultValue = "1", required = false, value = "page") int page, @RequestParam(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        
        CitationPage citationPage = null;
        if (searchTerm.isEmpty() || searchTerm.equals(" ")) {
            if (index != null) {
                citationPage = citationManager.getPrevAndNextCitation((IUser) authentication.getPrincipal(),
                        zoteroGroupId, collectionId, page, sortBy, Integer.valueOf(index));
            }
        } else {
            citationPage = engine.getPrevAndNextCitation(searchTerm, zoteroGroupId, page - 1, Integer.valueOf(index),
                    50);
        }
        
        if (citation != null) {
            model.addAttribute("citation", citation);
            List<String> fields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser)authentication.getPrincipal(), citation.getItemType()).forEach(f -> fields.add(f.getFilename()));
            model.addAttribute("fields", fields);
            if (citationPage == null) {
                citationPage = new CitationPage();
            }
            model.addAttribute("adjacentCitations", citationPage);
            model.addAttribute("searchTerm", searchTerm);
            model.addAttribute("index", index);
            model.addAttribute("page", page);
            model.addAttribute("collectionId", collectionId);
            model.addAttribute("sortBy", sortBy);
        }
        return "auth/group/item";
    }
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}/paging")
    public @ResponseBody CitationPage getPrevAndNextItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(required = false, value = "index") String index, @RequestParam(defaultValue = "1", required = false, value = "page") int page,@RequestParam(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        
        if(index != null && index != "") {
            return citationManager.getPrevAndNextCitation((IUser)authentication.getPrincipal(), zoteroGroupId, collectionId, page, sortBy, Integer.valueOf(index));  
        }
        return new CitationPage();
    }
}
