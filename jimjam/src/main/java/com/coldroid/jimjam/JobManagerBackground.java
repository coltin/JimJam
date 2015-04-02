package com.coldroid.jimjam;

import android.support.annotation.NonNull;

/**
 * This class is meant to be extended by {@link JobManager}. It abstracts away all the gross background thread
 * craziness. Essentially these methods wrap actual implementations inside of {@link JobManager} so its code can stay
 * nice and clean and it's clear what's running on the UI thread and what's on background threads.
 */
public abstract class JobManagerBackground {
    protected JobManagerThread mJobManagerThread;

    protected abstract void addJobBackground(final @NonNull Job job);

    protected abstract void startBackground();

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
     * This will be called when the JobManager is built. It will fetch jobs from disk and add them to the
     * mPriorityJobExecutor.
     *
     * Calls to this method are async.
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
