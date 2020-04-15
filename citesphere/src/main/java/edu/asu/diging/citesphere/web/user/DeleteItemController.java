package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.forms.CitationForm;

@Controller
public class DeleteItemController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private ICitationHelper citationHelper;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteItem(@PathVariable("zoteroGroupId") String zoteroGroupId,
            @PathVariable("itemId") String itemId, Authentication authentication, Model model)  {

        try {
            ICitation citation = citationManager.getCitation((IUser) authentication.getPrincipal(), zoteroGroupId,
                    itemId);
            citationManager.deleteCitation((IUser) authentication.getPrincipal(), zoteroGroupId, citation);
        } catch (CitationIsOutdatedException e) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        } catch (GroupDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (CannotFindCitationException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ZoteroHttpStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ZoteroConnectionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}