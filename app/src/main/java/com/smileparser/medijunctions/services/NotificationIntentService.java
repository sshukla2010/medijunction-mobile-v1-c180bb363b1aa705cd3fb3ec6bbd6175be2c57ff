package com.smileparser.medijunctions.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class NotificationIntentService extends Service {
    public NotificationIntentService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*try {
            AlarmManager AutoSyncAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent AutoSyncIntent = new Intent(getApplicationContext(),AutoSyncAlarm.class);
            PendingIntent piAutoUpdateAlert = PendingIntent.getBroadcast(getApplicationContext(), 0, AutoSyncIntent, 0);
            AutoSyncAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100,
                    (1000 * 60 * 7), piAutoUpdateAlert);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/

    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
       /* try {
            AlarmManager AutoSyncAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent AutoSyncIntent = new Intent(getApplicationContext(),AutoSyncAlarm.class);
            PendingIntent piAutoUpdateAlert = PendingIntent.getBroadcast(getApplicationContext(), 0, AutoSyncIntent, 0);
            AutoSyncAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100,
                    (1000 * 60 * 7), piAutoUpdateAlert);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
*/
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Distroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
