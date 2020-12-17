package edu.asu.diging.citesphere.core.service.impl;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.authority.IImportedAuthority;
import edu.asu.diging.citesphere.core.authority.impl.ImportedAuthority;
import edu.asu.diging.citesphere.core.exceptions.AuthorityImporterNotFoundException;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.repository.custom.AuthorityRepository;
import edu.asu.diging.citesphere.core.repository.custom.PersonAuthorityRepository;
import edu.asu.diging.citesphere.data.AuthorityEntryRepository;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.Person;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;
import edu.asu.diging.citesphere.web.user.AuthoritySearchResult;

public class AuthorityServiceTest {

    @Mock
    private AuthorityEntryRepository entryRepository;

    @Mock
    private CitationGroupRepository groupRepository;

    @Mock
    private PersonAuthorityRepository personAuthorityRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private AuthorityImporter testImporter;

    @Mock
    private Set<AuthorityImporter> importers;

    @InjectMocks
    private AuthorityService managerToTest;

    private IUser user;

    private CitationGroup group;

    private IAuthorityEntry entry1;
    private IAuthorityEntry entry2;
    private IAuthorityEntry entry3;
    private IAuthorityEntry entry4;
    private int page;
    private int pageSize;
    private Pageable paging;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        Iterator<AuthorityImporter> importerIterator = Mockito.mock(Iterator.class);
        Mockito.when(importerIterator.hasNext()).thenReturn(true, false);
        Mockito.when(importerIterator.next()).thenReturn(testImporter);
        Mockito.when(importers.iterator()).thenReturn(importerIterator);

