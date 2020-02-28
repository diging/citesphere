package edu.asu.diging.citesphere.web.user.authorities.helper;

import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.web.user.FoundAuthorities;

@Service
public class AuthorityEntryControllerHelper {
	
    @Value("${_conceptpower_authority_search_keyword}")
    private String conceptpowerSearckKeyword;
    
    public String[] getAuthoritySearchStrings(String firstName, String lastName) {
	  	
		String conceptpowerSearchString = "";
		String databaseSearchString = "";
		
    	if(firstName!=null && lastName!=null) {
    		conceptpowerSearchString = conceptpowerSearckKeyword+"%25"+firstName+"%25"+lastName;
    		databaseSearchString = "%"+lastName+"%"+firstName+"%";
    	}
    	else if(firstName==null) {
    		conceptpowerSearchString = conceptpowerSearckKeyword+"%25"+lastName;
    		databaseSearchString = "%"+lastName+"%";
    	}
    	else {
    		conceptpowerSearchString =  conceptpowerSearckKeyword+"%25"+firstName;
    		databaseSearchString = "%"+firstName+"%";
    	}
    	
    	return new String[] {conceptpowerSearchString, databaseSearchString};
    }

    public FoundAuthorities removeDuplicateAuthorities(FoundAuthorities foundAuthorities){
    	
    	ListIterator<IAuthorityEntry> iter = foundAuthorities.getImportedAuthorityEntries().listIterator();
    	
    	while(iter.hasNext()) {
    		if (foundAuthorities.getUserAuthorityEntries().contains(iter.next())) {
    			iter.remove();;
    		}
    	}
    	
      
        return foundAuthorities;
    }
}
