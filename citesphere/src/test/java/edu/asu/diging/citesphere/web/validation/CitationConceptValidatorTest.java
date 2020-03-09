package edu.asu.diging.citesphere.web.validation;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.impl.CitationManager;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.model.impl.User;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

public class CitationConceptValidatorTest {

    @InjectMocks
    private CitationConceptValidator citationConceptValidator;

    @Mock
    private ICitationConceptManager conceptManager;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private ICitationConcept concept;

    private IUser user;

    private CitationConceptForm conceptForm;

    private ICitationConcept dbConcept;

    private Errors errors;

    private final static String URI_FORM_INPUT = "http://www.google.com";

    private final static String URI_EXISTING = "http://www.google1.com";

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setUsername("akhil");

        initForm();
        initExistingConcept();
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        errors = new BeanPropertyBindingResult(conceptForm, "validator");

    }

    private void initForm() {
        conceptForm = new CitationConceptForm();
        conceptForm.setDescription("test dec");
        conceptForm.setName("name");
        conceptForm.setUri(URI_FORM_INPUT);

    }

    private void initExistingConcept() {
        dbConcept = new CitationConcept();
        dbConcept.setOwner(user);
        dbConcept.setId("CON1");
        dbConcept.setUri(URI_FORM_INPUT);
    }

    @Test
    public void test_addConcept_Success() {

        // return null for adding concept
        Mockito.when(conceptManager.getByUriAndOwner(conceptForm.getUri(), user)).thenReturn(null);
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") == null);
        assertTrue(!errors.hasErrors());

    }

    @Test
    public void test_addConcept_Uri_Exists() {

        Mockito.when(conceptManager.getByUriAndOwner(conceptForm.getUri(), user)).thenReturn(concept);
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") != null);

    }

    @Test
    public void test_editConcept_Success() {

        conceptForm.setConceptId("CON1");
        dbConcept.setUri(URI_EXISTING);
        Mockito.when(conceptManager.get("CON1")).thenReturn(dbConcept);
        Mockito.when(conceptManager.getByUriAndOwner(conceptForm.getUri(), user)).thenReturn(null);
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") == null);
        assertTrue(!errors.hasErrors());

    }

    @Test
    public void test_editConcept_Uri_Exists() {

        conceptForm.setConceptId("CON1");
        dbConcept.setUri(URI_EXISTING);
        Mockito.when(conceptManager.get("CON1")).thenReturn(dbConcept);
        Mockito.when(conceptManager.getByUriAndOwner(conceptForm.getUri(), user)).thenReturn(concept);
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") != null);

    }

    @Test
    public void test_editConcept_Owner_Only() {

        User user1 = new User();
        user1.setUsername("user1");

        conceptForm.setConceptId("CON1");
        dbConcept.setOwner(user1);
        Mockito.when(conceptManager.get("CON1")).thenReturn(dbConcept);
        Mockito.when(conceptManager.getByUriAndOwner(conceptForm.getUri(), user)).thenReturn(concept);
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") != null);

    }

}
