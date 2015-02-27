package com.coldroid.jimjam.sample;

import android.app.Application;

import com.coldroid.jimjam.JobManager;

public class SampleApplication extends Application {
    private static SampleApplication INSTANCE;
    private JobManager mJobManager;

    public SampleApplication() {
        INSTANCE = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        configureJobManager();
    }

    public static SampleApplication instance() {
        return INSTANCE;
    }

    public JobManager getJobManager() {
        return mJobManager;
    }

    private void configureJobManager() {
        mJobManager = new JobManager.Builder(this)
                .customLogger(new SampleJobLogger())
                .build();
    }
}
