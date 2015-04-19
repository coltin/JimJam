package com.coldroid.jimjam;

/**
 * Simple builder style class, allowing you to configure {@link Job Jobs}.
 */
public final class JobParameters {
    private static final JobPriority DEFAULT_JOB_PRIORITY = JobPriority.LOW;
    private static final String DEFAULT_LABEL = null;
    private static final boolean DEFAULT_REQUIRES_NETWORK = false;
    private static final boolean DEFAULT_IS_PERSISTENT = false;

    /* package */ JobPriority jobPriority = DEFAULT_JOB_PRIORITY;
    /* package */ String label = DEFAULT_LABEL;
    /* package */ boolean requiresNetwork = DEFAULT_REQUIRES_NETWORK;
    /* package */ boolean isPersistent = DEFAULT_IS_PERSISTENT;

    public JobParameters setJobPriority(JobPriority jobPriority) {
        this.jobPriority = jobPriority;
        return this;
    }

    public JobParameters setLabel(String label) {
        this.label = label;
        return this;
    }

    public JobParameters setRequiresNetwork() {
        this.requiresNetwork = true;
        return this;
    }

    public JobParameters setIsPersistent() {
        isPersistent = true;
        return this;
    }
}