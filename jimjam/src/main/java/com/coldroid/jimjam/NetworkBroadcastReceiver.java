package com.coldroid.jimjam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    public interface NetworkStateListener {
        public void networkConnected();
    }

    private static NetworkStateListener sNetworkStateListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (sNetworkStateListener != null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.isConnected()) {
                sNetworkStateListener.networkConnected();
            }
        }
    }

    /**
     * Registers your receiver. There can only be one receiver, so only the last one is used.
     */
    public static void registerListener(@Nullable NetworkStateListener networkStateListener) {
        sNetworkStateListener = networkStateListener;
    }
}
