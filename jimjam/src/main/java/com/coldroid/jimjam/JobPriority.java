package com.coldroid.jimjam;

/**
 * Used by the JobManager to prioritize scheduling jobs. See JobManager for details. JobPriority can be provided to the
 * JobParameters class on Job creation.
 */
public enum JobPriority {
    LOW, MEDIUM, HIGH
}
