package edu.asu.diging.citesphere.core.util.model.impl;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.util.model.ICitationHelper;
import edu.asu.diging.citesphere.web.forms.CitationForm;

@Component
public class CitationHelper implements ICitationHelper {

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.util.model.impl.ICitationHelper#updateCitation(edu.asu.diging.citesphere.core.model.bib.ICitation, edu.asu.diging.citesphere.web.forms.CitationForm)
     */
    @Override
    public void updateCitation(ICitation citation, CitationForm form) {
        citation.setAbstractNote(form.getAbstractNote());
        citation.setArchive(form.getArchive());
        citation.setArchiveLocation(form.getArchiveLocation());
        citation.setCallNumber(form.getCallNumber());
        citation.setDateFreetext(form.getDateFreetext());
        citation.setDoi(form.getDoi());
        citation.setIssn(form.getIssn());
        citation.setIssue(form.getIssue());
        citation.setItemType(form.getItemType());
        citation.setJournalAbbreviation(form.getJournalAbbreviation());
        citation.setLanguage(form.getLanguage());
        citation.setLibraryCatalog(form.getLibraryCatalog());
        citation.setPages(form.getPages());
        citation.setPublicationTitle(form.getPublicationTitle());
        citation.setRights(form.getRights());
        citation.setSeries(form.getSeries());
        citation.setSeriesText(form.getSeriesText());
        citation.setSeriesTitle(form.getSeriesTitle());
        citation.setShortTitle(form.getShortTitle());
        citation.setTitle(form.getTitle());
        citation.setUrl(form.getUrl());
        citation.setVolume(form.getVolume());
    }
}
