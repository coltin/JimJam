package com.coldroid.jimjam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    private ExecutorService mThreadExecutor;
    private JobDatabase mJobDatabase;

    /**
     * To create the JobManager, use the {@link Builder}.
     */
    private JobManager() {
    }

    /**
     * Adds the Job to the JobQueue in a background thread.
     */
    public void addJob(final @NonNull Job job) {
        if (job.isPersistent() && job.getRowId() == -1) {
            mJobDatabase.persistJob(job);
        }
        mJobLogger.d(job.toString());

        // TODO: Should add a job to the JobQueue for processing.
        mThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    job.run();
                    if (job.isPersistent()) {
                        mJobDatabase.removeJob(job);
                    }
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
        int i = 1;
        for (Job job : mJobDatabase.fetchJobs()) {
            mJobLogger.d("Job #" + i++ + ": " + job.toString());
        }
        mJobLogger.d("------Logging Jobs in DB Done-------");
    }

    /**
     * Can cause some serious havoc and will likely not be available in the release. Use with caution!
     */
    public void dumpDatabase() {
        mJobDatabase.dumpDatabase();
    }

    private void start() {
        for (Job job : mJobDatabase.fetchJobs()) {
            addJob(job);
        }
    }

    public static class Builder {
        private final JobManager mJobManager = new JobManager();
        private final Context mContext;
        private JobSerializer mJobSerializer;

        public Builder(@NonNull Context context) {
            mContext = context.getApplicationContext();
        }

        public JobManager build() {
            if (mJobManager.mJobLogger == null) {
                mJobManager.mJobLogger = new DefaultJobManagerLogger();
            }
            if (mJobSerializer == null) {
                mJobSerializer = new DefaultJobSerializer(mJobManager.mJobLogger);
            }
            mJobManager.mJobDatabase = new JobDatabase(mContext, mJobSerializer);
            /**
             * Hard code a thread executor to 3? Boooo, so lame.
             */
            mJobManager.mThreadExecutor = Executors.newFixedThreadPool(3);
            mJobManager.start();
            return mJobManager;
        }

        public Builder customLogger(@Nullable JobManagerLogger jobLogger) {
            mJobManager.mJobLogger = jobLogger;
            return this;
        }

        public Builder customSerializer(@Nullable JobSerializer jobSerializer) {
            mJobSerializer = jobSerializer;
            return this;
        }
    }
}
