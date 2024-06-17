package edu.asu.diging.citesphere.core.authority.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.citesphere.core.authority.IAuthorityItemsService;
import edu.asu.diging.citesphere.data.bib.ICitationDao;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.transfer.impl.Citations;

public class AuthorityItemsService implements IAuthorityItemsService {
    
    @Autowired
    private ICitationDao iCitationDao;
    
    @Override
    public Citations findAuthorityItems(IAuthorityEntry entry) {
//        iCitationDao.getCitationIterator("authorityId", authorityId);
        
        return iCitationDao.findCitationsByPersonUri(entry.getUri());
    }

}
