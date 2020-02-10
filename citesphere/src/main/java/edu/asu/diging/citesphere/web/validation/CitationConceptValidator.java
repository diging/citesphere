package edu.asu.diging.citesphere.web.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.web.forms.CitationConceptForm;

@Component
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
        if (!errors.hasErrors() && conceptForm.getUri() != null) {
            ICitationConcept concept = conceptManager.getByUriAndOwner(conceptForm.getUri(), user);
            if (conceptForm.getConceptId() != null && concept != null) {
              //edit mode
                if (conceptManager.get(conceptForm.getConceptId()) != null
                        && conceptManager.get(conceptForm.getConceptId()).getOwner() != null
                        && concept.getId().equals(conceptForm.getConceptId())) {
                    errors.rejectValue("uri", "uri", "Only the owner can edit a Concept.");
                } else if (!conceptManager.get(conceptForm.getConceptId()).getUri()
                                .equals(conceptForm.getUri())) {
                    errors.rejectValue("uri", "uri", "Concept with this uri already exists!");
                }
            } else if(concept != null) { 
                // add mode
                errors.rejectValue("uri", "uri", "Concept with this uri already exists!");
            }
        }
    }

}
