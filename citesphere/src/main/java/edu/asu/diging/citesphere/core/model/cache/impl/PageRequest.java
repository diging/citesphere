package edu.asu.diging.citesphere.core.model.cache.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

import edu.asu.diging.citesphere.core.model.cache.IPageRequest;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ZoteroObjectType;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(
                name="requestsWithFullCitations", 
                attributeNodes={@NamedAttributeNode(value="citations", subgraph="requestCitations")},
                subgraphs={
                        @NamedSubgraph(
                                name="requestCitations",
                                attributeNodes={
                                        @NamedAttributeNode(value="authors", subgraph="authorAffiliations"),
                                }                                
                        ),
                        @NamedSubgraph(
                                name="authorAffiliations",
                                attributeNodes={@NamedAttributeNode("affiliations")}
                        )
                }
          )
    })
public class PageRequest implements IPageRequest {

    @Id
    @GeneratedValue(generator = "page_id_generator")
    @GenericGenerator(name = "page_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "PAGE"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    private String id;
    @Enumerated(EnumType.STRING)
    private ZoteroObjectType zoteroObjectType;
    private int pageNumber;
    private int pageSize;
    private long totalNumResults;
    @ManyToOne(targetEntity=User.class)
    private IUser user;
    private String objectId;
    private long version;
    @NotFound(action=NotFoundAction.IGNORE)
    @ManyToMany(targetEntity=Citation.class)
    private Set<ICitation> citations;
    private String sortBy;
    private String collectionId;
    
    private OffsetDateTime lastUpdated;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#getId()
     */
    @Override
    public String getId() {
        return id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#getZoteroObjectType()
     */
    @Override
    public ZoteroObjectType getZoteroObjectType() {
        return zoteroObjectType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#setZoteroObjectType(edu.asu.diging.citesphere.core.model.bib.ZoteroObjectType)
     */
    @Override
    public void setZoteroObjectType(ZoteroObjectType zoteroObjectType) {
        this.zoteroObjectType = zoteroObjectType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#getPageNumber()
     */
    @Override
    public int getPageNumber() {
        return pageNumber;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#setPageNumber(int)
     */
    @Override
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#getPageSize()
     */
    @Override
    public int getPageSize() {
        return pageSize;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#setPageSize(int)
     */
    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    @Override
    public long getTotalNumResults() {
        return totalNumResults;
    }
    @Override
    public void setTotalNumResults(long totalNumResults) {
        this.totalNumResults = totalNumResults;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#getUser()
     */
    @Override
    public IUser getUser() {
        return user;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#setUser(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public void setUser(IUser user) {
        this.user = user;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#getObjectId()
     */
    @Override
    public String getObjectId() {
        return objectId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#setObjectId(java.lang.String)
     */
    @Override
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#getVersion()
     */
    @Override
    public long getVersion() {
        return version;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPageRequest#setVersion(long)
     */
    @Override
    public void setVersion(long version) {
        this.version = version;
    }
    @Override
    public Set<ICitation> getCitations() {
        return citations;
    }
    @Override
    public void setCitations(Set<ICitation> citations) {
        this.citations = citations;
    }
    @Override
    public String getSortBy() {
        return sortBy;
    }
    @Override
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    public String getCollectionId() {
        return collectionId;
    }
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
    @Override
    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }
    @Override
    public void setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
}
