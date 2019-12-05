package edu.asu.diging.citesphere.api.v1.model;

import java.util.List;

import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;


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

}
