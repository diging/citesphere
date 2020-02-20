package edu.asu.diging.citesphere.web.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.model.bib.IConceptType;
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
		if (!errors.hasErrors()) {
			ICitationConcept concept = conceptManager.getByUriAndOwner(conceptForm.getUri(), user);
			if (concept == null) {
				String conceptId = conceptForm.getConceptId();
				if (conceptId != null
					&& !conceptManager.get(conceptId).getOwner().getUsername().equals(user.getUsername())) {
					errors.rejectValue("uri", "uri", "Only the owner can edit a Concept.");
				}
			} else {
				errors.rejectValue("uri", "uri", "Concept with this uri already exists!");
			}
		}
	}
}
