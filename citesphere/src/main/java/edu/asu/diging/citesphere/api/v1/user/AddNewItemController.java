package edu.asu.diging.citesphere.api.v1.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.jobs.IUploadFileJobManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.util.IGilesUtil;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationForm;

@Controller
public class AddNewItemController extends V1Controller {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationHelper citationHelper;

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IGilesUtil gilesUtil;

    @Autowired
    private IUploadFileJobManager jobManager;

    @RequestMapping(value = "/groups/{groupId}/items/create", method = RequestMethod.POST, consumes = {
        MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ICitation> createNewItem(Authentication authentication,
            @PathVariable("zoteroGroupId") String zoteroGroupId, @ModelAttribute CitationForm itemWithGiles)
            throws ZoteroConnectionException, GroupDoesNotExistException, ZoteroHttpStatusException,
            ZoteroItemCreationFailedException {

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
                List<byte[]> fileBytes = new ArrayList<>();
                gilesUtil.convertFilesToBytesList(fileBytes, itemWithGiles.getFiles());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode root = mapper.createObjectNode(); 
                for (int i=0; i<itemWithGiles.getFiles().length; i++) {
                    IGilesUpload job;
                    try {
                        job = jobManager.createGilesJob(user, itemWithGiles.getFiles()[i], fileBytes.get(i), zoteroGroupId,
                                citation.getKey());
                    } catch (GroupDoesNotExistException e) {
                        logger.error("Could not create job because group does not exist.", e);
                        return new ResponseEntity<ICitation>(HttpStatus.BAD_REQUEST);
                    }

                    gilesUtil.createJobObjectNode(root, job);
                }
            } catch (CannotFindCitationException | CitationIsOutdatedException e) {
                return new ResponseEntity<ICitation>(HttpStatus.NOT_FOUND);
            } catch (ZoteroHttpStatusException | ZoteroConnectionException e) {
                return new ResponseEntity<ICitation>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (ZoteroItemCreationFailedException e) {
                return new ResponseEntity<ICitation>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<ICitation>(citation, HttpStatus.OK); 
    }

}