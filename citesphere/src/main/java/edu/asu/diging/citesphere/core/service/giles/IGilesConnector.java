package edu.asu.diging.citesphere.core.service.giles;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import edu.asu.diging.citesphere.user.IUser;

public interface IGilesConnector {

    <T> ResponseEntity<T> sendRequest(IUser user, String endpoint, Class<T> returnType, HttpMethod httpMethod)
            throws HttpClientErrorException;

    byte[] getFile(IUser user, String fileId);
    
    /**
      * Deletes a document for the given user.
      * @param user The user performing the document deletion.
      * @param documentId The ID of the document to be deleted.
      * @return The HTTP status code indicating the success or failure of the delete operation.
      * @throws RestClientException if there is an issue sending the delete request to the server.
    */
    HttpStatus deleteDocument(IUser user, String documentId);
}
