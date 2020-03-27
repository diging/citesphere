package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.CitationEnum;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemController {

    @Autowired
    private ICitationManager citationManager;
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}")
    public String getItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(required = false, value = "index") String index, @RequestParam(defaultValue = "1", required = false, value = "page") int page,@RequestParam(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "title", required = false, value = "sortBy") String sortBy) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        
        if (citation != null) {
            model.addAttribute("citation", citation);
            List<String> fields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser)authentication.getPrincipal(), citation.getItemType()).forEach(f -> fields.add(f.getFilename()));
            model.addAttribute("fields", fields);
            if(index != null) {
                model.addAttribute("index", index);
                Map<CitationEnum,String> results = citationManager.getPrevAndNextCitation((IUser)authentication.getPrincipal(), zoteroGroupId, collectionId, page, sortBy, Integer.valueOf(index));
                if(results.containsKey(CitationEnum.NEXT)) {
                    model.addAttribute("next", results.get(CitationEnum.NEXT));
                    model.addAttribute("nextIndex", results.get(CitationEnum.NEXTINDEX));
                    model.addAttribute("nextPage", results.get(CitationEnum.NEXTPAGE));
                }
                if(results.containsKey(CitationEnum.PREVIOUS)) {
                    model.addAttribute("previous", results.get(CitationEnum.PREVIOUS));
                    model.addAttribute("prevIndex", results.get(CitationEnum.PREVINDEX));
                    model.addAttribute("prevPage", results.get(CitationEnum.PREVPAGE));
                }
            }
            model.addAttribute("page", page);
            model.addAttribute("collectionId", collectionId);
            model.addAttribute("sortBy", sortBy);
        }
        return "auth/group/items/item";
    }
    
}
