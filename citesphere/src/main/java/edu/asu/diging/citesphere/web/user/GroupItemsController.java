package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.BreadCrumb;
import edu.asu.diging.citesphere.web.BreadCrumbType;

@Controller
@PropertySource("classpath:/config.properties")
public class GroupItemsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;
    
    @Value("${_available_item_columns}")
    private String availableColumns;

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ICitationCollectionManager collectionManager;
    
    @Autowired
    private IGroupManager groupManager;
    
    @Autowired
    private ICitationConceptManager conceptManager;

    @RequestMapping(value= { "/auth/group/{zoteroGroupId}","/auth/group/{zoteroGroupId}/items", "/auth/group/{zoteroGroupId}/collection/{collectionId}/items"})
    public String show(Authentication authentication, Model model, @PathVariable("zoteroGroupId") String groupId,
            @PathVariable(value="collectionId", required=false) String collectionId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort,
            @RequestParam(required = false, value = "columns") String[] columns,
            @RequestParam(required = false, defaultValue = "", value = "conceptIds") String[] conceptIds) {
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.warn("Trying to access invalid page number: " + page);
        }
        IUser user = (IUser) authentication.getPrincipal();
        CitationResults results;
        try {
            results = citationManager.getGroupItems(user, groupId, collectionId, pageInt, sort, Arrays.asList(conceptIds));
        } catch(ZoteroHttpStatusException e) {
            logger.error("Exception occured", e);
            return "error/500";
        } catch(GroupDoesNotExistException e) {
            logger.error("Exception occured", e);
            return "error/404";
        } catch(DuplicateKeyException e) {
            logger.error("Exception occured", e);
            return "error/500";
        }
        
        model.addAttribute("items", results.getCitations());
        model.addAttribute("total", results.getTotalResults());
        model.addAttribute("totalPages", Math.ceil(new Float(results.getTotalResults()) / new Float(zoteroPageSize)));
        model.addAttribute("currentPage", pageInt);
        model.addAttribute("zoteroGroupId", groupId);
        try {
            model.addAttribute("group", groupManager.getGroup(user, groupId));
        } catch(DuplicateKeyException e) {
            logger.error("Exception occured", e);
            return "error/500";
        }
        model.addAttribute("collectionId", collectionId);
        model.addAttribute("sort", sort);
        model.addAttribute("results", results);
        // more than 200 really don't make sense here, this needs to be changed
        try {
            model.addAttribute("citationCollections", collectionManager.getAllCollections(user, groupId, collectionId, "title", 200));
        } catch(GroupDoesNotExistException e) {
            logger.error("Exception occured", e);
            return "error/404";
        }
        List<String> allowedColumns = Arrays.asList(availableColumns.split(","));
        List<String> shownColumns = new ArrayList<>();
        if (columns != null && columns.length > 0) {
            for (String column : columns) {
                if (allowedColumns.contains(column)) {
                    shownColumns.add(column);
                }
            }
        }
        model.addAttribute("columns", shownColumns);
        model.addAttribute("availableColumns", allowedColumns);
        model.addAttribute("concepts", conceptManager.findAll(user));
        model.addAttribute("selectedConceptIds", Arrays.asList(conceptIds));
        
        ICitationGroup group = groupManager.getGroup(user, groupId);
        List<BreadCrumb> breadCrumbs = new ArrayList<>();
        ICitationCollection collection = null;
        if (collectionId != null) {
            collection = collectionManager.getCollection(user, groupId, collectionId);
            if(collection != null) {
                model.addAttribute("collectionName", collection.getName()); 
            }
        }
        while(collection != null) {
            breadCrumbs.add(new BreadCrumb(collection.getName(), BreadCrumbType.COLLECTION, collection.getKey(), collection));
            if (collection.getParentCollectionKey() != null) {
                collection = collectionManager.getCollection(user, groupId, collection.getParentCollectionKey());
            } else {
                collection = null;
            }
        }
        breadCrumbs.add(new BreadCrumb(group.getName(), BreadCrumbType.GROUP, group.getGroupId() + "", group));
        Collections.reverse(breadCrumbs);
        model.addAttribute("breadCrumbs", breadCrumbs);
        return "auth/group/items";
    }
}
