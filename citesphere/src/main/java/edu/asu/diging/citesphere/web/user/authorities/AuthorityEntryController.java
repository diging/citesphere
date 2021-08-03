package edu.asu.diging.citesphere.web.user.authorities;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

import edu.asu.diging.citesphere.core.exceptions.AuthorityImporterNotFoundException;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.user.AuthoritySearchResult;
import edu.asu.diging.citesphere.web.user.FoundAuthorities;

@Controller
@SessionScope
public class AuthorityEntryController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, AuthoritySearchResult> authoritySearchResult = new HashMap<>();
    
    @Autowired
    private IAuthorityService authorityService;
    
    @Autowired
    private IGroupManager groupManager;
    
    @RequestMapping("/auth/authority/{zoteroGroupId}/find/authorities/user")
    public ResponseEntity<AuthoritySearchResult> getUserAuthorities(Model model, Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId,
            @RequestParam(defaultValue = "0", required = false, value = "page") int page,
            @RequestParam(defaultValue = "10", required = false, value = "pageSize") int pageSize,
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {

        AuthoritySearchResult authorityResult = new AuthoritySearchResult();

        int totalPages = authorityService.getTotalUserAuthoritiesPages((IUser) authentication.getPrincipal(), firstName,
                lastName, pageSize);
        authorityResult.setTotalPages(totalPages);

        Page<IAuthorityEntry> fullNameEntries = authorityService
                .findByFirstNameAndLastName((IUser) authentication.getPrincipal(), firstName, lastName, page, pageSize);
        List<IAuthorityEntry> userEntries = new ArrayList<>();
        userEntries.addAll(fullNameEntries.getContent());
        // If the user entries found by full name does not fill the page entirely,
        // search by just the last name to complete the rest of the page
        // This works for both the cases:
        // 1) The page is partially filled by the entries found by full name
        // 2) All the entries by full name are exhausted and the page is suppose to be
        // entirely filled by entries for last name
        if (userEntries.size() < pageSize && !lastName.trim().isEmpty()) {
            Page<IAuthorityEntry> lastNameEntries = authorityService.findByLastNameAndExcludingFirstName(
                    (IUser) authentication.getPrincipal(), firstName, lastName,
                    page - Math.max(1, fullNameEntries.getTotalPages()) + 1, pageSize - userEntries.size());
            userEntries.addAll(lastNameEntries.getContent());
        }
        authorityResult.setFoundAuthorities(userEntries);
        authorityResult.setCurrentPage(page + 1);
        return new ResponseEntity<AuthoritySearchResult>(authorityResult, HttpStatus.OK);
    }
    
    @RequestMapping("/auth/authority/{zoteroGroupId}/find/authorities/group")
    public ResponseEntity<AuthoritySearchResult> getGroupAuthorities(
            @PathVariable("zoteroGroupId") String zoteroGroupId,
            @RequestParam(defaultValue = "0", required = false, value = "page") int page,
            @RequestParam(defaultValue = "10", required = false, value = "pageSize") int pageSize,
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {

        AuthoritySearchResult authorityResult = new AuthoritySearchResult();
        Long groupId = Long.valueOf(zoteroGroupId);
        int totalPages = authorityService.getTotalGroupAuthoritiesPages(groupId, firstName, lastName, pageSize);
        authorityResult.setTotalPages(totalPages);

        Page<IAuthorityEntry> fullNameEntries = authorityService.findByGroupAndFirstNameAndLastName(groupId, firstName,
                lastName, page, pageSize);
        List<IAuthorityEntry> groupEntries = new ArrayList<>();
        groupEntries.addAll(fullNameEntries.getContent());
        // Same reasoning as the getUserAuthorities() method
        if (groupEntries.size() < pageSize && !lastName.trim().isEmpty()) {
            Page<IAuthorityEntry> lastNameEntries = authorityService.findByGroupAndLastNameAndExcludingFirstName(
                    groupId, firstName, lastName, page - Math.max(1, fullNameEntries.getTotalPages()) + 1,
                    pageSize - groupEntries.size());
            groupEntries.addAll(lastNameEntries.getContent());
        }
        authorityResult.setFoundAuthorities(groupEntries);
        authorityResult.setCurrentPage(page + 1);
        return new ResponseEntity<AuthoritySearchResult>(authorityResult, HttpStatus.OK);
    }
    
    @RequestMapping("/auth/authority/{zoteroGroupId}/find/authorities/{source}")
    public ResponseEntity<AuthoritySearchResult> getAuthoritiesFromAuthorityService(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @PathVariable("source") String source,
            @RequestParam(defaultValue = "0", required = false, value = "page") int page,
            @RequestParam(defaultValue = "20", required = false, value = "pageSize") int pageSize,
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {

        if ((firstName == null || firstName.isEmpty()) && (lastName == null || lastName.isEmpty())) {
            logger.warn(
                    "At least one of the fields must be non-empty. firstName and lastName are empty " + zoteroGroupId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        AuthoritySearchResult searchResult = null;        
        try {
            searchResult = authorityService.searchAuthorityEntries((IUser) authentication.getPrincipal(), firstName,
                    lastName, source, page, pageSize);
            searchResult.setCurrentPage(page + 1);
            searchResult.setGroupName(groupManager.getGroup((IUser) authentication.getPrincipal(), zoteroGroupId).getName());
            authoritySearchResult.put(source, searchResult);

        } catch (AuthorityServiceConnectionException e) {
            logger.warn("Could not retrieve authority entries.", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (AuthorityImporterNotFoundException e) {
            logger.error("AuthorityImporter responsible for search in " + source + " not found ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            
        }

        return new ResponseEntity<AuthoritySearchResult>(searchResult, HttpStatus.OK);
    } 

    @RequestMapping("/auth/authority/get")
    public ResponseEntity<FoundAuthorities> retrieveAuthorityEntry(Authentication authentication, @RequestParam("uri") String uri, @RequestParam("zoteroGroupId") String zoteroGroupId) {
        List<IAuthorityEntry> userEntries = authorityService.findByUri((IUser) authentication.getPrincipal(), uri);
        FoundAuthorities foundAuthorities = new FoundAuthorities();
        foundAuthorities.setUserAuthorityEntries(userEntries);

        try {
            IAuthorityEntry entry = authorityService.importAuthority(uri);
            foundAuthorities.setImportedAuthority(entry);
        } catch (AuthorityServiceConnectionException e) {
            logger.warn("Could not retrieve authority entry.", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (URISyntaxException e) {
            logger.info("Not a valid URI: " + uri, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        
        Set<IAuthorityEntry> datasetEntries;
        try {
            datasetEntries = authorityService.findByUriInDataset(uri, zoteroGroupId);
        } catch (GroupDoesNotExistException e) {
            logger.warn("Group does not exist: " + zoteroGroupId, e);
            return new ResponseEntity<FoundAuthorities>(HttpStatus.BAD_REQUEST);
        }
        foundAuthorities.setDatasetAuthorityEntries(datasetEntries);
        return new ResponseEntity<FoundAuthorities>(foundAuthorities, HttpStatus.OK);
    }

    @RequestMapping(value="/auth/authority/create", method=RequestMethod.POST)
    public ResponseEntity<IAuthorityEntry> createAuthorityEntry(Authentication authentication, @RequestParam("uri") String uri) {
        IAuthorityEntry entry = null;
        try {
            entry = authorityService.importAuthority(uri);
        } catch (AuthorityServiceConnectionException e) {
            logger.warn("Could not retrieve authority entry.", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (URISyntaxException e) {
            logger.info("Not a valid URI: " + uri, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        entry = authorityService.create(entry, (IUser) authentication.getPrincipal());
        return new ResponseEntity<IAuthorityEntry>(entry, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/auth/authority/add", method = RequestMethod.POST)
    public ResponseEntity<IAuthorityEntry> addAuthorityEntry(Authentication authentication,
            @RequestParam("uri") String uri, @RequestParam("source") String source,
            @RequestParam(defaultValue = "", required = false, value = "zoteroGroupId") String zoteroGroupId) {
        
        IAuthorityEntry entry = null;
        if(source != null && authoritySearchResult!= null && authoritySearchResult.containsKey(source)) {
            Optional<IAuthorityEntry> authorityEntry = authoritySearchResult.get(source).getFoundAuthorities().stream()
                    .filter(authority -> authority.getUri().equals(uri)).findFirst();
            
            if (authorityEntry.isPresent()) {
                if(!zoteroGroupId.isEmpty()) {
                    if(authorityEntry.get().getGroups() == null) {
                        authorityEntry.get().setGroups(new HashSet<>());
                    }
                    authorityEntry.get().getGroups().add(Long.valueOf(zoteroGroupId));
                }
                entry = authorityService.create(authorityEntry.get(), (IUser) authentication.getPrincipal());
            }
        }
        return new ResponseEntity<IAuthorityEntry>(entry, HttpStatus.OK);
    }
    
}
