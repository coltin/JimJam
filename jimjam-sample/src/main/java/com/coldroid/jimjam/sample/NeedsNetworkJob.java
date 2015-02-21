package com.coldroid.jimjam.sample;

import com.coldroid.jimjam.Job;
import com.coldroid.jimjam.JobParameters;

/**
 * This Job will ask for network connectivity and complete immediately, sending a "Network Job complete" broadcast when
 * it's done.
 */
public class NeedsNetworkJob extends Job {
    public NeedsNetworkJob() {
        super(new JobParameters()
                .setRequiresNetwork(true));
    }

    @Override
    protected void run() {
        JobBroadcastReceiver.broadcastMessage("Network Job complete");
    }

    @Override
    protected void addedToQueue() {
        // Intentionally empty.
    }
}
