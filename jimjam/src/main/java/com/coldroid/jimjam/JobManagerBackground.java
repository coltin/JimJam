package com.coldroid.jimjam;

import android.support.annotation.NonNull;

/**
 * This class is meant to be extended by {@link JobManager}. It abstracts away all the gross background thread
 * crazyness. Essentially these methods wrap actual implementations inside of {@link JobManager} so its code can stay
 * nice and clean.
 */
public abstract class JobManagerBackground {
    protected JobManagerThread mJobManagerThread;

    protected abstract void addJobBackground(final @NonNull Job job);

    protected abstract void startBackground();

    /**
     * Queues the {@link Job} to be executed. If the {@link Job} is persistent then it will be written to disk first.
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
     * This will be called when the JobManager is built. It will fetch jobs from disk and add them to the
     * mPriorityJobExecutor.
     */
    protected void start() {
        mJobManagerThread.post(new Runnable() {
            @Override
            public void run() {
                startBackground();
            }
        });
    }
}
