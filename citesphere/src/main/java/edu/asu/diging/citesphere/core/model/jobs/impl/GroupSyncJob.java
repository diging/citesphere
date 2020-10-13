package edu.asu.diging.citesphere.core.model.jobs.impl;

import java.time.OffsetDateTime;

import javax.persistence.Entity;

import edu.asu.diging.citesphere.core.model.jobs.IGroupSyncJob;

@Entity
public class GroupSyncJob extends Job implements IGroupSyncJob {

    private String groupId;
    private long total;
    private long current;
    private OffsetDateTime finishedOn;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IGroupSyncJob#getGroupId()
     */
    @Override
    public String getGroupId() {
        return groupId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IGroupSyncJob#setGroupId(java.lang.String)
     */
    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IGroupSyncJob#getTotal()
     */
    @Override
    public long getTotal() {
        return total;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IGroupSyncJob#setTotal(long)
     */
    @Override
    public void setTotal(long total) {
        this.total = total;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IGroupSyncJob#getCurrent()
     */
    @Override
    public long getCurrent() {
        return current;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IGroupSyncJob#setCurrent(long)
     */
    @Override
    public void setCurrent(long current) {
        this.current = current;
    }
    @Override
    public OffsetDateTime getFinishedOn() {
        return finishedOn;
    }
    @Override
    public void setFinishedOn(OffsetDateTime finishedOn) {
        this.finishedOn = finishedOn;
    }  
}
