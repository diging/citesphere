package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Value;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.Person;
import edu.asu.diging.citesphere.model.transfer.impl.Citations;
import edu.asu.diging.citesphere.model.transfer.impl.Persons;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ShowPeopleController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;

    @Autowired
    private ICitationManager citationManager;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/people")
    public String showPeople(Model model, Authentication authentication, @PathVariable("zoteroGroupId") String groupId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page) {
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.warn("Trying to access invalid page number: " + page);
        }
        Persons personList = citationManager.getAllPeople(groupId, pageInt);
        model.addAttribute("groupIdNo", groupId);
        model.addAttribute("total", personList.getTotalResults());
        model.addAttribute("totalPages", Math.ceil(new Float(personList.getTotalResults()) / new Float(zoteroPageSize)));
        model.addAttribute("currentPage", pageInt);
        if (personList != null) {
            model.addAttribute("people", personList.getPersons());
        } else {
            model.addAttribute("people", null);
        }
        return "auth/group/showPeople";
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/showCitation")
    public String showCitation(Model model, Authentication authentication,
            @PathVariable("zoteroGroupId") String groupId,
            @RequestParam(defaultValue = "", required = false, value = "uri") String uri,
            @RequestParam(defaultValue = "", required = false, value = "citationKey") String citationKey) {

        int pageInt = 1;
        model.addAttribute("zoteroGroupId", groupId);
        model.addAttribute("currentPage", pageInt);
        
        Citations citations = new Citations();
        if (uri != null && !uri.trim().isEmpty()) {
            citations = citationManager.getCitationsByPersonUri(uri);
        }
        else {
            citations = citationManager.getCitationsByPersonCitationKey(citationKey);
        }
        if (citations != null) {
            model.addAttribute("involvedCitations", citations.getCitations());
        }
        return "auth/group/showCitations";
    }

}
