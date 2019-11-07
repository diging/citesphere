package edu.asu.diging.citesphere.core.model.bib.impl;

import java.util.List;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.ICollectionResult;

public class CollectionResult implements ICollectionResult {

    public CollectionResult() {}
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#getItems()
     */
    @Override
    public List<ICitation> getItems() {
        return items;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#setItems(java.util.List)
     */
    @Override
    public void setItems(List<ICitation> items) {
        this.items = items;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#getTotal()
     */
    @Override
    public long getTotal() {
        return total;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#setTotal(long)
     */
    @Override
    public void setTotal(long total) {
        this.total = total;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#getTotalPages()
     */
    @Override
    public double getTotalPages() {
        return totalPages;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#setTotalPages(double)
     */
    @Override
    public void setTotalPages(double totalPages) {
        this.totalPages = totalPages;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#getCurrentPage()
     */
    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#setCurrentPage(int)
     */
    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#getZoteroGroupId()
     */
    @Override
    public String getZoteroGroupId() {
        return zoteroGroupId;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#setZoteroGroupId(java.lang.String)
     */
    @Override
    public void setZoteroGroupId(String zoteroGroupId) {
        this.zoteroGroupId = zoteroGroupId;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#getGroup()
     */
    @Override
    public ICitationGroup getGroup() {
        return group;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#setGroup(edu.asu.diging.citesphere.core.model.bib.ICitationGroup)
     */
    @Override
    public void setGroup(ICitationGroup group) {
        this.group = group;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#getCollectionId()
     */
    @Override
    public String getCollectionId() {
        return collectionId;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#setCollectionId(java.lang.String)
     */
    @Override
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#getCitationCollections()
     */
    @Override
    public List<ICitationCollection> getCitationCollections() {
        return citationCollections;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICollectionResult#setCitationCollections(java.util.List)
     */
    @Override
    public void setCitationCollections(List<ICitationCollection> list) {
        this.citationCollections = list;
    }
    private List<ICitation> items;
    private long total;
    private double totalPages;
    private int currentPage;
    private String zoteroGroupId;
    private ICitationGroup group;
    private String collectionId;
    private List<ICitationCollection> citationCollections;
}
