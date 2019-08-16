package edu.asu.diging.citesphere.core.model.bib.impl;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import edu.asu.diging.citesphere.core.model.bib.ICreator;
import edu.asu.diging.citesphere.core.model.bib.IReference;


public class Reference implements IReference {

    private String authorString;
    
    @OneToMany(targetEntity=Creator.class, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="Reference_Contributor")
    @OrderBy("role, positionInList")
    @NotFound(action=NotFoundAction.IGNORE)
    private List<ICreator> contributors;
    
    private String title;
    private String year;
    private String identifier;
    private String identifierType;
    private String firstPage;
    private String endPage;
    private String volume;
    private String source;
    private String referenceId;
    private String referenceLabel;
    private String publicationType;
    private String citationId;
    
    private String referenceString;
    private String referenceStringRaw;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getAuthorString()
     */
    @Override
    public String getAuthorString() {
        return authorString;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setAuthorString(java.lang.String)
     */
    @Override
    public void setAuthorString(String authorString) {
        this.authorString = authorString;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getContributors()
     */
    @Override
    public List<ICreator> getContributors() {
        return contributors;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setContributors(java.util.List)
     */
    @Override
    public void setContributors(List<ICreator> contributors) {
        this.contributors = contributors;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getTitle()
     */
    @Override
    public String getTitle() {
        return title;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getYear()
     */
    @Override
    public String getYear() {
        return year;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setYear(java.lang.String)
     */
    @Override
    public void setYear(String year) {
        this.year = year;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getIdentifier()
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setIdentifier(java.lang.String)
     */
    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getIdentifierType()
     */
    @Override
    public String getIdentifierType() {
        return identifierType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setIdentifierType(java.lang.String)
     */
    @Override
    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getFirstPage()
     */
    @Override
    public String getFirstPage() {
        return firstPage;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setFirstPage(java.lang.String)
     */
    @Override
    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getEndPage()
     */
    @Override
    public String getEndPage() {
        return endPage;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setEndPage(java.lang.String)
     */
    @Override
    public void setEndPage(String endPage) {
        this.endPage = endPage;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getVolume()
     */
    @Override
    public String getVolume() {
        return volume;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setVolume(java.lang.String)
     */
    @Override
    public void setVolume(String volume) {
        this.volume = volume;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getSource()
     */
    @Override
    public String getSource() {
        return source;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setSource(java.lang.String)
     */
    @Override
    public void setSource(String source) {
        this.source = source;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getReferenceId()
     */
    @Override
    public String getReferenceId() {
        return referenceId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setReferenceId(java.lang.String)
     */
    @Override
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getReferenceLabel()
     */
    @Override
    public String getReferenceLabel() {
        return referenceLabel;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setReferenceLabel(java.lang.String)
     */
    @Override
    public void setReferenceLabel(String referenceLabel) {
        this.referenceLabel = referenceLabel;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getPublicationType()
     */
    @Override
    public String getPublicationType() {
        return publicationType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setPublicationType(java.lang.String)
     */
    @Override
    public void setPublicationType(String publicationType) {
        this.publicationType = publicationType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getCitationId()
     */
    @Override
    public String getCitationId() {
        return citationId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setCitationId(java.lang.String)
     */
    @Override
    public void setCitationId(String citationId) {
        this.citationId = citationId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getReferenceString()
     */
    @Override
    public String getReferenceString() {
        return referenceString;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setReferenceString(java.lang.String)
     */
    @Override
    public void setReferenceString(String referenceString) {
        this.referenceString = referenceString;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#getReferenceStringRaw()
     */
    @Override
    public String getReferenceStringRaw() {
        return referenceStringRaw;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IReference#setReferenceStringRaw(java.lang.String)
     */
    @Override
    public void setReferenceStringRaw(String referenceStringRaw) {
        this.referenceStringRaw = referenceStringRaw;
    }
    
}
