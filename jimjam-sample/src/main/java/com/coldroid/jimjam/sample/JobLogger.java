package com.coldroid.jimjam.sample;

import android.support.annotation.Nullable;
import android.util.Log;

public class JobLogger extends com.coldroid.jimjam.JobLogger {
    private static final String TAG = "JobManager";

    @Override
    public void d(String message, @Nullable Exception exception) {
        Log.d(TAG, message, exception);
    }

    @Override
    public void e(String message, @Nullable Exception exception) {
        Log.e(TAG, message, exception);
    }
}
