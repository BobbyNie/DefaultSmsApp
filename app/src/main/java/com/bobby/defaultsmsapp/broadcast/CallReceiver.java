package com.bobby.defaultsmsapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bobby.defaultsmsapp.listener.CallPhoneListener;

/**
 */
public class CallReceiver extends BroadcastReceiver {
	//private static boolean incomingFlag = false;
	// private String incomingNumber;
	@Override
	public void onReceive(Context context, Intent intent) {
		final String ring_state = intent.getStringExtra("state");
		CallPhoneListener.setListeners(context.getApplicationContext(),ring_state);
	}
}