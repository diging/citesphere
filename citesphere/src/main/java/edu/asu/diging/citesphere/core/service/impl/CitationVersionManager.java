package edu.asu.diging.citesphere.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import edu.asu.diging.citesphere.core.bib.ICitationVersionsDao;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationVersionException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.bib.CitationVersion;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.ICitationVersionManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class CitationVersionManager implements ICitationVersionManager {

    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private GroupManager groupManager;

    @Autowired
    private ICitationVersionsDao citationVersionsDao;

    /*
     * (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.ICitationVersionManager#
     * getCitationVersions(edu.asu.diging.citesphere.core.model.IUser,
     * java.lang.String, java.lang.String, int, int)
     */
    @Override
    public List<CitationVersion> getCitationVersions(IUser user, String groupId, String key, int page, int pageSize)
            throws GroupDoesNotExistException {
        ICitationGroup group;
        try {
            group = groupManager.getGroup(user, groupId);
        } catch (HttpClientErrorException ex) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        return group == null ? new ArrayList<>() : citationVersionsDao.getVersions(groupId, key, page, pageSize);
    }

    /*
     * (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.ICitationVersionManager#
     * getTotalCitationVersionPages(edu.asu.diging.citesphere.core.model.IUser,
     * java.lang.String, int)
     */
    @Override
    public int getTotalCitationVersionPages(String groupId, String key, int pageSize) {
        int totalCount = citationVersionsDao.getTotalCount(groupId, key);
        return (int) Math.ceil(Float.valueOf(totalCount) / pageSize);
    }

    /*
     * (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.ICitationVersionManager#
     * getCitationVersion(edu.asu.diging.citesphere.core.model.IUser,
     * java.lang.String, java.lang.String, java.lang.Long)
     */
    @Override
    public ICitation getCitationVersion(IUser user, String groupId, String key, Long version)
            throws GroupDoesNotExistException {
        ICitationGroup group;
        try {
            group = groupManager.getGroup(user, groupId);
        } catch (HttpClientErrorException ex) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        return group == null ? null : citationVersionsDao.getVersion(groupId, key, version);
    }

    @Override
    public void revertCitationVersion(IUser user, String groupId, String key, Long version)
            throws GroupDoesNotExistException, ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, CannotFindCitationVersionException {
        ICitationGroup group;
        try {
            group = groupManager.getGroup(user, groupId);
        } catch (HttpClientErrorException ex) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        if (group == null) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        ICitation citationVersion = citationVersionsDao.getVersion(groupId, key, version);
        if (citationVersion == null) {
            throw new CannotFindCitationVersionException(
                    "Citation with key " + key + " and version " + version + "does not exist.");
        }
        citationManager.updateCitation(user, groupId, citationVersion);
    }

}
