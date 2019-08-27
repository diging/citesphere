package edu.asu.diging.citesphere.core.service.jwt.impl;

import edu.asu.diging.citesphere.core.service.jwt.IJobApiTokenContents;

/**
 * Class that holds the information that was encoded in a token.
 * 
 * @author Julia Damerow
 *
 */
public class JobApiTokenContents implements IJobApiTokenContents {

    private String jobId;
    private boolean expired;
    
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenContents#getUsername()
     */
    @Override
    public String getJobId() {
        return jobId;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenContents#setUsername(java.lang.String)
     */
    @Override
    public void setJobId(String username) {
        this.jobId = username;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenContents#isExpired()
     */
    @Override
    public boolean isExpired() {
        return expired;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenContents#setExpired(boolean)
     */
    @Override
    public void setExpired(boolean expired) {
        this.expired = expired;
    }
    
    
}