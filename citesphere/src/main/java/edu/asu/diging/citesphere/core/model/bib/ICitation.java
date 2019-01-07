package edu.asu.diging.citesphere.core.model.bib;

import java.time.OffsetDateTime;
import java.util.Set;

public interface ICitation {

    String getKey();

    void setKey(String key);

    String getTitle();

    void setTitle(String title);

    Set<IPerson> getAuthors();

    void setAuthors(Set<IPerson> authors);

    Set<IPerson> getEditors();

    void setEditors(Set<IPerson> editors);

    ItemType getItemType();

    void setItemType(ItemType itemType);

    String getPublicationTitle();

    void setPublicationTitle(String publicationTitle);

    String getVolume();

    void setVolume(String volume);

    String getIssue();

    void setIssue(String issue);

    String getPages();

    void setPages(String pages);

    OffsetDateTime getDate();

    void setDate(OffsetDateTime date);

    String getSeries();

    void setSeries(String series);

    String getSeriesTitle();

    void setSeriesTitle(String seriesTitle);

    void setDateFreetext(String dateFreetext);

    String getDateFreetext();

    void setUrl(String url);

    String getUrl();

    void setRights(String rights);

    String getRights();

    void setCallNumber(String callNumber);

    String getCallNumber();

    void setLibraryCatalog(String libraryCatalog);

    String getLibraryCatalog();

    void setArchiveLocation(String archiveLocation);

    String getArchiveLocation();

    void setArchive(String archive);

    String getArchive();

    void setShortTitle(String shortTitle);

    String getShortTitle();

    void setIssn(String issn);

    String getIssn();

    void setDoi(String doi);

    String getDoi();

    void setLanguage(String language);

    String getLanguage();

    void setJournalAbbreviation(String journalAbbreviation);

    String getJournalAbbreviation();

    void setSeriesText(String seriesText);

    String getSeriesText();

    void setAbstractNote(String abstractNote);

    String getAbstractNote();

    void setExtra(String extra);

    String getExtra();

}