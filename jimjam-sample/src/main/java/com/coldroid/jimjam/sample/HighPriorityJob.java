package com.coldroid.jimjam.sample;

import com.coldroid.jimjam.Job;
import com.coldroid.jimjam.JobParameters;
import com.coldroid.jimjam.JobPriority;

/**
 * TODO: This Job will broadcast "High Priority Job complete" message. Use this to supersede lower priority jobs to test
 * priority.
 */
public class HighPriorityJob extends Job {
    public HighPriorityJob() {
        super(new JobParameters()
                .setRequiresNetwork(false)
                .setJobPriority(JobPriority.HIGH));
    }
}