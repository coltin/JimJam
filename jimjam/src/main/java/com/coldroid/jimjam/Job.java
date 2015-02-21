package com.coldroid.jimjam;

import java.io.Serializable;

public abstract class Job implements Serializable {
    private final boolean mRequiresNetwork;
    private final JobPriority mJobPriority;

    public Job(JobParameters parameters) {
        mRequiresNetwork = parameters.requiresNetwork;
        mJobPriority = parameters.jobPriority;
    }

    protected abstract void run() throws Throwable;

    protected abstract void addedToQueue();
}
