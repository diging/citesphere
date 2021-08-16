package edu.asu.diging.citesphere.core.bib.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.asu.diging.citesphere.core.bib.ICitationVersionsDao;
import edu.asu.diging.citesphere.core.model.bib.CitationVersion;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.impl.Citation;

@Repository
public class CitationVersionDao implements ICitationVersionsDao {

    @Autowired
    private Javers javers;

    private static final String GROUP_PROPERTY = "group";

    @Override
    public List<CitationVersion> getVersions(String groupId, String key, int page, int pageSize) {
        int offset = page * pageSize;
        List<CitationVersion> result = new ArrayList<>();
        List<CdoSnapshot> versions = javers.findSnapshots(QueryBuilder.byInstanceId(key, Citation.class)
                .withSnapshotTypeUpdate().skip(offset).limit(pageSize).build());
        result.addAll(versions.stream().filter(version -> version.getPropertyValue(GROUP_PROPERTY).equals(groupId))
                .map(version -> toCitationVersion(version, key)).collect(Collectors.toList()));

        // If the page is still empty and if the index of the last element of the
        // current page is less than or equal to the total version count, add the initial version
        if (result.size() < pageSize && getTotalCount(groupId, key) <= offset + pageSize) {
            CdoSnapshot initialVersion = javers
                    .findSnapshots(QueryBuilder.byInstanceId(key, Citation.class).withSnapshotType(SnapshotType.INITIAL)
                            .build())
                    .stream().filter(version -> version.getPropertyValue(GROUP_PROPERTY).equals(groupId)).findFirst()
                    .orElseGet(null);
            if (initialVersion != null) {
                result.add(toCitationVersion(initialVersion, key));
            }
        }
        return result;
    }

    @Override
    public int getTotalCount(String groupId, String key) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(key, Citation.class).withSnapshotTypeUpdate();
        return (int) javers.findSnapshots(jqlQuery.build()).stream()
                .filter(version -> version.getPropertyValue(GROUP_PROPERTY).equals(groupId)).count() + 1;
    }

    @Override
    public ICitation getVersion(String groupId, String key, long version) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(key, Citation.class).withVersion(version);
        List<Shadow<ICitation>> shadows = javers.findShadows(jqlQuery.build());
        if (!shadows.isEmpty()) {
            ICitation citation = shadows.get(0).get();
            if (citation.getGroup().equals(groupId)) {
                return citation;
            }
        }
        return null;
    }

    private CitationVersion toCitationVersion(CdoSnapshot itemSnapshot, String key) {
        CitationVersion version = new CitationVersion();
        version.setVersion(itemSnapshot.getVersion());
        version.setKey(key);
        version.setUpdatedBy(itemSnapshot.getCommitMetadata().getAuthor());
        version.setUpdatedDate(itemSnapshot.getCommitMetadata().getCommitDate().toString());
        return version;
    }

}
