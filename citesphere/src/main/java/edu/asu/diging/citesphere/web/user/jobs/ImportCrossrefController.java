package edu.asu.diging.citesphere.web.user.jobs;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.crossref.CrossrefService;
import edu.asu.diging.citesphere.core.service.jobs.IImportCrossrefJobManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.Item;

@Controller
public class ImportCrossrefController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CrossrefService crossrefService;
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private IUserManager userManager;

    @Autowired
    private IImportCrossrefJobManager jobManager;

    @RequestMapping("/auth/import/crossref")
    public String get(Model model, Principal principal) {
        model.addAttribute("groups", citationManager.getGroups(userManager.findByUsername(principal.getName())));
        return "auth/import/crossref";
    }

    @RequestMapping("/auth/import/crossref/search")
    public ResponseEntity<List<Item>> search(@RequestParam("query") String query, @RequestParam("page") int page) {
        List<Item> results = null;
        try {
            results = crossrefService.search(query, page);
        } catch (RequestFailedException | IOException e) {
            logger.error("Could not get Crossref results.", e);
            return new ResponseEntity<List<Item>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<List<Item>>(results, HttpStatus.OK);
    }

    @RequestMapping(value="/auth/import/crossref", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> post(Authentication authentication, @RequestParam("groupId") String groupId,
            @RequestParam("dois[]") List<String> dois, Model model) {
        Map<String, Object> response = new HashMap<>();
        try {
            jobManager.createJob((IUser) authentication.getPrincipal(), groupId, dois);
            response.put("show_alert", true);
            response.put("alert_type", "success");
            response.put("alert_msg", "Import in progress.");
        } catch (GroupDoesNotExistException e) {
            logger.error("Could not create crossref job because group does not exist.", e);
            response.put("show_alert", true);
            response.put("alert_type", "danger");
            response.put("alert_msg", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
}
