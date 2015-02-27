package com.coldroid.jimjam;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * This is a strange class used to query for network state. It's strange because it can be used statically or
 * non-statically. For non-static use you can create an object that holds an Application Context. It was made this way
 * because I do not want the JobManager to explicitly hold on to a Context. So the Builder provides the Context, and the
 * JobManager never sees it.
 */
public class NetworkUtils {
    private final Context mContext;

    public NetworkUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Checks if there is an active internet connection.
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
    }

    public boolean isNetworkConnected() {
        return isNetworkConnected(mContext);
    }
}
