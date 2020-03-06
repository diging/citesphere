package edu.asu.diging.citesphere.core.repository.custom;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.Person;

public interface PersonAuthorityRepository extends PagingAndSortingRepository<Person, String>{
	
  @Query("SELECT p from Citation c LEFT JOIN c.authors p WHERE p.name like (:name) AND c.group=(:group) AND p.uri not in (:uris) AND p.localAuthorityId != ''")
  List<Person> findPersonsByCitationGroupAndNameLikeAndUriNotIn(@Param("group") ICitationGroup group, @Param("name") String name, @Param("uris") List<String> uris);

  @Query("SELECT p from Citation c LEFT JOIN c.authors p WHERE p.name like (:name) AND c.group=(:group) AND p.localAuthorityId != ''")
  List<Person> findPersonsByCitationGroupAndNameLike(@Param("group") ICitationGroup group, @Param("name") String name);

}
