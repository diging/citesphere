package edu.asu.diging.citesphere.core.model.bib;

import java.util.Set;

public interface IReference {

    String getAuthorString();

    void setAuthorString(String authorString);

    Set<ICreator> getContributors();

    void setContributors(Set<ICreator> contributors);

    String getTitle();

    void setTitle(String title);

    String getYear();

    void setYear(String year);

    String getIdentifier();

    void setIdentifier(String identifier);

    String getIdentifierType();

    void setIdentifierType(String identifierType);

    String getFirstPage();

    void setFirstPage(String firstPage);

    String getEndPage();

    void setEndPage(String endPage);

    String getVolume();

    void setVolume(String volume);

    String getSource();

    void setSource(String source);

    String getReferenceId();

    void setReferenceId(String referenceId);

    String getReferenceLabel();

    void setReferenceLabel(String referenceLabel);

    String getPublicationType();

    void setPublicationType(String publicationType);

    String getCitationId();

    void setCitationId(String citationId);

    String getReferenceString();

    void setReferenceString(String referenceString);

    String getReferenceStringRaw();

    void setReferenceStringRaw(String referenceStringRaw);

}