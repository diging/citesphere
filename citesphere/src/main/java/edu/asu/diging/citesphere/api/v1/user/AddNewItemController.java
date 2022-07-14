package edu.asu.diging.citesphere.api.v1.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.http.ResponseEntity;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.api.v1.model.impl.ItemDetails;
import edu.asu.diging.citesphere.api.v1.model.impl.ItemWithGiles;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationForm;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.web.user.UploadItemFileController;

@Controller
public class AddNewItemController extends V1Controller {

    @Autowired
    private ICitationHelper citationHelper;

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private UploadItemFileController uploadItemFileController;

//    @PostMapping(value = {"items/create1/{zoteroGroupId}"}, consumes = {
//        MediaType.APPLICATION_JSON_VALUE })
//    @ResponseBody
    @RequestMapping(value = "/items/create1/{zoteroGroupId}", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> create1(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @ModelAttribute ItemWithGiles itemWithGiles)
            throws ZoteroConnectionException, GroupDoesNotExistException, ZoteroHttpStatusException {

//        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        IUser user = userManager.findByUsername("pgiri"); // Hard Coded. Change this.
        if (user == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        ICitation citation = new Citation();
        List<String> collectionIds = new ArrayList<>();
        if (itemWithGiles.getCollectionId() != null && !itemWithGiles.getCollectionId().trim().isEmpty()) {
            collectionIds.add(itemWithGiles.getCollectionId());
        }
        citation.setCollections(collectionIds);
        citationHelper.updateCitation(citation, itemWithGiles, user);
        try {
            citation = citationManager.createCitation(user, zoteroGroupId, collectionIds, citation);
        } catch (ZoteroItemCreationFailedException e) {
            return new ResponseEntity<String>("Failed!", HttpStatus.OK);
            // return "auth/group/editItem";
        }

        //giles:
        try {
            Principal principal = new UsernamePasswordAuthenticationToken(user, null);
            uploadItemFileController.uploadFile(principal, zoteroGroupId, citation.getKey(), itemWithGiles.getFiles());
        } catch (CannotFindCitationException | ZoteroHttpStatusException | ZoteroConnectionException
                | CitationIsOutdatedException | ZoteroItemCreationFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }





        return new ResponseEntity<String>("Success!", HttpStatus.OK);

    }

}