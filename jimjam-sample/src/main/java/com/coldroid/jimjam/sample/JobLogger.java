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
    public void d(String message, @Nullable Exception exception) {
        Log.d(TAG, message, exception);
    }

    @Override
    public void e(String message, @Nullable Exception exception) {
        Log.e(TAG, message, exception);
    }
}
