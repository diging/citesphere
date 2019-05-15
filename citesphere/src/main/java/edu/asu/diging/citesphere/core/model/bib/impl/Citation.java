package edu.asu.diging.citesphere.core.model.bib.impl;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.ICreator;
import edu.asu.diging.citesphere.core.model.bib.IPerson;
import edu.asu.diging.citesphere.core.model.bib.ItemType;

@Entity
public class Citation implements ICitation {

    @Id
    @Column(name = "citationKey")
    private String key;

    @ManyToOne(targetEntity = CitationGroup.class)
    @JoinColumn(name = "group_id")
    private ICitationGroup group;

    private long version;
    private String title;
    @OneToMany(targetEntity = Person.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Citation_Author")
    @OrderBy("positionInList")
    private Set<IPerson> authors;
    @OneToMany(targetEntity = Person.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Citation_Editor")
    @OrderBy("positionInList")
    private Set<IPerson> editors;

    @OneToMany(targetEntity = Creator.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(name = "Citation_Creator")
    @OrderBy("role, positionInList")
    private Set<ICreator> otherCreators;

    private ItemType itemType;
    private String publicationTitle;
    private String volume;
    private String issue;
    private String pages;
    private OffsetDateTime date;
    private String dateFreetext;
    private String series;
    private String seriesTitle;
    private String url;
    @Lob
    private String abstractNote;
    private String accessDate;

    private String seriesText;
    private String journalAbbreviation;
    private String language;
    private String doi;
    private String issn;
    private String shortTitle;
    private String archive;
    private String archiveLocation;
    private String libraryCatalog;
    private String callNumber;
    private String rights;

    private String dateAdded;
    private String dateModified;

    @OneToMany(targetEntity = CitationConceptTag.class, cascade = CascadeType.ALL)
    @JoinTable(name = "CitationConcept_ConceptTag")
    private Set<ICitationConceptTag> conceptTags;

    @Lob
    private String extra;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getKey()
     */
    @Override
    public String getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setKey(java.lang.
     * String)
     */
    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public ICitationGroup getGroup() {
        return group;
    }

    @Override
    public void setGroup(ICitationGroup group) {
        this.group = group;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getTitle()
     */
    @Override
    public String getTitle() {
        return title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setTitle(java.lang.
     * String)
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getAuthors()
     */
    @Override
    public Set<IPerson> getAuthors() {
        return authors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setAuthors(java.util.
     * List)
     */
    @Override
    public void setAuthors(Set<IPerson> authors) {
        this.authors = authors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getEditors()
     */
    @Override
    public Set<IPerson> getEditors() {
        return editors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setEditors(java.util.
     * List)
     */
    @Override
    public void setEditors(Set<IPerson> editors) {
        this.editors = editors;
    }

    @Override
    public Set<ICreator> getOtherCreators() {
        return otherCreators;
    }

    @Override
    public void setOtherCreators(Set<ICreator> otherCreators) {
        this.otherCreators = otherCreators;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getItemType()
     */
    @Override
    public ItemType getItemType() {
        return itemType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setItemType(edu.asu.
     * diging.citesphere.core.model.bib.ItemType)
     */
    @Override
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getPublicationTitle()
     */
    @Override
    public String getPublicationTitle() {
        return publicationTitle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setPublicationTitle(
     * java.lang.String)
     */
    @Override
    public void setPublicationTitle(String publicationTitle) {
        this.publicationTitle = publicationTitle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getVolume()
     */
    @Override
    public String getVolume() {
        return volume;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setVolume(java.lang.
     * String)
     */
    @Override
    public void setVolume(String volume) {
        this.volume = volume;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getIssue()
     */
    @Override
    public String getIssue() {
        return issue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setIssue(java.lang.
     * String)
     */
    @Override
    public void setIssue(String issue) {
        this.issue = issue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getPages()
     */
    @Override
    public String getPages() {
        return pages;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setPages(java.lang.
     * String)
     */
    @Override
    public void setPages(String pages) {
        this.pages = pages;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getDate()
     */
    @Override
    public OffsetDateTime getDate() {
        return date;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setDate(java.time.
     * OffsetDateTime)
     */
    @Override
    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    @Override
    public String getDateFreetext() {
        return dateFreetext;
    }

    @Override
    public void setDateFreetext(String dateFreetext) {
        this.dateFreetext = dateFreetext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getSeries()
     */
    @Override
    public String getSeries() {
        return series;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setSeries(java.lang.
     * String)
     */
    @Override
    public void setSeries(String series) {
        this.series = series;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getSeriesTitle()
     */
    @Override
    public String getSeriesTitle() {
        return seriesTitle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setSeriesTitle(java.
     * lang.String)
     */
    @Override
    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getAbstractNote() {
        return abstractNote;
    }

    @Override
    public void setAbstractNote(String abstractNote) {
        this.abstractNote = abstractNote;
    }

    @Override
    public String getAccessDate() {
        return accessDate;
    }

    @Override
    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    @Override
    public String getSeriesText() {
        return seriesText;
    }

    @Override
    public void setSeriesText(String seriesText) {
        this.seriesText = seriesText;
    }

    @Override
    public String getJournalAbbreviation() {
        return journalAbbreviation;
    }

    @Override
    public void setJournalAbbreviation(String journalAbbreviation) {
        this.journalAbbreviation = journalAbbreviation;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getDoi() {
        return doi;
    }

    @Override
    public void setDoi(String doi) {
        this.doi = doi;
    }

    @Override
    public String getIssn() {
        return issn;
    }

    @Override
    public void setIssn(String issn) {
        this.issn = issn;
    }

    @Override
    public String getShortTitle() {
        return shortTitle;
    }

    @Override
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    @Override
    public String getArchive() {
        return archive;
    }

    @Override
    public void setArchive(String archive) {
        this.archive = archive;
    }

    @Override
    public String getArchiveLocation() {
        return archiveLocation;
    }

    @Override
    public void setArchiveLocation(String archiveLocation) {
        this.archiveLocation = archiveLocation;
    }

    @Override
    public String getLibraryCatalog() {
        return libraryCatalog;
    }

    @Override
    public void setLibraryCatalog(String libraryCatalog) {
        this.libraryCatalog = libraryCatalog;
    }

    @Override
    public String getCallNumber() {
        return callNumber;
    }

    @Override
    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    @Override
    public String getRights() {
        return rights;
    }

    @Override
    public void setRights(String rights) {
        this.rights = rights;
    }

    @Override
    public String getDateAdded() {
        return dateAdded;
    }

    @Override
    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String getDateModified() {
        return dateModified;
    }

    @Override
    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    @Override
    public Set<ICitationConceptTag> getConceptTags() {
        return conceptTags;
    }

    @Override
    public void setConceptTags(Set<ICitationConceptTag> concepts) {
        this.conceptTags = concepts;
    }

    @Override
    public String getExtra() {
        return extra;
    }

    @Override
    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public Set<String> getOtherCreatorRoles() {
        Set<String> roles = new HashSet<>();
        if (otherCreators != null) {
            otherCreators.forEach(c -> roles.add(c.getRole()));
        }
        return roles;
    }

    @Override
    public Set<ICreator> getOtherCreators(String role) {
        Set<ICreator> creators = new HashSet<>();
        if (otherCreators != null) {
            otherCreators.forEach(c -> {
                if (c.getRole().equals(role)) {
                    creators.add(c);
                }
            });
        }
        return creators;
    }
}
