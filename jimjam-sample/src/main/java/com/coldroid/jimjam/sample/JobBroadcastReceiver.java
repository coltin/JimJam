package com.coldroid.jimjam.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * JobBroadcastReceiver is intended to respond to messages from Jobs. It will create a toast of the provided message.
 *
 * Usage: JobBroadcastReceiver.broadcastMessage("Message you want to toast");
 *
 * This BroadcastReceiver isn't  necessary, you could just have a job create the Toast using the application context.
 * This is meant to serve as an example of how you might send updates about a jobs progress/completion/failure. If you
 * wanted to update the Activity you could have a BroadcastReceiver registered in the Activity to receive the updates.
 * That way if the Activity is not visible or gone, no sadness occurs.
 *
 * An Android bus system would also be super awesome here, but this sample application will be native Android only.
 */
public class JobBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "JobBroadcastReceiver";
    private static final String ACTION_JOB_MESSAGE = "com.coldroid.jimjam.sample.jobmessage";
    private static final String EXTRA_JOB_MESSAGE = "job_message";

    private final String mLogTag;

    public JobBroadcastReceiver(String logTag) {
        mLogTag = logTag;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getStringExtra(EXTRA_JOB_MESSAGE), Toast.LENGTH_SHORT).show();
    }

    /**
     * Convenience method to send a broadcast that will be picked up and processed by any registered receivers.
     */
    public static void broadcastJobMessage(@NonNull String message) {
        Log.d(TAG, "broadcastJobMessage: " + message);
        Intent intent = new Intent(ACTION_JOB_MESSAGE).putExtra(EXTRA_JOB_MESSAGE, message);
        LocalBroadcastManager.getInstance(SampleApplication.instance()).sendBroadcast(intent);
    }

    /**
     * Call this convenience method to register your JobBroadcastReceiver. Should be called in onResume() of your
     * Activity.
     */
    public void registerReceiver() {
        LocalBroadcastManager.getInstance(SampleApplication.instance())
                             .registerReceiver(this, new IntentFilter(ACTION_JOB_MESSAGE));
    }

    /**
     * This is a convenience method that should be called in onPause() of your Activity to unregister your
     * JobBroadcastReceiver.
     */
    public void unregisterReceiver() {
        LocalBroadcastManager.getInstance(SampleApplication.instance()).unregisterReceiver(this);
    }
}
