package edu.asu.diging.citesphere.web.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.model.bib.IConceptType;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.ConceptTypeForm;

@Component
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
        IUser user = (IUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!errors.hasErrors()) {
            IConceptType type = conceptTypeManager.getByUriAndOwner(conceptTypeForm.getUri(), user);

            if (conceptTypeForm.getConceptTypeId() != null) {
                // edit
                IConceptType dbConceptType = conceptTypeManager.get(conceptTypeForm.getConceptTypeId());
                if (!dbConceptType.getOwner().getUsername().equals(user.getUsername())) {
                    errors.rejectValue("uri", "uri", "Only the owner can edit a Concept Type.");
                } else {
                    if (!dbConceptType.getUri().equals(conceptTypeForm.getUri()) && type != null) {
                        errors.rejectValue("uri", "uri", "Concept Type with this uri already exists!");
                    }
                }
            } else {
                // add
                if (type != null) {
                    errors.rejectValue("uri", "uri", "Concept Type with this uri already exists!");
                }
            }
        }
    }
}
