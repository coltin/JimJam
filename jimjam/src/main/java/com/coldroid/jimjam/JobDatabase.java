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
 * the path to the Job blob. Not a priority at this time unless SQLite has trouble storing Job blobs.
 *
 * Things like "job priority" are stored with the Job object, and not as fields in the DB. This keeps things simple.
 *
 * We do not currently support injecting a JobDatabase into the JobManager. Might be an interesting idea if people want
 * to provide an in-memory mock for testing. Will keep it in mind, but this is not currently supported.
 */
public class JobDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "JOB_DATABASE";
    private static final String TABLE_JOBS = "jobs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SERIALIZED_JOB = "serialized_job";
    private static final String SQL_QUERY_FETCH_JOBS =
            "SELECT " + COLUMN_ID + "," + COLUMN_SERIALIZED_JOB + " FROM " + TABLE_JOBS;

    private final JobSerializer mJobSerializer;

    public JobDatabase(@NonNull Context context, @NonNull JobSerializer jobSerializer) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mJobSerializer = jobSerializer;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder createTable = new StringBuilder();
        createTable.append("CREATE TABLE ").append(TABLE_JOBS);
        createTable.append("(");
        createTable.append(COLUMN_ID).append(" integer primary key autoincrement");
        createTable.append(", ").append(COLUMN_SERIALIZED_JOB).append(" BLOB");
        createTable.append(")");
        database.execSQL(createTable.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);
        onCreate(database);
    }

    public void persistJob(@NonNull Job job) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SERIALIZED_JOB, mJobSerializer.serialize(job));
        SQLiteDatabase database = getWritableDatabase();
        long rowId = database.insert(TABLE_JOBS, null, contentValues);
        database.close();
        job.setRowIdId(rowId);
    }

    public @NonNull List<Job> fetchJobs() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(SQL_QUERY_FETCH_JOBS, null);
        if (cursor == null) {
            return Collections.EMPTY_LIST;
        }
        List<Job> resultJobs = new ArrayList<Job>(cursor.getCount());
        while (cursor.moveToNext()) {
            Job deserializedJob = mJobSerializer.deserialize(cursor.getBlob(1));
            deserializedJob.setRowIdId(cursor.getLong(0));
            resultJobs.add(deserializedJob);
        }
        cursor.close();
        database.close();
        return resultJobs;
    }

    /**
     * Will not be available in final version.
     */
    public void dumpDatabase() {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);
        onCreate(database);
        database.close();
    }
}
