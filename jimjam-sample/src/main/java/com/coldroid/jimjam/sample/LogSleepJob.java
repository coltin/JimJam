package com.coldroid.jimjam.sample;

import android.util.Log;

import com.coldroid.jimjam.Job;
import com.coldroid.jimjam.JobParameters;

import java.util.Random;

/**
 * This Job will sleep for five seconds. It will log before/after sleeping to help diagnose the executor.
 */
public class LogSleepJob extends Job {
    private final int mId;

    public LogSleepJob() {
        super(new JobParameters()
                .setRequiresNetwork(false)
                .setIsPersistent());
        mId = new Random().nextInt() % 1000;
    }

    @Override
    protected void run() throws Exception {
        Log.d("LogSleepJob", "Starting job #" + mId);
        Thread.sleep(5 * 1000);
        Log.d("LogSleepJob", "Finished job #" + mId);
    }

    @Override
    protected boolean shouldRetry(int mRunAttempts, Exception exception) {
        return mRunAttempts <= 15;
    }

    @Override
    protected void addedToQueue() {
        // Intentionally empty.
    }

    @Override
    public String toString() {
        return super.toString() + "\nLogSleepJob\nmId: " + mId;
    }

    public static class Builder extends Job.Builder {
        public Job build() {
            return new LogSleepJob();
        }
    }
}
