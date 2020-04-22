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
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.user.AuthoritySearchResult;
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
     */
    @Override
    public AuthoritySearchResult searchAuthorityEntries(IUser user, String firstName, String lastName, String source,
            int page, int pageSize) throws URISyntaxException, AuthorityServiceConnectionException {

        AuthoritySearchResult searchResult = getAuthorityImporter(source).searchAuthorities(firstName, lastName, page,
                pageSize);

        if (searchResult.getFoundAuthorities() != null && searchResult.getFoundAuthorities().size() > 0) {
            List<String> uriList = authorityRepository
                    .findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(), firstName,
                            lastName)
                    .stream().map(IAuthorityEntry::getUri).collect(Collectors.toList());

            ListIterator<IAuthorityEntry> iter = searchResult.getFoundAuthorities().listIterator();
            String uri = "";
            while (iter.hasNext()) {
                uri = iter.next().getUri();
                if (!uri.trim().endsWith("/")) {
                    uri = uri + "/";
                }
                if (uriList.contains(uri)) {
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

    /**
     * This method returns all the Authorities that are imported by the user based
     * on first name and/or last name
     */
    @Override
    public List<IAuthorityEntry> findByName(IUser user, String firstName, String lastName, int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);
        List<IAuthorityEntry> results = authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(
                user.getUsername(), firstName, lastName, paging);
        return results;
    }

    @Override
    public Set<IAuthorityEntry> findByUriInDataset(String uri, String citationGroupId)
            throws GroupDoesNotExistException {
        Optional<CitationGroup> group = groupRepository.findById(new Long(citationGroupId));
        if (!group.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + citationGroupId + " does not exist.");
        }
        List<Person> persons = personRepository.findPersonsByCitationGroupAndUri((ICitationGroup) group.get(), uri);
        Set<IAuthorityEntry> entries = new HashSet<>();
        persons.forEach(p -> {
            Optional<AuthorityEntry> optional = entryRepository.findById(p.getLocalAuthorityId());
            if (optional.isPresent()) {
                entries.add(optional.get());
            }
        });
        return entries;
    }

    // This method queries database and retrieves authorities based on the first
    // name and/or last name of the authority, and based on the specified citation
    // group ID
    private Set<IAuthorityEntry> findByNameInDataset(String firstName, String lastName, String citationGroupId,
            int page, int pageSize, List<String> uris) throws GroupDoesNotExistException {
        Optional<CitationGroup> group = groupRepository.findById(new Long(citationGroupId));
        if (!group.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + citationGroupId + " does not exist.");
        }
        Pageable paging = PageRequest.of(page, pageSize);
        List<Person> persons = null;
        if (uris != null && uris.size() > 0) {
            persons = personAuthorityRepository.findPersonsByCitationGroupAndNameLikeAndUriNotIn(
                    (ICitationGroup) group.get(), firstName, lastName, uris, paging);
        } else {
            persons = personAuthorityRepository.findPersonsByCitationGroupAndNameLike((ICitationGroup) group.get(),
                    firstName, lastName, paging);
        }
        Set<IAuthorityEntry> entries = new HashSet<>();
        persons.forEach(p -> {
            Optional<AuthorityEntry> optional = entryRepository.findById(p.getLocalAuthorityId());
            if (optional.isPresent()) {
                entries.add(optional.get());
            }
        });
        return entries;
    }

    /**
     * This method returns all the Authorities that are imported by the other users
     * of citesphere and excludes that authorities that are imported by user
     */
    @Override
    public Set<IAuthorityEntry> findByNameInDataset(IUser user, String firstName, String lastName,
            String citationGroupId, int page, int pageSize) throws GroupDoesNotExistException {
        List<String> uriList = authorityRepository
                .findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(), firstName, lastName)
                .stream().map(IAuthorityEntry::getUri).collect(Collectors.toList());

        return this.findByNameInDataset(firstName, lastName, citationGroupId, page, pageSize, uriList);

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
        return (IAuthorityEntry) entryRepository.save((AuthorityEntry) entry);
    }

    @Override
    public int getTotalUserAuthoritiesPages(IUser user, String firstName, String lastName, int pageSize) {

        return (int) Math.ceil(new Float(authorityRepository
                .countByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(), firstName, lastName))
                / pageSize);
    }

    @Override
    public int getTotalDatasetAuthoritiesPages(String citationGroupId, String firstName, String lastName,
            int pageSize) {
        Optional<CitationGroup> group = groupRepository.findById(new Long(citationGroupId));

        return (int) Math.ceil(new Float(personAuthorityRepository
                .countByPersonsByCitationGroupAndNameLike((ICitationGroup) group.get(), firstName, lastName))
                / pageSize);
    }

    // Given a source like viaf or conceptpower, this method returns authority
    // importer responsible for search and import
    private AuthorityImporter getAuthorityImporter(String source) {

        for (AuthorityImporter importer : importers) {
            if (importer.isResponsible(source))
                return importer;
        }

        return null;
    }

}
