package com.bobby.defaultsmsapp.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.SmsManager;

import com.bobby.defaultsmsapp.utils.MailSender;
import com.bobby.defaultsmsapp.utils.WakeLockForNetUse;
import com.bobby.defaultsmsapp.utils.WeChartSender;
import com.bobby.defaultsmsapp.config.JSONConfig;
import com.bobby.defaultsmsapp.config.WechartAppWithCondition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MsgSendService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_DEAL_SMS_SERVICE = "com.bobby.firstsms.action.ACTION_DEAL_SMS_SERVICE";
    public static final String ACTION_DEAL_CALL = "com.bobby.firstsms.action.ACTION_DEAL_CALL";

    public MsgSendService() {
        super("MsgSendService");
    }

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_DEAL_SMS_SERVICE.equals(action)) {
                    dealMsg(intent);
                } else if (ACTION_DEAL_CALL.equals(action)) {
                    dealCall(intent);
                }
            }
        }finally {
            WakeLockForNetUse.getInstance().releaseCpuForNetCalls();
        }
    }

    private void dealCall(Intent intent) {
        int subscription = intent.getIntExtra("subscription", 0);
        final String incomingNumber = intent.getStringExtra("incoming_number");
        final String ring_state = intent.getStringExtra("state");
        final String phone = (subscription == 0 ? JSONConfig.instance().getHintForSubscription0():JSONConfig.instance().getHintForSubscription1());
        final int appId = (subscription == 0 ? JSONConfig.instance().getDefaultWechartAppIdForSubscription0() : JSONConfig.instance().getDefaultWechartAppIdForSubscription1());

        WeChartSender.sendWeChart(appId, format.format(new Timestamp(System.currentTimeMillis())) + ":\n" + incomingNumber+" " +ring_state + " on "+phone);
    }


    private boolean isFromOurPhones(String sender) {
        if(sender == null )return false;
        for(String phone : JSONConfig.instance().getReplaySmsFromNumbers()){
            if(sender.endsWith(phone)){
                return true;
            }
        }

        return false;
    }


    private void sendTextMessage(final Intent intent, final String sendContent, final SmsManager smsManager, final String no) {

        try {
            int subscription = intent.getIntExtra("subscription", 0);
            //写数据库
            writeToSmsDataBase(subscription, Telephony.Sms.MESSAGE_TYPE_OUTBOX, no, sendContent);
        }catch (Exception e){
            e.printStackTrace();
        }


        if(GSM_SMS_ACTION.equals(intent.getAction())){
            ArrayList<String> dividedMsgs = gsmDivideMessage(smsManager,sendContent);
            sendGsmSmsManage(smsManager,no,dividedMsgs);
        }else{
            ArrayList<String> dividedMsgs = smsManager.divideMessage(sendContent);
            smsManager.sendMultipartTextMessage(no, null, dividedMsgs, null, null);
        }
    }


    static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    static final String GSM_SMS_ACTION = "android.provider.Telephony.GSM_SMS_RECEIVED";
    private MailSender mailSender = new MailSender();


    private static Method gsmCreateFromPdu;
    private static Method gsmSendTextMemmage;
    private static Method gsmDivideMessage;

    public static final Class<?>[] SEND_MULTIPART_TEXT_PARM_TYPES = new Class<?>[]{String.class, String.class,ArrayList.class, ArrayList.class, ArrayList.class, boolean.class, int.class, int.class, int.class};

    public static final Class<?>[] DIVIDE_MESSAGE_PARMS_TYPES = new Class<?>[]{String.class, int.class};

    //	private static SmsManager gsmSmsManager ;
