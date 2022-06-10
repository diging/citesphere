package edu.asu.diging.citesphere.web.user;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;

/**
 * This class binds citation search results together.
 */
public class SearchItemsDataDto {
    private String searchTerm;
    private List<ICitation> items;
    private int totalPages;
    private long totalResults;
    private int currentPage;
    private String zoteroGroupId;
    private ICitationGroup group;
    private String sort;
    private List<CitationsDto> citationsData;
    private List<String> shownColumns;
    private List<AvailableColumnsDataDto> availableColumnsData;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<ICitation> getItems() {
        return items;
    }

    public void setItems(List<ICitation> items) {
        this.items = items;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getZoteroGroupId() {
        return zoteroGroupId;
    }

    public void setZoteroGroupId(String zoteroGroupId) {
        this.zoteroGroupId = zoteroGroupId;
    }

    public ICitationGroup getGroup() {
        return group;
    }

    public void setGroup(ICitationGroup group) {
        this.group = group;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<CitationsDto> getCitationsData() {
        return citationsData;
    }

    public void setCitationsData(List<CitationsDto> citationsData) {
        this.citationsData = citationsData;
    }

    public List<AvailableColumnsDataDto> getAvailableColumnsData() {
        return availableColumnsData;
    }

    public void setAvailableColumnsData(List<AvailableColumnsDataDto> availableColumnsData) {
        this.availableColumnsData = availableColumnsData;
    }

    public List<String> getShownColumns() {
        return shownColumns;
    }

    public void setShownColumns(List<String> shownColumns) {
        this.shownColumns = shownColumns;
    }
}