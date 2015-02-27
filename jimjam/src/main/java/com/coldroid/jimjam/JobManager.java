package com.coldroid.jimjam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.coldroid.jimjam.NetworkBroadcastReceiver.NetworkStateListener;

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
    private JobLogger mJobLogger;
    private NetworkUtils mNetworkUtils;
    private ExecutorService mPriorityJobExecutor;
    private JobDatabase mJobDatabase;

    private List<Job> mWaitingForNetwork;

    /**
     * To create the JobManager, use the {@link Builder}.
     */
    private JobManager() {
    }

    /**
     * Adds the Job to the mPriorityJobExecutor. Will store the Job to disk if it's set to be persistent.
     */
    public void addJob(final @NonNull Job job) {
        if (job.isPersistent() && job.getRowId() == -1) {
            mJobDatabase.persistJob(job);
        }
        job.addedToQueue();
        mPriorityJobExecutor.execute(new RunnableJob(job));
        mJobLogger.d("Job queued in priority executor");
    }

    /**
     * Prints some debugging information to logcat. Will not be in the release version.
     */
    public void logDatabaseJobs() {
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

    /**
     * This will be called when the JobManager is built. It will fetch jobs from disk and add them to the
     * mPriorityJobExecutor.
     */
    private void start() {
        for (Job job : mJobDatabase.fetchJobs()) {
            addJob(job);
        }
        NetworkBroadcastReceiver.registerListener(mNetworkStateListener);
    }

    private NetworkStateListener mNetworkStateListener = new NetworkStateListener() {
        /**
         * When the network connects, we will push mWaitingForNetwork to the PriorityJobExecutor, which
         * represents a "ready to run" state. mWaitingForNetwork will be empty after this call.
         */
        @Override
        public void networkConnected() {
            mJobLogger.d("Received 'network connected' event");
            List<Job> temporaryList;
            synchronized (mWaitingForNetwork) {
                temporaryList = new LinkedList<>(mWaitingForNetwork);
                mWaitingForNetwork.clear();
            }
            for (Job job : temporaryList) {
                addJob(job);
            }
        }
    };

    /**
     * This class wraps jobs and allows them to be scheduled/run by mPriorityJobExecutor.
     */
    private class RunnableJob implements Runnable, Comparable<RunnableJob> {
        private final Job mJob;

        public RunnableJob(@NonNull Job job) {
            mJob = job;
        }

        @Override
        public void run() {
            try {
                if (rescheduleNetworkJob()) {
                    // We return early because the job has been scheduled for later execution.
                    return;
                }
                mJob.incrementRuns();
                mJob.run();
                jobSuccess();
            } catch (Exception exception) {
                jobFailedWithException(exception);
            }
        }

        @Override
        public int compareTo(RunnableJob otherRunnable) {
            return mJob.compareTo(otherRunnable.mJob);
        }

        /**
         * This method checks whether the current job requires network access. If it does and there is no network
         * access, it will be added to the mWaitingForNetwork list. This list will be processed when the network
         * connection event occurs, and these jobs will be added back to mPriorityJobExecutor.
         *
         * We synchronize on mWaitingForNetwork because the network can connect/disconnect at will. If the network
         * connects and mNetworkStateListener.networkConnected() is called right before we add the job to
         * mWaitingForNetwork, it will sit there until the network disconnects and then reconnects. Since we lock on
         * mWaitingForNetwork, and so does networkConnected(), we will add jobs to mWaitingForNetwork in two cases:
         *
         * 1. rescheduleNetworkJob() enters synchronized block first, sees there is a bad connection, adds itself to the
         * queue. Then networkConnected() can run and it processes the job. Woo!
         *
         * 2. networkConnected() enters the synchronized block first, dumps mWaitingForNetwork into the waiting queue .
         * Then rescheduleNetworkJob() is called. If the network is still connected it will try to run the job. Woo! If
         * the network is no longer connected it will add itself to the queue. Woo2! This is not an issue because we
         * don't want to run when the network is disconnected.
         *
         * In 99.9% of cases this synchronized block is unnecessary, but it will keep jbos from being stuck in an
         * unprocessed state.
         *
         * @return boolean whether the job was rescheduled.
         */
        private boolean rescheduleNetworkJob() {
            synchronized (mWaitingForNetwork) {
                if (mJob.requiresNetwork() && !mNetworkUtils.isNetworkConnected()) {
                    mWaitingForNetwork.add(mJob);
                    return true;
                }
            }
            return false;
        }

        /**
         * Called when the job has successfully run to completion.
         */
        private void jobSuccess() {
            mJobDatabase.removeJob(mJob);
        }

        /**
         * Called when the job failed to run and threw an exception. This will reschedule the job if necessary (defined
         * by the {@link Job#shouldRetry(int, Exception)}
         */
        private void jobFailedWithException(Exception exception) {
            if (mJob.shouldRetry(exception)) {
                if (mJob.isPersistent()) {
                    mJobDatabase.updateJob(mJob);
                }
                mPriorityJobExecutor.submit(new RunnableJob(mJob));
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

        public Builder(@NonNull Context context) {
            mContext = context.getApplicationContext();
        }

        public JobManager build() {
            if (mJobManager.mJobLogger == null) {
                mJobManager.mJobLogger = new DefaultJobLogger();
            }
            if (mJobSerializer == null) {
                mJobSerializer = new DefaultJobSerializer(mJobManager.mJobLogger);
            }
            mJobManager.mNetworkUtils = new NetworkUtils(mContext);
            mJobManager.mWaitingForNetwork = new LinkedList<>();
            mJobManager.mJobDatabase = new JobDatabase(mContext, mJobSerializer);
            mJobManager.mPriorityJobExecutor = newThreadExecutorService();
            mJobManager.start();
            return mJobManager;
        }

        public Builder customLogger(@Nullable JobLogger jobLogger) {
            mJobManager.mJobLogger = jobLogger;
            return this;
        }

        public Builder customSerializer(@Nullable JobSerializer jobSerializer) {
            mJobSerializer = jobSerializer;
            return this;
        }

        private static @NonNull ExecutorService newThreadExecutorService() {
            return new ThreadPoolExecutor(0, 3, 300L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>());
        }
    }
}
