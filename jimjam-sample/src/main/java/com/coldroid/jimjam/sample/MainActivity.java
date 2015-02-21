package com.coldroid.jimjam.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;

import com.coldroid.jimjam.Job;

/**
 * This Activity will contain a number of buttons to create and test various kinds of Jobs. Every button tap creates a
 * new Job, so don't get crazy!
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        attachJobOnClickToView(R.id.new_job_button_sleep_for_twenty, new SleepForTwentyJob());
        attachJobOnClickToView(R.id.new_job_button_needs_network, new NeedsNetworkJob());
        attachJobOnClickToView(R.id.new_job_button_high_priority, new HighPriorityJob());
    }

    /**
     * This is a convenience method that takes a viewId and attaches an {@link OnClickListener} which will add a {@link
     * Job} to the JobManager. Meant to make the calling activity cleaner, I don't recommend this style in general.
     */
    private void attachJobOnClickToView(@IdRes int viewId, final @NonNull Job job) {
        findViewById(viewId).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SampleApplication.instance().getJobManager().addJob(job);
            }
        });
    }
}
