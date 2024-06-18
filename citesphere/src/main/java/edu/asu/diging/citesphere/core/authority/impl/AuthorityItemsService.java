package edu.asu.diging.citesphere.core.authority.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.authority.IAuthorityItemsService;
import edu.asu.diging.citesphere.data.bib.ICitationDao;
import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.transfer.impl.Citations;

@Service
public class AuthorityItemsService implements IAuthorityItemsService {
    
    @Autowired
    private ICitationDao iCitationDao;
    
    @Override
    public Citations findAuthorityItems(IAuthorityEntry entry) {       
        return iCitationDao.findCitationsByPersonUri(entry.getUri());
    }

}
