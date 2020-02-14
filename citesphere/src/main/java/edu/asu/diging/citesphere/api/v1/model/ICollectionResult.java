package edu.asu.diging.citesphere.api.v1.model;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;


public interface ICollectionResult {

    List<ICitation> getItems();

    void setItems(List<ICitation> items);

    long getTotal();

    void setTotal(long total);

    long getTotalPages();

    void setTotalPages(long totalPages);

    int getCurrentPage();

    void setCurrentPage(int currentPage);

    String getZoteroGroupId();

    void setZoteroGroupId(String zoteroGroupId);

    String getCollectionId();

    void setCollectionId(String collectionId);

}
