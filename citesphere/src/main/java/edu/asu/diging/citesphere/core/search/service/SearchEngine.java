package edu.asu.diging.citesphere.core.search.service;

import edu.asu.diging.citesphere.core.search.service.impl.ResultPage;

public interface SearchEngine {

    /**
     * Search for a given term in a group.
     * 
     * @param searchTerm Term to search for.
     * @param groupId Group id of the group that should be searched.
     * @param page Current page.
     * @param pageSize page size of results.
     * @return Page of results
     */
    ResultPage search(String searchTerm, String groupId, int page, int pageSize);

}