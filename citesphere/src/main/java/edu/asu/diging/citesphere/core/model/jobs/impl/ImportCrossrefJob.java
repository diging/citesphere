package edu.asu.diging.citesphere.core.model.jobs.impl;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import edu.asu.diging.citesphere.core.model.jobs.IImportCrossrefJob;

@Entity
public class ImportCrossrefJob extends Job implements IImportCrossrefJob {

    @ElementCollection
    private List<String> dois;

    /**
     * Get the DOIs of the resources to be imported from Crossref.
     * @return list of resources to be imported
     */
    @Override
    public List<String> getDois() {
        return dois;
    }

    @Override
    public void setDois(List<String> dois) {
        this.dois = dois;
    }
    
    
}
