package edu.asu.diging.citesphere.core.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import edu.asu.diging.citesphere.model.bib.impl.CitationCollection;

public interface CustomCitationCollectionDeleteRepository extends MongoRepository<CitationCollection, ObjectId> {

    void deleteByGroupId(String groupId);
    
}
