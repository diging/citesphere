package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationForm;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.AbstractMap.SimpleEntry;
import static java.util.stream.Collectors.toMap;

@Controller
public class ShowPeopleController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @RequestMapping(value = {"/auth/group/{zoteroGroupId}/items/showPeople", "/auth/group/{zoteroGroupId}/items/showCitation/{index}"})
    public String showPeople(Model model, Authentication authentication, @PathVariable("zoteroGroupId") String groupId,
            @PathVariable(value="index", required=false) String index, @PathVariable(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort,
            @RequestParam(required = false, value = "columns") String[] columns) {
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.warn("Trying to access invalid page number: " + page);
        }
        IUser user = (IUser) authentication.getPrincipal();
        CitationResults results;
        try {
            results = citationManager.getGroupItems(user, groupId, collectionId, pageInt, sort);
        } catch(ZoteroHttpStatusException e) {
            logger.error("Exception occured", e);
            return "error/500";
        } catch(GroupDoesNotExistException e) {
            logger.error("Exception occured", e);
            return "error/404";
        }
        model.addAttribute("collectionId", collectionId);
        model.addAttribute("zoteroGroupId", groupId);
        model.addAttribute("currentPage", pageInt);
        model.addAttribute("sort", sort);
        
        Map<IPerson, List<ICitation>> personCitationMap=new HashMap<IPerson, List<ICitation>>();  
        
        results.getCitations().stream().forEach(
                iCitation -> (Stream.of(iCitation.getAuthors().stream(), iCitation.getEditors().stream(), 
                        iCitation.getOtherCreators().stream().map(iOtherCreator -> iOtherCreator.getPerson()))
                        .reduce(Stream::concat).orElseGet(Stream::empty)).sorted().forEach(
                        au -> {
                            boolean containKey = false;
                                for (IPerson key : personCitationMap.keySet()) {
                                    if (key.getFirstName().equals(au.getFirstName()) && 
                                            key.getLastName().equals(au.getLastName())) {
                                        personCitationMap.get(key).add(iCitation);
                                        containKey = true;
                                        break;
                                    }
                                }
                                if (!containKey) {
                                  List<ICitation> citList = new ArrayList<ICitation>();
                                  citList.add(iCitation);
                                  personCitationMap.put(au, citList);
                                }
                                
                            }
                        ));
        
        Collection<IPerson> unsorted = personCitationMap.keySet();
        List<IPerson> peopleList = new ArrayList<IPerson>(unsorted);  
        
        peopleList = peopleList.stream().sorted().collect(Collectors.toList());
        
        model.addAttribute("people", peopleList);

    if (index != null && !index.isEmpty()) {
        model.addAttribute("involvedCitations", personCitationMap.get(personCitationMap.keySet().toArray()[Integer.parseInt(index)]));
        return "auth/group/showCitations";
    }
    else
        return "auth/group/showPeople";
    }

}
