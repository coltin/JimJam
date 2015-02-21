package com.coldroid.jimjam.sample;

import com.coldroid.jimjam.Job;
import com.coldroid.jimjam.JobParameters;

/**
 * TODO: This Job will ask for network connectivity and complete immediately, sending a broadcast when it's done.
 */
public class NeedsNetworkJob extends Job {
    public NeedsNetworkJob() {
        super(new JobParameters()
                .setRequiresNetwork(true));
    }
}
