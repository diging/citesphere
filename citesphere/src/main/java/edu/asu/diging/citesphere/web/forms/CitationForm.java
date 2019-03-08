package edu.asu.diging.citesphere.web.forms;

import java.util.List;

import edu.asu.diging.citesphere.core.model.bib.ItemType;

public class CitationForm {

    private String key;
    private String title;
    private ItemType itemType;
    private String publicationTitle;
    private String volume;
    private String issue;
    private String pages;
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
    
    private List<PersonForm> authors;
    private List<PersonForm> editors;
    
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public ItemType getItemType() {
        return itemType;
    }
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    public String getPublicationTitle() {
        return publicationTitle;
    }
    public void setPublicationTitle(String publicationTitle) {
        this.publicationTitle = publicationTitle;
    }
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    public String getIssue() {
        return issue;
    }
    public void setIssue(String issue) {
        this.issue = issue;
    }
    public String getPages() {
        return pages;
    }
    public void setPages(String pages) {
        this.pages = pages;
    }
    public String getDateFreetext() {
        return dateFreetext;
    }
    public void setDateFreetext(String dateFreetext) {
        this.dateFreetext = dateFreetext;
    }
    public String getSeries() {
        return series;
    }
    public void setSeries(String series) {
        this.series = series;
    }
    public String getSeriesTitle() {
        return seriesTitle;
    }
    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getAbstractNote() {
        return abstractNote;
    }
    public void setAbstractNote(String abstractNote) {
        this.abstractNote = abstractNote;
    }
    public String getSeriesText() {
        return seriesText;
    }
    public void setSeriesText(String seriesText) {
        this.seriesText = seriesText;
    }
    public String getJournalAbbreviation() {
        return journalAbbreviation;
    }
    public void setJournalAbbreviation(String journalAbbreviation) {
        this.journalAbbreviation = journalAbbreviation;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getDoi() {
        return doi;
    }
    public void setDoi(String doi) {
        this.doi = doi;
    }
    public String getIssn() {
        return issn;
    }
    public void setIssn(String issn) {
        this.issn = issn;
    }
    public String getShortTitle() {
        return shortTitle;
    }
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }
    public String getArchive() {
        return archive;
    }
    public void setArchive(String archive) {
        this.archive = archive;
    }
    public String getArchiveLocation() {
        return archiveLocation;
    }
    public void setArchiveLocation(String archiveLocation) {
        this.archiveLocation = archiveLocation;
    }
    public String getLibraryCatalog() {
        return libraryCatalog;
    }
    public void setLibraryCatalog(String libraryCatalog) {
        this.libraryCatalog = libraryCatalog;
    }
    public String getCallNumber() {
        return callNumber;
    }
    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }
    public String getRights() {
        return rights;
    }
    public void setRights(String rights) {
        this.rights = rights;
    }
    public List<PersonForm> getAuthors() {
        return authors;
    }
    public void setAuthors(List<PersonForm> authors) {
        this.authors = authors;
    }
    public List<PersonForm> getEditors() {
        return editors;
    }
    public void setEditors(List<PersonForm> editors) {
        this.editors = editors;
    }
}
