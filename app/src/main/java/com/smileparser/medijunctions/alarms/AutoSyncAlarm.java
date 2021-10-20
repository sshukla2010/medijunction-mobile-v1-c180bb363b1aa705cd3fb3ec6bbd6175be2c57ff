package com.smileparser.medijunctions.alarms;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.smileparser.medijunctions.services.BackgroundInternetService;

import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;


public class AutoSyncAlarm extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		try {

			//Toast.makeText(context, "call", Toast.LENGTH_SHORT).show();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				try {

					if(!isMyServiceRunning(BackgroundInternetService.class,context)) {
						//startForegroundService(context, new Intent(context, SensorService.class));
						context.startForegroundService(new Intent(context, BackgroundInternetService.class));
					}

				} catch (Exception e)
				{
					e.printStackTrace();
				}
				//startForegroundService(intent);
				//Toast.makeText(context, "start", Toast.LENGTH_SHORT).show();
			} else {
				if(!isMyServiceRunning(BackgroundInternetService.class,context)) {
					context.startService(new Intent(context, BackgroundInternetService.class));
				}
			}



		}
		catch (Exception e)
		{
			//Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		//context.startService(new Intent(context, NotificationsServices.class));
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

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					try {

						//if(!isMyServiceRunning(NotificationsServices.class,context)) {
						//startForegroundService(context, new Intent(context, SensorService.class));
						context.startForegroundService(new Intent(context, BackgroundInternetService.class));
						//}

					} catch (Exception e)
					{
						e.printStackTrace();
					}
					//startForegroundService(intent);
					//Toast.makeText(context, "start", Toast.LENGTH_SHORT).show();
				} else {
					//if(!isMyServiceRunning(NotificationsServices.class,context)) {
					context.startService(new Intent(context, BackgroundInternetService.class));
				}

				//startForegroundService(context, new Intent(context, SensorService.class));
			}
		}, 2500);

	}

}