package com.coldroid.jimjam;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Provides a default implementation of serialization for the {@link JobManager}. This will be used if none is
 * supplied to {@link JobManager.Builder}.
 */
public final class DefaultJobSerializer implements JobSerializer {

    private final JobLogger mJobLogger;

    public DefaultJobSerializer(@NonNull JobLogger jobLogger) {
        mJobLogger = jobLogger;
    }

    /**
     * Consumes a {@Link Job} and attempts to serialize it to a byte array using regular java serialization. Any error
     * will return null.
     *
     * Note: {@link ByteArrayOutputStream} does not need to be closed.
     */
    @Override
    public @Nullable byte[] serialize(@NonNull Job job) {
        ObjectOutputStream objectOutputStream = null;
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(job);
            return byteOutputStream.toByteArray();
        } catch (IOException exception) {
            mJobLogger.e("Unable to serialize job", exception);
        } finally {
            safeCloseClosable(objectOutputStream);
        }
        return null;
    }

    /**
     * Consumes a byte[] and attempts to deserialize it to Job using regular java serialization. Any error including an
     * invalid byte[] will return null.
     */
    @Override
    public @Nullable Job deserialize(@Nullable byte[] jobBytes) {
        if (jobBytes == null || jobBytes.length == 0) {
            return null;
        }

        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(jobBytes));
            return (Job) objectInputStream.readObject();
        } catch (IOException exception) {
            mJobLogger.e("Unable to deserialize job", exception);
        } catch (ClassNotFoundException exception) {
            mJobLogger.e("Unable to deserialize job, class not found", exception);
        } finally {
            safeCloseClosable(objectInputStream);
        }
        return null;
    }

    /**
     * Convenience method to close a Closeable safely (such as ObjectInputStream or ObjectOutputStream), ignoring
     * errors.
     */
    private void safeCloseClosable(@Nullable Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException exception) {
                // Intentionally empty.
            }
        }
    }
}
