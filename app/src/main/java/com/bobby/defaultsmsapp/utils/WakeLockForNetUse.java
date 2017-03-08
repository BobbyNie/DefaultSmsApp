package com.bobby.defaultsmsapp.utils;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by niebo on 2017/3/8.
 */

public class WakeLockForNetUse{

    PowerManager pm ;
    PowerManager.WakeLock wl ;

    private WakeLockForNetUse(){};

    static WakeLockForNetUse instance = new WakeLockForNetUse();


    public static WakeLockForNetUse getInstance(){
        return instance;
    }

    public void setPowerManager(Context applicationContext) {
        if(pm == null || wl == null){
            pm = (PowerManager)applicationContext.getApplicationContext().getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"PowerMaintainableBroadcastReceiver");
        }
    }

    /**
     * 申请电源 默认保持1分钟
     */
    public void acquireCpuForNetCalls(){
        if(wl != null) {
            wl.acquire(60 * 1000*10);//默认10分钟
        }
    }

    /**
     * 申请电源
     */
    public void releaseCpuForNetCalls(){
        if(wl != null) {
            wl.release();
        }
    }
}
