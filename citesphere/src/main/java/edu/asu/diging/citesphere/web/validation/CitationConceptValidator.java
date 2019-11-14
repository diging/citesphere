package edu.asu.diging.citesphere.web.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

public class CitationConceptValidator implements Validator {

    @Autowired
    private ICitationConceptManager conceptManager;

    @Override
    public boolean supports(Class<?> paramClass) {
        return CitationConceptForm.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CitationConceptForm conceptForm = (CitationConceptForm) target;
        IUser user = (IUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!errors.hasErrors()) {
            if (conceptForm.getConceptId() == null) {
                if (conceptManager.get(conceptForm.getConceptId()) != null
                        && conceptManager.get(conceptForm.getConceptId()).getOwner() != null
                        && !(conceptManager.get(conceptForm.getConceptId()).getOwner().getUsername()
                                .equals(user.getUsername()))) {
                    errors.rejectValue("uri", "uri", "Only the owner can edit a Concept.");
                } else if (!conceptManager.get(conceptForm.getConceptId()).getUri()
                                .equals(conceptForm.getUri())
                        && conceptManager.getByUriAndOwner(conceptForm.getUri(), user) != null) {
                    errors.rejectValue("uri", "uri", "Concept with this uri already exists!");
                }
            } else if (conceptForm.getConceptId() != null && conceptManager.getByUriAndOwner(conceptForm.getUri(), user) != null) {
                errors.rejectValue("uri", "uri", "Concept with this uri already exists!");
            }
        }
    }

}
