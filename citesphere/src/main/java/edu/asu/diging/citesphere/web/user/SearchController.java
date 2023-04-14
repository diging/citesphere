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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.AuthorityImporterNotFoundException;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.search.service.SearchEngine;
import edu.asu.diging.citesphere.core.search.service.impl.ResultPage;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.BreadCrumb;
import edu.asu.diging.citesphere.web.BreadCrumbType;

@Controller
@PropertySource("classpath:/config.properties")
public class SearchController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SearchEngine engine;

    @Autowired
    private IGroupManager groupManager;

    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;

    @Value("${_available_item_columns}")
    private String availableColumns;

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/search" })
    public String search(@PathVariable String zoteroGroupId,
            @RequestParam(value = "searchTerm", required = false) String searchTerm, Model model,
            @RequestParam(defaultValue = "0", required = false, value = "page") String page,
            @RequestParam(defaultValue = "title", required = false, value = "sort") String sort,
            @RequestParam(required = false, value = "columns") String[] columns, Authentication authentication) {

        IUser user = (IUser) authentication.getPrincipal();
        ICitationGroup group = groupManager.getGroup(user, zoteroGroupId);

        if (group == null) {
            logger.error("User " + user.getUsername() + " does not have access to group " + zoteroGroupId);
            return "error/404";
        }

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return "redirect:/auth/group/" + zoteroGroupId + "/items";
        }
        
        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.warn("Trying to access invalid page number: " + page);
        }
        
        pageInt = pageInt > 0 ? pageInt : 1;

        ResultPage citations = engine.search(searchTerm, zoteroGroupId, pageInt-1, 50);

        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("items", citations.getResults());
        model.addAttribute("totalPages", Math.max(1, citations.getTotalPages()));
        model.addAttribute("total", citations.getTotalResults());
        model.addAttribute("currentPage", pageInt);
        model.addAttribute("zoteroGroupId", zoteroGroupId);
        model.addAttribute("group", groupManager.getGroup(user, zoteroGroupId));
        model.addAttribute("sort", sort);

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

        List<BreadCrumb> breadCrumbs = new ArrayList<>();
        breadCrumbs.add(new BreadCrumb(group.getName(), BreadCrumbType.GROUP, group.getGroupId() + "", group));
        Collections.reverse(breadCrumbs);
        model.addAttribute("breadCrumbs", breadCrumbs);
        return "auth/group/items";
    }
    
    @RequestMapping(value = {"/auth/group/{zoteroGroupId}/find/references"})
    public ResponseEntity<ResultPage> getReferences(Authentication authentication,
            @PathVariable String zoteroGroupId,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(defaultValue = "0", required = false, value = "page") int page,
            @RequestParam(defaultValue = "20", required = false, value = "pageSize") int pageSize ) {
    
        IUser user = (IUser) authentication.getPrincipal();
        ICitationGroup group = groupManager.getGroup(user, zoteroGroupId);

        Integer pageInt = 1;
        try {
            pageInt = new Integer(page);
        } catch (NumberFormatException ex) {
            logger.warn("Trying to access invalid page number: " + page);
        }
        
        pageInt = pageInt > 0 ? pageInt : 1;

        ResultPage citations = engine.search(searchTerm, zoteroGroupId, pageInt-1, 50);
        
        return new ResponseEntity<ResultPage>(citations, HttpStatus.OK);
    }
}
