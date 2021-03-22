package edu.asu.diging.citesphere.core.mongo;

import org.bson.types.ObjectId;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;

@JaversSpringDataAuditable
public interface CustomCitationGroupRepository extends MongoRepository<CitationGroup, ObjectId>{
    
    void deleteByGroupId(int parseInt);

}
