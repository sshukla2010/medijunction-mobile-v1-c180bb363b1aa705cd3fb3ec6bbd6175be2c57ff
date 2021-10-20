package com.smileparser.medijunctions.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;

import com.smileparser.medijunctions.utils.Global;


@SuppressLint("Wakelock")
public class AutoSyncService extends Service
{
	@Override
	public IBinder onBind(Intent intent)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}



    @Override
    public void onStart(Intent intent, int startIds)
    {
    	try
		{
    		new Thread(new Runnable()
    		{
				@Override
				public void run() 
				{
					//PowerManager pm;
					//PowerManager.WakeLock wl = null;
					try 
					{
						//pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
						//wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AutoSyncService");
						if(Global.isInternetOn(getApplicationContext()))
						{
						/*	if(Global.IsNotNull(wl))
							{
								if(wl.isHeld())
								{
									wl.release();
								}
								wl.acquire();
							}
*/							if (android.os.Build.VERSION.SDK_INT > 9)
							{
								StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
										.permitAll().build();
								StrictMode.setThreadPolicy(policy);
							}
							if(Global.IsAutoSyncServiceOn==null || Global.IsAutoSyncServiceOn==false)
							{
								ItemService ServerUtil=new ItemService(AutoSyncService.this);
								ServerUtil.UpdateAll();
								ServerUtil.DeleteAll();

								AllergyDataService allergyDataService =new AllergyDataService(AutoSyncService.this);
								allergyDataService.getAll();

								HealthConditionDataService healthConditionDataService = new HealthConditionDataService(AutoSyncService.this);
								healthConditionDataService.getAll();
							}
						}
					}
					catch (Exception exp)
					{
/*
						if(wl!=null)
						{
							if(wl.isHeld())
								wl.release();
							wl=null;
						}
*/
						Global.IsAutoSyncServiceOn=false;
						if(Global.IsNotNull(exp)&& Global.IsNotNull(exp.getMessage()))
							Log.e(this.getClass().getName(), exp.getMessage());
					}
					finally
					{
/*
						if(wl!=null)
						{
							if(wl.isHeld())
								wl.release();
							wl=null;
						}
*/
						Global.IsAutoSyncServiceOn=false;
						AutoSyncService.this.stopSelf();
					}
				}
			}).start();
		}
		catch(Exception exp)
		{
			if(Global.IsNotNull(exp)&& Global.IsNotNull(exp.getMessage()))
				Log.e(this.getClass().getName(), exp.getMessage());
		}
    }
}
