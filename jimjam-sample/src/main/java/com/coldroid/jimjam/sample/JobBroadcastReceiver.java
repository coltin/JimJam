package com.coldroid.jimjam.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * JobBroadcastReceiver is intended to respond to messages from Jobs. It will create a toast of the provided message.
 *
 * Usage: JobBroadcastReceiver.broadcastMessage("Message you want to toast");
 *
 * This BroadcastReceiver isn't  necessary, you could just have a job create the Toast using the application context.
 * This is meant to serve as an example of how you might send updates. If you wanted to update the Activity you could
 * have a BroadcastReceiver registered in the Activity to receive the updates. That way if the Activity is not visible
 * or gone, no sadness occurs.
 *
 * Jobs can create toasts, but not update your UI.
 */
public class JobBroadcastReceiver extends BroadcastReceiver {
    /**
     * This defines the action this broadcast listens to. As far as I know it's not trivial to magically make this value
     * and what we supplied in the AndroidManifest be defined in one place, so you have to maintain both.
     */
    private static final String BROADCAST_ACTION = "com.coldroid.jimjam.sample.jobmessage";
    private static final String EXTRA_JOB_MESSAGE = "job_message";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getStringExtra(EXTRA_JOB_MESSAGE), Toast.LENGTH_SHORT).show();
    }

    /**
     * Convenience method to send a broadcast that will be picked up and processed by JobBroadcastReceiver.
     */
    public static void broadcastMessage(@NonNull String message) {
        Intent intent = new Intent(BROADCAST_ACTION).putExtra(EXTRA_JOB_MESSAGE, message);
        LocalBroadcastManager.getInstance(SampleApplication.instance()).sendBroadcast(intent);
    }
}
