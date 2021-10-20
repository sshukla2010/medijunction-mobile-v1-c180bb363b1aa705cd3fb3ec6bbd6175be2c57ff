package com.smileparser.medijunctions.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.smileparser.medijunctions.recivers.ConnectivityReceiver;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundInternetService extends Service {
    public int counter=0;
    ConnectivityReceiver connectivityReceiver;
    public BackgroundInternetService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");

    }

    public BackgroundInternetService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //startTimer();
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        checkInternet();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        try {
            unregisterReceiver(connectivityReceiver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //stoptimertask();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Log.i("EXIT", "onTaskRemoved!");
       // stoptimertask();
        try {
            unregisterReceiver(connectivityReceiver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        super.onTaskRemoved(rootIntent);
    }
    private boolean isInternet;
public void checkInternet()
{
    isInternet=ConnectivityReceiver.isConnected();
    /*if (isInternet)
    {
        Toast.makeText(this, "Internet Connected", Toast.LENGTH_SHORT).show();
    }
    else
    {
        Toast.makeText(this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
    }*/
}
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
