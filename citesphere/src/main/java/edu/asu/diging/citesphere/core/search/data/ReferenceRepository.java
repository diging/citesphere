package edu.asu.diging.citesphere.core.search.data;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import edu.asu.diging.citesphere.core.search.model.impl.Reference;

public interface ReferenceRepository extends ElasticsearchRepository<Reference, String> {

    List<Reference> findByTitleAndDeletedFalseOrCreatorsLastNameAndDeletedFalse(String searchTerm, String searchTermCreators, Pageable page);
    List<Reference> findByTitle(String searchTerm, Pageable page);
    long countByTitleOrCreatorsLastNameAndDeletedFalse(String searchTerm, String searchTermCreators);
}
