package com.coldroid.jimjam.sample;

import android.support.annotation.Nullable;
import android.util.Log;

import com.coldroid.jimjam.JobManagerLogger;

public class JobLogger extends JobManagerLogger {
    private static final String TAG = "JobManager";

    @Override
    public boolean isDebug() {
        return true;
    }

    @Override
    public void d(String message, @Nullable Throwable throwable) {
        Log.d(TAG, message, throwable);
    }

    @Override
    public void e(String message, @Nullable Throwable throwable) {
        Log.e(TAG, message, throwable);
    }
}
