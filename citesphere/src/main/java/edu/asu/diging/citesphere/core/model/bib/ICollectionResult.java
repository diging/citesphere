package edu.asu.diging.citesphere.core.model.bib;

import java.util.List;


public interface ICollectionResult {

    List<ICitation> getItems();

    void setItems(List<ICitation> items);

    long getTotal();

    void setTotal(long total);

    double getTotalPages();

    void setTotalPages(double totalPages);

    int getCurrentPage();

    void setCurrentPage(int currentPage);

    String getZoteroGroupId();

    void setZoteroGroupId(String zoteroGroupId);

    ICitationGroup getGroup();

    void setGroup(ICitationGroup group);

    String getCollectionId();

    void setCollectionId(String collectionId);

    List<ICitationCollection> getCitationCollections();

    void setCitationCollections(List<ICitationCollection> list);

}
