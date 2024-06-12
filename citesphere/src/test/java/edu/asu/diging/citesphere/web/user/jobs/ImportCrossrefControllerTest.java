package edu.asu.diging.citesphere.web.user.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.crossref.CrossrefService;
import edu.asu.diging.citesphere.core.service.jobs.IImportCrossrefJobManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.Item;

@RunWith(MockitoJUnitRunner.class)
public class ImportCrossrefControllerTest {

    @InjectMocks
    private ImportCrossrefController importCrossrefController;

    @Mock
    private CrossrefService crossrefService;

    @Mock
    private ICitationManager citationManager;

    @Mock
    private IUserManager userManager;

    @Mock
    private IImportCrossrefJobManager jobManager;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttrs;

    @Mock
    private IUser user;

    @Before
    public void setUp() {
        when(principal.getName()).thenReturn("testuser");
        when(userManager.findByUsername("testuser")).thenReturn(user);
    }

    @Test
    public void testGet() {
        List<ICitationGroup> groups = Arrays.asList(mock(ICitationGroup.class), mock(ICitationGroup.class));
        when(citationManager.getGroups(user)).thenReturn(groups);

        String viewName = importCrossrefController.get(model, principal);

        assertEquals("auth/import/crossref", viewName);
        verify(model).addAttribute(eq("groups"), anyList());
    }

    @Test
    public void testSearch_Success() throws RequestFailedException, IOException {
        String query = "sample query";
        int page = 1;
        List<Item> items = List.of(mock(Item.class), mock(Item.class));
        when(crossrefService.search(query, page)).thenReturn(items);

        ResponseEntity<List<Item>> response = importCrossrefController.search(query, page);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(items, response.getBody());
    }

    @Test
    public void testSearch_Failure() throws RequestFailedException, IOException {
        String query = "sample query";
        int page = 1;
        when(crossrefService.search(query, page)).thenThrow(new IOException("IO Exception"));

        ResponseEntity<List<Item>> response = importCrossrefController.search(query, page);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testPost_Success() throws GroupDoesNotExistException {
        String groupId = "groupId";
        List<String> dois = Arrays.asList("doi1", "doi2");
        when(authentication.getPrincipal()).thenReturn(user);

        String viewName = importCrossrefController.post(authentication, groupId, dois, redirectAttrs);

        assertEquals("redirect:/auth/import/crossref", viewName);
        verify(jobManager).createJob(user, groupId, dois);
        verify(redirectAttrs).addFlashAttribute("show_alert", true);
        verify(redirectAttrs).addFlashAttribute("alert_type", "success");
        verify(redirectAttrs).addFlashAttribute("alert_msg", "Import in progress.");
    }

    @Test
    public void testPost_GroupDoesNotExistException() throws GroupDoesNotExistException {
        String groupId = "groupId";
        List<String> dois = Arrays.asList("doi1", "doi2");
        when(authentication.getPrincipal()).thenReturn(user);
        doThrow(new GroupDoesNotExistException("Group does not exist")).when(jobManager).createJob(user, groupId, dois);

        String viewName = importCrossrefController.post(authentication, groupId, dois, redirectAttrs);

        assertEquals("redirect:/auth/import/crossref", viewName);
        verify(jobManager).createJob(user, groupId, dois);
        verify(redirectAttrs).addFlashAttribute("show_alert", true);
        verify(redirectAttrs).addFlashAttribute("alert_type", "danger");
        verify(redirectAttrs).addFlashAttribute("alert_msg", "Group does not exist");
    }
}
