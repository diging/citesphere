package edu.asu.diging.citesphere.core.search.service;

import edu.asu.diging.citesphere.core.search.service.impl.ResultPage;
import edu.asu.diging.citesphere.core.service.impl.CitationPage;

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
    
    /**
     * Search for a given term in a group.
     * 
     * @param searchTerm Term to search for.
     * @param groupId Group id of the group that should be searched.
     * @param collectionId id of the collection that should be searched.
     * @param page Current page.
     * @param pageSize page size of results.
     * @return Page of results
     */
    ResultPage search(String searchTerm, String groupId, String collectionId, int page, int pageSize);
    
    
    /**
     * Fetches the next and previous citation keys for the given search term in group and the current index and page number.
     * 
     * @param searchTerm Term to search for.
     * @param groupId Group id of the group that should be searched.
     * @param page Current page.
     * @param index Index of the current item.
     * @param pageSize Size of the page used.
     * @return Keys, index and page number for the next and previous items.
     */
    CitationPage getPrevAndNextCitation(String searchTerm, String groupId, int page, int index, int pageSize);

}