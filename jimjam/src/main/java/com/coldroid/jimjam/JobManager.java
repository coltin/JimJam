package com.coldroid.jimjam;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The following comment is <s>probably</s> definitely LIES AND DECEIT. I'm writing what it WILL support as if it's
 * already supported . SO TRICKY!
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
    private JobSerializer mJobSerializer;
    private ExecutorService mThreadExecutor;

    /**
     * To create the JobManager, use the {@link Builder}.
     */
    private JobManager() {
    }

    /**
     * Adds the Job to the JobQueue in a background thread.
     */
    public void addJob(final @NonNull Job job) {
        mJobLogger.d(job.toString());
        // TODO: Should add a job to the JobQueue for processing.
        mThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    job.run();
                } catch (Throwable throwable) {
                    // Intentionally empty.
                }
            }
        });
        mJobLogger.d("Job added to executor");
    }

    /**
     * Prints some debugging information to logcat. Will not be in the release version.
     */
    public void logDb() {
        mJobLogger.d("------Logging Jobs in DB Start------");
        // TODO: Log whatever jobs are stored in the DB here, one per line.
        mJobLogger.d("------Logging Jobs in DB Done-------");
    }

    public static class Builder {
        private JobManager mJobManager = new JobManager();

        public JobManager build() {
            if (mJobManager.mJobLogger == null) {
                mJobManager.mJobLogger = new DefaultJobManagerLogger();
            }
            if (mJobManager.mJobSerializer == null) {
                mJobManager.mJobSerializer = new DefaultJobSerializer(mJobManager.mJobLogger);
            }
            /**
             * Hard code a thread executor to 3? Boooo, so lame.
             */
            mJobManager.mThreadExecutor = Executors.newFixedThreadPool(3);
            return mJobManager;
        }

        public Builder customLogger(JobManagerLogger jobLogger) {
            mJobManager.mJobLogger = jobLogger;
            return this;
        }

        public Builder customSerializer(JobSerializer jobSerializer) {
            mJobManager.mJobSerializer = jobSerializer;
            return this;
        }
    }
}