        initAuthorityEntries();
        page = 1;
        pageSize = 10;
        paging = PageRequest.of(page, pageSize);

    }

    private void initAuthorityEntries() {
        String username = "user";
        user = new User();
        user.setUsername(username);
        group = new CitationGroup();
        group.setGroupId(5);

        entry1 = new AuthorityEntry();
        entry1.setUri("http://test1.uri/");
        entry1.setName("Albert Einstein");
        entry1.setId("1");

        entry2 = new AuthorityEntry();
        entry2.setUri("http://test2.uri/");
        entry2.setName("Albert Sam");
        entry2.setId("2");
        entry2.setUsername("chandana");

        entry3 = new AuthorityEntry();
        entry3.setUri("http://test1.uri/");
        entry3.setName("Albert Einstein 2");
        entry3.setId("3");
        entry2.setUsername("chandana");

        entry4 = new AuthorityEntry();
        entry4.setUri("http://test4.uri/");
        entry4.setName("Rose Jane");
        entry4.setId("4");
        entry2.setUsername("chandana");

        Optional<ICitationGroup> group_op = Optional.of(group);
        Mockito.when(groupRepository.findFirstByGroupId(new Long(5))).thenReturn(group_op);
    }

    @Test
    public void test_importAuthority_success() throws URISyntaxException, AuthorityServiceConnectionException {
        String testUri = "http://test.uri/";
        String importerId = "testId";
        Mockito.when(testImporter.isResponsible(testUri)).thenReturn(true);
        Mockito.when(testImporter.getId()).thenReturn(importerId);

        String name = "Name";

        IImportedAuthority authorityData = new ImportedAuthority();
        authorityData.setName(name);
        authorityData.setUri(testUri);
        Mockito.when(testImporter.retrieveAuthorityData(testUri)).thenReturn(authorityData);

        IAuthorityEntry actualEntry = managerToTest.importAuthority(testUri);
        Assert.assertEquals(name, actualEntry.getName());
        Assert.assertEquals(testUri, actualEntry.getUri());
        Assert.assertEquals(importerId, actualEntry.getImporterId());
    }

    @Test
    public void test_importAuthority_failure() throws URISyntaxException, AuthorityServiceConnectionException {
        String testUri = "http://test.uri/";
        Mockito.when(testImporter.isResponsible(testUri)).thenReturn(false);

        IAuthorityEntry actualEntry = managerToTest.importAuthority(testUri);
        Assert.assertNull(actualEntry);
    }

    @Test
    public void test_find_success() {
        String id = "ID";
        String name = "name";
        AuthorityEntry entry = new AuthorityEntry();
        entry.setId(id);
        entry.setName(name);
        Mockito.when(entryRepository.findById(id)).thenReturn(Optional.of(entry));

        IAuthorityEntry actualEntry = managerToTest.find(id);
        Assert.assertEquals(id, actualEntry.getId());
        Assert.assertEquals(name, actualEntry.getName());
    }

    @Test
    public void test_find_failure() {
        String id = "ID";
        Mockito.when(entryRepository.findById(id)).thenReturn(Optional.empty());
        IAuthorityEntry actualEntry = managerToTest.find(id);
        Assert.assertNull(actualEntry);
    }

    @Test
    public void test_findByUri_uriExists() {
        String username = "user";
        IUser user = new User();
        user.setUsername(username);

        String uri = "http://test.uri/";
        String uriNoSlash = "http://test.uri";

        IAuthorityEntry entry = new AuthorityEntry();
        entry.setUri(uri);
        List<IAuthorityEntry> entries = new ArrayList<>();
        entries.add(entry);

        Mockito.when(entryRepository.findByUsernameAndUriOrderByName(username, uri)).thenReturn(entries);
        Mockito.when(entryRepository.findByUsernameAndUriOrderByName(username, uriNoSlash))
                .thenReturn(new ArrayList<>());

        List<IAuthorityEntry> actual = managerToTest.findByUri(user, uri);
        Assert.assertEquals(entries, actual);
    }

    @Test
    public void test_findByUri_uriExistsNoSlash() {
        String username = "user";
        IUser user = new User();
        user.setUsername(username);

        String uri = "http://test.uri/";
        String uriNoSlash = "http://test.uri";

        IAuthorityEntry entry = new AuthorityEntry();
        entry.setUri(uriNoSlash);
        List<IAuthorityEntry> entries = new ArrayList<>();
        entries.add(entry);

        Mockito.when(entryRepository.findByUsernameAndUriOrderByName(username, uriNoSlash)).thenReturn(entries);
        Mockito.when(entryRepository.findByUsernameAndUriOrderByName(username, uri)).thenReturn(new ArrayList<>());

        List<IAuthorityEntry> actual = managerToTest.findByUri(user, uri);
        Assert.assertEquals(entries, actual);
    }

    @Test
    public void test_findByUri_uriExistsSlashAndNoSlash() {
        String username = "user";
        IUser user = new User();
        user.setUsername(username);

        String uri = "http://test.uri/";
        String uriNoSlash = "http://test.uri";

        IAuthorityEntry entry = new AuthorityEntry();
        entry.setUri(uri);
        List<IAuthorityEntry> entries = new ArrayList<>();
        entries.add(entry);

        IAuthorityEntry entryNoSlash = new AuthorityEntry();
        entryNoSlash.setUri(uriNoSlash);
        List<IAuthorityEntry> entriesNoSlash = new ArrayList<>();
        entriesNoSlash.add(entryNoSlash);

        Mockito.when(entryRepository.findByUsernameAndUriOrderByName(username, uri)).thenReturn(entries);
        Mockito.when(entryRepository.findByUsernameAndUriOrderByName(username, uriNoSlash)).thenReturn(entriesNoSlash);

        List<IAuthorityEntry> actual = managerToTest.findByUri(user, uri);
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(actual.containsAll(entries));
        Assert.assertTrue(actual.containsAll(entriesNoSlash));
    }

    @Test
    public void test_findByUri_noResults() {
        String username = "user";
        IUser user = new User();
        user.setUsername(username);

        String uri = "http://test.uri/";
        String uriNoSlash = "http://test.uri";

        Mockito.when(entryRepository.findByUsernameAndUriOrderByName(username, uriNoSlash))
                .thenReturn(new ArrayList<>());
        Mockito.when(entryRepository.findByUsernameAndUriOrderByName(username, uri)).thenReturn(new ArrayList<>());

        List<IAuthorityEntry> actual = managerToTest.findByUri(user, uri);
        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void test_create() throws InterruptedException {
        String username = "user";
        String id = "entry1";
        OffsetDateTime timeBfr = OffsetDateTime.now();

        AuthorityEntry entry = new AuthorityEntry();

        IUser user = new User();
        user.setUsername(username);

        Answer<AuthorityEntry> answer = new Answer<AuthorityEntry>() {
            public AuthorityEntry answer(InvocationOnMock invocation) {
                entry.setId(id);
                return entry;
            }
        };

        Thread.sleep(100);

        Mockito.when(entryRepository.save(entry)).thenAnswer(answer);

        IAuthorityEntry actualEntry = managerToTest.create(entry, user);
        Assert.assertEquals(username, actualEntry.getUsername());
        Assert.assertTrue(timeBfr.isBefore(actualEntry.getCreatedOn()));
        Assert.assertEquals(id, actualEntry.getId());
    }

    @Test
    public void test_save() {
        String id = "id";
        String name = "name";
        String uri = "uri";
        AuthorityEntry entry = new AuthorityEntry();
        entry.setId(id);
        entry.setName(name);
        entry.setUri(uri);
        Mockito.when(entryRepository.save(entry)).thenReturn(entry);

        IAuthorityEntry actualEntry = managerToTest.save(entry);
        Mockito.verify(entryRepository).save(entry);
        Assert.assertEquals(entry.getId(), actualEntry.getId());
        Assert.assertEquals(entry.getName(), actualEntry.getName());
        Assert.assertEquals(entry.getUri(), actualEntry.getUri());
    }

    @Test
    public void test_findByName_onlyOnFirstName() {

        List<IAuthorityEntry> entriesAlbert = new ArrayList<>();

        entriesAlbert.add(entry1);
        entriesAlbert.add(entry2);
        entriesAlbert.add(entry3);

        Mockito.when(authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(),
                "", "Albert", paging)).thenReturn(entriesAlbert);

        // Testing authority search with just first name
        List<IAuthorityEntry> searchFirstnameAlbert = managerToTest.findByName(user, "", "Albert", page, pageSize);

        Assert.assertEquals(3, searchFirstnameAlbert.size());
        for (IAuthorityEntry entry : searchFirstnameAlbert) {
            Assert.assertTrue(entry.getName().contains("Albert"));
        }
    }

    @Test
    public void test_findByName_onlyOnLastName() {

        List<IAuthorityEntry> entriesAlbert = new ArrayList<>();

        entriesAlbert.add(entry1);
        entriesAlbert.add(entry3);

        // Testing authority search with just last name
        Mockito.when(authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(),
                "", "Einstein", paging)).thenReturn(entriesAlbert);

        List<IAuthorityEntry> searchLastnameEinstein = managerToTest.findByName(user, "", "Einstein", page, pageSize);

        Assert.assertEquals(2, searchLastnameEinstein.size());

        for (IAuthorityEntry entry : searchLastnameEinstein) {
            Assert.assertTrue(entry.getName().contains("Einstein"));
        }

    }

    @Test
    public void test_findByName_withFullName() {

        // Testing authority search with full name 'Albert Einstein'
        List<IAuthorityEntry> entriesAlbert = new ArrayList<>();

        entriesAlbert.add(entry1);
        entriesAlbert.add(entry3);
        Mockito.when(authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(),
                "Albert", "Einstein", paging)).thenReturn(entriesAlbert);

        List<IAuthorityEntry> searchFullName = managerToTest.findByName(user, "Albert", "Einstein", page, pageSize);

        Assert.assertEquals(2, searchFullName.size());

        for (IAuthorityEntry entry : searchFullName) {
            Assert.assertTrue(entry.getName().contains("Einstein") && entry.getName().contains("Albert"));
        }

    }

    @Test
    public void test_findByName_withInterchangedFirstNameAndLastName() {

        List<IAuthorityEntry> entriesAlbert = new ArrayList<>();

        entriesAlbert.add(entry1);
        entriesAlbert.add(entry3);

        Mockito.when(authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(),
                "Einstein", "Albert", paging)).thenReturn(entriesAlbert);

        // Testing authority search by full name with first and last name interchanged
        List<IAuthorityEntry> searchFullNameReverseOrder = managerToTest.findByName(user, "Einstein", "Albert", page,
                pageSize);

        Assert.assertEquals(2, searchFullNameReverseOrder.size());

        for (IAuthorityEntry entry : searchFullNameReverseOrder) {
            Assert.assertTrue(entry.getName().contains("Einstein") && entry.getName().contains("Albert"));
        }

    }

    @Test
    public void test_findByName_resultsNotFound() {

        Mockito.when(authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(),
                "Peter", "Doe", paging)).thenReturn(new ArrayList<>());

        // Testing authority search by full name, name not found
        List<IAuthorityEntry> actual3 = managerToTest.findByName(user, "Peter", "Doe", page, pageSize);

        Assert.assertTrue(actual3.isEmpty());
    }

    @Test
    public void test_getTotalUserAuthoritiesPages_noResults() {

        // testing total number of user authority pages when search authority has first
        // name as "Peter", last name as "Doe" and pageSize is 10
        Mockito.when(authorityRepository
                .countByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(), "Peter", "Doe"))
                .thenReturn((long) 0);
        Assert.assertEquals(0, managerToTest.getTotalUserAuthoritiesPages(user, "Peter", "Doe", pageSize));
    }

    @Test
    public void test_getTotalUserAuthoritiesPages_oneResult() {

        // testing total number of user authority pages when search authority has last
        // name as "Albert" and pageSize is 10
        Mockito.when(authorityRepository
                .countByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(), "", "Albert"))
                .thenReturn((long) 3);
        Assert.assertEquals(1, managerToTest.getTotalUserAuthoritiesPages(user, "", "Albert", pageSize));
    }

    @Test
    public void test_getTotalUserAuthoritiesPages_severalResults() {

        // testing total number of user authority pages when search authority has last
        // name as "Albert" and pageSize is 2
        pageSize = 2;
        Mockito.when(authorityRepository
                .countByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(), "", "Albert"))
                .thenReturn((long) 3);
        Assert.assertEquals(2, managerToTest.getTotalUserAuthoritiesPages(user, "", "Albert", pageSize));
    }

    @Test
    public void test_findByNameInDataset_withUris() throws GroupDoesNotExistException {

        List<String> uriList = new ArrayList<String>();
        uriList.add("http://test1.uri/");
        uriList.add("http://test2.uri/");
        uriList.add("http://test1.uri/");

        List<IAuthorityEntry> entriesAlbert = new ArrayList<>();

        entriesAlbert.add(entry1);
        entriesAlbert.add(entry2);
        entriesAlbert.add(entry3);

        // user has imported authorities with first 'Albert' and uri 'http://test1.uri/'
        Mockito.when(authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(),
                "Albert", "")).thenReturn(entriesAlbert);

        Person p = new Person();
        p.setUri("http://test22.uri/");
        p.setLocalAuthorityId("6");
        List<Person> personList = new ArrayList<Person>();
        personList.add(p);

        AuthorityEntry albertEntry = new AuthorityEntry();
        albertEntry.setUri(p.getUri());

        Optional<AuthorityEntry> entry_op = Optional.of((AuthorityEntry) albertEntry);
        Mockito.when(entryRepository.findById(p.getLocalAuthorityId())).thenReturn(entry_op);

        // returning authorities imported by other users for first name 'Albert'
        Mockito.when(personAuthorityRepository.findPersonsByCitationGroupAndNameLikeAndUriNotIn(group, "Albert", "",
                uriList, paging)).thenReturn(personList);

        Set<IAuthorityEntry> searchResult = managerToTest.findByNameInDataset(user, "Albert", "", "5", page, pageSize);

        Assert.assertEquals(1, searchResult.size());

        // assert search results does not contain authority imported by user
        for (IAuthorityEntry entry : searchResult) {
            Assert.assertTrue(!uriList.contains(entry.getUri()));
        }
    }

    @Test
    public void test_findByNameInDataset_withoutUris() throws GroupDoesNotExistException {

        List<IAuthorityEntry> entriesAlbert = new ArrayList<>();

        entriesAlbert.add(entry1);
        entriesAlbert.add(entry2);
        entriesAlbert.add(entry3);

        // user has not imported any authority with name 'Albert' and uri
        // 'http://test1.uri/'
        Mockito.when(authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(),
                "Albert", "")).thenReturn(new ArrayList<IAuthorityEntry>());

        Person p = new Person();
        p.setUri("http://test1.uri/");
        p.setFirstName("Einstein");
        p.setLastName("Albert");
        p.setLocalAuthorityId("6");
        List<Person> personList = new ArrayList<Person>();
        personList.add(p);

        Optional<AuthorityEntry> entry_op = Optional.of((AuthorityEntry) entriesAlbert.get(0));
        Mockito.when(entryRepository.findById(p.getLocalAuthorityId())).thenReturn(entry_op);

        Mockito.when(personAuthorityRepository.findPersonsByCitationGroupAndNameLike(group, "Albert", "", paging))
                .thenReturn(personList);

        // search for authority by name 'Albert'
        Set<IAuthorityEntry> searchResult = managerToTest.findByNameInDataset(user, "Albert", "", "5", page, pageSize);

        Assert.assertEquals(1, searchResult.size());

        // assert search result contains authority with uri 'http://test1.uri/'
        Assert.assertFalse(searchResult.isEmpty());
        for (IAuthorityEntry entry : searchResult) {
            Assert.assertEquals("http://test1.uri/", entry.getUri());
            Assert.assertTrue(entry.getName().contains("Einstein"));
            Assert.assertTrue(entry.getName().contains("Albert"));
        }

    }

    @Test
    public void test_getTotalDatasetAuthoritiesPages_noResults() {

        Mockito.when(personAuthorityRepository.countByPersonsByCitationGroupAndNameLike(group, "Peter", "Doe"))
                .thenReturn((long) 0);

        Assert.assertEquals(0, managerToTest.getTotalDatasetAuthoritiesPages("5", "Peter", "Doe", pageSize));
    }

    @Test
    public void test_getTotalDatasetAuthoritiesPages_oneResult() {

        Mockito.when(personAuthorityRepository.countByPersonsByCitationGroupAndNameLike(group, "Jane", "Rose"))
                .thenReturn((long) 1);

        Assert.assertEquals(1, managerToTest.getTotalDatasetAuthoritiesPages("5", "Jane", "Rose", pageSize));
    }

    @Test
    public void test_getTotalDatasetAuthoritiesPages_severalResults() {

        Mockito.when(personAuthorityRepository.countByPersonsByCitationGroupAndNameLike(group, "", "Albert"))
                .thenReturn((long) 21);

        pageSize = 20;

        Assert.assertEquals(2, managerToTest.getTotalDatasetAuthoritiesPages("5", "", "Albert", pageSize));
    }

    @Test
    public void test_searchAuthorityEntries_withoutUserImportedAuthority()
            throws URISyntaxException, AuthorityServiceConnectionException, AuthorityImporterNotFoundException {

        // user has not imported authority with uri 'http://test1.uri/'
        Mockito.when(authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(),
                "", "Albert", paging)).thenReturn(new ArrayList<IAuthorityEntry>());

        String source = "conceptpower";
        IAuthorityEntry authorityData = new AuthorityEntry();
        authorityData.setName("Albert");
        authorityData.setUri("http://test1.uri/");

        List<IAuthorityEntry> foundAuthorities = new ArrayList<IAuthorityEntry>();
        foundAuthorities.add(authorityData);

        AuthoritySearchResult searchResult = new AuthoritySearchResult();
        searchResult.setTotalPages(1);
        searchResult.setFoundAuthorities(foundAuthorities);

        Set<AuthorityImporter> importers = new HashSet<AuthorityImporter>();
        importers.add(testImporter);

        managerToTest.register(testImporter);
        Mockito.when(testImporter.isResponsibleForSearch(source)).thenReturn(true);

        Mockito.when(testImporter.searchAuthorities("", "Albert", page, pageSize)).thenReturn(searchResult);

        AuthoritySearchResult result = managerToTest.searchAuthorityEntries(user, "", "Albert", source, page, pageSize);

        Assert.assertNotNull(result);

        Assert.assertEquals(1, result.getFoundAuthorities().size());

        // assert search result contains authority with uri 'http://test1.uri/'
        Assert.assertEquals("http://test1.uri/", result.getFoundAuthorities().get(0).getUri());
    }

    @Test
    public void test_searchAuthorityEntries_withUserImportedAuthority()
            throws URISyntaxException, AuthorityServiceConnectionException, AuthorityImporterNotFoundException {

        List<IAuthorityEntry> entriesAlbert = new ArrayList<>();

        entriesAlbert.add(entry1);
        entriesAlbert.add(entry2);
        entriesAlbert.add(entry3);

        // user has imported authority with uri 'http://test1.uri/'
        Mockito.when(authorityRepository.findByUsernameAndNameContainingAndNameContainingOrderByName(user.getUsername(),
                "", "Albert", paging)).thenReturn(entriesAlbert);

        String source = "conceptpower";
        IAuthorityEntry authorityData = new AuthorityEntry();
        authorityData.setName("Albert");
        authorityData.setUri("http://test198.uri/");

        List<IAuthorityEntry> foundAuthorities = new ArrayList<IAuthorityEntry>();
        foundAuthorities.add(authorityData);

        AuthoritySearchResult searchResult = new AuthoritySearchResult();
        searchResult.setTotalPages(1);
        searchResult.setFoundAuthorities(foundAuthorities);

        Set<AuthorityImporter> importers = new HashSet<AuthorityImporter>();
        importers.add(testImporter);

        managerToTest.register(testImporter);
        Mockito.when(testImporter.isResponsibleForSearch(source)).thenReturn(true);

        Mockito.when(testImporter.searchAuthorities("", "Albert", page, pageSize)).thenReturn(searchResult);

        // call searchAuthorityEntries
        AuthoritySearchResult result = managerToTest.searchAuthorityEntries(user, "", "Albert", source, page, pageSize);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getFoundAuthorities().size());

        // assert search result does not contain authority with uri 'http://test1.uri/'
        Assert.assertNotEquals("http://test1.uri/", result.getFoundAuthorities().get(0).getUri());
        Assert.assertEquals("http://test198.uri/", result.getFoundAuthorities().get(0).getUri());
    }

}
