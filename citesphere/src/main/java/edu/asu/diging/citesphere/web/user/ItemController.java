package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.cache.impl.PageRequest;
import edu.asu.diging.citesphere.core.repository.cache.PageRequestRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ZoteroObjectType;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemController {

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private PageRequestRepository pageRequestRepository;
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}")
    public String getItem(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("itemId") String itemId,
            @RequestParam(required = false, value = "index") int index, @RequestParam(defaultValue = "1", required = false, value = "page") String page) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        ICitation citation = citationManager.getCitation((IUser)authentication.getPrincipal(), zoteroGroupId, itemId);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        
        if (citation != null) {
            model.addAttribute("citation", citation);
            List<String> fields = new ArrayList<>();
            citationManager.getItemTypeFields((IUser)authentication.getPrincipal(), citation.getItemType()).forEach(f -> fields.add(f.getFilename()));
            model.addAttribute("fields", fields);
            model.addAttribute("index", index);
            model.addAttribute("page", page);
        }
        return "auth/group/items/item";
    }
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/item")
    public String getItemByIndex(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String zoteroGroupId,
            @RequestParam(value="index", required=true) int index, Pageable pageable) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        Iterable<PageRequest> results = pageRequestRepository.findAll();
        for(ICitation c: results.iterator().next().getCitations()) {
            System.out.println(c.getKey() + " " + c.getTitle());
        }
        ICitation citation = results.iterator().next().getCitations().get(index);
        return "redirect:/auth/group/" + zoteroGroupId + "/items/" + citation.getKey()+"?index="+index;
    }
    
}
