package edu.asu.diging.citesphere.web.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;

public class ConceptTypeValidator implements Validator {
    @Autowired
    private IConceptTypeManager conceptTypeManager;
    
    @Override
    public boolean supports(Class<?> paramClass) {
        return ConceptTypeForm.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ConceptTypeForm conceptTypeForm = (ConceptTypeForm) target;
        IUser user = (IUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!errors.hasErrors() && conceptTypeForm.getAccessMode().equals("edit") &&
                conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user) == null) {
            errors.rejectValue("uri", "uri", "Only the owner can edit a Concept Type.");
        } else if(conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user) != null) {
            errors.rejectValue("uri", "uri", "Concept Type with this uri already exists!");
        }
    }
}
