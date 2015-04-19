package com.coldroid.jimjam.sample;

import android.widget.TextView;

import com.coldroid.jimjam.Job;
import com.coldroid.jimjam.JobParameters;

import java.util.Random;

/**
 * This Job will sleep for five seconds and then broadcast a "sleepy time job done!" message. It will have a label, so
 * putting multiple jobs with the same label will run 1 after the other instead of at the same time, no matter how many
 * threads are waiting to execute.
 */
public class LabelledJob extends Job {
    private final int mId;
    private final String mLabel;

    public LabelledJob(String label) {
        super(new JobParameters()
                .setLabel(label));
        mId = new Random().nextInt() % 1000;
        mLabel = label;
    }

    @Override
    protected void run() throws Exception {
        Thread.sleep(5 * 1000);
        JobBroadcastReceiver.broadcastJobMessage("label job done: " + mLabel);
    }

    @Override
    protected boolean shouldRetry(int mRunAttempts, Exception exception) {
        return mRunAttempts <= 15;
    }

    @Override
    protected void addedToQueue() {
        // Intentionally empty.
    }

    @Override
    public String toString() {
        return super.toString() + "\nLabelledJob\nmId: " + mId;
    }

    public static class Builder extends Job.Builder {
        private final TextView mTextView;

        public Builder(TextView textView) {
            mTextView = textView;
        }

        public Job build() {
            return new LabelledJob(mTextView.getText().toString());
        }
    }
}
