package edu.asu.diging.citesphere.core.factory.zotero.impl;

import java.util.List;
import java.util.Set;

import edu.asu.diging.citesphere.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.IReference;

/**
 * Holds the additional Citesphere data for an item. This data has been moved
 * from the "extra" field to a "note" of the item.
 * 
 * @author Julia Damerow
 */
public class ExtraDataObject {

    private Set<IPerson> authors;

    private Set<IPerson> editors;
    
    private Set<ICreator> otherCreators;
    
    private Set<ICitationConceptTag> conceptTags;
    
    private Set<IReference> references;
    
    private Set<IGilesUpload> gilesUploads;
    
    private List<String> sameAs;

    public Set<ICreator> getOtherCreators() {
        return otherCreators;
    }

    public void setOtherCreators(Set<ICreator> otherCreators) {
        this.otherCreators = otherCreators;
    }

    public Set<IPerson> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<IPerson> authors) {
        this.authors = authors;
    }

    public Set<IPerson> getEditors() {
        return editors;
    }

    public void setEditors(Set<IPerson> editors) {
        this.editors = editors;
    }

    public Set<ICitationConceptTag> getConceptTags() {
        return conceptTags;
    }

    public void setConceptTags(Set<ICitationConceptTag> conceptTags) {
        this.conceptTags = conceptTags;
    }

    public Set<IReference> getReferences() {
        return references;
    }

    public void setReferences(Set<IReference> references) {
        this.references = references;
    }

    public Set<IGilesUpload> getGilesUploads() {
        return gilesUploads;
    }

    public void setGilesUploads(Set<IGilesUpload> gilesUploads) {
        this.gilesUploads = gilesUploads;
    }
    
    public List<String> getSameAs() {
        return sameAs;
    }

    public void setSameAs(List<String> sameAs) {
        this.sameAs = sameAs;
    }

}
