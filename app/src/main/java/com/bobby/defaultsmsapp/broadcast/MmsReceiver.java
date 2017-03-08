package com.bobby.defaultsmsapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bobby.defaultsmsapp.service.MsgSendService;

/**
 * Created by niebo on 2017/3/1.
 */
public class MmsReceiver extends BroadcastReceiver {
    /**
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent msgIntent = new Intent( context, MsgSendService.class);
        msgIntent.setAction(MsgSendService.ACTION_DEAL_SMS_SERVICE);
        msgIntent.putExtra("sysInAction",intent.getAction());
        msgIntent.putExtras(intent);
        //abortBroadcast();// 中止发送
        context.startService(msgIntent);

    }
}
