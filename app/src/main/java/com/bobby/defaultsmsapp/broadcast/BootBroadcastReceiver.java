package com.bobby.defaultsmsapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bobby.defaultsmsapp.activity.MainActivity;
import com.bobby.defaultsmsapp.service.MsgSendService;

public class BootBroadcastReceiver extends BroadcastReceiver {
    public BootBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 开机启动的Service
        Intent serviceIntent = new Intent(context, MsgSendService.class);
        // 启动Service
        context.startService(serviceIntent);
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         //启动Activity
        context.startActivity(activityIntent);
    }
}
