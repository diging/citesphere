package edu.asu.diging.citesphere.core.repository.bib;

import edu.asu.diging.citesphere.core.model.bib.ICitation;

public interface CustomCitationRepository {

    void detachCitation(ICitation citation);

    ICitation mergeCitation(ICitation citation);
}
