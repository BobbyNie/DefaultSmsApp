package com.bobby.defaultsmsapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bobby.defaultsmsapp.service.MsgSendService;
import com.bobby.defaultsmsapp.utils.WakeLockForNetUse;

/**
 * 华为手机特别处理，来电将收到4条消息 只要第一条和最后一条
 * 1   14715015872 RINGING on 134  -- 发消息
 2147483647   14715015872 RINGING on 134 --抛弃
 1   null IDLE on 134                    --抛弃
 2147483647   14715015872 IDLE on 134    --发消息 同时改变来电接收卡为第一条消息的接收卡
 */
public class CallReceiver extends BroadcastReceiver {
	//private static boolean incomingFlag = false;
	// private String incomingNumber;
	@Override
	public void onReceive(Context context, Intent intent) {
		WakeLockForNetUse.getInstance().setPowerManager(context);
		WakeLockForNetUse.getInstance().acquireCpuForNetCalls();
		Intent msgIntent = new Intent( context, MsgSendService.class);
		msgIntent.setAction(MsgSendService.ACTION_DEAL_CALL);
		msgIntent.putExtra("sysInAction",intent.getAction());
		msgIntent.putExtras(intent);
		context.startService(msgIntent);
	}
}