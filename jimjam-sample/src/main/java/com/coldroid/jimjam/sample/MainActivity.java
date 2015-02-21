package com.coldroid.jimjam.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * This Activity will contain a number of buttons to create and test various kinds of Jobs. Every button tap creates a
 * new Job, so don't get crazy!
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.new_job_button_sleep_for_twenty).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SampleApplication.instance().getJobManager().addJob(new SleepForTwentyJob());
            }
        });

        findViewById(R.id.new_job_button_needs_network).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SampleApplication.instance().getJobManager().addJob(new NeedsNetworkJob());
            }
        });
    }
}
