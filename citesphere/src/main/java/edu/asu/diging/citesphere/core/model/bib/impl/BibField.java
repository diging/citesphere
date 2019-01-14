package edu.asu.diging.citesphere.core.model.bib.impl;

import edu.asu.diging.citesphere.core.model.bib.IBibField;

public class BibField implements IBibField {

    private String filename;
    private String label;
    
    public BibField() {}
    
    public BibField(String filename, String label) {
        super();
        this.filename = filename;
        this.label = label;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IBibField#getFilename()
     */
    @Override
    public String getFilename() {
        return filename;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IBibField#setFilename(java.lang.String)
     */
    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IBibField#getLabel()
     */
    @Override
    public String getLabel() {
        return label;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IBibField#setLabel(java.lang.String)
     */
    @Override
    public void setLabel(String label) {
        this.label = label;
    }
}
