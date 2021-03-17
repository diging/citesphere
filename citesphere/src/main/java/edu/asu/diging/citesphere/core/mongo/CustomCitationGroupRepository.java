package edu.asu.diging.citesphere.core.mongo;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;

@JaversSpringDataAuditable
public interface CustomCitationGroupRepository extends MongoRepository<CitationGroup, ObjectId>{
    
    Optional<ICitationGroup> findFirstByGroupId(Long long1);

    void deleteByGroupId(int parseInt);

}
