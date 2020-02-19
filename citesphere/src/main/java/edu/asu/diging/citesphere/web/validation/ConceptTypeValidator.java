package edu.asu.diging.citesphere.web.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.IConceptType;
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
			
			
			  if (conceptTypeManager.getByUri(conceptTypeForm.getUri()) != null) {
				  errors.rejectValue("uri", "uri", "Concept type with this uri already exists!");
				  } else {
					  
					  if(conceptTypeForm.getConceptTypeId() != null) {
						  IConceptType prevConcept = conceptTypeManager.get(conceptTypeForm.getConceptTypeId());  

							IConceptType conceptType = conceptTypeManager.getByUriAndOwner(prevConcept.getUri(), user);
							
							  if(conceptType == null) {
								  errors.rejectValue("uri", "uri", "Only the owner can edit a Concept."); }
					  }
				
					
					  
				  }
		
				
				
			}
			
		
			
			
			 
			 
		}
	}

