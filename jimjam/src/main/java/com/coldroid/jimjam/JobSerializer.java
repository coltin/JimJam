package com.coldroid.jimjam;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Implement this class to provide your own {@link Job} serialization and provide it to the {@link JobManager.Builder}
 * when configuring the JobManager.
 */
public interface JobSerializer {
    public @Nullable byte[] serialize(@NonNull Job job);
    public @Nullable Job deserialize(@Nullable byte[] jobBytes);
}
