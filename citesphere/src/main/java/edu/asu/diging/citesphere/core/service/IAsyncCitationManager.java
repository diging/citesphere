package edu.asu.diging.citesphere.core.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.social.zotero.exception.ZoteroConnectionException;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.impl.async.AsyncDeleteCitationsResponse;
import edu.asu.diging.citesphere.core.service.impl.async.AsyncUpdateCitationsResponse;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

/**
 * Responsible for managing the state of async citation tasks.
 * @author Maulik Limbadiya
 *
 */
public interface IAsyncCitationManager {

    /**
     * This method updates citations asynchronously.
     * 
     * @param user          User accessing Zotero
     * @param zoteroGroupId GroupId of the citations
     * @param citations     citations that have to be updated
     * @return AsyncUpdateCitationsResponse contains task id, response and status.
     */
    public AsyncUpdateCitationsResponse updateCitations(IUser user, String zoteroGroupId, List<ICitation> citations) throws JsonProcessingException, ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException, InterruptedException, ExecutionException;

    /**
     * This method gets the response of an update citations request by giving task
     * id. The task id is generated when a new async task is submitted using
     * updateCitations() method in this class
     * 
     * @param taskId id of the task
     * @return AsyncUpdateCitationsResponse returns task status (complete or
     *         pending), task id and task response
     */
    public AsyncUpdateCitationsResponse getUpdateCitationsResponse(String taskId)
            throws ExecutionException, InterruptedException;

    /**
     * If you no longer need a task in memory, use this method to remove that task
     * to free memory. Once the task is removed, its corresponding result can no
     * longer be retrieved.
     * 
     * @param taskId id of the task that is not needed in memory
     */
    public void clearUpdateTask(String taskId);

    /**
     * Deletes Citations asynchronously
     * 
     * @param user           User accessing Zotero
     * @param groupId        GroupId of the Citations
     * @param citationIdList List of the Citations to be deleted
     * @return AsyncDeleteCitationsResponse containing taskId and its current status
     */
    public AsyncDeleteCitationsResponse deleteCitations(IUser user, String groupId, List<String> citationIdList)
            throws ZoteroConnectionException, ZoteroHttpStatusException;

    /**
     * Gets the current status of the deletion task. If the task is completed, it
     * returns the status for each Citation which was requested to be deleted.
     * 
     * @param taskId Id of the task
     * @return AsyncDeleteCitationsResponse containing task id, task status, and
     *         response if the task is completed
     */
    public AsyncDeleteCitationsResponse getDeleteCitationsResponse(String taskId)
            throws ExecutionException, InterruptedException;

}
