package edu.asu.diging.citesphere.web.user;

import java.util.List;

import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;

public class AuthoritySearchResult {

    private int totalPages;
    private int currentPage;

    private List<IAuthorityEntry> foundAuthorities;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<IAuthorityEntry> getFoundAuthorities() {
        return foundAuthorities;
    }

    public void setFoundAuthorities(List<IAuthorityEntry> foundAuthorities) {
        this.foundAuthorities = foundAuthorities;
    }

}
