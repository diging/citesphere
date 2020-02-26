package edu.asu.diging.citesphere.web.user.authorities;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javers.common.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
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
    public ResponseEntity<FoundAuthorities> retrieveAuthorityEntry(Authentication authentication, @RequestParam("uri") String uri, @RequestParam("zoteroGroupId") String zoteroGroupId) {
        List<IAuthorityEntry> userEntries = authorityService.findByUri((IUser)authentication.getPrincipal(), uri);
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
     
        entry = authorityService.create(entry, (IUser)authentication.getPrincipal());
        return new ResponseEntity<IAuthorityEntry>(entry, HttpStatus.OK);
    }
    
    @RequestMapping("/auth/authority/getAuthoritiesByName")
    public ResponseEntity<FoundAuthorities> getAuthoritiesByName(Authentication authentication, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName, @RequestParam("zoteroGroupId") String zoteroGroupId) {
 
    		String[] serachStrings = authorityEntryControllerHelper.getAuthoritySearchStrings(firstName, lastName);
    		String databaseSearchString = serachStrings[1];
    		String conceptpowerSearchString = serachStrings[0];
    		
    		
    	    List<IAuthorityEntry> userEntries = authorityService.findByName((IUser)authentication.getPrincipal(), databaseSearchString);
            FoundAuthorities foundAuthorities = new FoundAuthorities();
            foundAuthorities.setUserAuthorityEntries(userEntries);
            
            
            Set<IAuthorityEntry> datasetEntries;
            try {
                datasetEntries = authorityService.findByNameInDataset(databaseSearchString, zoteroGroupId);                
                
            } catch (GroupDoesNotExistException e) {
                logger.warn("Group does not exist: " + zoteroGroupId, e);
                return new ResponseEntity<FoundAuthorities>(HttpStatus.BAD_REQUEST);
            }
            foundAuthorities.setDatasetAuthorityEntries(datasetEntries);
            
            try {
            	List<IAuthorityEntry> importedEntries = authorityService.importAuthorityEntries(conceptpowerSearchString);
                	foundAuthorities.setImportedAuthorityEntries(importedEntries);             
                
            } catch (AuthorityServiceConnectionException e) {
                logger.warn("Could not retrieve authority entries.", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (URISyntaxException e) {
                logger.info("Not a valid URI: " + firstName, e);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            foundAuthorities = authorityEntryControllerHelper.removeDuplicateAuthorities(foundAuthorities);
            return new ResponseEntity<FoundAuthorities>(foundAuthorities, HttpStatus.OK);
    }
    
}
