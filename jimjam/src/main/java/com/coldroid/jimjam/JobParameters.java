package com.coldroid.jimjam;

public final class JobParameters {
    private static final JobPriority DEFAULT_JOB_PRIORITY = JobPriority.LOW;
    private static final boolean DEFAULT_REQUIRES_NETWORK = false;
    private static final boolean DEFAULT_IS_PERSISTENT = false;

    /* package */ JobPriority jobPriority = DEFAULT_JOB_PRIORITY;
    /* package */ boolean requiresNetwork = DEFAULT_REQUIRES_NETWORK;
    /* package */ boolean isPersistent = DEFAULT_IS_PERSISTENT;

    public JobParameters setRequiresNetwork(boolean requiresNetwork) {
        this.requiresNetwork = requiresNetwork;
        return this;
    }

    public JobParameters setJobPriority(JobPriority jobPriority) {
        this.jobPriority = jobPriority;
        return this;
    }

    public JobParameters setIsPersistent() {
        isPersistent = true;
        return this;
    }
}