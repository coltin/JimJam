package com.coldroid.jimjam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a convenience class which will handle database transactions for storing/retrieving persistent jobs. Currently
 * stores the serialized Jobs in the DB as a blob. May potentially store these byte[] blobs on the filesystem and store
 * the path to the Job blob. Not a priority at this time unless SQLite has trouble storing Job blobs. That's just really
 * fun to say! Job Blob. Job Blob.
 *
 * Things like "job priority" are stored with the Job object, and not as fields in the DB. This keeps things simple.
 *
 * We do not currently support injecting a JobDatabase into the JobManager. Might be an interesting idea if people want
 * to provide an in-memory mock for testing. Will keep it in mind, but this is not currently supported.
 */
public class JobDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "JOB_DATABASE";
    private static final String TABLE_JOBS = "jobs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_SERIALIZED_JOB = "serialized_job";
    private static final String SQL_QUERY_FETCH_JOBS = String.format("SELECT %s,%s,%s FROM %s", COLUMN_ID,
            COLUMN_PRIORITY, COLUMN_SERIALIZED_JOB, TABLE_JOBS);
    private static final String SQL_QUERY_FETCH_JOBS_SORTED = SQL_QUERY_FETCH_JOBS + " ORDER BY " + COLUMN_PRIORITY;
    private static final String ROW_DELETE_WHERE = COLUMN_ID + "=?";

    private final JobSerializer mJobSerializer;

    public JobDatabase(@NonNull Context context, @NonNull JobSerializer jobSerializer) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mJobSerializer = jobSerializer;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + TABLE_JOBS + "("
                + COLUMN_ID + " integer primary key autoincrement"
                + ", " + COLUMN_PRIORITY + " INTEGER"
                + ", " + COLUMN_SERIALIZED_JOB + " BLOB"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);
        onCreate(database);
    }

    public synchronized void persistJob(@NonNull Job job) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PRIORITY, job.getPriority());
        contentValues.put(COLUMN_SERIALIZED_JOB, mJobSerializer.serialize(job));
        SQLiteDatabase database = getWritableDatabase();
        job.setRowIdId(database.insert(TABLE_JOBS, null, contentValues));
        database.close();
    }

    /**
     * This will attempt to remove the supplied job from the database. If the job is not found or doesn't have an
     * associated rowId, it will silently return.
     */
    public synchronized void removeJob(@NonNull Job job) {
        long rowId = job.getRowId();
        if (rowId == -1) {
            return;
        }
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_JOBS, ROW_DELETE_WHERE, new String[]{Long.toString(rowId)});
        database.close();
    }

    /**
     * TODO: This will be called when the job is running and is marked as persistent to update the attempts. Might not
     * be a great idea. We shall seeee.
     */
    @SuppressWarnings({"EmptyMethod", "UnusedParameters"})
    public synchronized void updateJob(Job mJob) {
        // Intentionally empty, for now.
    }

    public synchronized @NonNull List<Job> fetchJobs(boolean sorted) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(sorted ? SQL_QUERY_FETCH_JOBS_SORTED : SQL_QUERY_FETCH_JOBS, null);
        if (cursor == null) {
            database.close();
            return Collections.emptyList();
        }
        List<Job> resultJobs = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            Job deserializedJob = mJobSerializer.deserialize(cursor.getBlob(2));
            if (deserializedJob != null) {
                deserializedJob.setRowIdId(cursor.getLong(0));
                resultJobs.add(deserializedJob);
            }
        }
        cursor.close();
        database.close();
        return resultJobs;
    }

    /**
     * TODO: Will not be available in final version. Deletes/removes all jobs.
     */
    public synchronized void dumpDatabase() {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);
        onCreate(database);
        database.close();
    }
}
