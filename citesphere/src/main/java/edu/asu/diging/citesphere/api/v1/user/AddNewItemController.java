package edu.asu.diging.citesphere.api.v1.user;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
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
import edu.asu.diging.citesphere.web.forms.PersonForm;

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

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "authors", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                    List<PersonForm> authors = objectMapper.readValue(text, new TypeReference<List<PersonForm>>() {});
                    authors = authors.stream()
                            .peek(author -> author.setRole("author"))
                            .collect(Collectors.toList());                    
                    setValue(authors);
                } catch (IOException e) {
                    setValue(null);  
                    logger.error("Could not parse authors", e);
                    throw new RuntimeException("authors: Error converting String to List<PersonForm>");
                }
            }
        });
        binder.registerCustomEditor(List.class, "editors", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                    List<PersonForm> editors = objectMapper.readValue(text, new TypeReference<List<PersonForm>>() {});
                    editors = editors.stream()
                            .peek(editor -> editor.setRole("editor"))
                            .collect(Collectors.toList()); 
                    setValue(editors);
                } catch (IOException e) {
                    setValue(null);  
                    logger.error("Could not parse editors", e);
                    throw new RuntimeException("editors: Error converting String to List<PersonForm>");
                }
            }
        });
        binder.registerCustomEditor(List.class, "otherCreators", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                    List<PersonForm> otherCreators = objectMapper.readValue(text, new TypeReference<List<PersonForm>>() {});
                    setValue(otherCreators);
                } catch (IOException e) {
                    setValue(null);  
                    logger.error("Could not parse otherCreators", e);
                    throw new RuntimeException("otherCreators: Error converting String to List<PersonForm>");
                }
            }
        });
    }

    @RequestMapping(value = "/groups/{groupId}/items/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createNewItem(Principal principal,
            @PathVariable("groupId") String zoteroGroupId, @ModelAttribute CitationForm itemWithGiles) {

        IUser user = userManager.findByUsername(principal.getName());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(itemWithGiles.getItemType() == null) {
            logger.error("Missing Item Type");
            return new ResponseEntity<>("Error: Missing Item Type", HttpStatus.BAD_REQUEST);
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
        } catch (ZoteroItemCreationFailedException | ZoteroConnectionException | ZoteroHttpStatusException e) {
            logger.error("Zotero Item creation failed. ", e);
            return new ResponseEntity<>("Error: Zetero Item creation failed. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (GroupDoesNotExistException e) {
            logger.error("Group " + zoteroGroupId +" does not exists. ", e);
            return new ResponseEntity<>("Error: Group " + zoteroGroupId +" does not exists. ", HttpStatus.BAD_REQUEST);
        }

        if (itemWithGiles.getFiles() != null && itemWithGiles.getFiles().length > 0) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode(); 
            for (int i=0; i<itemWithGiles.getFiles().length; i++) {
                IGilesUpload job;
                try {
                    job = jobManager.createGilesJob(user, itemWithGiles.getFiles()[i], itemWithGiles.getFiles()[i].getBytes(), zoteroGroupId,
                            citation.getKey());
                } catch (GroupDoesNotExistException e) {
                    logger.error("Could not create job because group does not exist.", e);
                    return new ResponseEntity<>("Error: Could not create job because group does not exist.", HttpStatus.BAD_REQUEST);
                } catch (CannotFindCitationException e) {
                    logger.error("Could not find the newly created citation.", e);
                    return new ResponseEntity<>("Error: Could not find the newly created citation.", HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (CitationIsOutdatedException e) {
                    logger.error("Citation outdated. ", e);
                    return new ResponseEntity<>("Error: Citation outdated.", HttpStatus.BAD_REQUEST);
                } catch (IOException e) {
                    logger.error("Could not read file from the request. ", e);
                    return new ResponseEntity<>("Error: Could not read file from the request.", HttpStatus.BAD_REQUEST);
                } catch (AccessForbiddenException e) {
                    logger.error("Access Forbidden for uploading files to Giles ", e);
                    return new ResponseEntity<>("Error: Access Forbidden for uploading files to Giles.", HttpStatus.FORBIDDEN);
                } catch (ZoteroHttpStatusException e) {
                    logger.error("Zotero HTTP Status Exception occured ", e);
                    return new ResponseEntity<>("Error: Zotero HTTP Status Exception occured.", HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (ZoteroConnectionException e) {
                    logger.error("Zotero connection failed ", e);
                    return new ResponseEntity<>("Error: Zotero connection failed.", HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (ZoteroItemCreationFailedException e) {
                    logger.error("Zotero Item creation failed ", e);
                    return new ResponseEntity<>("Error: Zotero Item creation failed.", HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (HttpClientErrorException.Unauthorized e) {
                    logger.error("Unauthorized to upload files to Giles ", e);
                    return new ResponseEntity<>("Error: Unauthorized to upload files to Giles.", HttpStatus.UNAUTHORIZED);
                }

                gilesUtil.createJobObjectNode(root, job);
            }
        }
        return new ResponseEntity<>(citation, HttpStatus.OK); 
    }

}