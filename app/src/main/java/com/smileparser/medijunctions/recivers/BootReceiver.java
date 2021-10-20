package com.smileparser.medijunctions.recivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.smileparser.medijunctions.services.NotificationIntentService;

import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            if(!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                return;
            }
            else {


                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {

                        if (!isMyServiceRunning(NotificationIntentService.class, context)) {
                            ContextCompat.startForegroundService(context, new Intent(context, NotificationIntentService.class));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    context.startService(new Intent(context, NotificationIntentService.class));
                }*/

            }

        }
        catch (Exception e)
        {
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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

    private boolean isServiceRunning(String serviceName, Context context){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i
                    .next();

            if(runningServiceInfo.service.getClassName().equals(serviceName)){


                if(runningServiceInfo.foreground)
                {
                    serviceRunning = true;	//service run in foreground
                }
            }
        }
        return serviceRunning;
    }

    public void RunMe(final Context context)
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ContextCompat.startForegroundService(context, new Intent(context, NotificationIntentService.class));
            }
        }, 2500);

    }

}
