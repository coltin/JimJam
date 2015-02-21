package com.coldroid.jimjam;

import android.support.annotation.Nullable;

public interface JobManagerLogger {
    public boolean isDebug();

    public void d(String message, @Nullable Throwable throwable);

    public void e(String message, @Nullable Throwable throwable);
}
