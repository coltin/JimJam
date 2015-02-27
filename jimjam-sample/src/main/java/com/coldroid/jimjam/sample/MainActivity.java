package com.coldroid.jimjam.sample;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.coldroid.jimjam.Job;
import com.coldroid.jimjam.Job.Builder;

import static android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * This Activity will contain a number of buttons to create and test various kinds of Jobs. Every button tap creates a
 * new Job, so don't get crazy!
 */
public class MainActivity extends Activity {
    private final JobBroadcastReceiver mJobBroadcastReceiver = new JobBroadcastReceiver();

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        attachJobBuilderToOnClick(R.id.new_job_button_sleep_for_twenty, new SleepForTwentyJob.Builder());
        attachJobBuilderToOnClick(R.id.new_job_button_needs_network, new NeedsNetworkJob.Builder());
        attachJobBuilderToOnClick(R.id.new_job_button_high_priority, new HighPriorityJob.Builder());
        findViewById(R.id.log_saved_jobs_to_logcat).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SampleApplication.instance().getJobManager().logDatabaseJobs();
            }
        });
        findViewById(R.id.dump_job_database).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SampleApplication.instance().getJobManager().dumpDatabase();
            }
        });

        final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        ToggleButton wifiButton = (ToggleButton) findViewById(R.id.wifi_toggler);
        wifiButton.setChecked(wifiManager.isWifiEnabled());
        wifiButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isWifiEnabled) {
                wifiManager.setWifiEnabled(isWifiEnabled);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mJobBroadcastReceiver.registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mJobBroadcastReceiver.unregisterReceiver();
    }

    /**
     * This is a convenience method that takes a viewId and attaches an {@link OnClickListener} which will use the
     * Builder to generate a new {@link Job} which will be added to the JobManager. Meant to make the calling activity
     * cleaner, I don't recommend this style in general, though it is pretty cool.
     */
    private void attachJobBuilderToOnClick(@IdRes int viewId, final @NonNull Builder jobBuilder) {
        findViewById(viewId).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SampleApplication.instance().getJobManager().addJob(jobBuilder.build());
            }
        });
    }
}
