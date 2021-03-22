package edu.asu.diging.citesphere.core.model.jobs;

import java.time.OffsetDateTime;

public interface IGroupSyncJob {

    String getGroupId();

    void setGroupId(String groupId);

    long getTotal();

    void setTotal(long total);

    long getCurrent();

    void setCurrent(long current);

    void setFinishedOn(OffsetDateTime finishedOn);

    OffsetDateTime getFinishedOn();

}