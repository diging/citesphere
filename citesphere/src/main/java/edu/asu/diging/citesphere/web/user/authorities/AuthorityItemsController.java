package edu.asu.diging.citesphere.web.user.authorities;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.IAuthorityService;
import edu.asu.diging.citesphere.data.bib.ICitationDao;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.transfer.impl.Citations;

@Controller
public class AuthorityItemsController {

    @Autowired
    private IAuthorityService authorityService;

   
    @Autowired
    private ICitationDao iCitationDao;

    @RequestMapping("/auth/authority/{authorityId}/items")
    public String showPage(Model model, @PathVariable("authorityId") String authorityId, Authentication authentication)
    		throws GroupDoesNotExistException, ZoteroHttpStatusException {
        IAuthorityEntry entry = authorityService.find(authorityId);
        iCitationDao.getCitationIterator("authorityId", authorityId);
	Citations citations = iCitationDao.findCitatationByName(entry.getName());
	if (citations != null) {
		model.addAttribute("items", citations.getCitations());
	} else {
		model.addAttribute("items", new ArrayList<ICitation>());
	}
	return "auth/authorities/showItemsByName";
    }
}
