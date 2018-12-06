package edu.asu.diging.citesphere.core.model.bib.impl;

import java.time.OffsetDateTime;
import java.util.List;

import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.IPerson;
import edu.asu.diging.citesphere.core.model.bib.ItemType;

public class Citation implements ICitation {

    private String key;
    private String title;
    private List<IPerson> authors;
    private List<IPerson> editors;
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
    private String abstractNote;
    
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
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getKey()
     */
    @Override
    public String getKey() {
        return key;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setKey(java.lang.String)
     */
    @Override
    public void setKey(String key) {
        this.key = key;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getTitle()
     */
    @Override
    public String getTitle() {
        return title;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getAuthors()
     */
    @Override
    public List<IPerson> getAuthors() {
        return authors;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setAuthors(java.util.List)
     */
    @Override
    public void setAuthors(List<IPerson> authors) {
        this.authors = authors;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getEditors()
     */
    @Override
    public List<IPerson> getEditors() {
        return editors;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setEditors(java.util.List)
     */
    @Override
    public void setEditors(List<IPerson> editors) {
        this.editors = editors;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getItemType()
     */
    @Override
    public ItemType getItemType() {
        return itemType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setItemType(edu.asu.diging.citesphere.core.model.bib.ItemType)
     */
    @Override
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getPublicationTitle()
     */
    @Override
    public String getPublicationTitle() {
        return publicationTitle;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setPublicationTitle(java.lang.String)
     */
    @Override
    public void setPublicationTitle(String publicationTitle) {
        this.publicationTitle = publicationTitle;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getVolume()
     */
    @Override
    public String getVolume() {
        return volume;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setVolume(java.lang.String)
     */
    @Override
    public void setVolume(String volume) {
        this.volume = volume;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getIssue()
     */
    @Override
    public String getIssue() {
        return issue;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setIssue(java.lang.String)
     */
    @Override
    public void setIssue(String issue) {
        this.issue = issue;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getPages()
     */
    @Override
    public String getPages() {
        return pages;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setPages(java.lang.String)
     */
    @Override
    public void setPages(String pages) {
        this.pages = pages;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getDate()
     */
    @Override
    public OffsetDateTime getDate() {
        return date;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setDate(java.time.OffsetDateTime)
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
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getSeries()
     */
    @Override
    public String getSeries() {
        return series;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setSeries(java.lang.String)
     */
    @Override
    public void setSeries(String series) {
        this.series = series;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#getSeriesTitle()
     */
    @Override
    public String getSeriesTitle() {
        return seriesTitle;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitation#setSeriesTitle(java.lang.String)
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
}
