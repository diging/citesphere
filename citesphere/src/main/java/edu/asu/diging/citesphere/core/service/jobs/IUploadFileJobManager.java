package edu.asu.diging.citesphere.core.service.jobs;

import java.util.List;

import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.jobs.IUploadFileJob;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

public interface IUploadFileJobManager {

    List<IUploadFileJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes, String groupId,
            String itemKey) throws GroupDoesNotExistException;

    IGilesUpload createGilesJob(IUser user, MultipartFile file, byte[] fileBytes, String groupId, String itemKey)
            throws GroupDoesNotExistException, AccessForbiddenException, CannotFindCitationException, ZoteroHttpStatusException, ZoteroConnectionException, CitationIsOutdatedException;

}