package edu.asu.diging.citesphere.core.service.jwt;

public interface IJobApiTokenContents {

    public abstract String getJobId();

    public abstract void setJobId(String username);

    public abstract boolean isExpired();

    public abstract void setExpired(boolean expired);

}