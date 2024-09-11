package edu.asu.diging.citesphere.core.util;

import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.model.bib.IGilesUpload;

public interface IGilesUtil {

    void createJobObjectNode(ObjectNode root, IGilesUpload job);
}