package com.coldroid.jimjam;

import android.support.annotation.Nullable;

/**
 * These methods will be called by the JobManager to log events. You can provide your own implementation via {@link
 * JobManager.Builder#customLogger(JobLogger)}.
 */
public abstract class JobLogger {
    public abstract void d(String message, @Nullable Exception exception);

    public abstract void e(String message, @Nullable Exception exception);

    public final void d(String message) {
        d(message, null);
    }

    public final void e(String message) {
        e(message, null);
    }
}
