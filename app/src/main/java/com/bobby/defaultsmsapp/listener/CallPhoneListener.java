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
    private long lastRingingOrOffHookTime = System.currentTimeMillis();
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

    static private  String lastReceiveRingState = "";

    public static void setListeners(Context applicationContext, String ring_state){
        //SubscriptionManager subscriptionManager = SubscriptionManager.from(applicationContext);
        lastReceiveRingState = ring_state;
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

    private CallPhoneListener getOtherListener(){
        if(this.subscriptionId == 0){
            return sim2Listener;
        }else {
            return sim1Listener;
        }
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        boolean isMatchBroadReceiveState = false;
        String stateOutStr = "";
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                stateOutStr += "IDLE 挂断";
                isMatchBroadReceiveState = "IDLE".equals(lastReceiveRingState);

                if(lastRingingOrOffHookTime - getOtherListener().lastRingingOrOffHookTime < 0){
                    //最后一次响铃或者 打电话时间 早的是不需要通知消息。
                    return;
                }

                break;
            case TelephonyManager.CALL_STATE_RINGING:
                lastRingingOrOffHookTime = System.currentTimeMillis();
                stateOutStr = "RINGING 响铃";
                isMatchBroadReceiveState = "RINGING".equals(lastReceiveRingState);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                stateOutStr = "OFFHOOK 接听或者往外拨打";
                isMatchBroadReceiveState = "OFFHOOK".equals(lastReceiveRingState);
                lastRingingOrOffHookTime = System.currentTimeMillis();
                break;
            default:
                break;
        }
        if(!isMatchBroadReceiveState){
            //如果listener状态和 广播状态不一致说明电话不是来自 当前listener的电话卡。 不做处理。
            return;
        }

        WakeLockForNetUse.getInstance().acquireCpuForNetCalls();
        Intent intent = new Intent(applicationContext, MsgSendService.class);
        intent.setAction(MsgSendService.ACTION_DEAL_CALL);
        intent.putExtra("subscription",subscriptionId);
        intent.putExtra("incoming_number",incomingNumber);

        intent.putExtra("state",stateOutStr);

        intent.putExtras(intent);
        applicationContext.startService(intent);
        super.onCallStateChanged(state, incomingNumber);
    }
}
