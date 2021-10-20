package com.smileparser.medijunctions.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.smileparser.medijunctions.utils.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);

    }
}
