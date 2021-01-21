package edu.asu.diging.citesphere.core.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.Person;
import edu.asu.diging.citesphere.model.transfer.impl.Persons;

public interface IPersonsMongoDao {
    
    Persons findPersonsByCitationGroupAndNameLike(ICitationGroup citationGroup, String firstName, String lastName, Pageable page);
    
    List<Persons> findPersonsByCitationGroupAndNameLikeAndUriNotIn(ICitationGroup citationGroup, String firstName, String lastName, List<String> uris, Pageable page);
    
    long countByPersonsByCitationGroupAndNameLikeAndUriNotIn(ICitationGroup citationGroup, String firstName, String lastName, List<String> uriList);
    
    long countByPersonsByCitationGroupAndNameLike(ICitationGroup citationGroup, String firstName, String lastName);
 
}