package edu.asu.diging.citesphere.core.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.transfer.impl.Persons;

public interface IPersonsMongoDao {
    
    Persons findPersonsByCitationGroupAndNameLike(ICitationGroup citationGroup, String firstName, String lastName, Pageable page);
    
    Persons findPersonsByCitationGroupAndNameLikeAndUriNotIn(ICitationGroup citationGroup, String firstName, String lastName, List<String> uris, Pageable page);
    
    long countByPersonsByCitationGroupAndNameLike(ICitationGroup citationGroup, String firstName, String lastName);
 
}