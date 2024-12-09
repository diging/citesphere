package edu.asu.diging.citesphere.core.service.crossref;

import java.io.IOException;
import java.util.List;

import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.Item;

public interface CrossrefService {

    List<Item> search(String query, int page) throws RequestFailedException, IOException;

}