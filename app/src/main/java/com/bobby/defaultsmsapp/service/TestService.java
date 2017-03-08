package com.bobby.defaultsmsapp.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class TestService extends IntentService {


    public TestService() {
        super("TestService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

//        Uri uri = CallLog.Calls.CONTENT_URI;
//        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
//
//        cursor.getCount();
//        StringBuilder sb = new StringBuilder();
//        int i = 0;
//        while(cursor.moveToNext()) {
//            cursor.moveToNext();
//            String [] names = cursor.getColumnNames();
//            for(String key: names){
//                String value = cursor.getString(cursor.getColumnIndex(key));
//                if(!key.equals("body")) sb.append(key+"="+value).append("   |  ");
//            }
//            sb.append("\r\n")    ;
//            i++;
//            if(i > 1000){
//                break;
//            }
//        }
//        System.out.print(sb);


//        SubscriptionManager subscriptionManager = SubscriptionManager.from(this);
//
//        SubscriptionInfo info0 = subscriptionManager.getActiveSubscriptionInfo(0);
//        SubscriptionInfo info1 = subscriptionManager.getActiveSubscriptionInfo(1);
//
//        SubscriptionInfo info3 = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0);
//
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//
//        //PhoneStateListener listener = new PhoneStateListener(0);
//
//        telephonyManager.listen(new PhoneStateListener(),PhoneStateListener.LISTEN_CALL_STATE);
//
//
//        System.out.print(info0);
//        System.out.print(info1);
//        info0.getNumber();
//        info0.getNumber();
//
//        StringBuilder sb = new StringBuilder();
//        Bundle bundle = intent.getExtras();
//        for(String key:bundle.keySet()){
//            sb.append(key+"="+bundle.get(key)+"\t");
//        }
//
//        System.out.print(sb);
        //获取卡来源  -- 天语手机
    }

}
