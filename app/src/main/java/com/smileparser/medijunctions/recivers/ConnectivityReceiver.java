package com.smileparser.medijunctions.recivers;

/**
 * Created by Lincoln on 18/03/16.
 */

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.core.content.ContextCompat;
import android.util.Log;

import com.smileparser.medijunctions.utils.Global;

import static android.content.Context.ACTIVITY_SERVICE;


public class ConnectivityReceiver
        extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }

        if(isConnected)
        {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {

                    if (!isMyServiceRunning(NotificationIntentService.class, context)) {
                        //ContextCompat.startForegroundService(context, new Intent(context, AutoSyncService.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

               // context.startService(new Intent(context, AutoSyncService.class));
            }*/
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already","running");
                return true;
            }
        }
        Log.i("Service not","running");
        return false;
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) Global.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

}