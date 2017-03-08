package com.bobby.defaultsmsapp.config;

public class WechartAppWithCondition{

    public WechartAppWithCondition(){

    }

    WechartAppWithCondition(int appId,boolean isImport,String[] senderContains,String[] bodyContains){
        this.appId = appId;
        this.bodyContains = bodyContains;
        this.senderContains = senderContains;
        this.isImport = isImport;
    }
    private int appId ;
    private boolean isImport;
    private String[] senderContains;
    private String[] bodyContains;

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public boolean isImport() {
        return isImport;
    }

    public void setImport(boolean anImport) {
        isImport = anImport;
    }

    public String[] getSenderContains() {
        return senderContains;
    }

    public void setSenderContains(String[] senderContains) {
        this.senderContains = senderContains;
    }

    public String[] getBodyContains() {
        return bodyContains;
    }

    public void setBodyContains(String[] bodyContains) {
        this.bodyContains = bodyContains;
    }
}