package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.search.service.SearchEngine;
import edu.asu.diging.citesphere.core.search.service.impl.ResultPage;

@Controller
@PropertySource("classpath:/config.properties")
public class SearchCitationsController {

    @Autowired
    private SearchEngine engine;

    @RequestMapping(value = { "/auth/group/{zoteroGroupId}/citations/search" })
    public ResponseEntity<ResultPage> searchCitations(Authentication authentication, @PathVariable String zoteroGroupId,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(defaultValue = "0", required = false, value = "page") int page,
            @RequestParam(defaultValue = "20", required = false, value = "pageSize") int pageSize) {
        return new ResponseEntity<ResultPage>(engine.search(searchTerm, zoteroGroupId, Math.max(page - 1, 0), pageSize), HttpStatus.OK);
    }

}
