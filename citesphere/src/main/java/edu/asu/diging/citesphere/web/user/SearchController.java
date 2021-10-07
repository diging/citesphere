package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

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
@PropertySource("classpath:/item_type_icons.properties")
@PropertySource("classpath:/labels.properties")
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
    
    @Autowired
    private Environment env;

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/search" })
    public @ResponseBody String search(@PathVariable String zoteroGroupId,
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
        
        SearchItemsDataDto searchItemsData  = new SearchItemsDataDto();
        searchItemsData.setSearchTerm(searchTerm);
        searchItemsData.setCurrentPage(pageInt);
        searchItemsData.setCitationsData(citations.getResults().stream().map(c -> new CitationsDto(c,
                env.getProperty(c.getItemType() + "_label"), env.getProperty(c.getItemType() + "_icon")))
                .collect(Collectors.toList()));
        searchItemsData.setTotalPages( Math.max(1, citations.getTotalPages()));
        searchItemsData.setZoteroGroupId(zoteroGroupId);
        searchItemsData.setSort(sort);
        searchItemsData.setTotalResults(citations.getTotalResults());
        searchItemsData.setGroup(group);
        
        List<String> availableColumnsList = Arrays.asList(availableColumns.split(","));        
        List<String> shownColumns = new ArrayList<>();
        if (columns != null && columns.length > 0) {
            for (String column : columns) {
                if (availableColumnsList.contains(column)) {
                    shownColumns.add(column);
                }
            }
        }
        searchItemsData.setShownColumns(shownColumns);
        searchItemsData.setAvailableColumnsData(availableColumnsList.stream().map(c -> 
            new AvailableColumnsDataDto(c, env.getProperty("_item_attribute_label_"+c))).collect(Collectors.toList()));

        Gson gson = new Gson();
        
        return gson.toJson(searchItemsData, SearchItemsDataDto.class);
    }
}
