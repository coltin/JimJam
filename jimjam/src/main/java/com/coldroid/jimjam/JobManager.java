package com.coldroid.jimjam;

import android.content.Context;
import android.support.annotation.NonNull;

import com.coldroid.jimjam.NetworkBroadcastReceiver.NetworkStateListener;
import com.coldroid.jimjam.queue.LabelledRunnable;

import java.util.List;

/**
 * The JobManager processes and cares for {@link Job Jobs}. Jobs are discrete units of work that may take a lot of time
 * to process, or things you want to have some guarantee will happen. If the Android system kills your app, it crashes,
 * or the phone reboots, the JobManager can be configured to persist these jobs to disk before they "run", allowing them
 * to be restarted the next time you initialize your JobManager.
 */
public class JobManager {
    private JobManagerThread mJobManagerThread;
    private PriorityThreadPoolExecutor mJobExecutor;
    private JobDatabase mJobDatabase;
    private NetworkUtils mNetworkUtils;
    private JobLogger mJobLogger;

    /**
     * To create the JobManager, use the {@link Builder}.
     */
    private JobManager() {
    }

    /**
     * Queues the {@link Job} to be executed. If the {@link Job} is persistent then it will be written to disk first.
     *
     * Calls to this method are async.
     */
    public void addJob(final @NonNull Job job) {
        mJobManagerThread.post(new Runnable() {
            @Override
            public void run() {
                addJobBackground(job);
            }
        });
    }

    /**
     * Prints some debugging information to logcat. Will not be in the release version.
     */
    public void logDatabaseJobs() {
        mJobLogger.d("------Logging Jobs in DB Start------");
        int i = 1;
        for (Job job : mJobDatabase.fetchJobs(false)) {
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

    /**
     * This will be called when the JobManager is built. It will fetch jobs from disk and add them to the
     * mPriorityJobExecutor.
     *
     * Calls to this method are async.
     */
    private void start() {
        mJobManagerThread.post(new Runnable() {
            @Override
            public void run() {
                NetworkBroadcastReceiver.registerListener(mNetworkStateListener);
                for (Job job : mJobDatabase.fetchJobs(true)) {
                    addJobBackground(job);
                }
            }
        });
    }

    /**
     * Adds the Job to the mJobExecutor. Will store the Job to disk first if it's set to be persistent.
     *
     * This method assumes it's being called from a background thread.
     */
    private void addJobBackground(@NonNull Job job) {
        if (job.isPersistent() && job.getRowId() == -1) {
            mJobDatabase.persistJob(job);
            job.addedToQueue();
        }
        mJobExecutor.execute(new RunnableJob(job));
        mJobLogger.d("Job queued in priority executor");
    }

    private final NetworkStateListener mNetworkStateListener = new NetworkStateListener() {
        /**
         * This broadcast will be received on the main UI thread, so we propagate this to the background thread
         * because {@link PriorityThreadPoolExecutor#networkConnected()} can block or potentially take up our
         * precious CPU time.
         */
        @Override
        public void networkConnected() {
            mJobManagerThread.post(new Runnable() {
                @Override
                public void run() {
                    mJobLogger.d("Received 'network connected' event, posting this to background thread");
                    List<Job> temporaryJobList = mJobExecutor.networkConnected();
                    for (Job job : temporaryJobList) {
                        addJobBackground(job);
                    }
                }
            });
        }
    };

    /**
     * This class wraps jobs and allows them to be scheduled/run by mJobExecutor.
     */
    private class RunnableJob implements LabelledRunnable<RunnableJob> {
        private final Job mJob;

        public RunnableJob(@NonNull Job job) {
            mJob = job;
        }

        @Override
        public void run() {
            try {
                if (mJobExecutor.rescheduleNetworkJob(mNetworkUtils, mJob)) {
                    mJobLogger.d("Network was down trying to run network job, rescheduled.");
                    // We short circuit because the job has been scheduled for later execution.
                    return;
                }
                mJob.incrementRuns();
                mJobLogger.d("Attempting to run job");
                mJob.run();
                mJobLogger.d("Job run() complete");
                jobSuccess();
            } catch (Exception exception) {
                mJobLogger.e("Job failed to execute", exception);
                jobFailedWithException(exception);
            }
        }

        @Override
        public String getLabel() {
            return mJob.getLabel();
        }

        @Override
        public int compareTo(@NonNull RunnableJob otherRunnable) {
            return mJob.compareTo(otherRunnable.mJob);
        }

        /**
         * Called when the job has successfully run to completion.
         */
        private void jobSuccess() {
            mJobDatabase.removeJob(mJob);
        }

        /**
         * Called when the job failed to run and threw an exception. This will reschedule the job if necessary (defined
         * by the {@link Job#shouldRetry(int, Exception)}.
         */
        private void jobFailedWithException(Exception exception) {
            if (mJob.shouldRetry(exception)) {
                if (mJob.isPersistent()) {
                    mJobDatabase.updateJob(mJob);
                }
                mJobExecutor.execute(new RunnableJob(mJob));
            } else {
                mJobDatabase.removeJob(mJob);
                mJob.failedToComplete();
            }
        }
    }

    public static class Builder {
        private final JobManager mJobManager = new JobManager();
        private final Context mContext;
        private JobSerializer mJobSerializer;
        private int mMinPoolSize = 0;
        private int mMaxPoolSize = 3;
        private long mThreadKeepAliveMillis = 500L;

        public Builder(@NonNull Context context) {
            mContext = context.getApplicationContext();
        }

        public JobManager build() {
            mJobManager.mJobManagerThread = new JobManagerThread("job_manager_thread");
            mJobManager.mJobManagerThread.start();
            if (mJobManager.mJobLogger == null) {
                mJobManager.mJobLogger = new DefaultJobLogger();
            }
            if (mJobSerializer == null) {
                mJobSerializer = new DefaultJobSerializer(mJobManager.mJobLogger);
            }
            mJobManager.mNetworkUtils = new NetworkUtils(mContext);
            mJobManager.mJobDatabase = new JobDatabase(mContext, mJobSerializer);
            mJobManager.mJobExecutor =
                    new PriorityThreadPoolExecutor(mMinPoolSize, mMaxPoolSize, mThreadKeepAliveMillis);
            // Block until the looper is setup in the background thread.
            mJobManager.mJobManagerThread.getLooper();
            mJobManager.start();
            return mJobManager;
        }

        /**
         * @param minPoolSize When there are no jobs running, the number of idle threads in the thread pool will remain
         * at this level or higher. The default is 0.
         * @param maxPoolSize This is how many concurrent jobs will execute at one time. The default is 3.
         * @param threadKeepAliveMillis How long idle threads are kept around before being killed, down to the
         * minPoolSize. Default value is 500.
         */
        public Builder configureExecutor(int minPoolSize, int maxPoolSize, long threadKeepAliveMillis) {
            mMinPoolSize = minPoolSize;
            mMaxPoolSize = maxPoolSize;
            mThreadKeepAliveMillis = threadKeepAliveMillis;
            return this;
        }

        public Builder customLogger(JobLogger jobLogger) {
            mJobManager.mJobLogger = jobLogger;
            return this;
        }

        public Builder customSerializer(JobSerializer jobSerializer) {
            mJobSerializer = jobSerializer;
            return this;
        }
    }
}
