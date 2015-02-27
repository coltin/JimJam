package com.coldroid.jimjam;

import android.support.annotation.Nullable;

public abstract class JobManagerLogger {
    public abstract boolean isDebug();

    public abstract void d(String message, @Nullable Exception exception);

    public abstract void e(String message, @Nullable Exception exception);

    public final void d(String message) {
        d(message, null);
    }

    public final void e(String message) {
        e(message, null);
    }
}
