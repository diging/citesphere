package edu.asu.diging.citesphere.web.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;

import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.impl.User;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

public class CitationConceptValidatorTest {
    
    
    @InjectMocks
    private CitationConceptValidator citationConceptValidator;
    
    @Mock
    private ICitationConceptManager conceptManager;
    
    private IUser user;
    private Errors errors;
    
    private CitationConceptForm conceptForm;
    
    
    @Before
    public void setUp() {
        
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setUsername("akhil");
        initForm();
       // errors = new 
        
        
    }
    
    private void initForm() {
        conceptForm = new CitationConceptForm();
        conceptForm.setDescription("test dec");
        conceptForm.setName("name");
        conceptForm.setUri("wwww.google.com"); 
    }

    @Test
    public void validateTest_AddConcept_Success() {
        
        //return null for adding concept
        Mockito.when(conceptManager.getByUriAndOwner("www.google.com", user)).thenReturn(null);
        citationConceptValidator.validate(conceptForm, errors);
        
        
    }
    
    @Test
    public void validateTest_EditConcept() {
        
        
        
        
    }

}
