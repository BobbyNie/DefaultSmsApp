package com.bobby.defaultsmsapp.listener;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.bobby.defaultsmsapp.service.MsgSendService;
import com.bobby.defaultsmsapp.utils.ReflectUtil;
import com.bobby.defaultsmsapp.utils.WakeLockForNetUse;

/**
 * Created by niebo on 2017/3/3.
 */

public class CallPhoneListener extends PhoneStateListener {

    private int subscriptionId = -1;
    Context applicationContext;
    /**
     * Create a PhoneStateListener for the Phone with the default subscription.
     * This class requires Looper.myLooper() not return null.
     */
    private CallPhoneListener(int subscriptionId, Context applicationContext) {
        super();
        this.subscriptionId = subscriptionId;
        this.applicationContext = applicationContext;
        //设置当前监听的sim卡
        ReflectUtil.setFieldValue(this, "mSubId", subscriptionId);
        WakeLockForNetUse.getInstance().setPowerManager(applicationContext);
    }

    static  private CallPhoneListener sim1Listener;
    static  private CallPhoneListener sim2Listener;

    public static void setListeners(Context applicationContext){
        //SubscriptionManager subscriptionManager = SubscriptionManager.from(applicationContext);

        if(sim1Listener == null){
            sim1Listener = new CallPhoneListener(0,applicationContext);
        }
        if(sim2Listener == null){
            sim2Listener = new CallPhoneListener(1,applicationContext);
        }

        TelephonyManager telephonyManager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(sim1Listener,PhoneStateListener.LISTEN_NONE);
        telephonyManager.listen(sim2Listener,PhoneStateListener.LISTEN_NONE);

        telephonyManager.listen(sim1Listener,PhoneStateListener.LISTEN_CALL_STATE);
        telephonyManager.listen(sim2Listener,PhoneStateListener.LISTEN_CALL_STATE);


    }


   // private String last_incomingNumber ;
    private boolean isFirstIdleForRegiste = true;
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if(isFirstIdleForRegiste ){
            isFirstIdleForRegiste = false;
            return;
        }
        WakeLockForNetUse.getInstance().acquireCpuForNetCalls();

        Intent intent = new Intent(applicationContext, MsgSendService.class);
        intent.setAction(MsgSendService.ACTION_DEAL_CALL);
        intent.putExtra("subscription",subscriptionId);
        intent.putExtra("incoming_number",incomingNumber);
        String stateStr = "";
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                stateStr += "IDLE 挂断";
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                stateStr = "RINGING 响铃";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                stateStr = "OFFHOOK 接听或者往外拨打";
                break;
            default:
                break;
        }
        intent.putExtra("state",stateStr);

        intent.putExtras(intent);
        applicationContext.startService(intent);
        super.onCallStateChanged(state, incomingNumber);
    }
}
