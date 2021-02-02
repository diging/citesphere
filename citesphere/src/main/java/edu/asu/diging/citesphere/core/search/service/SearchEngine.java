package edu.asu.diging.citesphere.core.search.service;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;

public interface SearchEngine {

    List<ICitation> search(String searchTerm, String groupId, int page, int pageSize);

}