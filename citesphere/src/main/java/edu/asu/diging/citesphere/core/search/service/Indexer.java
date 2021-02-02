package edu.asu.diging.citesphere.core.search.service;

import edu.asu.diging.citesphere.model.bib.ICitation;

public interface Indexer {

    void indexCitation(ICitation citation);

}