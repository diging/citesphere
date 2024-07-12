package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.util.HashSet;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.giles.GilesUploadChecker;
import edu.asu.diging.citesphere.core.service.giles.GilesUploadService;
import edu.asu.diging.citesphere.core.service.jobs.IUploadFileJobManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

@Service
@Transactional
@PropertySource("classpath:/config.properties")
public class UploadFileJobManager implements IUploadFileJobManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IGroupManager groupManager;
 
    @Autowired
    private GilesUploadService gilesUploadService;
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private GilesUploadChecker uploadChecker;

    
    @Override
    public IGilesUpload createGilesJob(IUser user, MultipartFile file, byte[] fileBytes, String groupId, String itemKey)
            throws GroupDoesNotExistException, AccessForbiddenException, CannotFindCitationException,
            ZoteroHttpStatusException, ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroItemCreationFailedException, DuplicateKeyException {
        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            throw new GroupDoesNotExistException();
        }
        
        ICitation citation = citationManager.getCitation(user, groupId, itemKey);
        if (citation != null) {
            IGilesUpload upload = gilesUploadService.uploadFile(user, file, fileBytes);
            if (citation.getGilesUploads() == null) {
                citation.setGilesUploads(new HashSet<>());
            }
            citation.getGilesUploads().add(upload);
            citationManager.updateCitation(user, groupId, citation);
            uploadChecker.add(citation);
            return upload;
        }
        
        return null;
    }
    
}
