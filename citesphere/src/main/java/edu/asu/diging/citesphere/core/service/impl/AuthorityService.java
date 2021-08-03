package edu.asu.diging.citesphere.core.service.impl;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.authority.IImportedAuthority;
import edu.asu.diging.citesphere.core.exceptions.AuthorityImporterNotFoundException;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.repository.custom.AuthorityRepository;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.data.AuthorityEntryRepository;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.data.bib.IPersonMongoDao;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.transfer.impl.Persons;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.user.AuthoritySearchResult;

@Service
public class AuthorityService implements IAuthorityService {

    @Autowired
    private AuthorityEntryRepository entryRepository;

    @Autowired
    private CitationGroupRepository groupRepository;

    @Autowired
    private IPersonMongoDao personDao;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private Set<AuthorityImporter> importers;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.impl.IAuthorityService#register(edu.
     * asu.diging.citesphere.authority.AuthorityImporter)
     */
    @Override
    public void register(AuthorityImporter importer) {
        this.importers.add(importer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.impl.IAuthorityService#importAuthority
     * (java.lang.String)
     */
    @Override
    public IAuthorityEntry importAuthority(String uri) throws URISyntaxException, AuthorityServiceConnectionException {
        for (AuthorityImporter importer : importers) {
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

    /**
     * This method returns all the authorities found by searching a source based on
     * first name and/or last name of authority and excludes the authorities that
     * are imported by the user in citesphere
     * 
     * @throws AuthorityImporterNotFoundException
     */
    @Override
    public AuthoritySearchResult searchAuthorityEntries(IUser user, String firstName, String lastName, String source,
            int page, int pageSize) throws AuthorityServiceConnectionException, AuthorityImporterNotFoundException {

        AuthorityImporter importer = getAuthorityImporter(source);

        if (importer == null) {
            throw new AuthorityImporterNotFoundException("Authority importer not found for " + source);
        }

        AuthoritySearchResult searchResult = importer.searchAuthorities(firstName, lastName, page, pageSize);

        if (searchResult.getFoundAuthorities() != null && searchResult.getFoundAuthorities().size() > 0) {
            List<String> uriList = authorityRepository
                    .findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(), firstName,
                            lastName)
                    .stream().map(IAuthorityEntry::getUri).collect(Collectors.toList());
  
            ListIterator<IAuthorityEntry> iter = searchResult.getFoundAuthorities().listIterator();
            String uri = "";
            while (iter.hasNext()) {
                uri = iter.next().getUri().trim();
                if (uriList.contains(uri) || uriList.contains(uri + "/")) {
                    iter.remove();
                }
            }
        }

        return searchResult;
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
    public Set<IAuthorityEntry> findByUriInDataset(String uri, String citationGroupId)
            throws GroupDoesNotExistException {
        Optional<ICitationGroup> group = groupRepository.findFirstByGroupId(new Long(citationGroupId));
        if (!group.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + citationGroupId + " does not exist.");
        }
        Persons persons = personDao.findPersonsByGroupAndUri(((ICitationGroup) group.get()).getGroupId() + "", uri);
        Set<IAuthorityEntry> entries = new HashSet<>();
        if (persons != null && persons.getPersons() != null) {
            persons.getPersons().forEach(p -> {
                Optional<AuthorityEntry> optional = entryRepository.findById(p.getLocalAuthorityId());
                if (optional.isPresent()) {
                    entries.add(optional.get());
                }
            });
        }
        return entries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.service.IAuthorityService#
     * findByFirstNameAndLastName(edu.asu.diging.citesphere.user.IUser,
     * java.lang.String, java.lang.String, int, int)
     */
    @Override
    public Page<IAuthorityEntry> findByFirstNameAndLastName(IUser user, String firstName, String lastName, int page,
            int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);
        return authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(
                user.getUsername(), firstName, lastName, paging);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.service.IAuthorityService#
     * findByLastNameAndExcludingFirstName(edu.asu.diging.citesphere.user.IUser,
     * java.lang.String, java.lang.String, int, int)
     */
    @Override
    public Page<IAuthorityEntry> findByLastNameAndExcludingFirstName(IUser user, String firstName, String lastName,
            int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);
        return authorityRepository
                .findByUsernameAndNameNotContainingAndNameContainingOrderByName(user.getUsername(), firstName, lastName,
                        paging);
    }
    
    @Override
    public int getTotalUserAuthoritiesPages(IUser user, String firstName, String lastName, int pageSize) {
        long total;
        // If the last name is not empty then total authority count would be same as the
        // authorities whose name contains the given last name
        if (!lastName.trim().isEmpty()) {
            total = authorityRepository.countByUsernameAndNameContaining(user.getUsername(), lastName);
        } else {
            total = authorityRepository.countByUsernameAndNameContaining(user.getUsername(), firstName);
        }
        return (int) Math.ceil(new Float(total) / pageSize);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.service.IAuthorityService#
     * findByGroupAndFirstNameAndLastName(java.lang.Long, java.lang.String,
     * java.lang.String, int, int)
     */
    @Override
    public Page<IAuthorityEntry> findByGroupAndFirstNameAndLastName(Long groupId, String firstName, String lastName,
            int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);
        return authorityRepository.findByGroupsContainingAndNameContainingAndNameContainingOrderByName(groupId,
                firstName, lastName, paging);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.service.IAuthorityService#
     * findByGroupAndLastNameAndExcludingFirstName(java.lang.Long, java.lang.String,
     * java.lang.String, int, int)
     */
    @Override
    public Page<IAuthorityEntry> findByGroupAndLastNameAndExcludingFirstName(Long groupId, String firstName,
            String lastName, int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);
        return authorityRepository.findByGroupsContainingAndNameNotContainingAndNameContainingOrderByName(groupId,
                firstName, lastName, paging);
    }
    
    @Override
    public int getTotalGroupAuthoritiesPages(Long groupId, String firstName, String lastName, int pageSize) {
        long total;
        // If the last name is not empty then total authority count would be same as the
        // users whose name contains the given last name
        if (!lastName.trim().isEmpty()) {
            total = authorityRepository.countByGroupsContainingAndNameContaining(groupId, lastName);
        } else {
            total = authorityRepository.countByGroupsContainingAndNameContaining(groupId, firstName);
        }
        return (int) Math.ceil(new Float(total) / pageSize);
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
    public List<IAuthorityEntry> getAuthoritiesByGroup(long groupId) {
        return entryRepository.findByGroupsOrderByName(groupId);
    }
    
    @Override
    public List<IAuthorityEntry> getUserSpecificAuthorities(IUser user) {
        return entryRepository.findByUsernameAndGroupsOrderByName(user.getUsername(), null);
    }

    @Override
    public IAuthorityEntry create(IAuthorityEntry entry, IUser user) {
        entry.setUsername(user.getUsername());
        entry.setCreatedOn(OffsetDateTime.now());
        return save(entry);
    }

    @Override
    public IAuthorityEntry save(IAuthorityEntry entry) {
        return (IAuthorityEntry) entryRepository.save((AuthorityEntry) entry);
    }

    private AuthorityImporter getAuthorityImporter(String source) {

        for (AuthorityImporter importer : importers) {
            if (importer.isResponsibleForSearch(source)) {
                return importer;
            }
        }
        return null;
    }

}
