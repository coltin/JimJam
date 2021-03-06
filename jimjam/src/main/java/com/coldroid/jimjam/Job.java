package com.coldroid.jimjam;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

/**
 * This is the base class used for {@link Job Jobs} you want managed by the {@link JobManager}. The constructor must
 * call into the super and pass a {@link JobParameters} which configures whether the job needs the network, should be
 * persisted to disk, and what it's priority should be for scheduling purposes.
 *
 * Jobs are saved to disk through regular Java serialization. Any fields that shouldn't be written to disk should be
 * marked `transient`.
 */
public abstract class Job implements Serializable, Comparable<Job> {
    private static long serialVersionUID = 1L;
    private final JobPriority mJobPriority;
    private final boolean mRequiresNetwork;
    private final boolean mIsPersistent;
    private final String mLabel;
    private int mRunAttempts;
    private transient long mRowId;

    public Job(@NonNull JobParameters parameters) {
        mJobPriority = parameters.jobPriority;
        mLabel = parameters.label;
        mRequiresNetwork = parameters.requiresNetwork;
        mIsPersistent = parameters.isPersistent;
        mRunAttempts = 0;
        // -1 indicates that the Job is not yet stored in a database table.
        mRowId = -1;
    }

    /**
     * The work of your Job is done here. run() will be called on its own thread. If run() finishes without exception it
     * is considered successful. A Job fails if run() throws an Exception. When a Job fails shouldRetry() will be called
     * with the attempt number and the exception that was thrown. The first failure will have mRunAttempts equal to
     * one.
     */
    protected abstract void run() throws Exception;

    /**
     * This will be called when a job fails (throws an exception). That exception a
     */
    protected abstract boolean shouldRetry(int mRunAttempts, Exception exception);

    /**
     * This is called when a job is about to be submitted to the Job Queue. Persisted jobs have been written to disk at
     * this point.
     */
    protected abstract void addedToQueue();

    @Override
    public String toString() {
        return String.format(Locale.US,
                "Job Name: %s\nJob Label: %s\nJob Fields\nmRequiresNetwork: %s\nmJobPriority: %s\nisPersistent: " +
                        "%b\nDbRowId: %d",
                getClass().getSimpleName(), mLabel, mRequiresNetwork, mJobPriority, mIsPersistent, mRowId);
    }

    @Override
    public int compareTo(@NonNull Job otherJob) {
        return mJobPriority.compareTo(otherJob.mJobPriority);
    }

    public final void incrementRuns() {
        mRunAttempts++;
    }

    /**
     * Called when a job run fails and shouldRetry() returned false. This is a hook that jobs can optionally override to
     * know when their job will not be retried anymore.
     */
    @SuppressWarnings("EmptyMethod")
    public void failedToComplete() {
        // Intentionally empty.
    }

    public final boolean isPersistent() {
        return mIsPersistent;
    }

    /**
     * Should only be called when inserted into a database table, and is used to update/delete the Job from that
     * database table.
     */
    public final void setRowIdId(long rowId) {
        mRowId = rowId;
    }

    /**
     * Returns the rowId of the Job used to updated/delete the Job from the database table. A rowId of -1 indicates the
     * job is not currently in a database table.
     */
    public final long getRowId() {
        return mRowId;
    }

    public boolean requiresNetwork() {
        return mRequiresNetwork;
    }

    /**
     * This is called by the JobManager. Jobs should @Override {@link Job#shouldRetry(int, Exception)} to handle whether
     * their jobs should retry after failure. There is no other reason to call this methods.
     */
    public final boolean shouldRetry(Exception exception) {
        return shouldRetry(mRunAttempts, exception);
    }

    public int getPriority() {
        return mJobPriority.ordinal();
    }

    public String getLabel() {
        return mLabel;
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
