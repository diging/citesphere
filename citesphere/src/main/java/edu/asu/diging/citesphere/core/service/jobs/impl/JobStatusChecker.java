package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.GroupSyncJobRepository;

/**
 * Service for checking job cancellation status from database with caching.
 * This reduces database load while still providing timely cancellation detection.
 */
@Service
public class JobStatusChecker {

    @Autowired
    private GroupSyncJobRepository jobRepo;

    @Value("${_job_status_cache_ttl:5000}")
    private long cacheTtl;

    private Map<String, CachedJobStatus> statusCache = new ConcurrentHashMap<>();

    private static class CachedJobStatus {
        JobStatus status;
        long timestamp;
    }

    /**
     * Check if a job has been cancelled.
     * Uses a cache with TTL to reduce database queries.
     *
     * @param jobId the job ID to check
     * @return true if the job is cancelled, false otherwise
     */
    public boolean isJobCancelled(String jobId) {
        // Check cache first
        CachedJobStatus cached = statusCache.get(jobId);
        long now = System.currentTimeMillis();

        if (cached != null && (now - cached.timestamp) < cacheTtl) {
            return cached.status == JobStatus.CANCELED;
        }

        // Query database if cache miss or expired
        Optional<GroupSyncJob> job = jobRepo.findById(jobId);
        if (job.isPresent()) {
            CachedJobStatus newCache = new CachedJobStatus();
            newCache.status = job.get().getStatus();
            newCache.timestamp = now;
            statusCache.put(jobId, newCache);
            return newCache.status == JobStatus.CANCELED;
        }

        return false;
    }

    /**
     * Invalidate the cache entry for a job.
     * This should be called when a job is explicitly cancelled to ensure
     * the async thread picks up the change immediately.
     *
     * @param jobId the job ID to invalidate
     */
    public void invalidateCache(String jobId) {
        statusCache.remove(jobId);
    }

    /**
     * Periodically clean up old cache entries to prevent memory leaks.
     * Runs every 5 minutes and removes entries older than 10x the cache TTL.
     */
    @Scheduled(fixedDelay = 300000) // Clean up every 5 minutes
    public void cleanupOldCache() {
        long threshold = System.currentTimeMillis() - (cacheTtl * 10);
        statusCache.entrySet().removeIf(entry ->
            entry.getValue().timestamp < threshold
        );
    }
}
