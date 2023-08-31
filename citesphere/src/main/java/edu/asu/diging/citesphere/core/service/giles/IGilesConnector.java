package edu.asu.diging.citesphere.core.service.giles;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import edu.asu.diging.citesphere.user.IUser;

public interface IGilesConnector {

    <T> ResponseEntity<T> sendRequest(IUser user, String endpoint, Class<T> returnType, HttpMethod httpMethod)
            throws HttpClientErrorException;

    byte[] getFile(IUser user, String fileId);
    
    ResponseEntity<String> reprocessDocument(IUser user, String documentId);
}
