package com.coldroid.jimjam;

import android.support.annotation.NonNull;

/**
 * The following comment is probably LIES AND DECEIT. I'm writing what it WILL support as if it's already supported. SO
 * TRICKY!
 *
 * The JobManager can be used to manage various kinds of {@link Job}'s that you will create. These Jobs are discrete
 * units of work that may take a lot of time to process, or things you want to have some guarantee will happen. If the
 * Android system kills your app, it crashes, or the phone reboots, the JobManager can be configured to persist these
 * jobs to disk before they are started. Persisted Jobs will be restarted once your app starts the JobManager until it
 * finishes or runs out of retries.
 *
 * Private member fields in your {@link Job} that are Serializable will auto-magically be written to disk if you
 * configure it to be persistent.
 */
public class JobManager {
    private JobManagerLogger mJobLogger;

    /**
     * To create the JobManager, use the {@link Builder}.
     */
    private JobManager() {
    }

    /**
     * Adds the Job to the JobQueue in a background thread.
     */
    public void addJob(@NonNull Job job) {
        // TODO: Should add a job to the JobQueue for processing.
    }

    public static class Builder {
        private JobManager mJobManager = new JobManager();

        public JobManager build() {
            if (mJobManager.mJobLogger == null) {
                mJobManager.mJobLogger = new DefaultJobManagerLogger();
            }
            return mJobManager;
        }

        public Builder customLogger(JobManagerLogger jobLogger) {
            mJobManager.mJobLogger = jobLogger;
            return this;
        }
    }
}
