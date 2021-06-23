package edu.asu.diging.citesphere.core.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import edu.asu.diging.citesphere.model.bib.impl.Citation;

public interface CustomCitationDeleteRepository extends MongoRepository<Citation, ObjectId> {
    
    Long deleteByGroup(String group);

}
