package edu.asu.diging.citesphere.core.util;

import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.model.bib.IGilesUpload;

/**
 * Interface for utilities related to Giles upload jobs.
 */
public interface IGilesUtil {

    /**
     * Creates a JSON node representing a Giles upload job and adds it to the specified root node.
     *
     * @param root the root {@link ObjectNode} to which the job object will be attached
     * @param job the {@link IGilesUpload} object containing job information to be represented in JSON form
     */
    void createJobObjectNode(ObjectNode root, IGilesUpload job);
}