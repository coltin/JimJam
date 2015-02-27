package com.coldroid.jimjam.sample;

import com.coldroid.jimjam.Job;
import com.coldroid.jimjam.JobParameters;
import com.coldroid.jimjam.JobPriority;

/**
 * This Job will broadcast "High Priority Job complete" message when completed. Use this Job to supersede lower priority
 * jobs to test priority if you've enqueued a bunch of long running low priority Jobs see {@link SleepForTwentyJob}.
 */
public class HighPriorityJob extends Job {
    public HighPriorityJob() {
        super(new JobParameters()
                .setRequiresNetwork(false)
                .setJobPriority(JobPriority.HIGH));
    }

    @Override
    protected void run() {
        JobBroadcastReceiver.broadcastJobMessage("High Priority Job complete");
    }

    @Override
    protected boolean shouldRetry(int mRunAttempts, Exception exception) {
        return mRunAttempts <= 15;
    }

    @Override
    protected void addedToQueue() {
        // Intentionally empty.
    }

    public static class Builder extends Job.Builder {
        public Job build() {
            return new HighPriorityJob();
        }
    }
}