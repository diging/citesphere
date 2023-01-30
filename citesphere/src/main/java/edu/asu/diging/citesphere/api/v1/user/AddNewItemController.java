package edu.asu.diging.citesphere.api.v1.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.http.ResponseEntity;

import edu.asu.diging.citesphere.api.v1.V1Controller;
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

    @RequestMapping(value = "/items/create/item/{zoteroGroupId}", method = RequestMethod.POST, consumes = {
        MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ICitation> createNewItem(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @ModelAttribute CitationForm itemWithGiles)
            throws ZoteroConnectionException, GroupDoesNotExistException, ZoteroHttpStatusException, ZoteroItemCreationFailedException {

        IUser user = userManager.findByUsername((String) authentication.getPrincipal());
        if (user == null) {
            return new ResponseEntity<ICitation>(HttpStatus.UNAUTHORIZED);
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
            throw new ZoteroItemCreationFailedException();
        }

        if (itemWithGiles.getFiles() != null && itemWithGiles.getFiles().length > 0) {
            try {
                Principal principal = new UsernamePasswordAuthenticationToken(user, null);
                uploadItemFileController.uploadFile(principal, zoteroGroupId, citation.getKey(), itemWithGiles.getFiles());
            } catch (CannotFindCitationException | ZoteroHttpStatusException | ZoteroConnectionException
                    | CitationIsOutdatedException | ZoteroItemCreationFailedException e) {
                return new ResponseEntity<ICitation>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<ICitation>(citation, HttpStatus.OK);
    }

}