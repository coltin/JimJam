package com.coldroid.jimjam;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Convenience class around {@link HandlerThread}. The JobManager uses this class to easily post tasks to the threads
 * {@link Looper} without having to fetch the {@link Handler} and then posting to it. When using this class, you must
 * call {@link #getLooper()} after calling {@link #start()} and before calling {@link #post}.
 */
public class JobManagerThread extends HandlerThread {
    private Handler mHandler;

    public JobManagerThread(String name) {
        super(name);
    }

    @Override
    public Looper getLooper() {
        mHandler = new Handler(super.getLooper());
        return mHandler.getLooper();
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }
}
