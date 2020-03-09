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

    private Errors errors;

    private CitationConceptForm conceptForm;

    private ICitationConcept dbConcept;

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

    }

    private void initForm() {
        conceptForm = new CitationConceptForm();
        conceptForm.setDescription("test dec");
        conceptForm.setName("name");
        conceptForm.setUri("http://wwww.google.com");

    }

    private void initExistingConcept() {
        dbConcept = new CitationConcept();
        dbConcept.setOwner(user);
        dbConcept.setId("CON1");
        dbConcept.setUri("http://www.google.com");
    }

    @Test
    public void validateTest_AddConcept_Success() {

        // return null for adding concept
        Mockito.when(conceptManager.getByUriAndOwner("http://wwww.google.com", user)).thenReturn(null);
        errors = new BeanPropertyBindingResult(conceptForm, "invalidConcept");
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") == null);

    }

    @Test
    public void validateTest_AddConcept_Uri_Exists() {

        Mockito.when(conceptManager.getByUriAndOwner("http://wwww.google.com", user)).thenReturn(concept);
        errors = new BeanPropertyBindingResult(conceptForm, "validConcept");
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") != null);

    }

    @Test
    public void validateTest_EditConcept_Success() {

        conceptForm.setConceptId("CON1");
        dbConcept.setUri("http://www.google1.com");
        Mockito.when(conceptManager.get("CON1")).thenReturn(dbConcept);
        Mockito.when(conceptManager.getByUriAndOwner("http://wwww.google.com", user)).thenReturn(null);
        errors = new BeanPropertyBindingResult(conceptForm, "validConcept");
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") == null);

    }

    @Test
    public void validateTest_EditConcept_Uri_Exists() {

        conceptForm.setConceptId("CON1");
        Mockito.when(conceptManager.get("CON1")).thenReturn(dbConcept);
        Mockito.when(conceptManager.getByUriAndOwner("http://wwww.google.com", user)).thenReturn(concept);
        errors = new BeanPropertyBindingResult(conceptForm, "validConcept");
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") != null);

    }

    @Test
    public void validateTest_EditConcept_Owner_Only() {

        User user1 = new User();
        user1.setUsername("user1");

        conceptForm.setConceptId("CON1");
        dbConcept.setOwner(user1);
        Mockito.when(conceptManager.get("CON1")).thenReturn(dbConcept);
        Mockito.when(conceptManager.getByUriAndOwner("http://wwww.google.com", user)).thenReturn(concept);
        errors = new BeanPropertyBindingResult(conceptForm, "validConcept");
        citationConceptValidator.validate(conceptForm, errors);
        assert (errors.getFieldError("uri") != null);

    }

}
