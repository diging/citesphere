package edu.asu.diging.citesphere.core.repository.bib;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.Person;

public interface PersonRepository extends PagingAndSortingRepository<Person, String> {

    @Query("SELECT p from Citation c LEFT JOIN c.authors p WHERE p.uri=(:uri) AND c.group=(:group) AND p.localAuthorityId != ''")
    List<Person> findPersonsByCitationGroupAndUri(@Param("group") ICitationGroup group, @Param("uri") String uri);
    
    Optional<Person> findByUri(String uri);
}
