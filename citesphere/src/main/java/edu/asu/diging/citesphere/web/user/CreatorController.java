package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.service.ICitationManager;

@Controller
public class CreatorController {
    
    @Autowired
    private ICitationManager citationManager;
    /**
     * Method to retrieve all creator types filtered by item type.
     */
    @RequestMapping("/auth/items/{itemType}/creators")
    public ResponseEntity<List<String>> getCreatorsByItemType(Authentication authentication, @PathVariable("itemType") ItemType itemType) {
        List<String> creators = new ArrayList<>();
        citationManager.getValidCreatorTypes((IUser) authentication.getPrincipal(), itemType).forEach(f -> creators.add(f));
        return new ResponseEntity<List<String>>(creators, HttpStatus.OK);
    }
}
