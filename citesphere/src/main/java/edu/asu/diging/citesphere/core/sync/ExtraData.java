package edu.asu.diging.citesphere.core.sync;

public interface ExtraData {

    public final static String CITESPHERE_PREFIX = "Citesphere:";
    
    public final static String CITESPHERE_PATTERN = "(?m)(?s)^" + CITESPHERE_PREFIX + " ?(\\{.*\\}).*?$";
    
    public final static String CITESPHERE_METADATA_TAG = "citesphere-metadata";
}
