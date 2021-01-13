package edu.asu.diging.citesphere.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import com.mongodb.BasicDBObject;

import edu.asu.diging.citesphere.core.dao.IPersonsMongoDao;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.transfer.impl.Persons;

@Repository
public class PersonsMongoDao implements IPersonsMongoDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Persons findPersonsByCitationGroupAndNameLike(ICitationGroup citationGroup, String firstName, String lastName,
            Pageable page) {
        UnwindOperation unwind = Aggregation.unwind("authors");
        MatchOperation match = Aggregation.match(Criteria.where("group").is(citationGroup.getGroupId()).and("authors.firstName").is(firstName).and("authors.lastName").is(lastName).and("authors.localAuthorityId").ne(""));
        GroupOperation group = Aggregation.group("authors.uri")
                .push(new BasicDBObject("name", "$authors.name").append("uri", "$authors.uri")
                        .append("firstName", "$authors.firstName").append("lastName", "$authors.lastName")
                        .append("localAuthorityId", "$authors.localAuthorityId"))
                .as("persons");
        AggregationResults<Persons> result = null;
        if(page.getPageNumber() > 0) {
            SkipOperation skip = Aggregation.skip((long)(page.getPageNumber()-1) * page.getPageSize());
            LimitOperation limit = Aggregation.limit(page.getPageSize());
            result = mongoTemplate.aggregate(Aggregation.newAggregation(unwind, match, group, skip, limit),
                    Citation.class, Persons.class);
        } else {
            result = mongoTemplate.aggregate(Aggregation.newAggregation(unwind, match, group),
                    Citation.class, Persons.class);
        }
        return result.getUniqueMappedResult();
    }

    @Override
    public Persons findPersonsByCitationGroupAndNameLikeAndUriNotIn(ICitationGroup citationGroup, String firstName,
            String lastName, List<String> uris, Pageable page) {
        UnwindOperation unwind = Aggregation.unwind("authors");
        MatchOperation match = Aggregation.match(Criteria.where("group").is(citationGroup.getGroupId()).and("authors.firstName").is(firstName).and("authors.lastName").is(lastName).and("authors.uri").nin(uris).and("authors.localAuthorityId").ne(""));
        GroupOperation group = Aggregation.group("authors.uri")
                .push(new BasicDBObject("name", "$authors.name").append("uri", "$authors.uri")
                        .append("firstName", "$authors.firstName").append("lastName", "$authors.lastName")
                        .append("localAuthorityId", "$authors.localAuthorityId"))
                .as("persons");
        AggregationResults<Persons> result = null;
        if(page.getPageNumber() > 0) {
            SkipOperation skip = Aggregation.skip((long)(page.getPageNumber()-1) * page.getPageSize());
            LimitOperation limit = Aggregation.limit(page.getPageSize());
            result = mongoTemplate.aggregate(Aggregation.newAggregation(unwind, match, group, skip, limit),
                    Citation.class, Persons.class);
        } else {
            result = mongoTemplate.aggregate(Aggregation.newAggregation(unwind, match, group),
                    Citation.class, Persons.class);
        }
        
        return result.getUniqueMappedResult();
    }

    @Override
    public long countByPersonsByCitationGroupAndNameLike(ICitationGroup citationGroup, String firstName, String lastName) {
        UnwindOperation unwind = Aggregation.unwind("authors");
        MatchOperation match = Aggregation.match(Criteria.where("group").is(citationGroup.getGroupId()).and("authors.firstName").is(firstName).and("authors.lastName").is(lastName).and("authors.localAuthorityId").ne(""));
        GroupOperation group = Aggregation.group("authors.uri")
                .push(new BasicDBObject("name", "$authors.name").append("uri", "$authors.uri")
                        .append("firstName", "$authors.firstName").append("lastName", "$authors.lastName")
                        .append("localAuthorityId", "$authors.localAuthorityId"))
                .as("persons");
        AggregationResults<Persons> result = mongoTemplate.aggregate(Aggregation.newAggregation(unwind, match, group),
                Citation.class, Persons.class);
        return result.getUniqueMappedResult()!= null ? (long) result.getUniqueMappedResult().getPersons().size() : 0;
    }
    
}
