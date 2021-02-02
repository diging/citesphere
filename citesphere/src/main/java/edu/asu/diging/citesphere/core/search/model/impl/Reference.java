package edu.asu.diging.citesphere.core.search.model.impl;

import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import edu.asu.diging.citesphere.model.bib.ItemType;

@Document(indexName="#{@indexName}", type = "_doc")
public class Reference {

    @Id
    private String key;
    @Field(type=FieldType.Keyword)
    private String group;
    @Field(type=FieldType.Text)
    private String title;
    private Set<Person> creators;
    @Field(type=FieldType.Keyword)
    private ItemType itemType;
    @Field(type=FieldType.Text)
    private String publicationTitle;
    @Field(type=FieldType.Keyword)
    private String volume;
    @Field(type=FieldType.Keyword)
    private String issue;
    @Field(type=FieldType.Keyword)
    private String pages;
    @Field(type=FieldType.Text)
    private String date;
    @Field(type=FieldType.Text)
    private String dateFreetext;
    @Field(type=FieldType.Text)
    private String series;
    @Field(type=FieldType.Text)
    private String seriesTitle;
    @Field(type=FieldType.Keyword)
    private String url;
    @Field(type=FieldType.Text)
    private String abstractNote;
    @Field(type=FieldType.Text)
    private String accessDate;
    @Field(type=FieldType.Text)
    private String seriesText;
    @Field(type=FieldType.Keyword)
    private String journalAbbreviation;
    @Field(type=FieldType.Keyword)
    private String language;
    @Field(type=FieldType.Keyword)
    private String doi;
    @Field(type=FieldType.Keyword)
    private String issn;
    @Field(type=FieldType.Text)
    private String shortTitle;
    @Field(type=FieldType.Text)
    private String archive;
    @Field(type=FieldType.Text)
    private String archiveLocation;
    @Field(type=FieldType.Text)
    private String libraryCatalog;
    @Field(type=FieldType.Keyword)
    private String callNumber;
    @Field(type=FieldType.Text)
    private String rights;
    private List<String> collections;
    @Field(type=FieldType.Boolean)
    private boolean deleted;
    @Field(type=FieldType.Text)
    private String dateAdded;
    @Field(type=FieldType.Text)
    private String dateModified;
    
    private Set<String> conceptTagIds;
    private Set<Tag> conceptTags;
    
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Set<Person> getCreators() {
        return creators;
    }
    public void setCreators(Set<Person> creators) {
        this.creators = creators;
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
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
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
    public String getAccessDate() {
        return accessDate;
    }
    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
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
    public List<String> getCollections() {
        return collections;
    }
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }
    public boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    public String getDateAdded() {
        return dateAdded;
    }
    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
    public String getDateModified() {
        return dateModified;
    }
    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
    public Set<String> getConceptTagIds() {
        return conceptTagIds;
    }
    public void setConceptTagIds(Set<String> conceptTagIds) {
        this.conceptTagIds = conceptTagIds;
    }
    public Set<Tag> getConceptTags() {
        return conceptTags;
    }
    public void setConceptTags(Set<Tag> conceptTags) {
        this.conceptTags = conceptTags;
    }
    
    
}
