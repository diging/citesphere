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
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(key, Citation.class);
        List<CdoSnapshot> versions = javers.findSnapshots(jqlQuery.build()).stream()
                .filter(snapshot -> snapshot.getType() != SnapshotType.TERMINAL
                        && groupId.equals((String) snapshot.getPropertyValue(GROUP_PROPERTY)))
                .collect(Collectors.toList());
        int offset = page * pageSize;
        if (offset >= versions.size()) {
            return new ArrayList<>();
        }
        List<CitationVersion> itemVersions = versions.subList(offset, Math.min(offset + pageSize, versions.size()))
                .stream().map(snapshot -> toCitationVersion(snapshot, key)).collect(Collectors.toList());
        return itemVersions;
    }

    @Override
    public int getTotalCount(String groupId, String key) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(key, Citation.class);
        return javers.findSnapshots(jqlQuery.build()).stream()
                .filter(snapshot -> snapshot.getType() != SnapshotType.TERMINAL
                        && groupId.equals((String) snapshot.getPropertyValue(GROUP_PROPERTY)))
                .collect(Collectors.toList()).size();

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
