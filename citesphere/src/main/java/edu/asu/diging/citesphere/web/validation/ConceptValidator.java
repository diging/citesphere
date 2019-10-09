package edu.asu.diging.citesphere.web.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

public class ConceptValidator implements Validator {

    @Override
    public boolean supports(Class<?> paramClass) {
        return CitationConceptForm.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "uri", "uri.required");
    }

    
}
