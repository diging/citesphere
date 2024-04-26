package edu.asu.diging.citesphere.core.model.jobs.impl;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import edu.asu.diging.citesphere.core.model.jobs.IImportCrossrefJob;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;

@Entity
public class ImportCrossrefJob extends Job implements IImportCrossrefJob {

    @ElementCollection
    private List<String> dois;
    private String citationGroup;
    @Transient
    private ICitationGroup citationGroupDetail;

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

    @Override
    public String getCitationGroup() {
        return citationGroup;
    }
    
    @Override
    public void setCitationGroup(String citationGroup) {
        this.citationGroup = citationGroup;
    }
    
    @Override
    public ICitationGroup getCitationGroupDetail() {
        return citationGroupDetail;
    }
    
    @Override
    public void setCitationGroupDetail(ICitationGroup citationGroupDetail) {
        this.citationGroupDetail = citationGroupDetail;
    }
    

    
    
}
