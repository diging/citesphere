package edu.asu.diging.citesphere.api.v1.model.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.asu.diging.citesphere.core.search.model.impl.Tag;
import edu.asu.diging.citesphere.factory.ExtraData;
import edu.asu.diging.citesphere.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.IReference;
import edu.asu.diging.citesphere.model.bib.ItemType;

public class ItemWithGiles {

//    private ObjectId id;
    
    // @Indexed(unique=true)
    private String key;
    
    private String group;
    
    private long version;
    private String title;
    private String parentItem;
    private Set<IPerson> authors;
    private Set<IPerson> editors;
    
    private Set<ICreator> otherCreators;
    
    private ItemType itemType;
    private String publicationTitle;
    private String volume;
    private String issue;
    private String pages;
    private String date;
    private String dateFreetext;
    private String series;
    private String seriesTitle;
    private String url;
    private String note;
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
    private List<String> collections;
    private int deleted;
    private List<Tag> tags;
    
    private String metaDataItemKey;
    private long metaDataItemVersion;
    
    private String dateAdded;
    private String dateModified;
    
    private Set<String> conceptTagIds;
    private Set<ICitationConceptTag> conceptTags;
    
    private Set<IReference> references;
    private Set<IGilesUpload> gilesUploads;
    
    private String extra;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getKey()
     */
    public String getKey() {
        return key;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setKey(java.lang.String)
     */
    // @Override
    public void setKey(String key) {
        this.key = key;
    }
    // @Override
    public String getGroup() {
        return group;
    }
    // @Override
    public void setGroup(String group) {
        this.group = group;
    }
    // @Override
    public long getVersion() {
        return version;
    }
    // @Override
    public void setVersion(long version) {
        this.version = version;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getTitle()
     */
    // @Override
    public String getTitle() {
        return title;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setTitle(java.lang.String)
     */
    // @Override
    public void setTitle(String title) {
        this.title = title;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.ICitation#getParentItem()
     */
    public String getParentItem() {
        return parentItem;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.ICitation#setParentItem(java.lang.String)
     */
    public void setParentItem(String parentItem) {
        this.parentItem = parentItem;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getAuthors()
     */
    // @Override
    public Set<IPerson> getAuthors() {
        return authors;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setAuthors(java.util.List)
     */
    // @Override
    public void setAuthors(Set<IPerson> authors) {
        // If we don't do the following, hibernate will complaining during update.
        if (this.authors != null) {
            this.authors.clear();
            this.authors.addAll(authors);
            return;
        }
        this.authors = authors;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getEditors()
     */
    // @Override
    public Set<IPerson> getEditors() {
        return editors;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setEditors(java.util.List)
     */
    // @Override
    public void setEditors(Set<IPerson> editors) {
        // If we don't do the following, hibernate will complaining during update.
        if (this.editors != null) {
            this.editors.clear();
            this.editors.addAll(editors);
            return;
        }
        this.editors = editors;
    }
    // @Override
    public Set<ICreator> getOtherCreators() {
        return otherCreators;
    }
    // @Override
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
    // @Override
    public void setOtherCreators(Set<ICreator> otherCreators) {
        // If we don't do the following, hibernate will complaining during update.
        if (this.otherCreators != null) {
            this.otherCreators.clear();
            this.otherCreators.addAll(otherCreators);
            return; 
        }
        this.otherCreators = otherCreators;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getItemType()
     */
    // @Override
    public ItemType getItemType() {
        return itemType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setItemType(edu.asu.diging.citesphere.core.model.bib.ItemType)
     */
    // @Override
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getPublicationTitle()
     */
    // @Override
    public String getPublicationTitle() {
        return publicationTitle;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setPublicationTitle(java.lang.String)
     */
    // @Override
    public void setPublicationTitle(String publicationTitle) {
        this.publicationTitle = publicationTitle;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getVolume()
     */
    // @Override
    public String getVolume() {
        return volume;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setVolume(java.lang.String)
     */
    // @Override
    public void setVolume(String volume) {
        this.volume = volume;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getIssue()
     */
    // @Override
    public String getIssue() {
        return issue;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setIssue(java.lang.String)
     */
    // @Override
    public void setIssue(String issue) {
        this.issue = issue;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getPages()
     */
    // @Override
    public String getPages() {
        return pages;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setPages(java.lang.String)
     */
    // @Override
    public void setPages(String pages) {
        this.pages = pages;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getDate()
     */
    // @Override
    public String getDate() {
        return date;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setDate(java.time.OffsetDateTime)
     */
    // @Override
    public void setDate(String date) {
        this.date = date;
    }
    // @Override
    public String getDateFreetext() {
        return dateFreetext;
    }
    // @Override
    public void setDateFreetext(String dateFreetext) {
        this.dateFreetext = dateFreetext;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getSeries()
     */
    // @Override
    public String getSeries() {
        return series;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setSeries(java.lang.String)
     */
    // @Override
    public void setSeries(String series) {
        this.series = series;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getSeriesTitle()
     */
    // @Override
    public String getSeriesTitle() {
        return seriesTitle;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setSeriesTitle(java.lang.String)
     */
    // @Override
    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }
    // @Override
    public String getUrl() {
        return url;
    }
    // @Override
    public void setUrl(String url) {
        this.url = url;
    }
    // @Override
    public String getNote() {
        return note;
    }
    // @Override
    public void setNote(String note) {
        this.note = note;
    }
    // @Override
    public String getAbstractNote() {
        return abstractNote;
    }
    // @Override
    public void setAbstractNote(String abstractNote) {
        this.abstractNote = abstractNote;
    }
    // @Override
    public String getAccessDate() {
        return accessDate;
    }
    // @Override
    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }
    // @Override
    public String getSeriesText() {
        return seriesText;
    }
    // @Override
    public void setSeriesText(String seriesText) {
        this.seriesText = seriesText;
    }
    // @Override
    public String getJournalAbbreviation() {
        return journalAbbreviation;
    }
    // @Override
    public void setJournalAbbreviation(String journalAbbreviation) {
        this.journalAbbreviation = journalAbbreviation;
    }
    // @Override
    public String getLanguage() {
        return language;
    }
    // @Override
    public void setLanguage(String language) {
        this.language = language;
    }
    // @Override
    public String getDoi() {
        return doi;
    }
    // @Override
    public void setDoi(String doi) {
        this.doi = doi;
    }
    // @Override
    public String getIssn() {
        return issn;
    }
    // @Override
    public void setIssn(String issn) {
        this.issn = issn;
    }
    // @Override
    public String getShortTitle() {
        return shortTitle;
    }
    // @Override
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }
    // @Override
    public String getArchive() {
        return archive;
    }
    // @Override
    public void setArchive(String archive) {
        this.archive = archive;
    }
    // @Override
    public String getArchiveLocation() {
        return archiveLocation;
    }
    // @Override
    public void setArchiveLocation(String archiveLocation) {
        this.archiveLocation = archiveLocation;
    }
    // @Override
    public String getLibraryCatalog() {
        return libraryCatalog;
    }
    // @Override
    public void setLibraryCatalog(String libraryCatalog) {
        this.libraryCatalog = libraryCatalog;
    }
    // @Override
    public String getCallNumber() {
        return callNumber;
    }
    // @Override
    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }
    // @Override
    public String getRights() {
        return rights;
    }
    // @Override
    public void setRights(String rights) {
        this.rights = rights;
    }
    // @Override
    public List<String> getCollections() {
        return collections;
    }
    // @Override
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }
    // @Override
    public int getDeleted() {
        return deleted;
    }
    // @Override
    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
    // @Override
    public List<Tag> getTags() {
        return tags;
    }
    // @Override
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
    // @Override
    public String getDateAdded() {
        return dateAdded;
    }
    // @Override
    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
    // @Override
    public String getDateModified() {
        return dateModified;
    }
    // @Override
    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
    
    // @Override
    public Set<String> getConceptTagIds() {
        return conceptTagIds;
    }
    // @Override
    public void setConceptTagIds(Set<String> conceptTagIds) {
        this.conceptTagIds = conceptTagIds;
    }
    // @Override
    public Set<ICitationConceptTag> getConceptTags() {
        return conceptTags;
    }
    // @Override
    public void setConceptTags(Set<ICitationConceptTag> concepts) {
        this.conceptTagIds = concepts.stream().map(c -> c.getLocalConceptId()).collect(Collectors.toSet());
        this.conceptTags = concepts;
    }
    // @Override
    public Set<IReference> getReferences() {
        return references;
    }
    // @Override
    public void setReferences(Set<IReference> references) {
        this.references = references;
    }
    // @Override
    public Set<IGilesUpload> getGilesUploads() {
        return gilesUploads;
    }
    // @Override
    public void setGilesUploads(Set<IGilesUpload> gilesUploads) {
        this.gilesUploads = gilesUploads;
    }
    // @Override
    public String getExtra() {
        return extra;
    }
    // @Override
    public void setExtra(String extra) {
        this.extra = extra;
    }
    
    // @Override
    public Set<String> getOtherCreatorRoles(){
        Set<String> roles = new HashSet<>();
        if (otherCreators != null) {
            otherCreators.forEach(c -> roles.add(c.getRole()));
        }
        return roles;
    }
    // @Override
    public String getMetaDataItemKey() {
        return metaDataItemKey;
    }
    // @Override
    public void setMetaDataItemKey(String metaDataItemKey) {
        this.metaDataItemKey = metaDataItemKey;
    }
    // @Override
    public long getMetaDataItemVersion() {
        return metaDataItemVersion;
    }
    // @Override
    public void setMetaDataItemVersion(long metaDataItemVersion) {
        this.metaDataItemVersion = metaDataItemVersion;
    }

    // @Override
//    public boolean isMetaDataNote() {
//        if (this.itemType != null && this.itemType.equals(ItemType.NOTE) && this.tags != null
//                && this.tags.stream().anyMatch(tag -> tag.getTag().equals(ExtraData.CITESPHERE_METADATA_TAG))) {
//            return true;
//        }
//        return false;
//    }

}