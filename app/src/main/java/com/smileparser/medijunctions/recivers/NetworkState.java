package com.smileparser.medijunctions.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

import com.smileparser.medijunctions.services.AutoSyncService;
import com.smileparser.medijunctions.utils.Global;


public class NetworkState extends BroadcastReceiver {


	SharedPreferences shared_pref;
	Context context;

	@Override
	public void onReceive(final Context context, Intent intent)
	{
		try 
		{
			this.context = context;
			shared_pref = context.getSharedPreferences("settings", 0);

			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] _Info = connectivityManager.getAllNetworkInfo();

			boolean isConnected = false;
			String NetworkTypeName = "";
			for (NetworkInfo networkInfo : _Info) {
				if (networkInfo.isConnected()) {
					isConnected = true;
					NetworkTypeName = networkInfo.getTypeName();
					break;
				}
			}

			if (isConnected) 
			{
				//logger.info("Network Type Wifi/Mobile: " + NetworkTypeName);
			/*	if(Global.IsNotNull(Global.IsSync) && !Global.IsSync)
	    		{
					new Thread(new Runnable()
		    		{
						@Override
						public void run()
						{
							try
							{
								if (android.os.Build.VERSION.SDK_INT > 9)
								{
									StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
											.permitAll().build();
									StrictMode.setThreadPolicy(policy);
								}
								Global.IsSync=true;
								//Intent intent=new Intent(context, AutoSyncService.class);
								*//*if(Global.IsNotNull(intent))
									context.startService(intent);*//*
								
							} 
							catch (Exception e)
							{
								Global.IsSync=false;
								if(Global.IsNotNull(e))
								Log.e("Error",e.getLocalizedMessage());
							}
							finally
							{
								Global.IsSync=false;
							}
						}
					}).start();
	    		}*/
			}
		}
		catch (Exception exp)
		{
			if(Global.IsNotNull(exp)&& Global.IsNotNull(exp.getMessage()))
				Log.d(this.getClass().getName(), exp.getMessage());
		}
	}
}