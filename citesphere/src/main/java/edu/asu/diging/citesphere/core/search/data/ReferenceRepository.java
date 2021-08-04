package edu.asu.diging.citesphere.core.search.data;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import edu.asu.diging.citesphere.core.search.model.impl.Reference;

public interface ReferenceRepository extends ElasticsearchRepository<Reference, String> {

    void deleteByGroup(String group);
    
}
