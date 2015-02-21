package com.coldroid.jimjam;

import android.support.annotation.Nullable;

/**
 * This is the default JobManagerLogger which is used by the JobManager if you don't supply one to the Builder on
 * creation.
 */
public class DefaultJobManagerLogger implements JobManagerLogger {

    @Override
    public boolean isDebug() {
        return false;
    }

    @Override
    public void d(String message, @Nullable Throwable throwable) {
        // Intentionally empty.
    }

    @Override
    public void e(String message, @Nullable Throwable throwable) {
        // Intentionally empty.
    }
}
