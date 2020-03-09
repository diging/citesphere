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
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.IConceptType;
import edu.asu.diging.citesphere.model.bib.impl.ConceptType;
import edu.asu.diging.citesphere.model.impl.User;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;

public class ConceptTypeValidatorTest {
    @InjectMocks
    private ConceptTypeValidator conceptTypeValidator;

    @Mock
    private IConceptTypeManager conceptTypeManager;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private IConceptType conceptType;

    private IUser user;

    private Errors errors;

    private ConceptTypeForm conceptTypeForm;

    private IConceptType dbConceptType;

    private final static String URI_FORM_INPUT = "http://www.google.com";

    private final static String URI_EXISTING = "http://www.google1.com";

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setUsername("akhil");

        initForm();
        initExistingConceptType();
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        errors = new BeanPropertyBindingResult(conceptTypeForm, "validator");

    }

    private void initForm() {
        conceptTypeForm = new ConceptTypeForm();
        conceptTypeForm.setDescription("test dec");
        conceptTypeForm.setName("name");
        conceptTypeForm.setUri(URI_FORM_INPUT);

    }

    private void initExistingConceptType() {
        dbConceptType = new ConceptType();
        dbConceptType.setOwner(user);
        dbConceptType.setId("CTY1");
        dbConceptType.setUri(URI_FORM_INPUT);
    }

    @Test
    public void test_addConceptType_Success() {

        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(null);
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assert (errors.getFieldError("uri") == null);
        assertTrue(!errors.hasErrors());

    }

    @Test
    public void test_addConceptType_Uri_Exists() {

        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(conceptType);
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assert (errors.getFieldError("uri") != null);

    }

    @Test
    public void test_editConceptType_Success() {

        conceptTypeForm.setConceptTypeId("CTY1");
        dbConceptType.setUri(URI_EXISTING);
        Mockito.when(conceptTypeManager.get("CTY1")).thenReturn(dbConceptType);
        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(null);
        errors = new BeanPropertyBindingResult(conceptTypeForm, "validConcept");
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assert (errors.getFieldError("uri") == null);
        assertTrue(!errors.hasErrors());

    }

    @Test
    public void test_editConceptType_Uri_Exists() {

        conceptTypeForm.setConceptTypeId("CTY1");
        dbConceptType.setUri(URI_EXISTING);
        Mockito.when(conceptTypeManager.get("CTY1")).thenReturn(dbConceptType);
        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(conceptType);
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assert (errors.getFieldError("uri") != null);

    }

    @Test
    public void test_editConceptType_Owner_Only() {

        User user1 = new User();
        user1.setUsername("user1");

        conceptTypeForm.setConceptTypeId("CTY1");
        dbConceptType.setOwner(user1);
        Mockito.when(conceptTypeManager.get("CTY1")).thenReturn(dbConceptType);
        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(conceptType);
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assert (errors.getFieldError("uri") != null);

    }

}
