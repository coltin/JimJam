package com.coldroid.jimjam;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

/**
 * Using the {@link JobManager} requires creating subclasses of Job to be added to the JobManager. You can configure
 * your job by passing the constructor a {@link JobParameters} to the constructor. Fields that are not-serializable
 * will not be persisted to disk. Mark fields that do not need to be persisted as "transient".
 */
public abstract class Job implements Serializable {
    private static long serialVersionUID = 1L;
    private final boolean mRequiresNetwork;
    private final JobPriority mJobPriority;
    private final boolean mIsPersistent;
    private transient long mRowId;

    public Job(@NonNull JobParameters parameters) {
        mRequiresNetwork = parameters.requiresNetwork;
        mJobPriority = parameters.jobPriority;
        mIsPersistent = parameters.isPersistent;
        // -1 indicates that the Job is not yet stored in a database table.
        mRowId = -1;
    }

    protected abstract void run() throws Throwable;

    protected abstract void addedToQueue();

    @Override
    public String toString() {
        return String.format(Locale.US,
                "Job Name: %s Job Fields\nmRequiresNetwork: %s\nmJobPriority: " + "%s\nisPersistent: %b\nDbRowId: %d",
                getClass().getSimpleName(), mRequiresNetwork, mJobPriority, mIsPersistent, mRowId);
    }

    public boolean isPersistent() {
        return mIsPersistent;
    }

    /**
     * Should only be called when inserted into a database table, and is used to update/delete the Job from that
     * database table.
     */
    public void setRowIdId(long rowId) {
        mRowId = rowId;
    }

    /**
     * Returns the rowId of the Job used to updated/delete the Job from the database table. A rowId of -1 indicates the
     * job is not currently in a database table.
     */
    public long getRowId() {
        return mRowId;
    }

    /**
     * This allows sub-classes to provide a unified "Job Builder". This will likely not be in the final design, but it
     * was REALLY NEAT in the jimjam-sample app to have a method which expects "Job.Builder" and can be supplied
     * implementations by sub-classes, for example "SleepForTwentyJob.Builder". Useful? I guess. Awesome? YES!
     */
    public static abstract class Builder {
        public abstract Job build();
    }
}
