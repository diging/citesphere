package edu.asu.diging.citesphere.web.user;

import edu.asu.diging.citesphere.model.bib.ICitation;

/**
 * This class binds citation, its label and icon together.
 */
public class CitationsDto {

    private ICitation citation;
    private String citationLabel;
    private String citationIcon;

    public CitationsDto(ICitation citation, String label, String icon) {
        this.citation = citation;
        this.citationLabel = label;
        this.citationIcon = icon;
    }

    public ICitation getCitation() {
        return citation;
    }

    public void setCitation(ICitation citation) {
        this.citation = citation;
    }

    public String getCitationLabel() {
        return citationLabel;
    }

    public void setCitationLabel(String citationLabel) {
        this.citationLabel = citationLabel;
    }

    public String getCitationIcon() {
        return citationIcon;
    }

    public void setCitationIcon(String citationIcon) {
        this.citationIcon = citationIcon;
    }
}