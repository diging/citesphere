package edu.asu.diging.citesphere.core.service.impl;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.authority.IImportedAuthority;
import edu.asu.diging.citesphere.core.authority.impl.ImportedAuthority;
import edu.asu.diging.citesphere.core.exceptions.AuthorityServiceConnectionException;
import edu.asu.diging.citesphere.data.AuthorityEntryRepository;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.data.bib.PersonRepository;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.authority.impl.AuthorityEntry;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class AuthorityServiceTest {

    @Mock
    private AuthorityEntryRepository entryRepository;

    @Mock
    private CitationGroupRepository groupRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private AuthorityImporter testImporter;

    @Mock
    private Set<AuthorityImporter> importers;

    @InjectMocks
    private AuthorityService managerToTest;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        Iterator<AuthorityImporter> importerIterator = Mockito.mock(Iterator.class);
        Mockito.when(importerIterator.hasNext()).thenReturn(true, false);
        Mockito.when(importerIterator.next()).thenReturn(testImporter);
        Mockito.when(importers.iterator()).thenReturn(importerIterator);

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
        String id="entry1";
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
}
