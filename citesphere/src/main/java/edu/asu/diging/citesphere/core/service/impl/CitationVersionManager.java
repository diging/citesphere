package edu.asu.diging.citesphere.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.bib.ICitationVersionsDao;
import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.bib.CitationVersion;
import edu.asu.diging.citesphere.core.service.ICitationVersionManager;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class CitationVersionManager implements ICitationVersionManager {

    // FIXME: needs to be replaced with group manager
    @Autowired
    private CitationGroupRepository groupRepository;

    @Autowired
    private ICitationVersionsDao citationVersionsDao;

    @Override
    public List<CitationVersion> getCitationVersions(IUser user, String groupId, String key, int page, int pageSize)
            throws AccessForbiddenException, GroupDoesNotExistException {
        Optional<ICitationGroup> groupOptional = groupRepository.findFirstByGroupId(new Long(groupId));
        if (!groupOptional.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        ICitationGroup group = groupOptional.get();
        if (group.getGroupId() == new Long(groupId)) {
            if (!group.getUsers().contains(user.getUsername())) {
                throw new AccessForbiddenException("User does not have access this citation.");
            }
            return citationVersionsDao.getVersions(groupId, key, page, pageSize);
        }
        return new ArrayList<>();
    }

    @Override
    public int getTotalCitationVersionPages(String groupId, String key, int pageSize) {
        int totalCount = citationVersionsDao.getTotalCount(groupId, key);
        return (int) Math.ceil(Float.valueOf(totalCount) / pageSize);
    }

    @Override
    public ICitation getCitationVersion(IUser user, String groupId, String key, Long version)
            throws AccessForbiddenException, GroupDoesNotExistException {
        Optional<ICitationGroup> groupOptional = groupRepository.findFirstByGroupId(new Long(groupId));
        if (!groupOptional.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        ICitationGroup group = groupOptional.get();
        if (group != null && group.getGroupId() == new Long(groupId)) {
            if (!group.getUsers().contains(user.getUsername())) {
                throw new AccessForbiddenException("User does not have access this citation.");
            }
            return citationVersionsDao.getVersion(groupId, key, version);
        }
        return null;
    }

}
