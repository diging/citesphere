package edu.asu.diging.citesphere.web.user;

import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class DownloadGilesFileController {
    
    @Autowired
    private ICitationManager citationManager;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}/file/{gilesFileId}")
    public void get(Authentication authentication, Model model,
            @PathVariable("zoteroGroupId") String zoteroGroupId,
            @PathVariable("itemId") String itemId,
            @PathVariable("gilesFileId") String gilesFileId,
            HttpServletResponse response)
            throws GroupDoesNotExistException, CannotFindCitationException,
            ZoteroHttpStatusException {
        IUser user = (IUser) authentication.getPrincipal();
        ICitation citation = citationManager.getCitation(
                user, zoteroGroupId, itemId);
        
        Set<IGilesUpload> uploads = citation.getGilesUploads();
        Optional<IGilesUpload> file = uploads.stream().filter(u -> u.getUploadedFile().getId().equals(gilesFileId)).findFirst();
        if (!file.isPresent()) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return;
        }
        
        
    }
}
