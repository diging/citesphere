package edu.asu.diging.citesphere.core.service.impl;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.authority.IImportedAuthority;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.repository.custom.AuthorityRepository;
import edu.asu.diging.citesphere.core.repository.custom.PersonAuthorityRepository;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.data.AuthorityEntryRepository;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.data.bib.PersonRepository;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.Person;

@Service
public class AuthorityService implements IAuthorityService {

    @Autowired
    private AuthorityEntryRepository entryRepository;
    
    @Autowired
    private CitationGroupRepository groupRepository;
    
    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
    @Autowired
    private PersonAuthorityRepository personAuthorityRepository;
      
    @Autowired
    private Set<AuthorityImporter> importers;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IAuthorityService#register(edu.asu.diging.citesphere.authority.AuthorityImporter)
     */
    @Override
    public void register(AuthorityImporter importer) {
        this.importers.add(importer);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IAuthorityService#importAuthority(java.lang.String)
     */
    @Override
    public IAuthorityEntry importAuthority(String uri) throws URISyntaxException, AuthorityServiceConnectionException {
        for(AuthorityImporter importer : importers) {
            if (importer.isResponsible(uri)) {
                IImportedAuthority importedAuthority = importer.retrieveAuthorityData(uri);
                if (importedAuthority == null) {
                    return null;
                }
                
                IAuthorityEntry entry = new AuthorityEntry();
                entry.setName(importedAuthority.getName());
                entry.setUri(importedAuthority.getUri());
                entry.setImporterId(importer.getId());
                return entry;
            }
        }
        return null;
    }
    
    @Override
    public List<IAuthorityEntry> importAuthorityEntries(String searchString, int page) throws URISyntaxException, AuthorityServiceConnectionException {
        List<IAuthorityEntry> authorityEntries = new ArrayList<IAuthorityEntry>();
        
        for(AuthorityImporter importer : importers) {

            if(importer.retrieveAuthoritiesData(searchString, page) != null)
                    authorityEntries.addAll(importer.retrieveAuthoritiesData(searchString, page)); 

        }
        return authorityEntries;
    }
    
    @Override
    public IAuthorityEntry find(String id) {
        Optional<AuthorityEntry> entryOptional = entryRepository.findById(id);
        if (entryOptional.isPresent()) {
            return entryOptional.get();
        }
        return null;
    }
    
    @Override
    public List<IAuthorityEntry> findByUri(IUser user, String uri) {
        List<IAuthorityEntry> results = entryRepository.findByUsernameAndUriOrderByName(user.getUsername(), uri);
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        } else {
            uri = uri + "/";
        }
        results.addAll(entryRepository.findByUsernameAndUriOrderByName(user.getUsername(), uri));
        return results;
    }
    
    @Override
    public List<IAuthorityEntry> findByName(IUser user, String firstName, String lastName, int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);
        List<IAuthorityEntry> results = authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(), firstName, lastName);
        return results;
    }
    
    @Override
    public Set<IAuthorityEntry> findByUriInDataset(String uri, String citationGroupId) throws GroupDoesNotExistException {
        Optional<CitationGroup> group = groupRepository.findById(new Long(citationGroupId));
        if (!group.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + citationGroupId + " does not exist.");
        }
        List<Person> persons = personRepository.findPersonsByCitationGroupAndUri((ICitationGroup)group.get(), uri);
        Set<IAuthorityEntry> entries = new HashSet<>();
        persons.forEach(p -> {
            Optional<AuthorityEntry> optional = entryRepository.findById(p.getLocalAuthorityId());
            if (optional.isPresent()) {
                entries.add(optional.get());
            }
        });
        return entries;
    }
    
    @Override
    public Set<IAuthorityEntry> findByNameInDataset(String firstName, String lastName, String citationGroupId, List<String> uris, int page) throws GroupDoesNotExistException {
        Optional<CitationGroup> group = groupRepository.findById(new Long(citationGroupId));
        if (!group.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + citationGroupId + " does not exist.");
        }
        Pageable paging = PageRequest.of(page, 20);
        List<Person> persons = personAuthorityRepository.findPersonsByCitationGroupAndNameLikeAndUriNotIn((ICitationGroup)group.get(), firstName, lastName, uris, paging);
        Set<IAuthorityEntry> entries = new HashSet<>();
        persons.forEach(p -> {
            Optional<AuthorityEntry> optional = entryRepository.findById(p.getLocalAuthorityId());
            if (optional.isPresent()) {
                entries.add(optional.get());
            }
        });
        return entries;
    }
    
    @Override
    public Set<IAuthorityEntry> findByNameInDataset(String firstName, String lastName, String citationGroupId, int page) throws GroupDoesNotExistException {
        Optional<CitationGroup> group = groupRepository.findById(new Long(citationGroupId));
        if (!group.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + citationGroupId + " does not exist.");
        }
        Pageable paging = PageRequest.of(page, 20);
        List<Person> persons = personAuthorityRepository.findPersonsByCitationGroupAndNameLike((ICitationGroup)group.get(), firstName, lastName, paging);
        Set<IAuthorityEntry> entries = new HashSet<>();
        persons.forEach(p -> {
            Optional<AuthorityEntry> optional = entryRepository.findById(p.getLocalAuthorityId());
            if (optional.isPresent()) {
                entries.add(optional.get());
            }
        });
        return entries;
    }
    
    @Override
    public boolean deleteAuthority(String id) {
        entryRepository.deleteById(id);
        return true;
    }
    
    @Override
    public List<IAuthorityEntry> getAll(IUser user) {
        return entryRepository.findByUsernameOrderByName(user.getUsername());
    }
    
    @Override
    public IAuthorityEntry create(IAuthorityEntry entry, IUser user) {
        entry.setUsername(user.getUsername());
        entry.setCreatedOn(OffsetDateTime.now());
        return save(entry);
    }
    
    @Override
    public IAuthorityEntry save(IAuthorityEntry entry) {
        return (IAuthorityEntry) entryRepository.save((AuthorityEntry)entry);
    }
    

}