//	private static SmsManager cdmaSmsManager ;
    static {
        try {
            Class<?> gsmMsgClass = Class.forName("com.android.internal.telephony.gsm.SmsMessage");
            if (gsmMsgClass != null)
                gsmCreateFromPdu = gsmMsgClass.getMethod("createFromPdu", byte[].class);

            try {
                gsmSendTextMemmage = SmsManager.getDefault().getClass().getMethod("sendMultipartTextMessage", SEND_MULTIPART_TEXT_PARM_TYPES);
                gsmDivideMessage = SmsManager.getDefault().getClass().getMethod("divideMessage", DIVIDE_MESSAGE_PARMS_TYPES);
            }catch(Exception e) {
                //非三星 电信手机
               // cdmaCreateFromPdu = SmsMessage.class.getMethod("createFromPdu", byte[].class);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class MyMessage {
        private Object msgbase;

        MyMessage(Object msg) {
            msgbase = msg;
        }

        /**
         * 根据不同的action代表来源于不同的卡。
         *
         * @param pdu  短信数据
         * @return  msg
         *
         */
        static MyMessage createSmsFromPdu(byte[] pdu) {
            MyMessage msg ;
            try {
                if (gsmCreateFromPdu == null)
                    return null;
                msg = new MyMessage(gsmCreateFromPdu.invoke(null, (Object) pdu));
                return msg;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Returns the originating address (sender) of this SMS message in
         * String form or null if unavailable
         */
         String getOriginatingAddress() {
            try {
                if (msgbase == null)
                    return "";
                Method getmethod = msgbase.getClass().getMethod("getOriginatingAddress", (Class[]) null);
                return getmethod.invoke(msgbase, (Object[])null).toString();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return "";
        }

        /**
         * Returns the service centre timestamp in currentTimeMillis() format
         */
        long getTimestampMillis() {
            try {
                if (msgbase == null)
                    return -1;
                Method getmethod = msgbase.getClass().getMethod("getTimestampMillis", (Class[]) null);
                return (Long)getmethod.invoke(msgbase, (Object[])null);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return -1;
        }


        /**
         * Returns the message body as a String, if it exists and is text based.
         * @return message body is there is one, otherwise null
         */
        String getMessageBody() {
            try {
                if (msgbase == null)
                    return "";
                Method getmethod = msgbase.getClass().getMethod("getMessageBody", (Class[]) null);
                return getmethod.invoke(msgbase, (Object[])null).toString();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return "";
        }

    }

    @SuppressWarnings("unchecked")
    private ArrayList<String> gsmDivideMessage(SmsManager smsManager, String msg){
        try {
            return (ArrayList<String>) gsmDivideMessage.invoke(smsManager, msg,0);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void sendGsmSmsManage(SmsManager smsManager, String sendto, ArrayList<String> dividedMsgs){
        try {
            gsmSendTextMemmage.invoke(smsManager, sendto,null,dividedMsgs,null,null,false,0,0,0);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private void dealMsg(Intent intent) {


        int subscription = intent.getIntExtra("subscription", 0);

        //134手机插入卡槽2  186手机插入插槽1    微信企业号 134手机id为 5  186手机id为 6
        int appId = (subscription == 0 ? JSONConfig.instance().getDefaultWechartAppIdForSubscription0() : JSONConfig.instance().getDefaultWechartAppIdForSubscription1());

        String msgAction = intent.getStringExtra("sysInAction");

        if (msgAction.equals(SMS_ACTION) || GSM_SMS_ACTION.equals(msgAction)) {

            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus != null && pdus.length != 0) {
                MyMessage[] messages = new MyMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    byte[] pdu = (byte[]) pdus[i];
                    messages[i] = MyMessage.createSmsFromPdu(pdu);
                }
                StringBuilder sb = new StringBuilder();
                Date date = null;
                String tmpSender = null;
                for (MyMessage message : messages) {
                    String messageBody = message.getMessageBody();
                    tmpSender = message.getOriginatingAddress();
                    sb.append(messageBody);
                    date = new Date(message.getTimestampMillis());
                }

                final String msg = sb.toString().trim();
                //使用收短信的卡发短信
                final SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscription);
                final String sender = tmpSender;
                //如果来自 指令发送手机 则判断是否转发指令
                if (sender != null && isFromOurPhones(sender) && msg.startsWith("to")) {
                    int i = msg.indexOf(":");
                    if (i > 0) {
                        String no = msg.substring(2, i).trim();
                        if (no.matches("\\d+")) {
                            sendTextMessage(intent, msg.substring(i + 1, msg.length()).trim(), smsManager, no);
                            return;//转发完成返回
                        }
                    }
                }


                final String sendContent = "" + format.format(date) + "\n" + "" + sender + ":\n" + "" + msg;

                //转发邮件
                try {
                    boolean isNeedSendSmsWhenWeChartFail = false;
                    OUTLABLE:
                    for(WechartAppWithCondition condition : JSONConfig.instance().getWithConditionsWechartAppIds()){
                        for(String bodyCond:condition.getBodyContains()){
                            if(msg != null && msg.contains(bodyCond)){
                                appId = condition.getAppId();
                                isNeedSendSmsWhenWeChartFail = condition.isImport();
                                break OUTLABLE;
                            }
                        }

                        for(String senderCond:condition.getSenderContains()){
                            if(sender != null && sender.contains(senderCond)){
                                appId = condition.getAppId();
                                isNeedSendSmsWhenWeChartFail = condition.isImport();
                                break OUTLABLE;
                            }
                        }
                    }


                    try {
                        //写数据库
                        writeToSmsDataBase(subscription, Telephony.Sms.MESSAGE_TYPE_INBOX, sender, msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //发送微信
                    if (WeChartSender.sendWeChart(appId, sendContent)) {
                        return;//发送微信成功 退出
                    }

                    //转发重要短信
                    if (isNeedSendSmsWhenWeChartFail) {
                        for (String no : JSONConfig.instance().getSmsForwardToNumbers()) {
                            if (sender != null && sender.endsWith(no.substring(3))) {
                                continue;
                            }

                            sendTextMessage(intent, sendContent, smsManager, no);

                        }
                    }

                    //发邮件
                    mailSender.sendMail(subscription ==0?JSONConfig.instance().getMailSubjectForSubscription0():JSONConfig.instance().getMailSubjectForSubscription1(), sendContent, JSONConfig.instance().getMailUser(), JSONConfig.instance().getMailForwardTo(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }else if ("android.provider.Telephony.WAP_PUSH_DELIVER".equals(msgAction)) {

//            int transactionId = intent.getIntExtra("transactionId",-1);
//            int pduType = intent.getIntExtra("pduType",-1);
//            byte[] header = intent.getByteArrayExtra("header");
//            byte[]  data = intent.getByteArrayExtra("data");
//
//
//            System.out.print(transactionId);
//            System.out.print(pduType);
//            System.out.print(header);
//            System.out.print(data);
//
//
//            HashMap<String,String>  contentTypeParameters = (HashMap<String,String>)intent.getSerializableExtra ("contentTypeParameters");
//
//            System.out.print(contentTypeParameters);
//

        }

    }

    /**
     *
     * @param subscription  卡信息
     * @param type   Telephony.Sms.MESSAGE_TYPE_INBOX 或者 Telephony.Sms.MESSAGE_TYPE_OUTBOX
     * @param address  地址
     * @param msg     消息内容
     */
    private void writeToSmsDataBase(int subscription, int type, String address, String msg) {
        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("body", msg);
        values.put("type", type);//1  收到的短信 2  发送的短信
        values.put("sub_id",subscription);
        values.put("read", "1");//"1"means has read ,1表示已读
        getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

    }

}
