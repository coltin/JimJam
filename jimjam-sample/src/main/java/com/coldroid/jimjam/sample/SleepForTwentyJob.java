package com.coldroid.jimjam.sample;

import com.coldroid.jimjam.Job;
import com.coldroid.jimjam.JobParameters;

/**
 * This Job will sleep for twenty seconds and then broadcast a "sleepy time job done!" message. Try rebooting your
 * device before it finishes.
 */
public class SleepForTwentyJob extends Job {
    public SleepForTwentyJob() {
        super(new JobParameters()
                .setRequiresNetwork(false));
    }

    @Override
    protected void run() throws Throwable {
        Thread.sleep(20 * 1000);
        JobBroadcastReceiver.broadcastJobMessage("sleepy time job done!");
    }

    @Override
    protected void addedToQueue() {
        // Intentionally empty.
    }
}
