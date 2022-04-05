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

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.impl.Person;
import edu.asu.diging.citesphere.model.transfer.impl.Persons;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ShowPeopleController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    private List<Person> peopleList = new ArrayList<Person>();

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/people")
    public String showPeople(Model model, Authentication authentication, @PathVariable("zoteroGroupId") String groupId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort) {

        Persons personList = null;
        personList = citationManager.getAllPeople(groupId) ;
        model.addAttribute("people", personList.getPersons());

        peopleList = personList.getPersons();
        return "auth/group/showPeople";
    }

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/showCitation/{index}")
    public String showCitation(Model model, Authentication authentication,
            @PathVariable("zoteroGroupId") String groupId,
            @PathVariable(value = "index", required = false) String index) {

        model.addAttribute("zoteroGroupId", groupId);

//        if (index != null && !index.isEmpty()) {
//            model.addAttribute("involvedCitations",
//                    personCitationSortedMap.get(personCitationSortedMap.keySet().toArray()[Integer.parseInt(index)]));
//        }
        
        Person p = peopleList.get(Integer.parseInt(index));
//        List<ICitation> citations;
        List<ICitation> citations = citationManager.getAllCitations(p) ;
//        model.addAttribute("people", personList.getPersons());

        
        return "auth/group/showCitations";
    }

}
