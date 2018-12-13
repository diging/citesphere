package edu.asu.diging.citesphere.core.repository.cache;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ZoteroObjectType;
import edu.asu.diging.citesphere.core.model.cache.impl.PageRequest;

public interface PageRequestRepository extends PagingAndSortingRepository<PageRequest, String> {

    @EntityGraph(value="requestsWithFullCitations", type=EntityGraphType.LOAD)
    List<PageRequest> findByUserAndObjectIdAndPageNumberAndZoteroObjectTypeAndSortBy(IUser user, String objectId, int page, ZoteroObjectType zoteroObjectType, String sortBy);

    @Query(value="SELECT DISTINCT pr from PageRequest pr LEFT JOIN FETCH pr.citations c LEFT JOIN FETCH c.authors a WHERE pr.objectId = ?2 AND pr.user = ?1 AND pr.pageNumber = ?3 AND pr.zoteroObjectType = ?4")
//    @EntityGraph(value="requestsWithFullCitations")
    List<PageRequest> findPageRequestWithCitations(IUser user, String objectId, int page, ZoteroObjectType zoteroObjectType);
} 

