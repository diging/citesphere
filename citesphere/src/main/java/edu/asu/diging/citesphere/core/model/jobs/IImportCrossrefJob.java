package edu.asu.diging.citesphere.core.model.jobs;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitationGroup;

public interface IImportCrossrefJob extends IJob {

    /**
     * Get the DOIs of the resources to be imported from Crossref.
     * @return list of resources to be imported
     */
    List<String> getDois();

    void setDois(List<String> dois);
    
    void setCitationGroup(String citationGroup);

    String getCitationGroup();

    ICitationGroup getCitationGroupDetail();

    void setCitationGroupDetail(ICitationGroup citationGroupDetail);


}