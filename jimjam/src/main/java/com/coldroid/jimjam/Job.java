package com.coldroid.jimjam;

import java.io.Serializable;

public abstract class Job implements Serializable {
    private final boolean mRequiresNetwork;
    private final JobPriority mJobPriority;
    private final boolean mIsPersistent;

    public Job(JobParameters parameters) {
        mRequiresNetwork = parameters.requiresNetwork;
        mJobPriority = parameters.jobPriority;
        mIsPersistent = parameters.isPersistent;
    }

    protected abstract void run() throws Throwable;

    protected abstract void addedToQueue();

    @Override
    public String toString() {
        return "Job Name: " + getClass().getSimpleName() + " Job Fields\nmRequiresNetwork: " + mRequiresNetwork + "\nmJobPriority: " + mJobPriority.name();

    public boolean isPersistent() {
        return mIsPersistent;
    }
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
