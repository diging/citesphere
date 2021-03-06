package edu.asu.diging.citesphere.core.service.jwt;

import edu.asu.diging.citesphere.core.model.jobs.IJob;

public interface IJwtTokenService {

    String generateJobApiToken(IJob job);

    /**
     * Method to parse a JWT token. If the token has expired, the returned {@link IJobApiTokenContents}
     * will be set to expired. If the token has an incorrect signature, null is returned.
     * 
     * @param token Token to parse.
     * @return an object of {@link IJobApiTokenContents} or null if signure is invalid.
     */
    IJobApiTokenContents getJobApiTokenContents(String token);

}