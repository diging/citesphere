package edu.asu.diging.citesphere.web.validation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

    private static final String CONCEPT_TYPE_ID = "CON1";

    private static final String URI_STRING = "uri";

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
        dbConceptType.setId(CONCEPT_TYPE_ID);
        dbConceptType.setUri(URI_FORM_INPUT);
    }

    @Test
    public void test_addConceptType_Success() {
        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(null);
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assertTrue(!errors.hasErrors());
        assertNull(errors.getFieldError(URI_STRING));
    }

    @Test
    public void test_addConceptType_Uri_Exists() {
        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(conceptType);
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assertNotNull(errors.getFieldError(URI_STRING));
    }

    @Test
    public void test_editConceptType_Success() {
        conceptTypeForm.setConceptTypeId(CONCEPT_TYPE_ID);
        dbConceptType.setUri(URI_EXISTING);

        Mockito.when(conceptTypeManager.get(CONCEPT_TYPE_ID)).thenReturn(dbConceptType);
        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(null);
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assertTrue(!errors.hasErrors());
        assertNull(errors.getFieldError(URI_STRING));
    }

    @Test
    public void test_editConceptType_Uri_Exists() {
        conceptTypeForm.setConceptTypeId(CONCEPT_TYPE_ID);
        dbConceptType.setUri(URI_EXISTING);

        Mockito.when(conceptTypeManager.get(CONCEPT_TYPE_ID)).thenReturn(dbConceptType);
        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(conceptType);
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assertNotNull(errors.getFieldError(URI_STRING));
    }

    @Test
    public void test_editConceptType_Owner_Only() {
        User user1 = new User();
        user1.setUsername("user1");

        conceptTypeForm.setConceptTypeId(CONCEPT_TYPE_ID);
        dbConceptType.setOwner(user1);

        Mockito.when(conceptTypeManager.get(CONCEPT_TYPE_ID)).thenReturn(dbConceptType);
        Mockito.when(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user)).thenReturn(conceptType);
        conceptTypeValidator.validate(conceptTypeForm, errors);
        assertNotNull(errors.getFieldError(URI_STRING));
    }
}
