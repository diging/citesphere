package edu.asu.diging.citesphere.web.user.authorities;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.web.user.AuthorityResult;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.web.user.FoundAuthorities;
import edu.asu.diging.citesphere.web.user.authorities.helper.AuthorityEntryControllerHelper;

@Controller
public class AuthorityEntryController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IAuthorityService authorityService;

    @Autowired
    private AuthorityEntryControllerHelper authorityEntryControllerHelper;

    @RequestMapping("/auth/authority/get")
    public ResponseEntity<FoundAuthorities> retrieveAuthorityEntry(Authentication authentication,
            @RequestParam("uri") String uri, @RequestParam("zoteroGroupId") String zoteroGroupId) {
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

    @RequestMapping(value = "/auth/authority/create", method = RequestMethod.POST)
    public ResponseEntity<IAuthorityEntry> createAuthorityEntry(Authentication authentication,
            @RequestParam("uri") String uri) {
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

    @RequestMapping("/auth/authority/{zoteroGroupId}/find/userAuthorities")
    public ResponseEntity<AuthorityResult> getUserAuthorities(Model model, Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId,
            @RequestParam(defaultValue = "0", required = false, value = "page") int page,
            @RequestParam(defaultValue = "10", required = false, value = "pageSize") int pageSize,
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {

        AuthorityResult authorityResult = new AuthorityResult();

        List<IAuthorityEntry> userEntries = authorityService.findByName((IUser) authentication.getPrincipal(),
                firstName, lastName, page, pageSize);

        authorityResult.setFoundAuthorities(userEntries);
        authorityResult.setCurrentPage(page + 1);
        authorityResult.setTotalPages(authorityService.getTotalUserAuthorities((IUser) authentication.getPrincipal(),
                firstName, lastName, pageSize));
        return new ResponseEntity<AuthorityResult>(authorityResult, HttpStatus.OK);
    }

    @RequestMapping("/auth/authority/{zoteroGroupId}/find/datasetAuthorities")
    public ResponseEntity<AuthorityResult> getDatasetAuthorities(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId,
            @RequestParam(defaultValue = "0", required = false, value = "page") int page,
            @RequestParam(defaultValue = "10", required = false, value = "pageSize") int pageSize,
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {

        AuthorityResult authorityResult = new AuthorityResult();

        Set<IAuthorityEntry> datasetEntries = null;

        try {
            datasetEntries = authorityService.findByNameInDataset((IUser) authentication.getPrincipal(), firstName,
                    lastName, zoteroGroupId, page, pageSize);
            authorityResult.setFoundAuthorities(datasetEntries.stream().collect(Collectors.toList()));
            authorityResult.setCurrentPage(page + 1);
            authorityResult.setTotalPages(
                    authorityService.getTotalDatasetAuthorities(zoteroGroupId, firstName, lastName, pageSize));

        } catch (GroupDoesNotExistException e) {
            logger.warn("Group does not exist: " + zoteroGroupId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<AuthorityResult>(authorityResult, HttpStatus.OK);
    }

    @RequestMapping("/auth/authority/{zoteroGroupId}/find/importedAuthorities")
    public ResponseEntity<AuthorityResult> getImportedAuthorities(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId,
            @RequestParam(defaultValue = "0", required = false, value = "page") int page,
            @RequestParam(defaultValue = "20", required = false, value = "pageSize") int pageSize,
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {

        AuthorityResult authorityResult = new AuthorityResult();

        List<IAuthorityEntry> importedEntries = null;

        try {
            importedEntries = authorityService.importAuthorityEntries((IUser) authentication.getPrincipal(), firstName,
                    lastName, authorityEntryControllerHelper.getConceptpowerSearchString(firstName, lastName), page,
                    pageSize);
            authorityResult.setFoundAuthorities(importedEntries.stream().collect(Collectors.toList()));
            authorityResult.setCurrentPage(page + 1);
            if (page == 0) {
                authorityResult.setTotalPages(authorityService.getTotalImportedAuthorities(
                        authorityEntryControllerHelper.getConceptpowerSearchString(firstName, lastName), pageSize));
            }

        } catch (AuthorityServiceConnectionException e) {
            logger.warn("Could not retrieve authority entries.", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (URISyntaxException e) {
            logger.warn("Not a valid URI: " + firstName + lastName, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<AuthorityResult>(authorityResult, HttpStatus.OK);
    }

}
