package edu.asu.diging.citesphere.web.user.dto;

import java.util.List;

public class CitationStatusesData {
    private List<String> movedCitations;
    private List<String> notMovedCitations;
    
    public void setMovedCitations(List<String> movedCitationsList) {
        this.movedCitations = movedCitationsList;
    }
    
    public void setNotMovedCitations(List<String> notMovedCitationsList) {
        this.notMovedCitations = notMovedCitationsList;
    }
    
}
