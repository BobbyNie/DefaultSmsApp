package com.bobby.defaultsmsapp.config;

import android.os.Environment;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by niebo on 2017/3/8.
 */

public class JSONConfig {

    private JSONConfig(){
    }


    private static JSONConfig loadConfigFromFile(String filePath){
        try {
            File sd = Environment.getExternalStorageDirectory();
            //boolean can_write = sd.canWrite();
            FileReader fr = new FileReader(new File(sd,filePath));
            StringBuffer sb = new StringBuffer(512);
            char[] buff = new char[512];
            int count = -1;
            while ((count = fr.read(buff)) >= 0){
                sb.append(buff,0,count);
            }

            fr.close();
            JSONConfig config = JSON.parseObject(sb.toString(),JSONConfig.class);
            return config;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public WechartAppWithCondition[] getWithConditionsWechartAppIds() {
        return withConditionsWechartAppIds;
    }

    private static JSONConfig _instance = null;

    private String wechartAccessTokenUrl ;
    private  String wechartMsgSendUrl;

    private String mailUser;
    private String mailPassword;
    private String mailUrl;
    private String mailSubjectForSubscription0;
    private String mailSubjectForSubscription1;
    private String mailForwardTo;

    private String hintForSubscription0;
    private String hintForSubscription1;

    private int defaultWechartAppIdForSubscription0;
    private int defaultWechartAppIdForSubscription1;

    private String[] smsForwardToNumbers;

    private String[] replaySmsFromNumbers;

    private HashMap<String,String> weChartSecrets;

    public void setWithConditionsWechartAppIds(WechartAppWithCondition[] withConditionsWechartAppIds) {
        this.withConditionsWechartAppIds = withConditionsWechartAppIds;
    }

    private WechartAppWithCondition[] withConditionsWechartAppIds;


    public synchronized static JSONConfig instance() {
        if(_instance == null){
            _instance = loadConfigFromFile("DefaultSmsApp/DefaultSmsAppConfig.json");
        }
        return _instance;
    }


    public String getWechartAccessTokenUrl() {
        return wechartAccessTokenUrl;
    }

    public String getWechartMsgSendUrl() {
        return wechartMsgSendUrl;
    }

    public String getMailUser() {
        return mailUser;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public String getMailUrl() {
        return mailUrl;
    }

    public String getHintForSubscription0() {
        return hintForSubscription0;
    }

    public String getHintForSubscription1() {
        return hintForSubscription1;
    }

    public int getDefaultWechartAppIdForSubscription1() {
        return defaultWechartAppIdForSubscription1;
    }

    public int getDefaultWechartAppIdForSubscription0() {
        return defaultWechartAppIdForSubscription0;
    }

    public String[] getSmsForwardToNumbers() {
        return smsForwardToNumbers;
    }

    public String[] getReplaySmsFromNumbers() {
        return replaySmsFromNumbers;
    }

    public String getMailSubjectForSubscription0() {
        return mailSubjectForSubscription0;
    }

    public String getMailSubjectForSubscription1() {
        return mailSubjectForSubscription1;
    }

    public String getMailForwardTo() {
        return mailForwardTo;
    }


    public void setWechartAccessTokenUrl(String wechartAccessTokenUrl) {
        this.wechartAccessTokenUrl = wechartAccessTokenUrl;
    }

    public void setWechartMsgSendUrl(String wechartMsgSendUrl) {
        this.wechartMsgSendUrl = wechartMsgSendUrl;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public void setMailUrl(String mailUrl) {
        this.mailUrl = mailUrl;
    }

    public void setMailSubjectForSubscription0(String mailSubjectForSubscription0) {
        this.mailSubjectForSubscription0 = mailSubjectForSubscription0;
    }

    public void setMailSubjectForSubscription1(String mailSubjectForSubscription1) {
        this.mailSubjectForSubscription1 = mailSubjectForSubscription1;
    }

    public void setMailForwardTo(String mailForwardTo) {
        this.mailForwardTo = mailForwardTo;
    }

    public void setHintForSubscription0(String hintForSubscription0) {
        this.hintForSubscription0 = hintForSubscription0;
    }

    public void setHintForSubscription1(String hintForSubscription1) {
        this.hintForSubscription1 = hintForSubscription1;
    }

    public void setDefaultWechartAppIdForSubscription0(int defaultWechartAppIdForSubscription0) {
        this.defaultWechartAppIdForSubscription0 = defaultWechartAppIdForSubscription0;
    }

    public void setDefaultWechartAppIdForSubscription1(int defaultWechartAppIdForSubscription1) {
        this.defaultWechartAppIdForSubscription1 = defaultWechartAppIdForSubscription1;
    }

    public void setSmsForwardToNumbers(String[] smsForwardToNumbers) {
        this.smsForwardToNumbers = smsForwardToNumbers;
    }

    public void setReplaySmsFromNumbers(String[] replaySmsFromNumbers) {
        this.replaySmsFromNumbers = replaySmsFromNumbers;
    }

    public HashMap<String, String> getWeChartSecrets() {
        return weChartSecrets;
    }

    public void setWeChartSecrets(HashMap<String, String> weChartSecrets) {
        this.weChartSecrets = weChartSecrets;
    }
}
