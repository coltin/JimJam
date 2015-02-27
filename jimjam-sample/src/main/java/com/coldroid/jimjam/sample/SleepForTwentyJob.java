package com.coldroid.jimjam.sample;

import com.coldroid.jimjam.Job;
import com.coldroid.jimjam.JobParameters;

import java.util.Random;

/**
 * This Job will sleep for twenty seconds and then broadcast a "sleepy time job done!" message. Try rebooting your
 * device before it finishes.
 */
public class SleepForTwentyJob extends Job {
    private final int mId;

    public SleepForTwentyJob() {
        super(new JobParameters()
                .setRequiresNetwork(false)
                .setIsPersistent());
        mId = new Random().nextInt() % 1000;
    }

    @Override
    protected void run() throws Exception {
        Thread.sleep(20 * 1000);
        JobBroadcastReceiver.broadcastJobMessage("sleepy time job done!");
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
        return super.toString() + "\nSleepForTwentyJob\nmId: " + mId;
    }

    public static class Builder extends Job.Builder {
        public Job build() {
            return new SleepForTwentyJob();
        }
    }
}
