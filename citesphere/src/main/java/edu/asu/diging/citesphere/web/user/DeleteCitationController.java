package edu.asu.diging.citesphere.web.user;

import java.util.List;
import java.util.Properties;

import org.javers.common.collections.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationManager;

@Controller
public class DeleteCitationController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_creation_default_item_type}")
    private String defaultItemType;

    @Autowired
    @Qualifier("creatorsFile")
    private Properties properties;

    @Autowired
    private ICitationManager citationManager;


    @RequestMapping(value = "/auth/group/{groupId}/delete", method = RequestMethod.POST)
    public String delete(Authentication authentication,@PathVariable("groupId") String groupId, @RequestParam(value="citationList", required=false) String citationList)
            throws ZoteroConnectionException, GroupDoesNotExistException, ZoteroHttpStatusException, ZoteroItemCreationFailedException {
        List citations = Arrays.asList(citationList.split(","));
        try {
            citationManager.deleteCitations(citations);
        } catch (CannotFindCitationException e) {
            logger.error("Cition not found.", e);
            return new String ("{\"error\": \"Selected citation(s) not found.\"}" +  HttpStatus.NOT_FOUND);
        }
        return "redirect:/auth/group/{groupId}/items";
    }
}
