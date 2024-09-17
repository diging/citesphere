package edu.asu.diging.citesphere.core.util;

import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.model.bib.IGilesUpload;

/**
 * Interface for utilities related to Giles upload jobs.
 */
public interface IGilesUtil {

    /**
     * Creates a JSON node representing a Giles upload job and adds it to the specified root node.
     */
    void createJobObjectNode(ObjectNode root, IGilesUpload job);
}